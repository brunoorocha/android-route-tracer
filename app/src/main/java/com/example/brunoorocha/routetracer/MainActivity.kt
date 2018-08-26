package com.example.brunoorocha.routetracer

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.support.v4.content.ContextCompat
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.TextView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mCurrentLocation: Location? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val latitudeTextView = findViewById<TextView>(R.id.latitudeTextView)
        val longitudeTextView = findViewById<TextView>(R.id.longitudeTextView)

        if (checkLocationPermission()) {
            getLocation { location: Location? ->
                if (location != null) {
                    latitudeTextView.text = String.format("Latitude: ${location.latitude}")
                    longitudeTextView.text = String.format("Latitude: ${location.longitude}")
                }
            }
        }
    }

    fun didTapShowMapButton(button: View) {
        val mapIntent = Intent(this, MapsActivity::class.java)

        startActivity(mapIntent)
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(completion: (Location?) -> Unit) {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->

            completion(location)

        }
    }

    private fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder(this)
                        .setTitle("Location permission")
                        .setMessage("You need the location permission to some things to work!")
                        .setPositiveButton("OK", DialogInterface.OnClickListener{ dialogInterface, i ->
                            ActivityCompat.requestPermissions(this@MainActivity,
                                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                    100)
                        })
                        .create()
                        .show()
            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        100)
            }

            return false

        } else {
            return true
        }
    }

}
