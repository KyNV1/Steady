package com.steady.app.feature.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steady.app.BuildConfig
import com.steady.app.core.storage.AppDataStore
import com.steady.app.domain.model.AppNotification
import com.steady.app.domain.model.localEpochDay
import com.steady.app.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NotificationGroup { TODAY, EARLIER_THIS_WEEK, OLDER }

data class NotificationSection(val group: NotificationGroup, val items: List<AppNotification>)

data class NotificationsUiState(
    val isLoading: Boolean = true,
    val sections: List<NotificationSection> = emptyList(),
    val nowMillis: Long = 0L,
    val canLoadSamples: Boolean = BuildConfig.DEBUG,
) {
    val hasUnread: Boolean get() = sections.any { section -> section.items.any { it.isUnread } }
    val isEmpty: Boolean get() = sections.isEmpty()
}

private const val DAYS_IN_WEEK = 7

/** Buckets an inbox (newest first) by local calendar day relative to [nowMillis]; empty groups are dropped. */
fun groupNotifications(items: List<AppNotification>, nowMillis: Long): List<NotificationSection> {
    val today = localEpochDay(nowMillis)
    return items
        .groupBy { item ->
            val age = today - localEpochDay(item.createdAtEpochMillis)
            when {
                age <= 0 -> NotificationGroup.TODAY
                age < DAYS_IN_WEEK -> NotificationGroup.EARLIER_THIS_WEEK
                else -> NotificationGroup.OLDER
            }
        }
        .toSortedMap()
        .map { (group, groupItems) -> NotificationSection(group, groupItems) }
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    appDataStore: AppDataStore,
) : ViewModel() {
    private val userId = appDataStore.preferences.map { it.userId }.distinctUntilChanged()

    val state: StateFlow<NotificationsUiState> = userId
        .flatMapLatest { id -> notificationRepository.observeInbox(id) }
        .map { items ->
            val now = System.currentTimeMillis()
            NotificationsUiState(isLoading = false, sections = groupNotifications(items, now), nowMillis = now)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NotificationsUiState())

    fun markRead(notification: AppNotification) {
        if (!notification.isUnread) return
        viewModelScope.launch { notificationRepository.markRead(notification.id) }
    }

    fun markAllRead() {
        viewModelScope.launch {
            val id = userId.first()
            if (id.isNotBlank()) notificationRepository.markAllRead(id)
        }
    }

    /** Debug-only: fills the inbox with the Stitch design's sample messages so the layout can be checked. */
    fun loadSamples() {
        if (!BuildConfig.DEBUG) return
        viewModelScope.launch {
            val id = userId.first()
            if (id.isNotBlank()) notificationRepository.add(debugSampleNotifications(id, System.currentTimeMillis()))
        }
    }
}
