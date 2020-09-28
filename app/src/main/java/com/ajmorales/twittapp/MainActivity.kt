package com.ajmorales.twittapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    val ZOOM_LEVEL = 10f

    var mMap: GoogleMap?=null //Inicializo

    var mLocation = ArrayList<LatLng>()
    var myTittle = ArrayList<String>()

    //Creo a lo bestia

    var sydney= LatLng(-33.862, 151.21)
    var Tamworth= LatLng(-31.08332, 150.916672)
    var Newcastle=LatLng(-32.916668, 151.750000)
    var Madrid=LatLng(40.4631, -3.6501)
    var NY=LatLng(40.73, -73.99)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_main)

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)




        mLocation.add(sydney)
        mLocation.add(Tamworth)
        mLocation.add(Newcastle)
        mLocation.add(Madrid)
        mLocation.add(NY)

        myTittle.add("sydney")
        myTittle.add("Tamworth")
        myTittle.add("Newcastle")
        myTittle.add("Madrid")
        myTittle.add("New York")
    }



    override fun onMapReady(googleMap: GoogleMap?) {

        mMap = googleMap

        mMap?.uiSettings?.isZoomControlsEnabled = true

            for(i:Int in 0 until  mLocation.size){
                mMap?.addMarker(MarkerOptions().position(mLocation[i]).title(myTittle[i]))?.showInfoWindow()
               // moveCamera(CameraUpdateFactory.newLatLng(mLocation[i]))
                //googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocation[i], ZOOM_LEVEL))
            }

        mMap?.setOnMarkerClickListener { marker ->

            if (marker.isInfoWindowShown) {
               // marker.hideInfoWindow()
                marker.showInfoWindow()
               //  Toast.makeText(this,marker.title , Toast.LENGTH_SHORT).show()

            } else {
                val intent = Intent(this,TwittDetail::class.java)
                intent.putExtra("tittle",marker.title)
                this.startActivity(intent)

            }
            true
        }

    }
}



