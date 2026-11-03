package com.example

import com.example.data.local.CustomBlanketEntity
import com.example.data.model.BlanketType
import com.example.data.model.CoatType
import com.example.data.model.Horse
import com.example.data.model.ShelterType
import com.example.data.model.WeatherForecast
import com.example.domain.BlanketAdvisorEngine
import org.junit.Assert.*
import org.junit.Test

class BlanketAdvisorEngineTest {

    private fun makeWeather(
        lowTempF: Double = 32.0,
        apparentLowF: Double = 26.0,
        isRaining: Boolean = false,
        windSpeed: Double = 10.0,
        precipProb: Int = 10
    ): WeatherForecast {
        return WeatherForecast(
            locationName = "Test Barn",
            latitude = 38.0,
            longitude = -84.0,
            currentTempF = lowTempF + 10.0,
            eveningLowTempF = lowTempF,
            overnightApparentLowF = apparentLowF,
            maxWindSpeedMph = windSpeed,
            precipitationProbMax = precipProb,
            precipitationInches = if (isRaining) 0.3 else 0.0,
            weatherCode = if (isRaining) 61 else 0,
            weatherDescription = if (isRaining) "Rain" else "Clear",
            isRaining = isRaining,
            isSnowing = false,
            isFreezingPrecip = false
        )
    }

    @Test
    fun testWarmWeatherNoBlanket() {
        val horse = Horse(
            name = "Sunny",
            coatType = CoatType.UNCLIPPED,
            shelterType = ShelterType.STALLED
        )
        val weather = makeWeather(lowTempF = 65.0, apparentLowF = 64.0)
        val rec = BlanketAdvisorEngine.evaluate(horse, weather)

        assertFalse(rec.needsBlanket)
        assertEquals(BlanketType.NO_BLANKET, rec.blanketType)
    }

    @Test
    fun testFreezingWeatherNeedsMediumOrHeavyBlanket() {
        val horse = Horse(
            name = "Dakota",
            coatType = CoatType.UNCLIPPED,
            shelterType = ShelterType.STALLED
        )
        val weather = makeWeather(lowTempF = 30.0, apparentLowF = 25.0)
        val rec = BlanketAdvisorEngine.evaluate(horse, weather)

        assertTrue(rec.needsBlanket)
        assertTrue(rec.blanketType == BlanketType.MEDIUM_TURNOUT || rec.blanketType == BlanketType.HEAVY_TURNOUT)
    }

    @Test
    fun testClippedHorseNeedsHeavierBlanketThanUnclipped() {
        val unclippedHorse = Horse(
            name = "Natural",
            coatType = CoatType.UNCLIPPED,
            shelterType = ShelterType.RUN_IN
        )
        val clippedHorse = Horse(
            name = "Clipped",
            coatType = CoatType.CLIPPED,
            shelterType = ShelterType.RUN_IN
        )
        val weather = makeWeather(lowTempF = 46.0, apparentLowF = 44.0)

        val unclippedRec = BlanketAdvisorEngine.evaluate(unclippedHorse, weather)
        val clippedRec = BlanketAdvisorEngine.evaluate(clippedHorse, weather)

        assertTrue(clippedRec.blanketType.grams >= unclippedRec.blanketType.grams)
        assertTrue(clippedRec.needsBlanket)
    }

    @Test
    fun testRainCausesRainSheetRequirementInCoolWeather() {
        val horse = Horse(
            name = "PastureHorse",
            coatType = CoatType.UNCLIPPED,
            shelterType = ShelterType.RUN_IN
        )
        val dryWeather = makeWeather(lowTempF = 56.0, apparentLowF = 55.0, isRaining = false, precipProb = 0)
        val wetWeather = makeWeather(lowTempF = 56.0, apparentLowF = 55.0, isRaining = true, precipProb = 80)

        val dryRec = BlanketAdvisorEngine.evaluate(horse, dryWeather)
        val wetRec = BlanketAdvisorEngine.evaluate(horse, wetWeather)

        assertEquals(BlanketType.NO_BLANKET, dryRec.blanketType)
        assertTrue(wetRec.needsBlanket)
        assertEquals(BlanketType.RAIN_SHEET, wetRec.blanketType)
    }

    @Test
    fun testCustomBlanketTypeWithUserDefinedTemperatureRange() {
        val horse = Horse(
            name = "Dakota",
            coatType = CoatType.UNCLIPPED,
            shelterType = ShelterType.STALLED
        )
        val customBlankets = listOf(
            CustomBlanketEntity(
                name = "Amigo Midweight 200g",
                category = "MEDIUM",
                fillGrams = 200,
                minTempF = 25,
                maxTempF = 38
            ),
            CustomBlanketEntity(
                name = "Rambo Heavy 400g + Hood",
                category = "HEAVY",
                fillGrams = 400,
                minTempF = -20,
                maxTempF = 24,
                hasNeckCover = true
            )
        )
        val weather = makeWeather(lowTempF = 32.0, apparentLowF = 30.0)

        val rec = BlanketAdvisorEngine.evaluate(horse, weather, customBlankets)

        assertTrue(rec.needsBlanket)
        assertEquals("Amigo Midweight 200g", rec.customBlanketName)
        assertTrue(rec.headline.contains("Amigo Midweight 200g"))
    }
}
