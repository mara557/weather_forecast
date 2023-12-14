package com.mara.weatherforecast.model

// Represents the overall response from the weather forecast API.
data class ForecastResponse(
    val list: List<ForecastData> // List of forecast data for multiple time periods
)

// Represents individual forecast data.
data class ForecastData(
    val dt: Long, // Date and time in Unix timestamp
    val main: Main, // Main weather data
    val weather: List<Weather> // Weather conditions
)

// Represents main weather data like temperature.
data class Main(
    val temp: Double // Temperature
)

// Represents weather condition details.
data class Weather(
    val main: String, // Main weather condition (Rain, Snow, Clear, etc.)
    val description: String, // Weather condition within the group
    val icon: String  // Icon id for the corresponding weather condition
)
