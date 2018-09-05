package com.example.brunoorocha.routetracer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.example.brunoorocha.routetracer.interfaces.LocationHandlerDelegate
import com.example.brunoorocha.routetracer.provider.LocationContract

class LocationReceiver : BroadcastReceiver() {

    lateinit var delegate: LocationHandlerDelegate

    override fun onReceive(context: Context, intent: Intent) {
        this.delegate.getUpdatedLocation()
    }
}
