package com.example.brunoorocha.routetracer.provider

import android.content.ContentResolver
import android.content.Context
import android.content.UriMatcher
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.provider.BaseColumns


object LocationContract {
    val CONTENT_AUTHORITY = "com.example.brunoorocha.routetracer.location"
    val BASE_CONTENT_URI= Uri.parse("content://"+ CONTENT_AUTHORITY)

    val PATH_LOCATION = "location"

    object LocationEntry: BaseColumns {
        val CONTENT_URI = Uri.withAppendedPath(LocationContract.BASE_CONTENT_URI, LocationContract.PATH_LOCATION)

        val CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + LocationContract.CONTENT_AUTHORITY + "/" + LocationContract.PATH_LOCATION
        val CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + LocationContract.CONTENT_AUTHORITY + "/" + LocationContract.PATH_LOCATION

        val TABLE_NAME = "driver_location"

        val _ID = BaseColumns._ID
        val COLUMN_LATITUDE = "latitude"
        val COLUMN_LONGITUDE = "longitude"
        val COLUMN_TIMESTAMP = "timestamp"

        val SQL_CREATE_ENTIRIES = "CREATE TABLE " + LocationEntry.TABLE_NAME + "(" +
                LocationEntry._ID + " INTEGER PRIMARY KEY, " +
                LocationEntry.COLUMN_LATITUDE + " DOUBLE NOT NULL, " +
                LocationEntry.COLUMN_LONGITUDE + " DOUBLE NOT NULL, " +
                LocationEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP);"

        val SQL_DELETE_ENTIRIES = "DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME
    }

}


class LocationDbHelper: SQLiteOpenHelper {
    companion object {
        val DB_NAME = "routetracer"
        val VERSION = 1

        val LOCATIONS = 1
        val LOCATION_ID = 2

        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {

            this.addURI(LocationContract.CONTENT_AUTHORITY, LocationContract.PATH_LOCATION, LOCATIONS)
            this.addURI(LocationContract.CONTENT_AUTHORITY, LocationContract.PATH_LOCATION + "/#", LOCATION_ID)
        }
    }

    constructor(context: Context) : super(context, DB_NAME, null, VERSION)

    override fun onCreate(db: SQLiteDatabase?) {
        if (db != null) {
            db.execSQL(LocationContract.LocationEntry.SQL_CREATE_ENTIRIES)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        if (db != null) {
            db.execSQL(LocationContract.LocationEntry.SQL_DELETE_ENTIRIES)
            onCreate(db)
        }
    }

}