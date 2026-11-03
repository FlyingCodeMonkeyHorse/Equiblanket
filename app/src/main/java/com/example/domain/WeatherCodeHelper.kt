package com.example.domain

object WeatherCodeHelper {
    fun getDescription(code: Int): String {
        return when (code) {
            0 -> "Clear Sky"
            1 -> "Mainly Clear"
            2 -> "Partly Cloudy"
            3 -> "Overcast"
            45 -> "Foggy"
            48 -> "Depositing Rime Fog"
            51 -> "Light Drizzle"
            53 -> "Moderate Drizzle"
            55 -> "Dense Drizzle"
            56, 57 -> "Freezing Drizzle"
            61 -> "Slight Rain"
            63 -> "Moderate Rain"
            65 -> "Heavy Rain"
            66, 67 -> "Freezing Rain"
            71 -> "Slight Snow"
            73 -> "Moderate Snow"
            75 -> "Heavy Snow"
            77 -> "Snow Grains"
            80 -> "Light Showers"
            81 -> "Moderate Showers"
            82 -> "Violent Showers"
            85 -> "Slight Snow Showers"
            86 -> "Heavy Snow Showers"
            95 -> "Thunderstorm"
            96, 99 -> "Thunderstorm with Hail"
            else -> "Variably Cloudy"
        }
    }

    fun isPrecipitation(code: Int): Boolean = code in 51..99

    fun isRain(code: Int): Boolean = code in listOf(51, 53, 55, 61, 63, 65, 80, 81, 82, 95)

    fun isSnow(code: Int): Boolean = code in listOf(71, 73, 75, 77, 85, 86)

    fun isFreezing(code: Int): Boolean = code in listOf(56, 57, 66, 67, 96, 99)
}
