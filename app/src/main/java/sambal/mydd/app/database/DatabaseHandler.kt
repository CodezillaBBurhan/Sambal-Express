package sambal.mydd.app.database

import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase
import sambal.mydd.app.beans.LocationModel
import android.content.ContentValues
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import java.lang.Exception
import java.util.ArrayList

class DatabaseHandler(private val context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITE_AGENT)
        val CREATE_FAVOURITE_BRAND_TABLE = ("CREATE TABLE " + TABLE_FAVOURITE_AGENT + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_FAV_AGENT_ID + " TEXT" + ")")
        val CREATE_RECENT_LOCATION_TABLE =
            "CREATE TABLE " + TABLE_RECENT_LOCATION + "(" + KEY_LOC_ID + " INTEGER PRIMARY KEY," + KEY_LOC_NAME + " TEXT," + KEY_LOC_CITY_NAME + " TEXT," + KEY_LOC_DISTANCE + " TEXT," + KEY_LOC_LATITUDE + " TEXT," + KEY_LOC_LONGITUDE + " TEXT" + ")"
        db.execSQL(CREATE_FAVOURITE_BRAND_TABLE)
        db.execSQL(CREATE_RECENT_LOCATION_TABLE)
        Log.e("TABLE CREATED", "SUCCESSFULLY")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITE_AGENT)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECENT_LOCATION)

        // Create tables again
        onCreate(db)
    }


    fun addFavLocation(model: LocationModel): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_LOC_NAME, model.locationName)
        values.put(KEY_LOC_CITY_NAME, model.cityName)
        values.put(KEY_LOC_DISTANCE, model.distance)
        values.put(KEY_LOC_LATITUDE, model.latitude)
        values.put(KEY_LOC_LONGITUDE, model.longitude)
        val i = db.insert(TABLE_RECENT_LOCATION, null, values)
        db.close()
        if (i == 1L) {
            //Toast.makeText(context, "Data inserted Successfully", Toast.LENGTH_SHORT).show();
            Log.e("INSERTED SUCCESSFULLY", "")
        } else if (i == -1L) {
            //Toast.makeText(context, "Data insertion Failed", Toast.LENGTH_SHORT).show();
            Log.e("NOT INSERTED", "FAILED")
        }
        return i
    }

    @get:SuppressLint("Range")
    val favLocationList: List<LocationModel>
        get() {
            val locList: MutableList<LocationModel> = ArrayList()
            val query = "SELECT * FROM " + TABLE_RECENT_LOCATION
            val db = this.writableDatabase
            val cursor = db.rawQuery(query, null)
            if (cursor != null) {
                if (cursor.count > 0) {
                    if (cursor.moveToFirst()) {
                        do {
                            try {
                                val model = LocationModel()
                                model.locationName =
                                    cursor.getString(cursor.getColumnIndex(KEY_LOC_NAME))
                                model.cityName = cursor.getString(cursor.getColumnIndex(
                                    KEY_LOC_CITY_NAME))
                                model.distance =
                                    cursor.getString(cursor.getColumnIndex(KEY_LOC_DISTANCE))
                                model.latitude =
                                    cursor.getString(cursor.getColumnIndex(KEY_LOC_LATITUDE))
                                model.longitude = cursor.getString(cursor.getColumnIndex(
                                    KEY_LOC_LONGITUDE))
                                locList.add(model)
                            } catch (e: Exception) {
                                Log.e("get data exp", e.toString() + "")
                            }
                        } while (cursor.moveToNext())
                    }
                }
                cursor.close()
            }
            db.close()
            return locList
        }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "MeasureXDb"
        private const val TABLE_FAVOURITE_AGENT = "favourite_agent"
        private const val TABLE_RECENT_LOCATION = "recent_location"

        //TODO TABLE_FAVOURITE_BRAND Columns names
        private const val KEY_ID = "id"
        private const val KEY_FAV_AGENT_ID = "fav_agent_id"

        //TODO TABLE_RECENT_LOCATION Columns names
        private const val KEY_LOC_ID = "id"
        private const val KEY_LOC_NAME = "location_name"
        private const val KEY_LOC_CITY_NAME = "city_name"
        private const val KEY_LOC_DISTANCE = "distance"
        private const val KEY_LOC_LATITUDE = "latitude"
        private const val KEY_LOC_LONGITUDE = "longitude"
    }

}