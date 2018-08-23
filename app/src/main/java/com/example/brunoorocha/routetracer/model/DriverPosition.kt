package com.example.brunoorocha.routetracer.model

class DriverPosition {

    var latitude: Double? = null
    var longitude: Double? = null
    var timestamp: Number? = null

    constructor(latitude: Double, longitude: Double, timestamp: Number) {
        this.latitude = latitude
        this.longitude = longitude
        this.timestamp = timestamp
    }
}