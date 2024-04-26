package com.example.lab3

import android.content.Intent
import android.opengl.Matrix
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import kotlin.math.max
import kotlin.math.min

interface NasaApiService {
    @GET("planetary/apod")
    fun getAstronomyPictureOfTheDay(@Query("api_key") apiKey: String): Call<NasaResponse>
}

data class NasaResponse(val url: String, val title: String)


class NasaActivity : AppCompatActivity() {

    //private val NASA_API_KEY = getString(R.string.nasa_api_key);
    private val NASA_API_KEY = "w7kMU1obTlqIHRp84CrHXxXxf0NaLYzqiTzNsB5b";
    private lateinit var imageView: ImageView
    private lateinit var titleTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nasa)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imageView = findViewById(R.id.imageView)
        titleTextView = findViewById(R.id.titleTextView)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.nasa.gov/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(NasaApiService::class.java)

        val call = service.getAstronomyPictureOfTheDay(NASA_API_KEY)
        call.enqueue(object : Callback<NasaResponse> {
            override fun onResponse(call: Call<NasaResponse>, response: Response<NasaResponse>) {
                if (response.isSuccessful) {
                    val nasaResponse = response.body()
                    if (nasaResponse != null) {
                        val imageUrl = nasaResponse.url
                        val title = nasaResponse.title
                        Glide.with(applicationContext).load(imageUrl).into(imageView)

                        titleTextView.text = title
                    }
                }
                else {
                    // Обработка ошибок
                }
            }

            override fun onFailure(call: Call<NasaResponse>, t: Throwable) {
                Log.e("NasaActivity", "Ошибка при выполнении запроса к API NASA", t)
            }
        })
    }

    fun goBack(v: View?) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun rateApp(v: View?) {
        val intent = Intent(this, FeedbackActivity::class.java)
        startActivity(intent)
    }
}