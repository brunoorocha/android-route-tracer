package com.example.brunoorocha.routetracer.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri

class LocationContentProvider : ContentProvider() {

    private var db: SQLiteDatabase? = null
    private var PROJECTION_LOCATION: HashMap<String, String>? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun getType(uri: Uri): String? {
        val match = LocationDbHelper.uriMatcher.match(uri)

        when (match) {
            LocationDbHelper.LOCATIONS -> {
                return LocationContract.LocationEntry.CONTENT_LIST_TYPE
            }

            LocationDbHelper.LOCATION_ID -> {
                return LocationContract.LocationEntry.CONTENT_ITEM_TYPE
            }

            else -> {
                throw IllegalStateException("Unknow URI " + uri + " with match " + match)
            }
        }

    }

    override fun insert(uri: Uri, values: ContentValues): Uri? {
        val rowID = db?.insert(LocationContract.LocationEntry.TABLE_NAME, null, values)

        if (rowID != null && rowID > 0) {
            val _uri = ContentUris.withAppendedId(LocationContract.BASE_CONTENT_URI, rowID)
            context.contentResolver.notifyChange(_uri, null)

            return _uri
        }

        throw SQLException("Fail to add a register in content provider")
    }

    override fun onCreate(): Boolean {
        val dbHelper = LocationDbHelper(context)
        db = dbHelper.writableDatabase

        return (db == null)
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? {

        val db = LocationDbHelper(context).readableDatabase
        var cursor: Cursor
        val match = LocationDbHelper.uriMatcher.match(uri)
        val sqLiteQueryBuilder = SQLiteQueryBuilder()

        when (match) {

            LocationDbHelper.LOCATIONS -> {
                sqLiteQueryBuilder.setProjectionMap(PROJECTION_LOCATION)
            }

            LocationDbHelper.LOCATION_ID -> {
                sqLiteQueryBuilder.appendWhere(LocationContract.LocationEntry._ID + "=" + uri.pathSegments.get(1))
            }

            else -> {
                throw IllegalArgumentException("Cannot query unknow URI "+ uri)
            }

        }

        cursor = db.query(LocationContract.LocationEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
        return cursor
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<String>?): Int {
        return 0
    }
}
