package com.mara.weatherforecast.model

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

// Service class for fetching weather data from the OpenWeatherMap API.
class WeatherService(private val apiKey: String) {
    private val client = OkHttpClient()
    private val gson = Gson()

    // Generic function to make a network request and parse the response.
    private suspend fun <T> makeRequest(url: String, parseResponse: (String) -> T): T {
        val request = Request.Builder().url(url).build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val responseBody = response.body?.string()
            responseBody?.let(parseResponse) ?: throw IOException("Response body is null")
        }
    }

    // Function to fetch current weather data by city name.
    suspend fun fetchWeatherData(city: String): Pair<String, String> {
        val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey&units=metric"
        return makeRequest(url) { jsonString ->
            val jsonResponse = JSONObject(jsonString)
            val weatherDescription = jsonResponse.getJSONArray("weather").getJSONObject(0).getString("description")
            val temp = jsonResponse.getJSONObject("main").getString("temp")
            Pair(weatherDescription, temp)
        }
    }

    // Function to fetch current weather data by latitude and longitude.
    suspend fun fetchWeatherData(latitude: Double, longitude: Double): Pair<String, String> {
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&appid=$apiKey&units=metric"
        return makeRequest(url) { jsonString ->
            val jsonResponse = JSONObject(jsonString)
            val weatherDescription = jsonResponse.getJSONArray("weather").getJSONObject(0).getString("description")
            val temp = jsonResponse.getJSONObject("main").getString("temp")
            Pair(weatherDescription, temp)
        }
    }

    // Function to fetch five-day weather forecast by city name.
    suspend fun fetchFiveDayForecast(city: String): ForecastResponse {
        val url = "https://api.openweathermap.org/data/2.5/forecast?q=$city&appid=$apiKey&units=metric"
        return makeRequest(url) { jsonString ->
            gson.fromJson(jsonString, ForecastResponse::class.java)
        }
    }

    // Function to fetch five-day weather forecast by latitude and longitude.
    suspend fun fetchFiveDayForecast(latitude: Double, longitude: Double): ForecastResponse {
        val url = "https://api.openweathermap.org/data/2.5/forecast?lat=$latitude&lon=$longitude&appid=$apiKey&units=metric"
        return makeRequest(url) { jsonString ->
            gson.fromJson(jsonString, ForecastResponse::class.java)
        }
    }
}
