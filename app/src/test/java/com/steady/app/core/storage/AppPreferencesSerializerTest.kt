package com.steady.app.core.storage

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AppPreferencesSerializerTest {
    @Test
    fun `preferences survive protobuf round trip`() = runTest {
        val expected = AppPreferences(
            accessToken = "token",
            refreshToken = "refresh",
            userId = "42",
            fcmToken = "fcm",
            themeMode = ThemeMode.DARK,
            onboardingCompleted = true,
        )
        val output = ByteArrayOutputStream()

        AppPreferencesSerializer.writeTo(expected, output)
        val actual = AppPreferencesSerializer.readFrom(ByteArrayInputStream(output.toByteArray()))

        assertEquals(expected, actual)
    }

    @Test
    fun `onboardingCompleted defaults to false when absent`() = runTest {
        val output = ByteArrayOutputStream()
        AppPreferencesSerializer.writeTo(AppPreferences(userId = "42"), output)

        val actual = AppPreferencesSerializer.readFrom(ByteArrayInputStream(output.toByteArray()))

        assertEquals(false, actual.onboardingCompleted)
    }
}
