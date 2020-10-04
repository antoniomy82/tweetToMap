package com.ajmorales.twittapp.view

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.ajmorales.twittapp.R
import com.ajmorales.twittapp.model.Geo
import com.ajmorales.twittapp.viewmodel.TweetViewModel
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
    private var myMarker: Marker? = null

    var lifeSpan: Long = 3000
    var iterator: Int = 0
    var iterator2: Int = 0
    private var searchWord: String? = null

    private var tweetViewModel: TweetViewModel = TweetViewModel()
    private var myLocations: ArrayList<Geo>? = ArrayList<Geo>()
    private var progressBar: ProgressBar? = null
    private var tvSearching: TextView? = null
    private var tvLifespan: TextView? = null
    private var btnSearch: Button? = null
    private var edSearch: EditText? = null
    var isConnected = false

    var spLifeSpan: Spinner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        supportActionBar?.hide()

        progressBar = findViewById<ProgressBar>(R.id.progressBar) as ProgressBar
        tvSearching = findViewById(R.id.tvSearching)
        tvLifespan = findViewById(R.id.tvLifespan)
        btnSearch = findViewById(R.id.btnSearch)
        edSearch = findViewById(R.id.etSearch)

        tvSearching?.visibility = View.GONE
        progressBar?.visibility = View.GONE

        spLifeSpan = findViewById(R.id.sp_lifespan)
        val lifeSpanList: List<Int> = listOf(3, 5, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60)
        val spAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, lifeSpanList)
        spLifeSpan?.adapter = spAdapter

        spLifeSpan?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                lifeSpan = (lifeSpanList[position] * 1000.toLong())
                println(lifeSpan)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) { //Nothing to do
            }
        }

        btnSearch?.setOnClickListener {

            if (!isConnected) {
                textInVisible()
                tvSearching?.text = "CHECK YOUR CONNECTION"
                Toast.makeText(this, "NO INTERNET CONNECTION", Toast.LENGTH_SHORT).show()
            } else {
                textInVisible()
                tvSearching?.text = "LOADING DATA.."
                tvSearching?.visibility = View.VISIBLE
                progressBar?.visibility = View.VISIBLE

                searchWord = edSearch?.text.toString()
                tweetViewModel.callTweets(edSearch?.text.toString()) //ViewModel
            }

        }

        myLocations = tweetViewModel.getSimulatedLocations()

        // setupBinding(savedInstanceState) //Activamos DataBinding
    }

    override fun onResume() {
        super.onResume()

        handler.postDelayed(Runnable {
            handler.postDelayed(runnable!!, lifeSpan)

            if (isOnline(this)) {
                isConnected = isOnline(this)
                textVisible()


                if (iterator == 9) {
                    iterator = 0
                } else {
                    iterator++
                }


                if (iterator2 == 39) {
                    searchWord?.let { tweetViewModel.callTweets(it) }
                    iterator2 = 0
                } else {
                    iterator2++
                }
                tweetViewModel.setListTweets()
                if (tweetViewModel.getResponse().contains("code=420")) {
                    Toast.makeText(
                        this,
                        "420 Enhance Your Calm:\n \n Returned by the Twitter Search and Trends API when the client is being rate limited.\n \n Get a professional API or try increasing the lifespan \n \n Showing previous tweets!! \n\n ¡Merezco el curro por los dolores de cabeza! -> API HORRIBLE :-)",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                if (tweetViewModel.getListTweets()?.size == 40 && isOnline(this)) {
                    textVisible()
                    myMarker?.remove()
                    //Update Map
                    val mapFragment =
                        supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
                    mapFragment?.getMapAsync(this)
                } else {
                    tweetViewModel.setListTweets()
                }

            } else {
                isConnected = isOnline(this)
                textInVisible()
                tvSearching?.visibility = View.VISIBLE
                tvSearching?.text = "CHECK YOUR CONNECTION"
                Toast.makeText(this, "NO INTERNET CONNECTION", Toast.LENGTH_SHORT).show()
                myMarker?.remove()
            }

        }.also { runnable = it }, lifeSpan)

    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable!!) //stop handler when activity not visible super.onPause();
    }


    //A ViewModel
    override fun onMapReady(googleMap: GoogleMap?) {

        googleMap?.uiSettings?.isZoomControlsEnabled = true //Zoom in/out
        val myLatLon: LatLng

        //If there are geo or coordinates
        if (tweetViewModel.getListTweets()!![iterator].geo?.coordinates?.get(0) != null && tweetViewModel.getListTweets()!![iterator].geo?.coordinates?.get(
                1
            ) != null
        ) {
            myLatLon = LatLng(
                tweetViewModel.getListTweets()!![iterator].geo?.coordinates!!.get(0),
                tweetViewModel.getListTweets()!![iterator].geo?.coordinates!!.get(1)
            )
        } else if (tweetViewModel.getListTweets()!![iterator].coordinates?.coordinates?.get(0) != null && tweetViewModel.getListTweets()!![iterator].coordinates?.coordinates?.get(
                1
            ) != null
        ) {
            myLatLon = LatLng(
                tweetViewModel.getListTweets()!![iterator].coordinates?.coordinates!!.get(1),
                tweetViewModel.getListTweets()!![iterator].coordinates?.coordinates!!.get(0)
            )
        } else { //Simulated location
            myLatLon = LatLng(
                myLocations?.get(iterator)?.coordinates!![0],
                myLocations?.get(iterator)?.coordinates!![1]
            )
        }

        if (myLocations != null && tweetViewModel.getListTweets() != null) {
            myMarker = googleMap?.addMarker(
                MarkerOptions().position(myLatLon)
                    .title(tweetViewModel.getListTweets()!![iterator].user?.name)
            )
            myMarker?.showInfoWindow()

            Log.d(
                "Dato List:",
                tweetViewModel.getListTweets()!![iterator].user?.name.toString() + " [" + iterator2 + "]"
            )

            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLon, zoom))

        }

        googleMap?.setOnMarkerClickListener {
            val intent = Intent(this, MarkerDetail::class.java)
            intent.putExtra("tweet", tweetViewModel.getListTweets()!![iterator])
            intent.putExtra("user", tweetViewModel.getListTweets()!![iterator].user)
            this.startActivity(intent)

            true
        }
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true

                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    fun textVisible() {
        spLifeSpan?.visibility = View.VISIBLE
        tvLifespan?.visibility = View.VISIBLE
        btnSearch?.visibility = View.VISIBLE
        edSearch?.visibility = View.VISIBLE
        tvSearching?.visibility = View.GONE
        progressBar?.visibility = View.GONE
    }

    fun textInVisible() {
        edSearch?.hideKeyboard()
        spLifeSpan?.visibility = View.GONE
        tvLifespan?.visibility = View.GONE
        btnSearch?.visibility = View.GONE
        edSearch?.visibility = View.GONE
    }
/*
    //Activa el sistema de bindeo
    fun setupBinding(savedInstanceState: Bundle?){
        var activityMainBinding: ActivityMainBinding = DataBindingUtil.setContentView(this,R.layout.activity_maps)

        tweetViewModel = ViewModelProvider(this).get(TweetViewModel::class.java)

        activityMainBinding.setMyViewModel(tweetViewModel)  //set Nombre que hayamos puesto en el XML
        setupLoqueSea()
    }

    fun setupLoqueSea(){
        //Aquí lanzamos el Thread principal, que dejaremos en TweetViewModel

    }*/
}



