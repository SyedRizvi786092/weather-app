package com.project.weatherapp

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    val CITY: String = "Lucknow"
    val API: String = "70b40caf1fc9475bb3b115715242806"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        weatherTask().execute()
    }

    inner class weatherTask() : AsyncTask<String, Void, Pair<String?,String?>>() {
        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.error).visibility = View.GONE
        }

        fun callAPI(url: String): String? {
            var response: String?
            try {
                response = URL(url)
                    .readText(Charsets.UTF_8)
            } catch (e: Exception) {
                response = null
            }
            return response
        }

        override fun doInBackground(vararg p0: String?): Pair<String?, String?> {
            var currentApiResponse: String?
            var astroApiResponse: String?
            try {
                currentApiResponse = callAPI("https://api.weatherapi.com/v1/current.json?q=$CITY&key=$API")
                val currentDate = LocalDate.now().toString()
                astroApiResponse = callAPI("https://api.weatherapi.com/v1/astronomy.json?q=$CITY&dt=$currentDate&key=$API")
            } catch (e: Exception) {
                currentApiResponse = null
                astroApiResponse = null
            }
            return Pair(currentApiResponse, astroApiResponse)
        }

        override fun onPostExecute(result: Pair<String?,String?>) {
            super.onPostExecute(result)
            try {
                val jsonObj1 = JSONObject(result.first.toString())
                val location = jsonObj1.getJSONObject("location")
                val current = jsonObj1.getJSONObject("current")
                val updatedAt: Long = current.getLong("last_updated_epoch")
                val updatedAtText =
                    "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                        Date(updatedAt * 1000)
                    )
                val temp = current.getDouble("temp_c").toString() + "°C"
                val pressure = current.getDouble("pressure_mb").toString() + " mb"
                val humidity = current.getDouble("humidity").toString() + "%"
                val windSpeed = current.getDouble("wind_kph").toString() + " km/h"
                val status = current.getJSONObject("condition").getString("text")
                val address =
                    location.getString("name") + ", " + location.getString("region") + ", " + location.getString(
                        "country"
                    )

                val jsonObj2 = JSONObject(result.second.toString())
                val astro = jsonObj2.getJSONObject("astronomy").getJSONObject("astro")
                val sunrise = astro.getString("sunrise")
                val sunset = astro.getString("sunset")

                findViewById<TextView>(R.id.address).text = address
                findViewById<TextView>(R.id.updated_at).text = updatedAtText
                findViewById<TextView>(R.id.status).text = status.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }
                findViewById<TextView>(R.id.temperature).text = temp
                findViewById<TextView>(R.id.wind).text = windSpeed
                findViewById<TextView>(R.id.pressure).text = pressure
                findViewById<TextView>(R.id.humidity).text = humidity
                findViewById<TextView>(R.id.sunrise).text = sunrise
                findViewById<TextView>(R.id.sunset).text = sunset

                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE
            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.error).visibility = View.VISIBLE
            }
        }
    }
}