package com.mara.weatherforecast

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var locationSearch: EditText
    private lateinit var textCurrent: TextView
    private lateinit var currentCondition: TextView
    private lateinit var textTemp: TextView
    private lateinit var locationButton: Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var dayOneText: TextView
    private lateinit var tempOneText: TextView
    private lateinit var dayTwoText: TextView
    private lateinit var tempTwoText: TextView
    private lateinit var dayThreeText: TextView
    private lateinit var tempThreeText: TextView
    private lateinit var dayFourText: TextView
    private lateinit var tempFourText: TextView
    private lateinit var dayFiveText: TextView
    private lateinit var tempFiveText: TextView

    private lateinit var weatherIconOneImage: ImageView
    private lateinit var weatherIconTwoImage: ImageView
    private lateinit var weatherIconThreeImage: ImageView
    private lateinit var weatherIconFourImage: ImageView
    private lateinit var weatherIconFiveImage: ImageView

    private val weatherIconsMap = mapOf(
        "01d" to R.drawable.ic_01d,
        "01n" to R.drawable.ic_01d,
        "02d" to R.drawable.ic_02d,
        "02n" to R.drawable.ic_02n,
        "03d" to R.drawable.ic_03d,
        "03n" to R.drawable.ic_03n,
        "04d" to R.drawable.ic_04d,
        "04n" to R.drawable.ic_04n,
        "09d" to R.drawable.ic_09d,
        "09n" to R.drawable.ic_09n,
        "10d" to R.drawable.ic_10d,
        "10n" to R.drawable.ic_10n,
        "11d" to R.drawable.ic_11d,
        "11n" to R.drawable.ic_11n,
        "13d" to R.drawable.ic_13d,
        "13n" to R.drawable.ic_13n,
        "50d" to R.drawable.ic_50n,
        "50n" to R.drawable.ic_50n,
    )

    private lateinit var viewModel: WeatherViewModel

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupViewModel()
        setupListeners()
        checkLocationPermissionAndFetchLocation()
    }

    private fun initializeViews() {
        locationSearch = findViewById(R.id.locationSearch)
        textCurrent = findViewById(R.id.textCurrent)
        currentCondition = findViewById(R.id.currentCondition)
        textTemp = findViewById(R.id.textTemp)
        locationButton = findViewById(R.id.button)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize TextViews for the day names and temperatures
        dayOneText = findViewById(R.id.dayOne)
        tempOneText = findViewById(R.id.tempOne)
        dayTwoText = findViewById(R.id.dayTwo)
        tempTwoText = findViewById(R.id.tempTwo)
        dayThreeText = findViewById(R.id.dayThree)
        tempThreeText = findViewById(R.id.tempThree)
        dayFourText = findViewById(R.id.dayFour)
        tempFourText = findViewById(R.id.tempFour)
        dayFiveText = findViewById(R.id.dayFive)
        tempFiveText = findViewById(R.id.tempFive)

        // Initialize ImageViews for the weather icons
        weatherIconOneImage = findViewById(R.id.weatherIconOne)
        weatherIconTwoImage = findViewById(R.id.weatherIconTwo)
        weatherIconThreeImage = findViewById(R.id.weatherIconThree)
        weatherIconFourImage = findViewById(R.id.weatherIconFour)
        weatherIconFiveImage = findViewById(R.id.weatherIconFive)
    }



    private fun setupViewModel() {
        val service = WeatherService("43b4184a92fc42b7fa9ea7e01101a481") // Replace with actual API key
        val factory = WeatherViewModelFactory(service)
        viewModel = ViewModelProvider(this, factory)[WeatherViewModel::class.java]
    }


    private fun setupListeners() {
        locationSearch.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                fetchWeatherData(locationSearch.text.toString())
                true
            } else false
        }


        locationButton.setOnClickListener { checkLocationPermissionAndFetchLocation() }
    }

    private fun fetchWeatherData(city: String) {
        viewModel.fetchWeatherData(city).observe(this) { result ->
            when (result) {
                is Result.Success -> handleWeatherResponse(result.data)
                is Result.Failure -> handleWeatherFailure(result.exception)
                is Result.Loading -> {
                    // Handle loading state
                }
            }
        }

        viewModel.fetchFiveDayForecast(city).observe(this) { result ->
            when (result) {
                is Result.Success -> updateForecastViews(result.data)
                is Result.Failure -> handleWeatherFailure(result.exception)
                is Result.Loading -> {
                    // Handle loading state
                }
            }
        }
    }

    private fun fetchWeatherData(latitude: Double, longitude: Double) {
        viewModel.fetchWeatherData(latitude, longitude).observe(this) { result ->
            when (result) {
                is Result.Success -> handleWeatherResponse(result.data)
                is Result.Failure -> handleWeatherFailure(result.exception)
                is Result.Loading -> {
                    // Handle loading state, e.g., show a loading spinner
                }
            }
        }

        viewModel.fetchFiveDayForecast(latitude, longitude).observe(this) { result ->
            when (result) {
                is Result.Success -> updateForecastViews(result.data)
                is Result.Failure -> handleWeatherFailure(result.exception)
                is Result.Loading -> {
                    // Handle loading state, e.g., show a loading spinner
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun handleWeatherResponse(weatherData: Pair<String, String>) {
        // Assuming weatherData.first is the weather description and weatherData.second is the temperature
        currentCondition.text = weatherData.first
        textTemp.text = "${weatherData.second}°C"
    }

    private fun updateForecastView(dayText: TextView, iconImage: ImageView, tempText: TextView, date: String, iconCode: String, temperature: String) {
        dayText.text = date
        iconImage.setImageResource(weatherIconsMap[iconCode] ?: R.drawable.ic_default)
        tempText.text = temperature
    }
    private fun updateForecastViews(forecastResponse: ForecastResponse) {
        // Update your UI with the forecast data
        forecastResponse.list.chunked(8).take(5).forEachIndexed { index, forecasts ->
            val forecast = forecasts.maxByOrNull { it.dt } ?: return@forEachIndexed
            val date = Date(forecast.dt * 1000)
            val dateFormat = SimpleDateFormat("EEE", Locale.getDefault())
            val formattedDate = dateFormat.format(date)
            val iconCode = forecast.weather.first().icon
            val temperature = "${forecast.main.temp}°C"

            when (index) {
                0 -> updateForecastView(dayOneText, weatherIconOneImage, tempOneText, formattedDate, iconCode, temperature)
                1 -> updateForecastView(dayTwoText, weatherIconTwoImage, tempTwoText, formattedDate, iconCode, temperature)
                2 -> updateForecastView(dayThreeText, weatherIconThreeImage, tempThreeText, formattedDate, iconCode, temperature)
                3 -> updateForecastView(dayFourText, weatherIconFourImage, tempFourText, formattedDate, iconCode, temperature)
                4 -> updateForecastView(dayFiveText, weatherIconFiveImage, tempFiveText, formattedDate, iconCode, temperature)
            }
        }
    }

    private fun checkLocationPermissionAndFetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            getLastKnownLocation()
        }
    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    fetchWeatherData(location.latitude, location.longitude)
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun handleWeatherFailure(e: Exception) {
        showToast("Failed to fetch weather data: ${e.message}")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLastKnownLocation()
        } else {
            showToast("Location permission denied")
        }
    }}
