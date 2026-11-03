package com.example.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

interface WeatherApiService {

    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,weather_code,wind_speed_10m",
        @Query("hourly") hourly: String = "temperature_2m,apparent_temperature,precipitation_probability,weather_code,wind_speed_10m",
        @Query("daily") daily: String = "weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max,precipitation_sum,wind_speed_10m_max",
        @Query("temperature_unit") tempUnit: String = "fahrenheit",
        @Query("wind_speed_unit") windUnit: String = "mph",
        @Query("precipitation_unit") precipUnit: String = "inch",
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") days: Int = 6
    ): ForecastResponseDto

    @GET
    suspend fun searchLocations(
        @Url url: String,
        @Query("name") query: String,
        @Query("count") count: Int = 8,
        @Query("language") language: String = "en",
        @Query("format") format: String = "json"
    ): GeocodingResponseDto

    companion object {
        private const val BASE_URL = "https://api.open-meteo.com/"
        const val GEOCODING_URL = "https://geocoding-api.open-meteo.com/v1/search"

        fun create(): WeatherApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(WeatherApiService::class.java)
        }
    }
}
