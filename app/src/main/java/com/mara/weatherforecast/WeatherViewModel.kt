package com.mara.weatherforecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers

class WeatherViewModel(private val weatherService: WeatherService) : ViewModel() {

    fun fetchWeatherData(city: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val weatherData = weatherService.fetchWeatherData(city)
            emit(Result.Success(weatherData))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun fetchWeatherData(latitude: Double, longitude: Double) = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val weatherData = weatherService.fetchWeatherData(latitude, longitude)
            emit(Result.Success(weatherData))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun fetchFiveDayForecast(city: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val forecastData = weatherService.fetchFiveDayForecast(city)
            emit(Result.Success(forecastData))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun fetchFiveDayForecast(latitude: Double, longitude: Double) = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val forecastData = weatherService.fetchFiveDayForecast(latitude, longitude)
            emit(Result.Success(forecastData))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }
}

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Failure(val exception: Exception) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}


class WeatherViewModelFactory(private val weatherService: WeatherService): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(weatherService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
