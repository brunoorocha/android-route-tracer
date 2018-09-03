package com.example.brunoorocha.routetracer

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.example.brunoorocha.routetracer.provider.LocationContract
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

        showAllLocationsOnDatabase()
    }

    private fun showAllLocationsOnDatabase() {
        val uri = Uri.parse("content://" + LocationContract.CONTENT_AUTHORITY + "/location")
        val cursor = contentResolver.query(uri, null, null, null, "timestamp")

        if (cursor.moveToFirst()) {
            do {
                val description = "_ID: " + cursor.getString(cursor.getColumnIndex(LocationContract.LocationEntry._ID)) +
                        " | LATITUDE: " + cursor.getString(cursor.getColumnIndex(LocationContract.LocationEntry.COLUMN_LATITUDE)) +
                        " | LONGITUDE: " + cursor.getString(cursor.getColumnIndex(LocationContract.LocationEntry.COLUMN_LONGITUDE))

                Log.i("DB_RESULT", description)
            } while (cursor.moveToNext())
        }
    }

    fun didTapOnCallServiceButton(view: View) {
        startUpdatedLocationService()
    }

    private fun startUpdatedLocationService() {
        val intent = Intent(this, UpdatedLocationService::class.java)

        startService(intent)
    }

    private fun stopUpdatedLocationService() {
        val intent = Intent(this, UpdatedLocationService::class.java)

        stopService(intent)
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

    override fun onDestroy() {
        super.onDestroy()

        stopUpdatedLocationService()
    }
}
