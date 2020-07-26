package com.nielsmasdorp.lvbnotifier.presentation.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nielsmasdorp.lvbnotifier.work.StateUpdateScheduler
import com.nielsmasdorp.domain.notifications.MessageProvider
import com.nielsmasdorp.domain.settings.CanSendBuyBoxNotifications
import com.nielsmasdorp.domain.settings.CanSendNotifications
import com.nielsmasdorp.domain.settings.SettingsRepository
import com.nielsmasdorp.domain.settings.NotificationType.TELEGRAM
import com.nielsmasdorp.domain.settings.SettingsRepository.Companion.BUY_BOX_NOTIFICATIONS_ENABLED_KEY
import com.nielsmasdorp.domain.settings.SettingsRepository.Companion.CREDENTIALS
import com.nielsmasdorp.domain.settings.SettingsRepository.Companion.NOTIFICATIONS
import com.nielsmasdorp.domain.settings.SettingsRepository.Companion.NOTIFICATIONS_TYPE
import com.nielsmasdorp.domain.settings.SettingsRepository.Companion.ORDER_NOTIFICATIONS_ENABLED_KEY
import com.nielsmasdorp.domain.settings.SettingsRepository.Companion.POLL_FREQUENCY
import com.nielsmasdorp.domain.settings.SettingsRepository.Companion.SELLER_NAME
import com.nielsmasdorp.lvbnotifier.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settings: SettingsRepository,
    private val canSendNotifications: CanSendNotifications,
    private val canSendBuyBoxNotifications: CanSendBuyBoxNotifications,
    private val messageProvider: MessageProvider,
    private val stateUpdateScheduler: StateUpdateScheduler
) : ViewModel() {

    val error = SingleLiveEvent<PreferenceError>()

    val telegramSettingsVisible: LiveData<Boolean>
        get() = _telegramSettingsVisible

    private val _telegramSettingsVisible = MutableLiveData<Boolean>()

    init {
        setNotificationType()
    }

    fun onNotificationSettingChanged(key: String, enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            when (key) {
                BUY_BOX_NOTIFICATIONS_ENABLED_KEY -> {
                    if (enabled && !canSendBuyBoxNotifications.invoke()) {
                        disallowBuyBoxNotifications()
                    } else {
                        stateUpdateScheduler.schedule()
                    }
                }
                ORDER_NOTIFICATIONS_ENABLED_KEY -> {
                    if (enabled && !canSendNotifications.invoke()) {
                        disallowAllNotifications()
                    } else {
                        stateUpdateScheduler.schedule()
                    }
                }
            }
        }
    }

    fun onStringValueChanged(key: String, newValue: String) {
        when {
            key == NOTIFICATIONS_TYPE -> setNotificationType()
            key == POLL_FREQUENCY -> stateUpdateScheduler.schedule()
            newValue.isBlank() -> {
                viewModelScope.launch(Dispatchers.IO) {
                    if (CREDENTIALS.contains(key)) {
                        if (isAnyNotificationEnabled()) {
                            viewModelScope.launch(Dispatchers.Main) {
                                disallowAllNotifications()
                            }
                            stateUpdateScheduler.schedule()
                        }
                    } else if (key == SELLER_NAME) {
                        if (settings.isBuyBoxNotificationsEnabled()) {
                            viewModelScope.launch(Dispatchers.Main) {
                                disallowBuyBoxNotifications()
                            }
                            stateUpdateScheduler.schedule()
                        }
                    }
                }
            }
        }
    }

    private fun setNotificationType() {
        viewModelScope.launch(Dispatchers.IO) {
            val selectedNotificationType = settings.getSelectedNotificationType()
            viewModelScope.launch(Dispatchers.Main) {
                _telegramSettingsVisible.value = selectedNotificationType == TELEGRAM
                if (selectedNotificationType == TELEGRAM && isAnyNotificationEnabled() && !hasValidTelegramCredentials()) {
                    disallowAllNotifications()
                    stateUpdateScheduler.schedule()
                }
            }
        }
    }

    private suspend fun isAnyNotificationEnabled(): Boolean {
        return settings.isOrderNotificationsEnabled() || settings.isBuyBoxNotificationsEnabled()
    }

    private suspend fun hasValidTelegramCredentials(): Boolean {
        return settings.getTelegramChatId().isNotBlank() &&
                settings.getTelegramChatId().isNotBlank()
    }

    private suspend fun disallowBuyBoxNotifications() {
        settings.setBuyBoxNotificationsEnabled(false)
        setError(
            listOf(BUY_BOX_NOTIFICATIONS_ENABLED_KEY),
            messageProvider.getNotificationsNotAllowedError()
        )
    }

    private suspend fun disallowAllNotifications() {
        settings.setBuyBoxNotificationsEnabled(false)
        settings.setOrderNotificationsEnabled(false)
        setError(NOTIFICATIONS, messageProvider.getNotificationsNotAllowedError())
    }

    private fun setError(keys: List<String>, message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            error.value = PreferenceError(keys, message)
        }
    }

    data class PreferenceError(val preferenceKeys: List<String>, val errorMessage: String)
}