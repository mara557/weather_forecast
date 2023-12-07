package com.mara.weatherforecast

import WeatherService
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.widget.Toast
import android.view.inputmethod.EditorInfo
import android.view.KeyEvent
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import android.widget.ImageView





class MainActivity : AppCompatActivity() {

    private lateinit var locationSearch: EditText
    private lateinit var textCurrent: TextView
    private lateinit var currentCondition: TextView
    private lateinit var textTemp: TextView
    private lateinit var locationButton: Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var weatherService: WeatherService

    // TextViews for the five-day forecast
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

    // ImageViews for the weather icons
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

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        weatherService = WeatherService("43b4184a92fc42b7fa9ea7e01101a481") // Replace with your actual API key
        setupListeners()

        initializeViews()
        setupListeners()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            getLastKnownLocation()
        }
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

    private fun setupListeners() {
        locationSearch.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                val city = locationSearch.text.toString()
                if (city.isNotEmpty()) {
                    fetchWeatherData(city)
                    fetchFiveDayForecast(city)
                }
                true
            } else {
                false
            }
        }

        locationButton.setOnClickListener {
            getLastKnownLocation()
        }
    }

    private fun fetchWeatherData(city: String) {
        weatherService.fetchWeatherData(city, this::handleWeatherResponse, this::handleWeatherFailure)
    }

    private fun fetchFiveDayForecast(city: String) {
        weatherService.fetchFiveDayForecast(city, this::handleForecastResponse, this::handleWeatherFailure)
    }

    @SuppressLint("SetTextI18n")
    private fun handleWeatherResponse(weatherDescription: String, temp: String) {
        runOnUiThread {
            currentCondition.text = weatherDescription
            textTemp.text = "$temp°C"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleForecastResponse(forecastResponse: ForecastResponse) {
        runOnUiThread {
            updateForecastViews(forecastResponse)
        }
    }

    private fun updateForecastViews(forecastResponse: ForecastResponse) {
        val dateFormat = SimpleDateFormat("EEE", Locale.getDefault())

        forecastResponse.list.chunked(8).take(5).forEachIndexed { index, dayForecast ->
            val forecast = dayForecast.first()
            val date = Date(forecast.dt * 1000)
            val iconCode = forecast.weather.first().icon
            val temperature = forecast.main.temp

            when (index) {
                0 -> updateForecastView(dayOneText, weatherIconOneImage, tempOneText, dateFormat.format(date), iconCode, temperature)
                1 -> updateForecastView(dayTwoText, weatherIconTwoImage, tempTwoText, dateFormat.format(date), iconCode, temperature)
                2 -> updateForecastView(dayThreeText, weatherIconThreeImage, tempThreeText, dateFormat.format(date), iconCode, temperature)
                3 -> updateForecastView(dayFourText, weatherIconFourImage, tempFourText, dateFormat.format(date), iconCode, temperature)
                4 -> updateForecastView(dayFiveText, weatherIconFiveImage, tempFiveText, dateFormat.format(date), iconCode, temperature)
            }
        }
    }


    private fun updateForecastView(dayText: TextView, iconImage: ImageView, tempText: TextView, date: String, iconCode: String, temperature: Double) {
        dayText.text = date
        iconImage.setImageResource(weatherIconsMap[iconCode] ?: R.drawable.ic_default)
        tempText.text = "${temperature}°C"
    }



    private fun handleWeatherFailure(e: IOException) {
        runOnUiThread {
            showToast("Failed to fetch weather data: ${e.message}")
        }
    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                fetchWeatherData(location.latitude, location.longitude)
                fetchFiveDayForecast(location.latitude, location.longitude)
            }
        }
    }

    private fun fetchWeatherData(latitude: Double, longitude: Double) {
        weatherService.fetchWeatherData(latitude, longitude, this::handleWeatherResponse, this::handleWeatherFailure)
    }

    private fun fetchFiveDayForecast(latitude: Double, longitude: Double) {
        weatherService.fetchFiveDayForecast(latitude, longitude, this::handleForecastResponse, this::handleWeatherFailure)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLastKnownLocation()
        } else {
            showToast("Location permission denied")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}