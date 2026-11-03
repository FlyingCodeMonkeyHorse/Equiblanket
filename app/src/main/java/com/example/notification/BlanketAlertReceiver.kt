package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.local.AppDatabase
import com.example.data.remote.WeatherApiService
import com.example.domain.BlanketAdvisorEngine
import com.example.domain.WeatherCodeHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Calendar

class BlanketAlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)
                val horses = db.blanketDao().getAllHorses().firstOrNull() ?: emptyList()
                val prefs = context.getSharedPreferences("horse_blanket_prefs", Context.MODE_PRIVATE)

                val lat = prefs.getFloat("barn_lat", 38.0406f).toDouble() // Default Lexington, KY
                val lon = prefs.getFloat("barn_lon", -84.5037f).toDouble()
                val barnName = prefs.getString("barn_name", "Home Barn") ?: "Home Barn"

                val weatherApi = WeatherApiService.create()
                val response = weatherApi.getForecast(latitude = lat, longitude = lon)

                val daily = response.daily
                val minTemp = daily?.temperature2mMin?.firstOrNull() ?: 35.0
                val precipProb = daily?.precipitationProbabilityMax?.firstOrNull() ?: 20
                val weatherCode = daily?.weatherCode?.firstOrNull() ?: 0
                val windSpeed = daily?.windSpeed10mMax?.firstOrNull() ?: 8.0

                val hourly = response.hourly
                val apparentMin = hourly?.apparentTemperature?.take(24)?.minOrNull() ?: (minTemp - 4.0)

                val weatherForecast = com.example.data.model.WeatherForecast(
                    locationName = barnName,
                    latitude = lat,
                    longitude = lon,
                    currentTempF = response.current?.temperature2m ?: minTemp,
                    eveningLowTempF = minTemp,
                    overnightApparentLowF = apparentMin,
                    maxWindSpeedMph = windSpeed,
                    precipitationProbMax = precipProb,
                    precipitationInches = daily?.precipitationSum?.firstOrNull() ?: 0.0,
                    weatherCode = weatherCode,
                    weatherDescription = WeatherCodeHelper.getDescription(weatherCode),
                    isRaining = WeatherCodeHelper.isRain(weatherCode),
                    isSnowing = WeatherCodeHelper.isSnow(weatherCode),
                    isFreezingPrecip = WeatherCodeHelper.isFreezing(weatherCode)
                )

                // Check for each horse
                val activeHorses = if (horses.isNotEmpty()) horses.map { it.toDomain() } else {
                    listOf(
                        com.example.data.model.Horse(
                            id = 1,
                            name = "My Horse",
                            coatType = com.example.data.model.CoatType.UNCLIPPED,
                            shelterType = com.example.data.model.ShelterType.STALLED
                        )
                    )
                }

                // If any horse needs blanketing, send an alert
                val needingBlanket = activeHorses.map { horse ->
                    BlanketAdvisorEngine.evaluate(horse, weatherForecast)
                }.filter { it.needsBlanket }

                if (needingBlanket.isNotEmpty()) {
                    val primary = needingBlanket.first()
                    BlanketNotificationHelper.sendBlanketAlert(context, primary)
                }

                // Reschedule for next day
                val alertHour = prefs.getInt("alert_hour", 17)
                val alertMinute = prefs.getInt("alert_minute", 0)
                AlarmScheduler.scheduleDailyAlert(context, alertHour, alertMinute)
            } catch (e: Exception) {
                // Silently handle offline/error in broadcast
            } finally {
                pendingResult.finish()
            }
        }
    }
}
