package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchweaterdata("Gorakhpur")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView=binding.searchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchweaterdata(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchweaterdata(cityname: String) {
       val retrofit=Retrofit.Builder()
           .addConverterFactory(GsonConverterFactory.create())
           .baseUrl("https://api.openweathermap.org/data/2.5/")
           .build().create(ApiInterface::class.java)
        val response=retrofit.getWeatherData(cityname,"4d9f705093d78796a12334bd6d54b7de","metric")
        response.enqueue(object :Callback<Weatherapp>{
            override fun onResponse(call: Call<Weatherapp>, response: Response<Weatherapp>) {
                val responsebody = response.body()
                if (response.isSuccessful && responsebody != null) {
                    val temperature = responsebody.main.temp.toString()
                    binding.temperature.text="$temperature °C"
                    val humidity=responsebody.main.humidity
                    val windspeed=responsebody.wind.speed
                    val sunrise=responsebody.sys.sunrise.toLong()
                    val sunset=responsebody.sys.sunset.toLong()
                    val sealevel=responsebody.main.pressure
                    val condition=responsebody.weather.firstOrNull()?.main?:"unknown"
                    val mintemp=responsebody.main.temp_min
                    val maxtemp=responsebody.main.temp_max
                    binding.wheather.text=condition
                    binding.maxtemp.text="Max Temp: $maxtemp °C"
                    binding.mintemp.text="Min Temp: $mintemp °C"
                    binding.humidity.text="$humidity %"
                    binding.wind.text="$windspeed m/s"
                    binding.sunrise.text="${time(sunrise)}"
                    binding.sunset.text="${time(sunset)}"
                    binding.sea.text="$sealevel hPa"
                    binding.condition.text=condition
                    binding.day.text=dayname(System.currentTimeMillis())
                        binding.date.text=date()
                        binding.cityname.text="$cityname"
                // Log.d("Tag", "onresponse : $temperature")

                    changeimage(condition)

                }
            }

            override fun onFailure(call: Call<Weatherapp>, t: Throwable) {

            }


        } )


    }

    private fun changeimage(condition:String) {
        when(condition){
            "Partly Clouds", "Clouds", "Overcast","Mist","Foggy"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)

            }
            "Clear Sky", "Sunny", "Clear"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)

            }
            "Light Rain", "Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)

            }
            "Lignt Snow", "Moderate Snow", "Heavy Snow", "Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)

            }
            else->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)

            }

        }

        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String{
        val sdf=SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))

    }
    private fun time(timestamp: Long): String{
        val sdf=SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))

    }

    fun dayname(timestamp: Long):String{
        val sdf=SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}