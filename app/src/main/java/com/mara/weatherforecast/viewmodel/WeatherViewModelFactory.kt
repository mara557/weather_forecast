package com.mara.weatherforecast.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mara.weatherforecast.model.WeatherService

// Factory class for creating instances of WeatherViewModel.
class WeatherViewModelFactory(private val weatherService: WeatherService) :
    ViewModelProvider.Factory {

    // Function to create an instance of WeatherViewModel
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the requested ViewModel is WeatherViewModel
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            // Create and return an instance of WeatherViewModel with the provided WeatherService
            @Suppress("UNCHECKED_CAST") return WeatherViewModel(weatherService) as T
        }
        // If an unknown ViewModel is requested, throw an exception
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
