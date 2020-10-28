package com.ajmorales.tweetToMap.view

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ajmorales.tweetToMap.BR
import com.ajmorales.tweetToMap.R
import com.ajmorales.tweetToMap.databinding.ActivityMapsBinding
import com.ajmorales.tweetToMap.util.Util
import com.ajmorales.tweetToMap.viewmodel.TweetViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private var handler= Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null

    private var myMarker: Marker? = null
    private var util: Util? = Util()

    private var mapFragment: SupportMapFragment? = null
    private var binding: ActivityMapsBinding? = null

    var model: TweetViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        model = ViewModelProvider(this).get(TweetViewModel::class.java)  //LifeCycle

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.setVariable(
            BR.mapFragment,
            supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        )
        mapFragment = binding?.mapFragment//Map fragment

        model?.setupView(binding, this)  //Binding TweetViewModel
    }

    //Refresh mapFragment when change portrait to landscape
    override fun onConfigurationChanged(newConfig: Configuration) {

        myMarker?.remove()
        mapFragment?.getMapAsync(this)
        super.onConfigurationChanged(newConfig)
    }


    override fun onResume() {
        super.onResume()

        //This handler, asks for tweets (to TweetViewModel) every lifeSpan and update the map putting tweets as marker in it
        model?.lifeSpan?.let {
            handler.postDelayed(Runnable {
                model?.lifeSpan?.let { handler.postDelayed(runnable!!, it) }

                if (util!!.isOnline(this)) {

                    if (model?.apiResponses(binding, this)!!) {
                        myMarker?.remove()
                        mapFragment?.getMapAsync(this)
                        binding?.progressBarVisibility = false
                    }else if (binding?.etSearch?.text.toString() ==model?.mySearchWord){
                        binding?.progressBarVisibility = true
                        util?.onSnack(binding?.root!!,"UPDATING DATA", model?.lifeSpan!!)
                    }

                } else {//No internet
                    util?.onSnack(binding?.root!!, getString(R.string.tvCheckConnection), model?.lifeSpan!!)
                    myMarker?.remove()
                }

            }.also { runnable = it }, it)
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable!!) //stop handler when activity not visible
    }

    //It show tweet's coordinates in the map like marker
    override fun onMapReady(googleMap: GoogleMap?) {

        //googleMap?.uiSettings?.isZoomControlsEnabled = true //Zoom in/out
        val zoom = 2f
        var iterator = 0
        if (model?.callIterator()?.value != null) {
            iterator = model?.callIterator()?.value!!
        }

        val myLatLon=model?.getLatLng(iterator) //getLatLng TweetViewModel
        //It add markter to map
        if (model?.myTweetList?.get(iterator) != null) {
            myMarker = googleMap?.addMarker(
                myLatLon?.let {
                    MarkerOptions().position(it) //TweetViewModel
                        .title(model?.myTweetList?.get(iterator)?.user?.name)
                }
            )
            myMarker?.showInfoWindow()

            Log.d(
                "Showing tweet:",
                model?.myTweetList?.get(iterator)?.user?.name.toString() + " [" + iterator + "]"
            )

            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLon, zoom))
        }

        //MarkerClickListener to MarkerDetail
        googleMap?.setOnMarkerClickListener {
            val intent = Intent(this, MarkerDetail::class.java)
            intent.putExtra("position", iterator)
            intent.putExtra("model", model)
            this.startActivity(intent)

            true
        }
    }
}




