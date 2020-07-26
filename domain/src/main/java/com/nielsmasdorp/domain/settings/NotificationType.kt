package com.nielsmasdorp.domain.settings

enum class NotificationType(private val value: String) {

    SYSTEM("0"),
    TELEGRAM("1");

    companion object {

        fun fromValue(value: String): NotificationType? = values().find { it.value == value }
    }
}