package com.mara.weatherforecast

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class WeatherService(private val apiKey: String) {
    private val client = OkHttpClient()
    private val gson = Gson()

    suspend fun fetchWeatherData(city: String): Pair<String, String> {
        val url =
            "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey&units=metric"
        val request = Request.Builder().url(url).build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            response.body?.string()?.let {
                val jsonResponse = JSONObject(it)
                val weatherDescription =
                    jsonResponse.getJSONArray("weather").getJSONObject(0).getString("description")
                val temp = jsonResponse.getJSONObject("main").getString("temp")
                Pair(weatherDescription, temp)
            } ?: throw IOException("Response body is null")
        }
    }

    suspend fun fetchWeatherData(latitude: Double, longitude: Double): Pair<String, String> {
        val url =
            "https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&appid=$apiKey&units=metric"
        val request = Request.Builder().url(url).build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            response.body?.string()?.let {
                val jsonResponse = JSONObject(it)
                val weatherDescription =
                    jsonResponse.getJSONArray("weather").getJSONObject(0).getString("description")
                val temp = jsonResponse.getJSONObject("main").getString("temp")
                Pair(weatherDescription, temp)
            } ?: throw IOException("Response body is null")
        }
    }

    suspend fun fetchFiveDayForecast(city: String): ForecastResponse {
        val url =
            "https://api.openweathermap.org/data/2.5/forecast?q=$city&appid=$apiKey&units=metric"
        val request = Request.Builder().url(url).build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val responseBody = response.body?.string()
            responseBody?.let {
                gson.fromJson(it, ForecastResponse::class.java)
            } ?: throw IOException("Response body is null")
        }
    }

    suspend fun fetchFiveDayForecast(latitude: Double, longitude: Double): ForecastResponse {
        val url =
            "https://api.openweathermap.org/data/2.5/forecast?lat=$latitude&lon=$longitude&appid=$apiKey&units=metric"
        val request = Request.Builder().url(url).build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val responseBody = response.body?.string()
            responseBody?.let {
                gson.fromJson(it, ForecastResponse::class.java)
            } ?: throw IOException("Response body is null")
        }
    }
}
