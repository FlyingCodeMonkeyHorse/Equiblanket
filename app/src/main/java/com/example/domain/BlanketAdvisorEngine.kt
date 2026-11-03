package com.example.domain

import com.example.data.local.CustomBlanketEntity
import com.example.data.model.BlanketRecommendation
import com.example.data.model.BlanketType
import com.example.data.model.CoatType
import com.example.data.model.Horse
import com.example.data.model.ShelterType
import com.example.data.model.WeatherForecast

object BlanketAdvisorEngine {

    /**
     * Determines the optimal blanketing strategy based on equine thermoregulation guidelines,
     * considering coat condition, shelter, age, overnight temperature, wind chill, precipitation,
     * and optional user-defined custom blanket types with specific temperature ranges.
     */
    fun evaluate(
        horse: Horse,
        weather: WeatherForecast,
        customBlankets: List<CustomBlanketEntity> = emptyList()
    ): BlanketRecommendation {
        val overnightLow = weather.eveningLowTempF
        val windChill = weather.overnightApparentLowF
        val windSpeed = weather.maxWindSpeedMph
        val precipProb = weather.precipitationProbMax
        val isWet = weather.isRaining || weather.isSnowing || weather.isFreezingPrecip || precipProb >= 45

        // Effective comfort temperature starts with the lower of evening low and wind chill
        var effectiveTemp = minOf(overnightLow, windChill)

        val factors = mutableListOf<String>()

        // 1. Wetness / Precipitation penalty:
        if (isWet && horse.shelterType != ShelterType.STALLED) {
            effectiveTemp -= 8.0
            factors.add("Wet coat / rain penalty: -8°F thermal loss")
        }

        // 2. High wind penalty (>15 mph):
        if (windSpeed > 15 && horse.shelterType != ShelterType.STALLED) {
            factors.add("High wind (${windSpeed.toInt()} mph) penetrates coat")
        }

        // 3. Coat clipping:
        val perceivedColdTemp = effectiveTemp - horse.totalTempOffset

        if (horse.coatType == CoatType.CLIPPED) {
            factors.add("Body clipped: lacks natural hair coat (+15°F warmth need)")
        } else if (horse.coatType == CoatType.MODERATE_CLIP) {
            factors.add("Partial clip: chest/flank exposed (+8°F warmth need)")
        } else {
            factors.add("Natural winter coat provides baseline insulation")
        }

        if (horse.isSenior) {
            factors.add("Senior horse: decreased metabolic heat generation")
        }

        if (horse.shelterType == ShelterType.STALLED) {
            factors.add("Stalled overnight: shielded from wind and precipitation")
        } else if (horse.shelterType == ShelterType.OPEN_PASTURE) {
            factors.add("Open pasture: fully exposed to elements overnight")
        }

        if (horse.customTempOffsetF != 0) {
            factors.add("Custom horse preference offset: ${if (horse.customTempOffsetF > 0) "+" else ""}${horse.customTempOffsetF}°F")
        }

        // Check if user has defined custom blankets with temperature ranges
        val tempInt = perceivedColdTemp.toInt()
        val customMatch = if (customBlankets.isNotEmpty()) {
            customBlankets.firstOrNull { tempInt in it.minTempF..it.maxTempF }
                ?: if (tempInt < (customBlankets.minOfOrNull { it.minTempF } ?: 0)) {
                    // Colder than lowest threshold -> recommend the heaviest blanket
                    customBlankets.minByOrNull { it.minTempF }
                } else if (tempInt > (customBlankets.maxOfOrNull { it.maxTempF } ?: 60)) {
                    // Warmer than highest threshold -> recommend no blanket
                    customBlankets.firstOrNull { it.category == "NONE" || it.fillGrams == 0 }
                } else null
        } else null

        val recommendationType: BlanketType
        val headline: String
        val rationale: String
        val customName: String?
        val customRange: String?

        if (customMatch != null) {
            customName = customMatch.name
            customRange = customMatch.tempRangeDisplay

            recommendationType = when (customMatch.category.uppercase()) {
                "NONE" -> BlanketType.NO_BLANKET
                "SHEET" -> BlanketType.RAIN_SHEET
                "LIGHT" -> BlanketType.LIGHT_TURNOUT
                "MEDIUM" -> BlanketType.MEDIUM_TURNOUT
                "HEAVY" -> if (customMatch.hasNeckCover) BlanketType.HEAVY_WITH_HOOD else BlanketType.HEAVY_TURNOUT
                else -> when {
                    customMatch.fillGrams == 0 && customMatch.isWaterproof -> BlanketType.RAIN_SHEET
                    customMatch.fillGrams == 0 -> BlanketType.NO_BLANKET
                    customMatch.fillGrams in 1..150 -> BlanketType.LIGHT_TURNOUT
                    customMatch.fillGrams in 151..280 -> BlanketType.MEDIUM_TURNOUT
                    customMatch.hasNeckCover -> BlanketType.HEAVY_WITH_HOOD
                    else -> BlanketType.HEAVY_TURNOUT
                }
            }

            val needsBlanket = recommendationType != BlanketType.NO_BLANKET

            if (needsBlanket) {
                headline = "${customMatch.name} Recommended"
                rationale = "Perceived temperature tonight for ${horse.name} is ${perceivedColdTemp.toInt()}°F (actual low ${overnightLow.toInt()}°F). This matches your custom blanket '${customMatch.name}' configured for ${customMatch.tempRangeDisplay} (${customMatch.fillGrams}g fill)."
            } else {
                headline = "No Blanket Needed"
                rationale = "Perceived temperature tonight for ${horse.name} is ${perceivedColdTemp.toInt()}°F (actual low ${overnightLow.toInt()}°F). Matches your '${customMatch.name}' threshold for mild weather."
            }

            return BlanketRecommendation(
                needsBlanket = needsBlanket,
                blanketType = recommendationType,
                horseName = horse.name,
                headline = headline,
                rationale = rationale,
                factorsList = factors,
                effectiveComfortTempF = effectiveTemp,
                eveningLowF = overnightLow,
                overnightApparentLowF = windChill,
                rainRisk = isWet,
                windWarning = windSpeed >= 20.0,
                customBlanketName = customName,
                customTempRange = customRange
            )
        }

        // Fallback default calculation when no custom blanket list is present
        when {
            // Extreme freezing: Below 18°F perceived
            perceivedColdTemp < 18.0 -> {
                recommendationType = BlanketType.HEAVY_WITH_HOOD
                headline = "Heavy Turnout + Hood Required"
                rationale = "Severe freezing conditions tonight (perceived ${perceivedColdTemp.toInt()}°F). ${horse.name} requires a heavy insulated rug (360g-400g) with neck hood."
            }

            // Deep cold: 18°F to 29°F perceived
            perceivedColdTemp in 18.0..29.9 -> {
                recommendationType = BlanketType.HEAVY_TURNOUT
                headline = "Heavy Turnout Rug Needed"
                rationale = "Freezing overnight low (${overnightLow.toInt()}°F). A heavy turnout (300g-350g) will preserve body condition and prevent shivering."
            }

            // Moderate cold: 30°F to 42°F perceived
            perceivedColdTemp in 30.0..42.9 -> {
                recommendationType = BlanketType.MEDIUM_TURNOUT
                headline = "Medium Turnout Rug Needed"
                rationale = "Chilly evening forecast (low ${overnightLow.toInt()}°F, wind chill ${windChill.toInt()}°F). A medium turnout (200g-250g) offers ideal comfort."
            }

            // Brisk cool: 43°F to 52°F perceived
            perceivedColdTemp in 43.0..52.9 -> {
                if (isWet && horse.shelterType != ShelterType.STALLED) {
                    recommendationType = BlanketType.RAIN_SHEET
                    headline = "Waterproof Rain Sheet Needed"
                    rationale = "Temperatures are moderate (${overnightLow.toInt()}°F), but rain/snow (${precipProb}% chance) will soak the coat. A waterproof 0g rain sheet will keep ${horse.name} dry."
                } else if (horse.coatType == CoatType.CLIPPED) {
                    recommendationType = BlanketType.LIGHT_TURNOUT
                    headline = "Light Turnout Rug Needed"
                    rationale = "Because ${horse.name} is clipped, an overnight low of ${overnightLow.toInt()}°F requires a light turnout (100g-150g) for warmth."
                } else {
                    recommendationType = BlanketType.NO_BLANKET
                    headline = "No Blanket Needed"
                    rationale = "Mild temperatures (${overnightLow.toInt()}°F) with dry conditions. ${horse.name}'s natural coat will comfortably regulate temperature."
                }
            }

            // Mild weather: 53°F to 60°F perceived
            perceivedColdTemp in 53.0..60.9 -> {
                if (isWet && horse.shelterType != ShelterType.STALLED && windSpeed > 10) {
                    recommendationType = BlanketType.RAIN_SHEET
                    headline = "Optional Rain Sheet for Chilling Rain"
                    rationale = "Mild temperatures (${overnightLow.toInt()}°F), but rain and wind are expected. A breathable rain sheet will prevent getting soaked."
                } else {
                    recommendationType = BlanketType.NO_BLANKET
                    headline = "No Blanket Needed"
                    rationale = "Overnight weather is pleasant (${overnightLow.toInt()}°F). Blanketing could cause overheating or sweating."
                }
            }

            // Warm weather: Above 60°F perceived
            else -> {
                recommendationType = BlanketType.NO_BLANKET
                headline = "No Blanket Needed"
                rationale = "Warm evening with overnight low around ${overnightLow.toInt()}°F. Keep naked to avoid sweating and distress."
            }
        }

        val needsBlanket = recommendationType != BlanketType.NO_BLANKET

        return BlanketRecommendation(
            needsBlanket = needsBlanket,
            blanketType = recommendationType,
            horseName = horse.name,
            headline = headline,
            rationale = rationale,
            factorsList = factors,
            effectiveComfortTempF = effectiveTemp,
            eveningLowF = overnightLow,
            overnightApparentLowF = windChill,
            rainRisk = isWet,
            windWarning = windSpeed >= 20.0,
            customBlanketName = null,
            customTempRange = null
        )
    }
}
