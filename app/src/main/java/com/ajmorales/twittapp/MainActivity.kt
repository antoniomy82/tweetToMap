package com.ajmorales.twittapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val myTwitterList: ArrayList<TwitModel>? = ArrayList<TwitModel>()
    var handler = Handler()
    var runnable: Runnable? = null
    var lifeSpan: Long = 5000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        setSpinner()
        loadData()

        /*
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)*/
    }

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

    private fun setSpinner() {
        //Spinner
        val spLifeSpan: Spinner = findViewById(R.id.sp_lifespan)
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

}



