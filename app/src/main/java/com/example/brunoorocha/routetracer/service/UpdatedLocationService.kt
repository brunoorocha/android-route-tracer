package com.example.brunoorocha.routetracer.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task

class UpdatedLocationService : Service() {

    private var location: Location? = null
    private val TAG = "UpdatedLocationService"

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "Service started!")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    if (location != null) {
                        Log.i(TAG, location.toString())
                    }
                }
            }
        }

        createLocationRequest()
        startLocationUpdates()

        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (checkManifestPermissions()) {
            this.fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    private fun checkManifestPermissions(): Boolean {
        return ((ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                && (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED))
    }

    fun createLocationRequest() {

        this.locationRequest = LocationRequest().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
    }

    override fun onDestroy() {
        super.onDestroy()

        this.fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.i(TAG, "Service Destroyed!")
    }

}
