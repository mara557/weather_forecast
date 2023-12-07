import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import com.google.gson.Gson
import com.mara.weatherforecast.ForecastResponse
import org.json.JSONObject

class WeatherService(private val apiKey: String) {
    private val client = OkHttpClient()
    private val gson = Gson()

    fun fetchWeatherData(city: String, onResponse: (String, String) -> Unit, onFailure: (IOException) -> Unit) {
        val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey&units=metric"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    onFailure(IOException("Unexpected code $response"))
                    return
                }

                response.body?.string()?.let {
                    val jsonResponse = JSONObject(it)
                    val weatherDescription = jsonResponse.getJSONArray("weather").getJSONObject(0).getString("description")
                    val temp = jsonResponse.getJSONObject("main").getString("temp")
                    onResponse(weatherDescription, temp)
                }
            }
        })
    }

    fun fetchWeatherData(latitude: Double, longitude: Double, onResponse: (String, String) -> Unit, onFailure: (IOException) -> Unit) {
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&appid=$apiKey&units=metric"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    onFailure(IOException("Unexpected code $response"))
                    return
                }

                response.body?.string()?.let {
                    val jsonResponse = JSONObject(it)
                    val weatherDescription = jsonResponse.getJSONArray("weather").getJSONObject(0).getString("description")
                    val temp = jsonResponse.getJSONObject("main").getString("temp")
                    onResponse(weatherDescription, temp)
                }
            }
        })
    }

    fun fetchFiveDayForecast(city: String, onResponse: (ForecastResponse) -> Unit, onFailure: (IOException) -> Unit) {
        val url = "https://api.openweathermap.org/data/2.5/forecast?q=$city&appid=$apiKey&units=metric"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    onFailure(IOException("Unexpected code $response"))
                    return
                }

                val responseBody = response.body?.string()
                if (responseBody != null) {
                    val forecastResponse = gson.fromJson(responseBody, ForecastResponse::class.java)
                    onResponse(forecastResponse)
                } else {
                    onFailure(IOException("Response body is null"))
                }
            }
        })
    }

    fun fetchFiveDayForecast(latitude: Double, longitude: Double, onResponse: (ForecastResponse) -> Unit, onFailure: (IOException) -> Unit) {
        val url = "https://api.openweathermap.org/data/2.5/forecast?lat=$latitude&lon=$longitude&appid=$apiKey&units=metric"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    onFailure(IOException("Unexpected code $response"))
                    return
                }

                val responseBody = response.body?.string()
                if (responseBody != null) {
                    val forecastResponse = gson.fromJson(responseBody, ForecastResponse::class.java)
                    onResponse(forecastResponse)
                } else {
                    onFailure(IOException("Response body is null"))
                }
            }
        })
    }
}
