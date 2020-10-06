package com.ajmorales.tweetToMap.view
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.ajmorales.tweetToMap.R
import com.ajmorales.tweetToMap.model.Geo
import com.ajmorales.tweetToMap.model.Tweet
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

    private var myTweets: MutableLiveData<List<Tweet>>? = null
    private var myListTweets: List<Tweet>? = null
    private var searchWord: String? = null

    private var progressBar: ProgressBar? = null
    private var tvSearching: TextView? = null
    private var tvLifespan: TextView? = null
    private var btnSearch: Button? = null
    private var edSearch: EditText? = null
    private var spLifeSpan: Spinner? = null
    private var mapFragment: SupportMapFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        supportActionBar?.hide()
        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        tvSearching = findViewById(R.id.tvSearching)
        tvLifespan = findViewById(R.id.tvLifespan)
        btnSearch = findViewById(R.id.btnSearch)
        edSearch = findViewById(R.id.etSearch)

        tvSearching?.visibility = View.GONE
        progressBar?.visibility = View.GONE

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        //To preserve data in case to switch portrait to landscape - LifeCycle
        model = ViewModelProvider(this).get(TweetViewModel::class.java)

        //Lifespan spinner
        spLifeSpan = findViewById(R.id.sp_lifespan)
        val lifeSpanList: List<Int> = listOf(5, 7, 9, 10, 12, 15)
        val spAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, lifeSpanList)
        spLifeSpan?.adapter = spAdapter

        spLifeSpan?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                lifeSpan = (lifeSpanList[position] * 1000.toLong())
            }

            override fun onNothingSelected(p0: AdapterView<*>?) { //Nothing to do
            }
        }


        //Search button
        btnSearch?.setOnClickListener {

            if (!util!!.isOnline(this)) {
                textInVisible()
                tvSearching?.text = getString(R.string.tvCheckConnection)
                Toast.makeText(this, "NO INTERNET CONNECTION", Toast.LENGTH_SHORT).show()
            } else {
                textInVisible()
                tvSearching?.text = getString(R.string.tvSearchingConnection)
                tvSearching?.visibility = View.VISIBLE
                progressBar?.visibility = View.VISIBLE
                iterator = 0
                searchWord = edSearch?.text.toString()
                model!!.callTweets(edSearch?.text.toString()) //ViewModel @@@
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
                //When the parsing process is done!
                if (model?.getResponse()!!.contains("DONE")) {
                    myTweets = model?.getTweets()
                    myListTweets = myTweets?.value
                }

                if (iterator == 29) {
                    iterator = 0
                    model!!.setIterator(iterator)
                    model!!.callTweets(edSearch?.text.toString()) //Automatically relaunch
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

                if (myListTweets?.size == 30) { //If there are tweets, it reload map
                    textVisible()
                    myMarker?.remove()

                    mapFragment?.getMapAsync(this)

                }

            } else {//No internet
                textInVisible()
                tvSearching?.visibility = View.VISIBLE
                tvSearching?.text = getString(R.string.tvCheckConnection)
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
        if (myListTweets?.get(iterator)?.geo?.coordinates?.get(0) != null && myListTweets?.get(
                iterator
            )?.geo?.coordinates?.get(
                1
            ) != null
        ) {
            myLatLon = LatLng(
                myListTweets?.get(iterator)?.geo?.coordinates!![0],
                myListTweets?.get(iterator)?.geo?.coordinates!![1]
            )
        } else if (myListTweets?.get(iterator)?.coordinates?.coordinates?.get(0) != null && myListTweets?.get(
                iterator
            )?.coordinates?.coordinates?.get(
                1
            ) != null
        ) {
            myLatLon = LatLng(
                myListTweets?.get(iterator)?.coordinates?.coordinates!![1],
                myListTweets?.get(iterator)?.coordinates?.coordinates!![0]
            )
        } else { //Simulated location
            myLatLon = LatLng(
                myLocations?.get(iterator)?.coordinates!![0],
                myLocations?.get(iterator)?.coordinates!![1]
            )
        }

        //I add markter to map
        if (myLocations != null && myListTweets?.get(iterator) != null) {
            myMarker = googleMap?.addMarker(
                MarkerOptions().position(myLatLon)
                    .title(myListTweets?.get(iterator)?.user?.name)
            )
            myMarker?.showInfoWindow()

            Log.d(
                "Showing tweet:",
                myListTweets?.get(iterator)?.user?.name.toString() + " [" + iterator + "]"
            )

            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLon, zoom))

            iterator++
            model!!.setIterator(iterator)
        }

        //MarkerClickListener
        googleMap?.setOnMarkerClickListener {
            val intent = Intent(this, MarkerDetail::class.java)
            intent.putExtra("tweet", myListTweets?.get(iterator))
            intent.putExtra("user", myListTweets?.get(iterator)?.user)
            this.startActivity(intent)

            true
        }
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun textVisible() {
        spLifeSpan?.visibility = View.VISIBLE
        tvLifespan?.visibility = View.VISIBLE
        btnSearch?.visibility = View.VISIBLE
        edSearch?.visibility = View.VISIBLE
        tvSearching?.visibility = View.GONE
        progressBar?.visibility = View.GONE
    }

    private fun textInVisible() {
        edSearch?.hideKeyboard()
        spLifeSpan?.visibility = View.GONE
        tvLifespan?.visibility = View.GONE
        btnSearch?.visibility = View.GONE
        edSearch?.visibility = View.GONE
    }
}



