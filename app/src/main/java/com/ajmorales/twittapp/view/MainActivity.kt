package com.ajmorales.twittapp.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.ajmorales.twittapp.R
import com.ajmorales.twittapp.model.Geo
import com.ajmorales.twittapp.model.Tweet
import com.ajmorales.twittapp.model.TweetObservable
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private val ZOOM_LEVEL = 2f
    private var handler = Handler()
    private var runnable: Runnable? = null
    var lifeSpan: Long = 2000
    var simulatedLocation: Int = 0
    var iterator: Int = 0
    var lastSearch: String? = null
    private var myMarker: Marker? = null

    private val tweetsObservable: TweetObservable = TweetObservable()  //@@@@@ Pasarlo a ViewModel
    var myList: List<Tweet>? = null //@@@@@@@@@@@@@@@@@@@@

    //Pasarlo a ViewModel
    fun getTweetAt(position: Int): Tweet? {
        return myList?.get(position)
    }

    companion object {
        var myLocations: ArrayList<Geo>? = null
        var myListT: List<Tweet>? = null

        //Pasarlo a ViewModel
        fun getTweetAt(position: Int): Tweet? {
            return myListT?.get(position)
        }

        @JvmName("setMyListT1")
        fun setMyListT(lista: List<Tweet>) {
            this.myListT = lista
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


                myList = tweetsObservable.getTweet().value
                myList?.let { setMyListT(it) }

                Log.d("@@MyList", myList?.size.toString())

                if (myList?.size == 10) {
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
            //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

            tweetsObservable.callTweets(edSearch.text.toString())
            // getTweet(edSearch.text.toString()) //TweetRepositoryImpl Antiguo

        }
    }

    //A ViewModel
    override fun onMapReady(googleMap: GoogleMap?) {

        googleMap?.uiSettings?.isZoomControlsEnabled = true //Zoom in/out

        if (myLocations != null && myList != null) {
            myMarker = googleMap?.addMarker(
                MarkerOptions().position(
                    LatLng(
                        myLocations?.get(simulatedLocation)?.coordinates!![0],
                        myLocations?.get(simulatedLocation)?.coordinates!![1]
                    )
                ).title(myList!![simulatedLocation].user?.name)
            ) //myTweetList!![simulatedLocation].user?.name @@@@@@@@@@@@@@@@@@@@
            myMarker?.showInfoWindow()

            Log.d("Dato List:", myList!![simulatedLocation].user?.name.toString())

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
}



