package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ForecastResponseDto(
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "timezone") val timezone: String?,
    @Json(name = "current") val current: CurrentWeatherDto?,
    @Json(name = "hourly") val hourly: HourlyWeatherDto?,
    @Json(name = "daily") val daily: DailyWeatherDto?
)

@JsonClass(generateAdapter = true)
data class CurrentWeatherDto(
    @Json(name = "temperature_2m") val temperature2m: Double?,
    @Json(name = "apparent_temperature") val apparentTemperature: Double?,
    @Json(name = "relative_humidity_2m") val relativeHumidity2m: Double?,
    @Json(name = "precipitation") val precipitation: Double?,
    @Json(name = "weather_code") val weatherCode: Int?,
    @Json(name = "wind_speed_10m") val windSpeed10m: Double?
)

@JsonClass(generateAdapter = true)
data class HourlyWeatherDto(
    @Json(name = "time") val time: List<String>?,
    @Json(name = "temperature_2m") val temperature2m: List<Double>?,
    @Json(name = "apparent_temperature") val apparentTemperature: List<Double>?,
    @Json(name = "precipitation_probability") val precipitationProbability: List<Int>?,
    @Json(name = "weather_code") val weatherCode: List<Int>?,
    @Json(name = "wind_speed_10m") val windSpeed10m: List<Double>?
)

@JsonClass(generateAdapter = true)
data class DailyWeatherDto(
    @Json(name = "time") val time: List<String>?,
    @Json(name = "weather_code") val weatherCode: List<Int>?,
    @Json(name = "temperature_2m_max") val temperature2mMax: List<Double>?,
    @Json(name = "temperature_2m_min") val temperature2mMin: List<Double>?,
    @Json(name = "precipitation_probability_max") val precipitationProbabilityMax: List<Int>?,
    @Json(name = "precipitation_sum") val precipitationSum: List<Double>?,
    @Json(name = "wind_speed_10m_max") val windSpeed10mMax: List<Double>?
)

@JsonClass(generateAdapter = true)
data class GeocodingResponseDto(
    @Json(name = "results") val results: List<GeocodingResultDto>?
)

@JsonClass(generateAdapter = true)
data class GeocodingResultDto(
    @Json(name = "id") val id: Long?,
    @Json(name = "name") val name: String,
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "admin1") val admin1: String?, // State / province
    @Json(name = "country") val country: String?,
    @Json(name = "country_code") val countryCode: String?
)
