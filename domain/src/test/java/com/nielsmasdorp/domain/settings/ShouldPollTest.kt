package com.nielsmasdorp.domain.settings

import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ShouldPollTest {

    @Mock
    lateinit var settingsRepository: SettingsRepository

    @Mock
    lateinit var canSendNotifications: CanSendNotifications

    @Test
    fun `when all notifications are off, do not poll`() = runBlockingTest {
        // given
        whenever(settingsRepository.isBuyBoxNotificationsEnabled()).thenReturn(false)
        whenever(settingsRepository.isOrderNotificationsEnabled()).thenReturn(false)

        // when
        val shouldPoll = ShouldPoll(settingsRepository, canSendNotifications)

        // then
        Assert.assertFalse(shouldPoll.invoke())
    }

    @Test
    fun `when buy box notifications are on but cant send notifications, do not poll`() = runBlockingTest {
        // given
        whenever(settingsRepository.isBuyBoxNotificationsEnabled()).thenReturn(true)
        whenever(settingsRepository.isOrderNotificationsEnabled()).thenReturn(false)
        whenever(canSendNotifications.invoke()).thenReturn(false)

        // when
        val shouldPoll = ShouldPoll(settingsRepository, canSendNotifications)

        // then
        Assert.assertFalse(shouldPoll.invoke())
    }

    @Test
    fun `when order notifications are on but cant send notifications, do not poll`() = runBlockingTest {
        // given
        whenever(settingsRepository.isOrderNotificationsEnabled()).thenReturn(true)
        whenever(canSendNotifications.invoke()).thenReturn(false)

        // when
        val shouldPoll = ShouldPoll(settingsRepository, canSendNotifications)

        // then
        Assert.assertFalse(shouldPoll.invoke())
    }

    @Test
    fun `when order notifications are on and credentials available, poll`() = runBlockingTest {
        // given
        whenever(settingsRepository.isOrderNotificationsEnabled()).thenReturn(true)
        whenever(canSendNotifications.invoke()).thenReturn(true)

        // when
        val shouldPoll = ShouldPoll(settingsRepository, canSendNotifications)

        // then
        Assert.assertTrue(shouldPoll.invoke())
    }

    @Test
    fun `when buy box notifications are on and credentials available, poll`() = runBlockingTest {
        // given
        whenever(settingsRepository.isBuyBoxNotificationsEnabled()).thenReturn(true)
        whenever(settingsRepository.isOrderNotificationsEnabled()).thenReturn(false)
        whenever(canSendNotifications.invoke()).thenReturn(true)

        // when
        val shouldPoll = ShouldPoll(settingsRepository, canSendNotifications)

        // then
        Assert.assertTrue(shouldPoll.invoke())
    }
}