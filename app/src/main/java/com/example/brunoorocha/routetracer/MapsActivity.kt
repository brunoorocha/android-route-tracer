package com.example.brunoorocha.routetracer

import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.brunoorocha.routetracer.utils.EXTRA_DEVICE_POSITION_LATITUDE
import com.example.brunoorocha.routetracer.utils.EXTRA_DEVICE_POSITION_LONGITUDE

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

        var latitude: Double = intent.getDoubleExtra(EXTRA_DEVICE_POSITION_LATITUDE, 0.0)
        var longitude: Double = intent.getDoubleExtra(EXTRA_DEVICE_POSITION_LONGITUDE, 0.0)

        this.mCurrentLocation = LatLng(latitude, longitude)

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
        mMap = googleMap

        mMap.addMarker(MarkerOptions().position(this.mCurrentLocation).title("You are here!"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(this.mCurrentLocation))

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))

        mMap.setMaxZoomPreference(20.0f)
        mMap.setMinZoomPreference(15.0f)
    }
}
