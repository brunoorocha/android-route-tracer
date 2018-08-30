package com.example.brunoorocha.routetracer.provider

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


object RTLocationProvider {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mCurrentLocation: Location

    @SuppressLint("MissingPermission")
    fun getLocation(context: Activity, completion: (Location?) -> Unit) {
        if (checkLocationPermission(context)) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->

                completion(location)

            }
        }
    }

    private fun checkLocationPermission(context: Activity): Boolean {
        if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder(context)
                        .setTitle("Location permission")
                        .setMessage("You need the location permission to some things to work!")
                        .setPositiveButton("OK", DialogInterface.OnClickListener{ dialogInterface, i ->
                            ActivityCompat.requestPermissions(context,
                                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                    100)
                        })
                        .create()
                        .show()
            } else {
                ActivityCompat.requestPermissions(context,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        100)
            }

            return false

        } else {
            return true
        }
    }
}
