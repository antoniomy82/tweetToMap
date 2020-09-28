package com.ajmorales.twittapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val myTwitterList: ArrayList<TwittModel>? = ArrayList<TwittModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadData()

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap?) {

        googleMap?.uiSettings?.isZoomControlsEnabled = true //Zoom in/out

        for (i: Int in 0 until myTwitterList!!.size) {
            googleMap?.addMarker(myTwitterList[i].latlong?.let {
                MarkerOptions().position(it).title(myTwitterList[i].tittle)
            })?.showInfoWindow()
        }

        googleMap?.setOnMarkerClickListener { marker ->
            val intent = Intent(this, MarkerDetail::class.java)
            intent.putExtra("tittle", marker.title)
            this.startActivity(intent)

            true
        }

    }

    fun loadData() {
        myTwitterList?.add(TwittModel(LatLng(-33.862, 151.21), "Sydney"))
        myTwitterList?.add(TwittModel(LatLng(-31.08332, 150.916672), "Tamworth"))
        myTwitterList?.add(TwittModel(LatLng(-32.916668, 151.750000), "Newcastle"))
        myTwitterList?.add(TwittModel(LatLng(40.4631, -3.6501), "Madrid"))
        myTwitterList?.add(TwittModel(LatLng(40.73, -73.99), "Nueva York"))
    }
}



