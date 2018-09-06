package com.example.brunoorocha.routetracer

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import com.example.brunoorocha.routetracer.config.EXTRA_FETCH_LOCATION
import com.example.brunoorocha.routetracer.interfaces.LocationHandlerDelegate
import com.example.brunoorocha.routetracer.provider.LocationContract
import com.example.brunoorocha.routetracer.provider.RTLocationProvider
import com.example.brunoorocha.routetracer.receiver.LocationReceiver
import com.example.brunoorocha.routetracer.service.UpdatedLocationService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationHandlerDelegate {

    private lateinit var mMap: GoogleMap
    private lateinit var mCurrentLocation: LatLng
    private lateinit var alarmManager: AlarmManager
    private lateinit var marker: MarkerOptions
    private lateinit var startTimeStamp: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun createALocationReceiver() {
        val locationReceiver = LocationReceiver()
        locationReceiver.delegate = this

        applicationContext.registerReceiver(locationReceiver, IntentFilter(EXTRA_FETCH_LOCATION))
    }

    private fun setAlarmManager() {
        val intent = Intent(EXTRA_FETCH_LOCATION)
        val pIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        this.alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // The AlarmManager forces up the intervalMillis param to 60000 (60 seconds)
        // to avoid that very frequent alarms drain the battery.
        this.alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, 1, 60000, pIntent)
    }

    private fun getLocationPoints(): MutableList<LatLng> {
        val uri = Uri.parse("content://" + LocationContract.CONTENT_AUTHORITY + "/location")

        val selection = LocationContract.LocationEntry.COLUMN_TIMESTAMP + " >= ?"
        val selectionArgs = Array(1) { i -> startTimeStamp }
        val cursor = contentResolver.query(uri, null, selection, selectionArgs, "timestamp")

        var locations: MutableList<LatLng> = mutableListOf()

        if (cursor.moveToFirst()) {
            do {
                val latitude = cursor.getDouble(cursor.getColumnIndex(LocationContract.LocationEntry.COLUMN_LATITUDE))
                val longitude = cursor.getDouble(cursor.getColumnIndex(LocationContract.LocationEntry.COLUMN_LONGITUDE))
                val location = LatLng(latitude, longitude)

                locations.add(location)

            } while (cursor.moveToNext())
        }

        return locations
    }

    fun didTapOnCallServiceButton(view: View) {
        this.startTimeStamp = DateFormat.format("yyyy-MM-dd hh:mm:ss", Date()).toString()

        startUpdatedLocationService()
        createALocationReceiver()
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

                this.mMap = googleMap

                this.marker = MarkerOptions().position(this.mCurrentLocation).title("You are here!")
                this.mMap.addMarker(this.marker)
                this.mMap.moveCamera(CameraUpdateFactory.newLatLng(this.mCurrentLocation))

                this.mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))

                this.mMap.setMaxZoomPreference(20.0f)
                this.mMap.setMinZoomPreference(15.0f)

                setAlarmManager()
            }
        }


    }

    override fun getUpdatedLocation() {
        val uri = Uri.parse("content://" + LocationContract.CONTENT_AUTHORITY + "/location")

        val selection = LocationContract.LocationEntry.COLUMN_TIMESTAMP + " >= ?"
        val selectionArgs = Array(1) { i -> startTimeStamp }
        val cursor = contentResolver.query(uri, null, selection, selectionArgs, null)

        var latitude: Double = 0.0
        var longitude: Double = 0.0
        var timestamp: String = ""

        if (cursor.moveToLast()) {

            latitude = cursor.getDouble(cursor.getColumnIndex(LocationContract.LocationEntry.COLUMN_LATITUDE))
            longitude = cursor.getDouble(cursor.getColumnIndex(LocationContract.LocationEntry.COLUMN_LONGITUDE))
            timestamp = cursor.getString(cursor.getColumnIndex(LocationContract.LocationEntry.COLUMN_TIMESTAMP))

        }

        this.mCurrentLocation = LatLng(latitude, longitude)
        this.marker = MarkerOptions().position(mCurrentLocation)

        Log.i("LOCATION", this.mCurrentLocation.toString())
        Log.i("TIMESTAMP", timestamp)

        this.mMap.clear()
        this.mMap.addMarker(this.marker)
        this.mMap.moveCamera(CameraUpdateFactory.newLatLng(this.mCurrentLocation))

        val locations = getLocationPoints()

        this.mMap.addPolyline(PolylineOptions().addAll(locations))
        Log.i("LOCATIONS", locations.count().toString())
    }

    override fun onDestroy() {
        super.onDestroy()

        stopAlarmManager()
        stopUpdatedLocationService()
    }

    private fun stopAlarmManager() {
        val intent = Intent(EXTRA_FETCH_LOCATION)
        val pIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

        this.alarmManager.cancel(pIntent)
    }
}
