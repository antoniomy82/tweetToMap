package com.ajmorales.twittapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.ajmorales.twittapp.model.ApiAdapter
import com.ajmorales.twittapp.model.Geo
import com.ajmorales.twittapp.model.Tweet
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
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
    private val ZOOM_LEVEL = 2f
    private var handler = Handler()
    private var runnable: Runnable? = null
    var lifeSpan: Long = 2000
    var simulatedLocation: Int = 0
    var iterator: Int = 0
    var lastSearch: String? = null
    private var myMarker: Marker? = null

    companion object {
        var myTweetList: ArrayList<Tweet>? = null
        var myLocations: ArrayList<Geo>? = null

        fun getTweet(position: Int): Tweet {
            return myTweetList!![position]
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        setSpinner()

        loadCoordinates()

    }


        override fun onResume() {
            handler.postDelayed(Runnable {
                handler.postDelayed(runnable!!, lifeSpan)

                if (simulatedLocation == 9) {
                    simulatedLocation = 0
                } else {
                    simulatedLocation++
                }


                if (myTweetList?.size == 10) {
                    myMarker?.remove()
                    //Update Map
                    val mapFragment =
                        supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
                    mapFragment?.getMapAsync(this)
                }


            }.also { runnable = it }, lifeSpan)
            super.onResume()
        }

        override fun onPause() {
            super.onPause()
            handler.removeCallbacks(runnable!!) //stop handler when activity not visible super.onPause();
        }

    private fun setSpinner() {
        //Spinner
        val spLifeSpan: Spinner = findViewById(R.id.sp_lifespan)
        val btnSearch: Button = findViewById(R.id.btnSearch)
        val edSearch: EditText = findViewById(R.id.etSearch)


        val lifeSpanList: List<Int> = listOf(2, 5, 10, 20, 30, 60, 90, 120)
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
            lastSearch = edSearch.text.toString()
            getTweet(edSearch.text.toString())
        }
    }


    override fun onMapReady(googleMap: GoogleMap?) {

        googleMap?.uiSettings?.isZoomControlsEnabled = true //Zoom in/out

        if (myLocations != null && myTweetList != null) {
            myMarker = googleMap?.addMarker(
                MarkerOptions().position(
                    LatLng(
                        myLocations?.get(simulatedLocation)?.coordinates!![0],
                        myLocations?.get(simulatedLocation)?.coordinates!![1]
                    )
                ).title(myTweetList!![simulatedLocation].user?.name)
            )
            myMarker?.showInfoWindow()


            googleMap?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        myLocations?.get(
                            simulatedLocation
                        )?.coordinates!![0], myLocations?.get(simulatedLocation)?.coordinates!![1]
                    ), ZOOM_LEVEL
                )
            )

        }


        googleMap?.setOnMarkerClickListener { marker ->
            val intent = Intent(this, MarkerDetail::class.java)
            intent.putExtra("position", simulatedLocation)
            this.startActivity(intent)

            true
        }

    }


    private fun loadCoordinates() {

        val newLocation: ArrayList<Geo> = ArrayList<Geo>()

        newLocation.add(Geo("Málaga", listOf(36.72016, -4.42034)))
        newLocation.add(Geo("Londres", listOf(51.51279, -0.09184)))
        newLocation.add(Geo("Tetuan", listOf(35.5784500, -5.3683700)))
        newLocation.add(Geo("Madrid", listOf(40.4631, -3.6501)))
        newLocation.add(Geo("Granada ", listOf(37.18817, -3.60667)))
        newLocation.add(Geo("Nueva York", listOf(40.73, -73.99)))
        newLocation.add(Geo("Mexico", listOf(19.4978, -99.1269)))
        newLocation.add(Geo("Buenos Aires", listOf(-34.61315, -58.37723)))
        newLocation.add(Geo("Toronto", listOf(43.70011, -79.4163)))
        newLocation.add(Geo("Sydney", listOf(-33.862, 151.21)))


        myLocations = newLocation
    }

    //Meter esto en ViewModel
    private fun getTweet(str: String) {
        val currentCall: Call<ResponseBody>? = ApiAdapter().api!!.getTweet(str)
        currentCall?.enqueue(streamResponse)
    }

    private val streamResponse: Callback<ResponseBody> = object : Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

            Log.d("debug", "Getting data - ON RESPONSE")

            if (response.isSuccessful) {
                Log.e("SUCCESS!", "Call OK")


                Thread(Runnable {
                    try {
                        val reader = JsonReader(InputStreamReader(response.body()!!.byteStream()))
                        val gson = GsonBuilder().create()

                        var i = 0
                        myTweetList = ArrayList<Tweet>()

                        while (i < 10) {
                            val j = gson.fromJson<JsonObject>(reader, JsonObject::class.java)

                            if (j.getAsJsonObject("user") != null) {
                                val tweet = gson.fromJson(j, Tweet::class.java)


                                myTweetList?.add(tweet)

                                Log.d(
                                    "Searching location(",
                                    i.toString() + ") [Autor]:" + tweet.user?.name.toString()
                                )

                                if (tweet.coordinates != null || tweet.geo != null) {
                                    //  Log.d("debug", "JSON: $j")

                                    Log.d(
                                        "\n Location found!",
                                        tweet.user?.name.toString() + " GEO: [" + tweet.geo?.coordinates?.get(
                                            0
                                        ) + "," + tweet.geo?.coordinates?.get(1) + "] Coordinates [" +
                                                tweet.coordinates?.coordinates?.get(0) + "," + tweet.coordinates?.coordinates?.get(
                                            1
                                        ) + "]\n[Text:]" + tweet.text
                                    )
                                }

                                i++
                                // Updating UI
                                //  updateUI(tweetsList)
                            }
                        }

                    } catch (e: Exception) {
                        Log.e("error", "ERROR : ${e.message}")
                    }
                }).start()

            } else {
                Log.e("ERROR:", "[Warning]" + response.toString())
            }

            Thread.interrupted()
        }


        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            Log.e("error", "onFailure call")
        }
    }
}



