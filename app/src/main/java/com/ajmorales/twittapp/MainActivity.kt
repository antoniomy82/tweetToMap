package com.ajmorales.twittapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.ajmorales.twittapp.model.ApiAdapter
import com.ajmorales.twittapp.model.ApiService
import com.ajmorales.twittapp.model.Tweet
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.stream.JsonReader
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStreamReader

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val myTwitterList: ArrayList<TwitModel>? = ArrayList<TwitModel>()

    //var handler = Handler()
    var runnable: Runnable? = null
    var lifeSpan: Long = 5000

    private lateinit var service: ApiService

    private val BASE_URL =
        "http://api.openweathermap.org/" //"http://api.openweathermap.org/data/2.5/weather?q=Ceuta&appid=8d9f4873f77268809bd675a3c061c3a9&units=metric
    var AppId = "8d9f4873f77268809bd675a3c061c3a9"
    var lat = "35"
    var lon = "139"
    var CITYNAME = "Ceuta"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        setSpinner()
        loadData()
        //getTweet()
        /*
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)*/
    }

    /*
        override fun onResume() {
            handler.postDelayed(Runnable {
                handler.postDelayed(runnable!!, lifeSpan)
                Toast.makeText(
                    this@MainActivity,
                    "Update map every " + (lifeSpan / 1000).toString() + " seconds",
                    Toast.LENGTH_SHORT
                ).show()

                //Update map fragment
                val mapFragment =
                    supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
                mapFragment?.getMapAsync(this)
            }.also { runnable = it }, lifeSpan)
            super.onResume()
        }

        override fun onPause() {
            super.onPause()
            handler.removeCallbacks(runnable!!) //stop handler when activity not visible super.onPause();
        }
    */
    private fun setSpinner() {
        //Spinner
        val spLifeSpan: Spinner = findViewById(R.id.sp_lifespan)
        val btnSearch: Button = findViewById(R.id.btnSearch)
        val edSearch: EditText = findViewById(R.id.etSearch)

        val lifeSpanList: List<Int> = listOf(5, 10, 20, 30, 60, 90, 120)
        val spAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, lifeSpanList)
        spLifeSpan.adapter = spAdapter

        spLifeSpan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                lifeSpan = (lifeSpanList[position] * 1000.toLong())
                println(lifeSpan)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) { //Nothing to do
            }
        }

        btnSearch.setOnClickListener {
            getTweet(edSearch.text.toString())
        }
    }


    override fun onMapReady(googleMap: GoogleMap?) {

        googleMap?.uiSettings?.isZoomControlsEnabled = true //Zoom in/out

        for (i: Int in 0 until myTwitterList!!.size) {
            googleMap?.addMarker(myTwitterList[i].latlong?.let {
                MarkerOptions().position(it).title(myTwitterList[i].tittle)
            })
        }

        googleMap?.setOnMarkerClickListener { marker ->
            val intent = Intent(this, MarkerDetail::class.java)
            intent.putExtra("tittle", marker.title)
            this.startActivity(intent)

            true
        }

    }


    private fun loadData() {
        myTwitterList?.add(TwitModel(LatLng(-33.862, 151.21), "Sydney"))
        myTwitterList?.add(TwitModel(LatLng(-31.08332, 150.916672), "Tamworth"))
        myTwitterList?.add(TwitModel(LatLng(-32.916668, 151.750000), "Newcastle"))
        myTwitterList?.add(TwitModel(LatLng(40.4631, -3.6501), "Madrid"))
        myTwitterList?.add(TwitModel(LatLng(40.73, -73.99), "Nueva York"))
    }

    //Meter esto en ViewModel
    fun getTweet(str: String) {
        val currentCall: Call<ResponseBody>? = ApiAdapter().api!!.getTweet(str)
        currentCall?.enqueue(streamResponse)

    }

    private val streamResponse: Callback<ResponseBody> = object : Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

            Log.d("debug", "Getting data - ON RESPONSE")

            if (response.isSuccessful) {
                Log.e("SUCCESS!", "Call OK")

                try {
                    val reader = JsonReader(InputStreamReader(response.body()!!.byteStream()))
                    val gson = GsonBuilder().create()

                    var i = 0

                    while (true) {
                        val j = gson.fromJson<JsonObject>(reader, JsonObject::class.java)

                        //Log.d("debug", "JSON: $j")

                        Log.d("debug", "SEARCHING.." + i++)

                        if (j.getAsJsonObject("user") != null) {
                            val tweet = gson.fromJson(j, Tweet::class.java)
                            // val coordinates: com.ajmorales.twittapp.model.Geo= gson.fromJson(j, com.ajmorales.twittapp.model.Geo::class.java)

                            if (tweet.coordinates != null || tweet.geo != null) {
                                Log.d("debug", "JSON: $j")

                                Log.d(
                                    "@User",
                                    tweet.user?.username.toString() + " GEO: [" + tweet.geo?.coordinates?.get(
                                        0
                                    ) + "," + tweet.geo?.coordinates?.get(1) + "] Coordinates [" +
                                            tweet.coordinates?.coordinates?.get(0) + "," + tweet.coordinates?.coordinates?.get(
                                        1
                                    ) + "]"
                                )
                            }
                            /*
                              tweet.geo?.coordinates?.get(0)
                                  Log.d("@ User", tweet.user?.username.toString()+" GEO: "+ tweet.geo_enabled.toString())*/

                            //myTwitterList.add()
                            //tweetsList.add(tweet)
                            // Updating UI
                            //  updateUI(tweetsList)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("error", "ERROR : ${e.message}")
                }
            }
        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            Log.e("error", "onFailure call")
        }
    }

}



