package com.mara.weatherforecast.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mara.weatherforecast.model.ForecastResponse
import com.mara.weatherforecast.model.WeatherService
import com.mara.weatherforecast.utils.Result
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

