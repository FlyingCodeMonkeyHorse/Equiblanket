package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.AppDatabase
import com.example.data.local.BlanketDao
import com.example.data.local.BlanketLogEntity
import com.example.data.local.CustomBlanketEntity
import com.example.data.local.HorseEntity
import com.example.data.model.BlanketAlertConfig
import com.example.data.model.BlanketType
import com.example.data.model.CoatType
import com.example.data.model.DailyWeatherItem
import com.example.data.model.Horse
import com.example.data.model.HourlyTempItem
import com.example.data.model.KeeperType
import com.example.data.model.ShelterType
import com.example.data.model.WeatherForecast
import com.example.data.remote.WeatherApiService
import com.example.domain.WeatherCodeHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HorseBlanketRepository(private val context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val dao: BlanketDao = db.blanketDao()
    private val api: WeatherApiService = WeatherApiService.create()
    private val prefs: SharedPreferences = context.getSharedPreferences("horse_blanket_prefs", Context.MODE_PRIVATE)

    val horsesFlow: Flow<List<Horse>> = dao.getAllHorses().map { list ->
        list.map { it.toDomain() }
    }

    val logsFlow: Flow<List<BlanketLogEntity>> = dao.getAllLogs()

    val customBlanketsFlow: Flow<List<CustomBlanketEntity>> = dao.getAllCustomBlankets()

    suspend fun initializeDefaultsIfNeeded() = withContext(Dispatchers.IO) {
        if (dao.getCustomBlanketCount() == 0) {
            dao.insertCustomBlankets(getDefaultBlankets())
        }

        if (dao.getHorseCount() == 0) {
            val starterHorses = listOf(
                HorseEntity(
                    name = "Dakota",
                    breed = "Thoroughbred",
                    coatType = CoatType.CLIPPED.name,
                    shelterType = ShelterType.STALLED.name,
                    keeperType = KeeperType.HARD_KEEPER.name,
                    isSenior = true,
                    customTempOffsetF = 3,
                    notes = "Sensitive to cold drafts; prefers high neck rugs."
                ),
                HorseEntity(
                    name = "Sterling",
                    breed = "Warmblood",
                    coatType = CoatType.UNCLIPPED.name,
                    shelterType = ShelterType.RUN_IN.name,
                    keeperType = KeeperType.NORMAL.name,
                    isSenior = false,
                    customTempOffsetF = 0,
                    notes = "Thick natural coat; sweats easily if over-blanketed."
                ),
                HorseEntity(
                    name = "Barnaby",
                    breed = "Quarter Horse",
                    coatType = CoatType.MODERATE_CLIP.name,
                    shelterType = ShelterType.OPEN_PASTURE.name,
                    keeperType = KeeperType.EASY_KEEPER.name,
                    isSenior = false,
                    customTempOffsetF = -2,
                    notes = "Trace clip for light winter arena work."
                )
            )
            dao.insertHorses(starterHorses)

            // Seed historical logs for the past few nights for reference
            val cal = Calendar.getInstance()
            val sampleLogs = mutableListOf<BlanketLogEntity>()

            // Yesterday
            cal.add(Calendar.DAY_OF_YEAR, -1)
            sampleLogs.add(
                BlanketLogEntity(
                    horseId = 1,
                    horseName = "Dakota",
                    timestamp = cal.timeInMillis,
                    eveningLowTempF = 32.0,
                    weatherDescription = "Light Drizzle & Cold",
                    weatherCode = 51,
                    precipitationInches = 0.15,
                    windSpeedMph = 14.0,
                    recommendedBlanket = BlanketType.MEDIUM_TURNOUT.shortName,
                    actualBlanketUsed = BlanketType.MEDIUM_TURNOUT.displayName,
                    comfortFeedback = "Warm & Comfortable",
                    notes = "Cozy under medium rug this morning, no shivering."
                )
            )
            sampleLogs.add(
                BlanketLogEntity(
                    horseId = 2,
                    horseName = "Sterling",
                    timestamp = cal.timeInMillis,
                    eveningLowTempF = 32.0,
                    weatherDescription = "Light Drizzle & Cold",
                    weatherCode = 51,
                    precipitationInches = 0.15,
                    windSpeedMph = 14.0,
                    recommendedBlanket = BlanketType.RAIN_SHEET.shortName,
                    actualBlanketUsed = BlanketType.RAIN_SHEET.displayName,
                    comfortFeedback = "Perfect Dryness",
                    notes = "Run-in shed kept him out of wind; rain sheet kept back dry."
                )
            )

            // 2 days ago
            cal.add(Calendar.DAY_OF_YEAR, -1)
            sampleLogs.add(
                BlanketLogEntity(
                    horseId = 1,
                    horseName = "Dakota",
                    timestamp = cal.timeInMillis,
                    eveningLowTempF = 24.0,
                    weatherDescription = "Freezing Clear Skies",
                    weatherCode = 0,
                    precipitationInches = 0.0,
                    windSpeedMph = 9.0,
                    recommendedBlanket = BlanketType.HEAVY_TURNOUT.shortName,
                    actualBlanketUsed = BlanketType.HEAVY_TURNOUT.displayName,
                    comfortFeedback = "Very Warm & Content",
                    notes = "Deep freeze night; heavy rug kept ears and shoulders warm."
                )
            )

            // 3 days ago
            cal.add(Calendar.DAY_OF_YEAR, -1)
            sampleLogs.add(
                BlanketLogEntity(
                    horseId = 2,
                    horseName = "Sterling",
                    timestamp = cal.timeInMillis,
                    eveningLowTempF = 56.0,
                    weatherDescription = "Mild & Clear",
                    weatherCode = 0,
                    precipitationInches = 0.0,
                    windSpeedMph = 6.0,
                    recommendedBlanket = BlanketType.NO_BLANKET.shortName,
                    actualBlanketUsed = BlanketType.NO_BLANKET.displayName,
                    comfortFeedback = "Happy & Naked",
                    notes = "Warm night, coat fluffed nicely."
                )
            )

            dao.insertLogs(sampleLogs)
        }
    }

    suspend fun saveHorse(horse: Horse): Long = withContext(Dispatchers.IO) {
        if (horse.id == 0L) {
            dao.insertHorse(HorseEntity.fromDomain(horse))
        } else {
            dao.updateHorse(HorseEntity.fromDomain(horse))
            horse.id
        }
    }

    suspend fun deleteHorse(horse: Horse) = withContext(Dispatchers.IO) {
        dao.deleteHorse(HorseEntity.fromDomain(horse))
    }

    suspend fun addLog(log: BlanketLogEntity): Long = withContext(Dispatchers.IO) {
        dao.insertLog(log)
    }

    suspend fun deleteLog(log: BlanketLogEntity) = withContext(Dispatchers.IO) {
        dao.deleteLog(log)
    }

    suspend fun deleteLogById(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteLogById(id)
    }

    suspend fun saveCustomBlanket(blanket: CustomBlanketEntity): Long = withContext(Dispatchers.IO) {
        if (blanket.id == 0L) {
            dao.insertCustomBlanket(blanket)
        } else {
            dao.updateCustomBlanket(blanket)
            blanket.id
        }
    }

    suspend fun deleteCustomBlanket(blanket: CustomBlanketEntity) = withContext(Dispatchers.IO) {
        dao.deleteCustomBlanket(blanket)
    }

    suspend fun resetDefaultBlankets() = withContext(Dispatchers.IO) {
        dao.deleteAllCustomBlankets()
        dao.insertCustomBlankets(getDefaultBlankets())
    }

    fun getDefaultBlankets(): List<CustomBlanketEntity> {
        return listOf(
            CustomBlanketEntity(
                name = "No Blanket (Naked)",
                category = "NONE",
                fillGrams = 0,
                minTempF = 60,
                maxTempF = 110,
                isWaterproof = false,
                hasNeckCover = false,
                notes = "Mild temperatures; natural coat regulation"
            ),
            CustomBlanketEntity(
                name = "Waterproof Rain Sheet (0g)",
                category = "SHEET",
                fillGrams = 0,
                minTempF = 50,
                maxTempF = 59,
                isWaterproof = true,
                hasNeckCover = false,
                notes = "Breathable waterproof shell for chilling damp wind and rain"
            ),
            CustomBlanketEntity(
                name = "Light Turnout Rug (100g)",
                category = "LIGHT",
                fillGrams = 100,
                minTempF = 43,
                maxTempF = 49,
                isWaterproof = true,
                hasNeckCover = false,
                notes = "Light 100g polyfill for crisp autumn/spring nights"
            ),
            CustomBlanketEntity(
                name = "Medium Turnout Rug (200g)",
                category = "MEDIUM",
                fillGrams = 200,
                minTempF = 30,
                maxTempF = 42,
                isWaterproof = true,
                hasNeckCover = false,
                notes = "Standard midweight 200g turnout for near-freezing nights"
            ),
            CustomBlanketEntity(
                name = "Heavy Turnout Rug (300g)",
                category = "HEAVY",
                fillGrams = 300,
                minTempF = 18,
                maxTempF = 29,
                isWaterproof = true,
                hasNeckCover = false,
                notes = "Heavyweight 300g polyfill for hard freezes and frost"
            ),
            CustomBlanketEntity(
                name = "Heavy Turnout + Hood (400g)",
                category = "HEAVY",
                fillGrams = 400,
                minTempF = -25,
                maxTempF = 17,
                isWaterproof = true,
                hasNeckCover = true,
                notes = "Full coverage deep freeze rug with detachable neck cover"
            )
        )
    }

    fun getBarnLocation(): Triple<String, Double, Double> {
        val name = prefs.getString("barn_name", "Lexington Equine Barn, KY") ?: "Lexington Equine Barn, KY"
        val lat = prefs.getFloat("barn_lat", 38.0406f).toDouble()
        val lon = prefs.getFloat("barn_lon", -84.5037f).toDouble()
        return Triple(name, lat, lon)
    }

    fun saveBarnLocation(name: String, latitude: Double, longitude: Double) {
        prefs.edit()
            .putString("barn_name", name)
            .putFloat("barn_lat", latitude.toFloat())
            .putFloat("barn_lon", longitude.toFloat())
            .apply()
    }

    fun getAlertConfig(): BlanketAlertConfig {
        return BlanketAlertConfig(
            dailyAlertEnabled = prefs.getBoolean("daily_alert_enabled", true),
            alertHour = prefs.getInt("alert_hour", 17),
            alertMinute = prefs.getInt("alert_minute", 0),
            alertWhenBlanketNeeded = prefs.getBoolean("alert_blanket_needed", true),
            customLowTempAlertEnabled = prefs.getBoolean("custom_temp_alert_enabled", true),
            customLowTempThresholdF = prefs.getInt("custom_temp_threshold", 32),
            rainSnowAlertEnabled = prefs.getBoolean("rain_snow_alert_enabled", true),
            highWindAlertEnabled = prefs.getBoolean("high_wind_alert_enabled", true),
            highWindThresholdMph = prefs.getInt("high_wind_threshold", 20)
        )
    }

    fun saveAlertConfig(config: BlanketAlertConfig) {
        prefs.edit()
            .putBoolean("daily_alert_enabled", config.dailyAlertEnabled)
            .putInt("alert_hour", config.alertHour)
            .putInt("alert_minute", config.alertMinute)
            .putBoolean("alert_blanket_needed", config.alertWhenBlanketNeeded)
            .putBoolean("custom_temp_alert_enabled", config.customLowTempAlertEnabled)
            .putInt("custom_temp_threshold", config.customLowTempThresholdF)
            .putBoolean("rain_snow_alert_enabled", config.rainSnowAlertEnabled)
            .putBoolean("high_wind_alert_enabled", config.highWindAlertEnabled)
            .putInt("high_wind_threshold", config.highWindThresholdMph)
            .apply()
    }

    fun getThemeMode(): String {
        return prefs.getString("app_theme_mode", "SYSTEM") ?: "SYSTEM"
    }

    fun saveThemeMode(mode: String) {
        prefs.edit().putString("app_theme_mode", mode).apply()
    }

    suspend fun fetchWeather(latitude: Double, longitude: Double, locationName: String): WeatherForecast = withContext(Dispatchers.IO) {
        val dto = api.getForecast(latitude = latitude, longitude = longitude)

        val currentTemp = dto.current?.temperature2m ?: 45.0
        val currentCode = dto.current?.weatherCode ?: 0

        val dailyTimes = dto.daily?.time ?: emptyList()
        val dailyCodes = dto.daily?.weatherCode ?: emptyList()
        val dailyMinTemps = dto.daily?.temperature2mMin ?: emptyList()
        val dailyMaxTemps = dto.daily?.temperature2mMax ?: emptyList()
        val dailyPrecipProbs = dto.daily?.precipitationProbabilityMax ?: emptyList()
        val dailyPrecipSums = dto.daily?.precipitationSum ?: emptyList()
        val dailyWindMax = dto.daily?.windSpeed10mMax ?: emptyList()

        val eveningLow = dailyMinTemps.firstOrNull() ?: currentTemp
        val precipProb = dailyPrecipProbs.firstOrNull() ?: 0
        val precipSum = dailyPrecipSums.firstOrNull() ?: 0.0
        val windSpeed = dailyWindMax.firstOrNull() ?: (dto.current?.windSpeed10m ?: 5.0)

        // Hourly apparent temperatures for tonight (hours 18:00 through 08:00 tomorrow)
        val hourlyTemps = dto.hourly?.temperature2m ?: emptyList()
        val hourlyApparent = dto.hourly?.apparentTemperature ?: emptyList()
        val hourlyTimes = dto.hourly?.time ?: emptyList()
        val hourlyPrecip = dto.hourly?.precipitationProbability ?: emptyList()
        val hourlyCodes = dto.hourly?.weatherCode ?: emptyList()

        val overnightApparentMin = if (hourlyApparent.isNotEmpty()) {
            hourlyApparent.take(24).minOrNull() ?: (eveningLow - 3.0)
        } else {
            eveningLow - 3.0
        }

        val primaryCode = dailyCodes.firstOrNull() ?: currentCode
        val weatherDesc = WeatherCodeHelper.getDescription(primaryCode)

        // Build Daily Items
        val sdfIso = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val sdfDay = SimpleDateFormat("EEE, MMM d", Locale.US)

        val dailyItems = dailyTimes.indices.mapNotNull { i ->
            val dateStr = dailyTimes.getOrNull(i) ?: return@mapNotNull null
            val dayName = try {
                val date = sdfIso.parse(dateStr)
                if (date != null) sdfDay.format(date) else dateStr
            } catch (e: Exception) {
                dateStr
            }
            DailyWeatherItem(
                dateIso = dateStr,
                dayOfWeek = dayName,
                minTempF = dailyMinTemps.getOrNull(i) ?: 32.0,
                maxTempF = dailyMaxTemps.getOrNull(i) ?: 55.0,
                precipProbMax = dailyPrecipProbs.getOrNull(i) ?: 10,
                weatherCode = dailyCodes.getOrNull(i) ?: 0,
                weatherDescription = WeatherCodeHelper.getDescription(dailyCodes.getOrNull(i) ?: 0)
            )
        }

        // Build next 12 hours
        val hourlyItems = hourlyTimes.indices.take(12).map { i ->
            val rawTime = hourlyTimes.getOrNull(i) ?: ""
            val timeFormatted = if (rawTime.contains("T")) {
                rawTime.substringAfter("T")
            } else {
                rawTime
            }
            HourlyTempItem(
                timeLabel = timeFormatted,
                tempF = hourlyTemps.getOrNull(i) ?: currentTemp,
                apparentTempF = hourlyApparent.getOrNull(i) ?: currentTemp,
                precipProb = hourlyPrecip.getOrNull(i) ?: 0,
                weatherCode = hourlyCodes.getOrNull(i) ?: 0
            )
        }

        WeatherForecast(
            locationName = locationName,
            latitude = latitude,
            longitude = longitude,
            currentTempF = currentTemp,
            eveningLowTempF = eveningLow,
            overnightApparentLowF = overnightApparentMin,
            maxWindSpeedMph = windSpeed,
            precipitationProbMax = precipProb,
            precipitationInches = precipSum,
            weatherCode = primaryCode,
            weatherDescription = weatherDesc,
            isRaining = WeatherCodeHelper.isRain(primaryCode),
            isSnowing = WeatherCodeHelper.isSnow(primaryCode),
            isFreezingPrecip = WeatherCodeHelper.isFreezing(primaryCode),
            dailyForecasts = dailyItems,
            hourlyTemperatures = hourlyItems,
            lastUpdatedMillis = System.currentTimeMillis()
        )
    }

    suspend fun searchLocations(query: String) = withContext(Dispatchers.IO) {
        try {
            val response = api.searchLocations(
                url = WeatherApiService.GEOCODING_URL,
                query = query
            )
            response.results ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
