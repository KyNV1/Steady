package com.steady.app.feature.subscription

import androidx.lifecycle.ViewModel
import com.steady.app.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class PlanPeriod { WEEKLY, YEARLY }

/** A plan's price as Google Play reports it (micros + ISO currency), never a hardcoded amount. */
data class PlanOffer(
    val priceMicros: Long,
    val currencyCode: String,
    val formattedPrice: String,
    val freeTrialDays: Int? = null,
)

enum class PaywallMessage { PURCHASE_UNAVAILABLE, RESTORE_UNAVAILABLE, LEGAL_UNAVAILABLE }

data class PaywallUiState(
    val selected: PlanPeriod = PlanPeriod.YEARLY,
    /** Empty until Play returns ProductDetails; the UI then shows "price unavailable". */
    val offers: Map<PlanPeriod, PlanOffer> = emptyMap(),
    val canLoadSamples: Boolean = BuildConfig.DEBUG,
    val message: PaywallMessage? = null,
) {
    val selectedOffer: PlanOffer? get() = offers[selected]
}

/**
 * Paywall shell. Play Billing (live ProductDetails, purchase flow, backend verification) isn't
 * integrated yet, so release builds show no prices and the CTA can't start a purchase.
 */
@HiltViewModel
class PremiumPaywallViewModel @Inject constructor() : ViewModel() {
    private val mutableState = MutableStateFlow(PaywallUiState())
    val state: StateFlow<PaywallUiState> = mutableState.asStateFlow()

    fun select(period: PlanPeriod) = mutableState.update { it.copy(selected = period) }

    fun onSubscribe() = show(PaywallMessage.PURCHASE_UNAVAILABLE)
    fun onRestore() = show(PaywallMessage.RESTORE_UNAVAILABLE)
    fun onLegalLink() = show(PaywallMessage.LEGAL_UNAVAILABLE)
    fun onMessageShown() = mutableState.update { it.copy(message = null) }

    /** Debug-only: the design's sample prices, so the plan cards can be reviewed without Play. */
    fun loadSamplePrices() {
        if (!BuildConfig.DEBUG) return
        mutableState.update { it.copy(offers = debugSampleOffers()) }
    }

    private fun show(message: PaywallMessage) = mutableState.update { it.copy(message = message) }
}

internal fun debugSampleOffers(): Map<PlanPeriod, PlanOffer> = mapOf(
    PlanPeriod.WEEKLY to PlanOffer(6_990_000, "USD", "$6.99", freeTrialDays = 7),
    PlanPeriod.YEARLY to PlanOffer(39_990_000, "USD", "$39.99", freeTrialDays = 7),
)

private const val WEEKS_PER_YEAR = 52
private const val MICROS_PER_UNIT = 1_000_000L

/** Yearly price spread over 52 weeks, rounded to the currency's minor unit ("$0.77"). */
internal fun weeklyEquivalent(offer: PlanOffer, locale: Locale = Locale.getDefault()): String {
    val currency = Currency.getInstance(offer.currencyCode)
    val digits = currency.defaultFractionDigits.coerceAtLeast(0)
    val perWeek = BigDecimal(offer.priceMicros)
        .divide(BigDecimal(MICROS_PER_UNIT * WEEKS_PER_YEAR), digits, RoundingMode.HALF_UP)
    return NumberFormat.getCurrencyInstance(locale).apply {
        this.currency = currency
        minimumFractionDigits = digits
        maximumFractionDigits = digits
    }.format(perWeek)
}
