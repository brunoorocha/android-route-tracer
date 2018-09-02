package com.example.brunoorocha.routetracer

import android.app.PendingIntent
import android.content.Intent
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.brunoorocha.routetracer.config.EXTRA_DEVICE_POSITION_LATITUDE
import com.example.brunoorocha.routetracer.config.EXTRA_DEVICE_POSITION_LONGITUDE
import com.example.brunoorocha.routetracer.provider.RTLocationProvider
import com.example.brunoorocha.routetracer.service.UpdatedLocationService

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mCurrentLocation: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {

        RTLocationProvider.getLocation(this){ location: Location? ->
            if (location != null) {
                var latitude: Double = location.latitude
                var longitude: Double = location.longitude

                this.mCurrentLocation = LatLng(latitude, longitude)

                mMap = googleMap

                mMap.addMarker(MarkerOptions().position(this.mCurrentLocation).title("You are here!"))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(this.mCurrentLocation))

                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))

                mMap.setMaxZoomPreference(20.0f)
                mMap.setMinZoomPreference(15.0f)
            }
        }

    }

    fun didTapOnStartRunningButton(view: View) {
        startUpdatedLocationService()
    }

    private fun startUpdatedLocationService() {
        val intent = Intent(this, UpdatedLocationService::class.java)
        startService(intent)

    }

    private fun getPendingIntent(intent: Intent): PendingIntent {
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun stopUpdatedLocationService() {
        val intent = Intent(this, UpdatedLocationService::class.java)
        stopService(intent)
    }

    override fun onDestroy() {
        super.onDestroy()

        stopUpdatedLocationService()
    }
}
