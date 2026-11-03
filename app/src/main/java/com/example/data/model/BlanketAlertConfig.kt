package com.example.data.model

data class BlanketAlertConfig(
    val dailyAlertEnabled: Boolean = true,
    val alertHour: Int = 17, // 5:00 PM default (typical barn evening feed / night check)
    val alertMinute: Int = 0,
    val alertWhenBlanketNeeded: Boolean = true,
    val customLowTempAlertEnabled: Boolean = true,
    val customLowTempThresholdF: Int = 32, // Alert if overnight low drops <= 32°F
    val rainSnowAlertEnabled: Boolean = true,
    val highWindAlertEnabled: Boolean = true,
    val highWindThresholdMph: Int = 20
)
