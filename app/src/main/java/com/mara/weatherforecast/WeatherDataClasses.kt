package com.mara.weatherforecast

data class ForecastResponse(
    val list: List<ForecastData>
)

data class ForecastData(
    val dt: Long, // Date and time in Unix timestamp
    val main: Main,
    val weather: List<Weather>
)

data class Main(
    val temp: Double // Temperature
)

data class Weather(
    val main: String, // Weather condition main category
    val icon: String  // Weather condition icon code
)


