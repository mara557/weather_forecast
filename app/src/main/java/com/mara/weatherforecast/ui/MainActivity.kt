package com.mara.weatherforecast.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mara.weatherforecast.R
import com.mara.weatherforecast.model.ForecastResponse
import com.mara.weatherforecast.model.WeatherService
import com.mara.weatherforecast.utils.Result
import com.mara.weatherforecast.viewmodel.WeatherViewModel
import com.mara.weatherforecast.viewmodel.WeatherViewModelFactory
import okio.IOException
import java.text.SimpleDateFormat
import java.util.*


// The main screen of the weather app where weather information is displayed.

class MainActivity : AppCompatActivity() {

    // UI elements declaration
    private lateinit var locationSearch: EditText
    private lateinit var textCurrent: TextView
    private lateinit var currentCondition: TextView
    private lateinit var textTemp: TextView
    private lateinit var locationButton: Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Declaration of UI elements for the five-day forecast
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

    // Declaration of UI elements for weather icons
    private lateinit var weatherIconOneImage: ImageView
    private lateinit var weatherIconTwoImage: ImageView
    private lateinit var weatherIconThreeImage: ImageView
    private lateinit var weatherIconFourImage: ImageView
    private lateinit var weatherIconFiveImage: ImageView

    // Mapping of weather icon codes to their corresponding resource IDs
    private val weatherIconsMap = mapOf(
        "01d" to R.drawable.ic_01d,
        "01n" to R.drawable.ic_01n,
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
        "50d" to R.drawable.ic_50d,
        "50n" to R.drawable.ic_50n
    )

    // ViewModel responsible for managing UI-related data
    private lateinit var viewModel: WeatherViewModel

