package com.ajmorales.tweetToMap.util

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.ajmorales.tweetToMap.model.Geo
import com.google.android.material.snackbar.Snackbar

class Util {

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

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

        return false
    }

  //Emulated coordinates (Tweeter Api free)
    fun getSimulatedLocations(): ArrayList<Geo> {

        val locationLits: ArrayList<Geo> = ArrayList<Geo>()

        locationLits.add(Geo("Málaga", listOf(36.72016, -4.42034)))
        locationLits.add(Geo("Mexico", listOf(19.4978, -99.1269)))
        locationLits.add(Geo("Londres", listOf(51.51279, -0.09184)))
        locationLits.add(Geo("Sydney", listOf(-33.862, 151.21)))
        locationLits.add(Geo("Tetuan", listOf(35.5784500, -5.3683700)))
        locationLits.add(Geo("Nueva York", listOf(40.73, -73.99)))
        locationLits.add(Geo("Madrid", listOf(40.4631, -3.6501)))
        locationLits.add(Geo("Toronto", listOf(43.70011, -79.4163)))
        locationLits.add(Geo("Granada ", listOf(37.18817, -3.60667)))
        locationLits.add(Geo("Buenos Aires", listOf(-34.61315, -58.37723)))
        locationLits.add(Geo("Bijing", listOf(39.9075, 116.39723)))
        locationLits.add(Geo("Santiago", listOf(-33.45694, -70.64827)))
        locationLits.add(Geo("Rome", listOf(41.89193, 12.51133)))
        locationLits.add(Geo("Rabat", listOf(34.01325, -6.83255)))
        locationLits.add(Geo("Paris", listOf(48.85341, 2.3488)))
        locationLits.add(Geo("Dubai", listOf(25.07725, 55.30927)))
        locationLits.add(Geo("Dublin", listOf(53.33306, -6.24889)))
        locationLits.add(Geo("Tel Aviv", listOf(32.08088, 34.78057)))
        locationLits.add(Geo("Manchester ", listOf(53.48095, -2.23743)))
        locationLits.add(Geo("Yaunde", listOf(3.86667, 11.51667)))
        locationLits.add(Geo("Tokyo", listOf(35.6895, 139.69171)))
        locationLits.add(Geo("Moscu", listOf(55.75222, 37.61556)))
        locationLits.add(Geo("Caracas", listOf(10.48801, -66.87919)))
        locationLits.add(Geo("Berlin", listOf(52.52437, 3.41053)))
        locationLits.add(Geo("New Delhi", listOf(28.63576, 77.22445)))
        locationLits.add(Geo("Vienna", listOf(48.20849, 16.37208)))
        locationLits.add(Geo("Argelia", listOf(28.033886, 1.659626)))
        locationLits.add(Geo("Quito", listOf(-0.22985, -78.52495)))
        locationLits.add(Geo("Lisbon ", listOf(38.71667, -9.13333)))
        locationLits.add(Geo("Helsinki", listOf(60.16952, 24.93545)))

        return locationLits
    }

    fun onSnack(view: View, message: String, lifeSpan: Long){
        //Snackbar(view)
        val snackbar = Snackbar.make(view, message, lifeSpan.toInt())

        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(Color.BLACK)

        val textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        textView.setTextColor(Color.RED)

        textView.textSize = 20f
        snackbar.show()
    }

    fun onToast(msg:String, context: Context){
        Toast.makeText(context,msg, Toast.LENGTH_SHORT).show()
    }

    fun hideKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}