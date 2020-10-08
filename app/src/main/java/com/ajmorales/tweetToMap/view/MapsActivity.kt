package com.ajmorales.tweetToMap.view
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ajmorales.tweetToMap.BR
import com.ajmorales.tweetToMap.R
import com.ajmorales.tweetToMap.databinding.ActivityMapsBinding
import com.ajmorales.tweetToMap.model.Geo
import com.ajmorales.tweetToMap.utils.Utils
import com.ajmorales.tweetToMap.viewmodel.TweetViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val zoom = 2f
    private var handler = Handler()
    private var runnable: Runnable? = null

    var lifeSpan: Long = 5000
    var iterator: Int = 0

    private var myLocations: ArrayList<Geo>? = ArrayList<Geo>()
    private var myMarker: Marker? = null
    var model: TweetViewModel? = null
    var util: Utils? = Utils()

    private var searchWord: String? = null
    private var mapFragment: SupportMapFragment? = null
    private var binding: ActivityMapsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        //To preserve data - LifeCycle
        model = ViewModelProvider(this).get(TweetViewModel::class.java)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.setVariable(
            BR.mapFragment,
            supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        ) //Map fragment
        mapFragment = binding?.mapFragment//Map fragment

        setupView()
    }


    private fun setupView() {
        binding?.tvSearchingVisibility = false
        binding?.progressBarVisibility = false
        binding?.searchingElementsVisibility =
            true  // spLifespan, tvLifespan? ,btnSearch? , edSearch?.visibility

        //Lifespan spinner
        val lifeSpanList: List<Int> = listOf(5, 7, 9, 10, 12, 15)
        val spAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, lifeSpanList)
        binding?.spLifespan?.adapter = spAdapter

        binding?.spLifespan?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                lifeSpan = (lifeSpanList[position] * 1000.toLong())
            }

            override fun onNothingSelected(p0: AdapterView<*>?) { //Nothing to do
            }
        }

        //Search button
        binding?.btnSearch?.setOnClickListener {

            if (!util!!.isOnline(this)) {
                binding?.searchingElementsVisibility =
                    false  // spLifespan, tvLifespan? ,btnSearch? , edSearch?.visibility
                binding?.setVariable(BR.tvSearching, getString(R.string.tvCheckConnection))
                binding?.tvSearchingVisibility = true

                Toast.makeText(this, "NO INTERNET CONNECTION", Toast.LENGTH_SHORT).show()
            } else {
                binding?.searchingElementsVisibility =
                    false  // spLifespan, tvLifespan? ,btnSearch? , edSearch?.visibility
                binding?.etSearch?.hideKeyboard()
                binding?.tvSearchingVisibility = true //Visible tvSearching
                binding?.setVariable(BR.tvSearching, getString(R.string.tvSearchingConnection))
                binding?.progressBarVisibility = true
                iterator = 0

                searchWord = binding?.etSearch?.text.toString()
                model!!.callTweets(binding?.etSearch?.text.toString()) //ViewModel @@@
            }
        }

        myLocations = util?.getSimulatedLocations() //load simulated locations
    }

    //Refresh mapFragment when change landscape to portrait
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        myMarker?.remove()
        mapFragment?.getMapAsync(this)

    }

    //Here is the action :-)
    override fun onResume() {
        super.onResume()

        handler.postDelayed(Runnable {
            handler.postDelayed(runnable!!, lifeSpan)

            if (util!!.isOnline(this)) {
                binding?.searchingElementsVisibility =
                    true  // spLifespan, tvLifespan? ,btnSearch? , edSearch?.visibility
                //When the parsing process is done!
                if (model?.getResponse()!!.contains("DONE")) {
                    model?.myTweets = model?.getTweets()
                    model!!.myTweetList = model?.myTweets?.value
                }

                if (iterator == 29) {
                    iterator = 0
                    model!!.setIterator(iterator)
                    model!!.callTweets(binding?.etSearch?.text.toString()) //Automatically relaunch
                }

                //Warning response 420
                val timeRemaining =
                    ((lifeSpan / 1000).toInt() * 30) - ((lifeSpan / 1000).toInt() * iterator)
                if (model?.getResponse()!!.contains("code=420")) {

                    Toast.makeText(
                        this,
                        "420 Enhance Your Calm:\n Returned by the Twitter Search and Trends API when the client is being rate limited.\n Get a professional API or try increasing the lifespan \n Showing previous tweets!! \n ¡Merezco el curro por los dolores de cabeza! -> API HORRIBLE :-) \n\n Relauching call in: " + timeRemaining.toString() + " sec",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                if (model!!.myTweetList?.size == 30) { //If there are tweets, it reload map

                    binding?.searchingElementsVisibility =
                        true  // spLifespan, tvLifespan? ,btnSearch? , edSearch?.visibility
                    binding?.tvSearchingVisibility = false
                    binding?.progressBarVisibility = false

                    myMarker?.remove()
                    mapFragment?.getMapAsync(this)
                    iterator++
                    model!!.setIterator(iterator)
                }

            } else {//No internet
                binding?.searchingElementsVisibility =
                    false  // spLifespan, tvLifespan? ,btnSearch? , edSearch?.visibility
                binding?.tvSearchingVisibility = true
                binding?.setVariable(BR.tvSearching, getString(R.string.tvCheckConnection))

                Toast.makeText(this, "NO INTERNET CONNECTION", Toast.LENGTH_SHORT).show()
                myMarker?.remove()
            }

        }.also { runnable = it }, lifeSpan)

    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable!!) //stop handler when activity not visible super.onPause();
    }

    //Prepare the map with the locations and show them
    override fun onMapReady(googleMap: GoogleMap?) {

        googleMap?.uiSettings?.isZoomControlsEnabled = true //Zoom in/out
        val myLatLon: LatLng

        if (model!!.callIterator()?.value != null) {
            iterator = model!!.callIterator()?.value!!
        }

        //If there are geo or coordinates
        if (model!!.myTweetList?.get(iterator)?.geo?.coordinates?.get(0) != null && model!!.myTweetList?.get(
                iterator
            )?.geo?.coordinates?.get(
                1
            ) != null
        ) {
            myLatLon = LatLng(
                model!!.myTweetList?.get(iterator)?.geo?.coordinates!![0],
                model!!.myTweetList?.get(iterator)?.geo?.coordinates!![1]
            )
        } else if (model!!.myTweetList?.get(iterator)?.coordinates?.coordinates?.get(0) != null && model!!.myTweetList?.get(
                iterator
            )?.coordinates?.coordinates?.get(
                1
            ) != null
        ) {
            myLatLon = LatLng(
                model!!.myTweetList?.get(iterator)?.coordinates?.coordinates!![1],
                model!!.myTweetList?.get(iterator)?.coordinates?.coordinates!![0]
            )
        } else { //Simulated location
            myLatLon = LatLng(
                myLocations?.get(iterator)?.coordinates!![0],
                myLocations?.get(iterator)?.coordinates!![1]
            )
        }

        //I add markter to map
        if (myLocations != null && model!!.myTweetList?.get(iterator) != null) {
            myMarker = googleMap?.addMarker(
                MarkerOptions().position(myLatLon)
                    .title(model!!.myTweetList?.get(iterator)?.user?.name)
            )
            myMarker?.showInfoWindow()

            Log.d(
                "Showing tweet:",
                model!!.myTweetList?.get(iterator)?.user?.name.toString() + " [" + iterator + "]"
            )

            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLon, zoom))
        }

        //MarkerClickListener
        googleMap?.setOnMarkerClickListener {
            val intent = Intent(this, MarkerDetail::class.java)
            intent.putExtra("position", iterator)
            intent.putExtra("model", model)
            this.startActivity(intent)

            true
        }
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}



