package com.mara.weatherforecast

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherViewModel(private val weatherService: WeatherService) : ViewModel() {

    val weatherData = MutableLiveData<Result<Pair<String, String>>>()
    val forecastData = MutableLiveData<Result<ForecastResponse>>()

    fun fetchWeatherData(city: String) {
        weatherData.value = Result.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val data = weatherService.fetchWeatherData(city)
                weatherData.postValue(Result.Success(data))
            } catch (e: Exception) {
                weatherData.postValue(Result.Failure(e))
            }
        }
    }

    fun fetchWeatherData(latitude: Double, longitude: Double) {
        weatherData.value = Result.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val data = weatherService.fetchWeatherData(latitude, longitude)
                weatherData.postValue(Result.Success(data))
            } catch (e: Exception) {
                weatherData.postValue(Result.Failure(e))
            }
        }
    }

    fun fetchFiveDayForecast(city: String) {
        forecastData.value = Result.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val data = weatherService.fetchFiveDayForecast(city)
                forecastData.postValue(Result.Success(data))
            } catch (e: Exception) {
                forecastData.postValue(Result.Failure(e))
            }
        }
    }

    fun fetchFiveDayForecast(latitude: Double, longitude: Double) {
        forecastData.value = Result.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val data = weatherService.fetchFiveDayForecast(latitude, longitude)
                forecastData.postValue(Result.Success(data))
            } catch (e: Exception) {
                forecastData.postValue(Result.Failure(e))
            }
        }
    }
}

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Failure(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

class WeatherViewModelFactory(private val weatherService: WeatherService) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(weatherService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
