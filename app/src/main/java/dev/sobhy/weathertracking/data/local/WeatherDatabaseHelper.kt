package dev.sobhy.weathertracking.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import dev.sobhy.weathertracking.helper.Constant.FORECAST_TABLE
import dev.sobhy.weathertracking.helper.Constant.TODAY_WEATHER_TABLE

class WeatherDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "weather.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS $TODAY_WEATHER_TABLE (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "current_hour TEXT," +
                    "min_temp REAL," +
                    "max_temp REAL," +
                    "hours_json TEXT," +
                    "timestamp INTEGER)"
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $FORECAST_TABLE (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                forecast_json TEXT,
                timestamp INTEGER
            )
            """
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int,
    ) {
        db.execSQL("DROP TABLE IF EXISTS $TODAY_WEATHER_TABLE")
        db.execSQL("DROP TABLE IF EXISTS $FORECAST_TABLE")
        onCreate(db)
    }
}