package com.mara.weatherforecast.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mara.weatherforecast.model.ForecastResponse
import com.mara.weatherforecast.model.WeatherService
import com.mara.weatherforecast.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// ViewModel that manages UI-related data for the weather app.
class WeatherViewModel(private val weatherService: WeatherService) : ViewModel() {

    // LiveData to hold the current weather data
    val weatherData = MutableLiveData<Result<Pair<String, String>>>()

    // LiveData to hold the five-day forecast data
    val forecastData = MutableLiveData<Result<ForecastResponse>>()

    // Function to fetch current weather data by city name
    fun fetchWeatherData(city: String) {
        // Indicate that the data is still loading
        weatherData.value = Result.Loading
        // Perform the data fetching operation in a background thread
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Fetch weather data using the WeatherService
                val data = weatherService.fetchWeatherData(city)
                // Update the LiveData with the successful result
                weatherData.postValue(Result.Success(data))
            } catch (e: Exception) {
                // Update the LiveData with the failure result
                weatherData.postValue(Result.Failure(e))
            }
        }
    }

    // Function to fetch current weather data by latitude and longitude
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

    // Function to fetch five-day forecast data by city name
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

    // Function to fetch five-day forecast data by latitude and longitude
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