    // Constant value for location permission request code
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    // The entry point when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialization of UI elements
        initializeViews()
        // Setting up ViewModel to manage data
        setupViewModel()
        // Setting up listeners for UI elements
        setupListeners()
    }

    // Function to initialize UI elements
    private fun initializeViews() {
        locationSearch = findViewById(R.id.locationSearch)
        textCurrent = findViewById(R.id.textCurrent)
        currentCondition = findViewById(R.id.currentCondition)
        textTemp = findViewById(R.id.textTemp)
        locationButton = findViewById(R.id.button)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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

        weatherIconOneImage = findViewById(R.id.weatherIconOne)
        weatherIconTwoImage = findViewById(R.id.weatherIconTwo)
        weatherIconThreeImage = findViewById(R.id.weatherIconThree)
        weatherIconFourImage = findViewById(R.id.weatherIconFour)
        weatherIconFiveImage = findViewById(R.id.weatherIconFive)
    }

    // Function to set up the ViewModel for data management
    private fun setupViewModel() {
        // Creation of WeatherService with the API key
        val service =
            WeatherService("43b4184a92fc42b7fa9ea7e01101a481") // Replace with your actual API key
        // Creation of ViewModelFactory with the WeatherService
        val factory = WeatherViewModelFactory(service)
        // Creating the ViewModel instance
        viewModel = ViewModelProvider(this, factory)[WeatherViewModel::class.java]

        // Observing changes in ViewModel data
        observeViewModel()
    }

    // Function to observe changes in ViewModel data
    private fun observeViewModel() {
        viewModel.weatherData.observe(this) { result ->
            when (result) {
                is Result.Success -> updateWeatherUI(result.data)
                is Result.Failure -> showToast("Failed to fetch weather data: ${result.exception.message}")
                is Result.Loading -> {
                    // Handle loading state, e.g., show a loading spinner
                }
            }
        }


        viewModel.forecastData.observe(this) { result ->
            when (result) {
                is Result.Success -> updateForecastViews(result.data)
                is Result.Failure -> showToast("Failed to fetch forecast data: ${result.exception.message}")
                is Result.Loading -> {
                    // Handle loading state
                }
            }
        }
    }

    // Function to set up listeners for UI elements
    private fun setupListeners() {
        locationSearch.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                val city = locationSearch.text.toString()
                updateTownName(city)
                viewModel.fetchWeatherData(city)
                viewModel.fetchFiveDayForecast(city)
                true
            } else false
        }

        locationButton.setOnClickListener {
            Log.d("location", "button pressed")
            checkLocationPermissionAndFetchLocation()
        }
    }

    // Function to update the town name based on user input or current location
    private fun updateTownName(city: String) {
        if (!isFinishing) {
            Log.d("TownName", "Updating town name to: $city")
            val townTextView: TextView = findViewById(R.id.Town)
            townTextView.apply {
                text = city.capitalize()
                setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.white))
            }
        }
    }





    // Function to update the UI with current weather data
    @SuppressLint("SetTextI18n")
    private fun updateWeatherUI(weatherData: Pair<String, String>) {
        currentCondition.text = weatherData.first
        textTemp.text = "${weatherData.second}°C"
    }


    // Function to update the five-day forecast views with data from the forecast response
    private fun updateForecastViews(forecastResponse: ForecastResponse) {
        val dateFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val forecastList = forecastResponse.list.chunked(8).take(5)

        forecastList.forEachIndexed { index, forecasts ->
            val forecast = forecasts.maxByOrNull { it.dt } ?: return@forEachIndexed

            // Calculate the offset from the current day (index 2)
            val dayOffset = index - 2

            // Calculate the date based on the offset
            val date = Calendar.getInstance()
            date.add(Calendar.DAY_OF_MONTH, dayOffset)

            val formattedDate = dateFormat.format(date.time)
            val iconCode = forecast.weather.first().icon
            val temperature = "${forecast.main.temp}°C"

            when (index) {
                0 -> updateForecastView(
                    dayOneText,
                    weatherIconOneImage,
                    tempOneText,
                    formattedDate,
                    iconCode,
                    temperature
                )
                1 -> updateForecastView(
                    dayTwoText,
                    weatherIconTwoImage,
                    tempTwoText,
                    formattedDate,
                    iconCode,
                    temperature
                )
                2 -> updateForecastView(
                    dayThreeText,
                    weatherIconThreeImage,
                    tempThreeText,
                    formattedDate,
                    iconCode,
                    temperature
                )
                3 -> updateForecastView(
                    dayFourText,
                    weatherIconFourImage,
                    tempFourText,
                    formattedDate,
                    iconCode,
                    temperature
                )
                4 -> updateForecastView(
                    dayFiveText,
                    weatherIconFiveImage,
                    tempFiveText,
                    formattedDate,
                    iconCode,
                    temperature
                )
            }
        }
    }



    // Function to update a single forecast view
    private fun updateForecastView(
        dayText: TextView,
        iconImage: ImageView,
        tempText: TextView,
        date: String,
        iconCode: String,
        temperature: String
    ) {
        dayText.text = date
        iconImage.setImageResource(weatherIconsMap[iconCode] ?: R.drawable.ic_default)
        tempText.text = temperature
    }

    // Function to check location permission and fetch location if granted
    private fun checkLocationPermissionAndFetchLocation() {
        Log.d("location", "starting")
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("location", "no permissions")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            Log.d("location", "permissions accepted")
            getLastKnownLocation()
        }
    }

    // Function to fetch the last known location if location permission is granted
    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("location", "permission granted again")
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    // Fetch weather data and forecast
                    Log.d("location", "fetching")
                    Log.d("location", location.toString())
                    viewModel.fetchWeatherData(location.latitude, location.longitude)
                    viewModel.fetchFiveDayForecast(location.latitude, location.longitude)

                    setCityName(location.latitude, location.longitude)

                }
            }
        }
    }

    // Function to get the city name from latitude and longitude using Geocoder

    private fun setCityName(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())

        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]

                // Use locality if not null, otherwise extract from address string
                val cityName = address.locality ?: extractCityFromAddressString(address.getAddressLine(0))

                Log.d("location", address.toString())
                runOnUiThread {
                    updateTownName(cityName)
                }


            } else {
                Log.d("location", "No address found")
            }
        } catch (e: IOException) {
            Log.e("location", "Geocoder error: ${e.localizedMessage}")
        }
    }

    private fun extractCityFromAddressString(addressString: String): String {
        // Split the addressString by commas and trim each part
        val addressParts = addressString.split(",").map { it.trim() }

        // Assuming the city name is the third part
        return if (addressParts.size > 2) addressParts[2] else ""
    }



    // Function to show a toast message on the screen
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }


    // Handling the result of the location permission request
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLastKnownLocation()
        } else {
            showToast("Location permission denied")
        }
    }
}




