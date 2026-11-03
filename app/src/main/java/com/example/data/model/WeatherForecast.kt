package com.example.data.model

data class WeatherForecast(
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val currentTempF: Double,
    val eveningLowTempF: Double,
    val overnightApparentLowF: Double,
    val maxWindSpeedMph: Double,
    val precipitationProbMax: Int,
    val precipitationInches: Double,
    val weatherCode: Int,
    val weatherDescription: String,
    val isRaining: Boolean,
    val isSnowing: Boolean,
    val isFreezingPrecip: Boolean,
    val dailyForecasts: List<DailyWeatherItem> = emptyList(),
    val hourlyTemperatures: List<HourlyTempItem> = emptyList(),
    val lastUpdatedMillis: Long = System.currentTimeMillis()
)

data class DailyWeatherItem(
    val dateIso: String,
    val dayOfWeek: String,
    val minTempF: Double,
    val maxTempF: Double,
    val precipProbMax: Int,
    val weatherCode: Int,
    val weatherDescription: String
)

data class HourlyTempItem(
    val timeLabel: String,
    val tempF: Double,
    val apparentTempF: Double,
    val precipProb: Int,
    val weatherCode: Int
)

data class BlanketRecommendation(
    val needsBlanket: Boolean,
    val blanketType: BlanketType,
    val horseName: String,
    val headline: String,
    val rationale: String,
    val factorsList: List<String>,
    val effectiveComfortTempF: Double,
    val eveningLowF: Double,
    val overnightApparentLowF: Double,
    val rainRisk: Boolean,
    val windWarning: Boolean,
    val customBlanketName: String? = null,
    val customTempRange: String? = null
)
