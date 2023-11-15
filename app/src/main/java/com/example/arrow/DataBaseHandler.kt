package com.example.sqlitedatabase

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.FileOutputStream

class DataBaseHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "Coordinates"
        // private const val TABLE_NAME = "coords"
    }

    init {
        // Check if the database exists in internal storage
        if (!checkDatabase(context)) {
            // If it doesn't exist, copy it from assets to internal storage
            copyDatabase(context)
        }
    }

    private fun checkDatabase(context: Context): Boolean {
        val dbFile = context.getDatabasePath(DATABASE_NAME)
        return dbFile.exists()
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Not implementing table creation as the table already exists in the database
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle the database upgrade if needed
    }

    private fun copyDatabase(context: Context) {
        val inputStream = context.assets.open(DATABASE_NAME)
        val outputStream = FileOutputStream(context.getDatabasePath(DATABASE_NAME))

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
    }
}