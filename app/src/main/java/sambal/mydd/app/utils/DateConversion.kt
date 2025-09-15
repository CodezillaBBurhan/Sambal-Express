package sambal.mydd.app.utils

import android.util.Log
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object DateConversion {
    fun local(date: String?): String? {
        var date = date
        try {
            val localFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            Log.e("sas", date!!)
            val utcFormat: DateFormat = SimpleDateFormat("MMM dd,yyyy")
            date = utcFormat.format(localFormat.parse(date))
            Log.e("Dates", date)
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
        }
        return date
    }

    fun localToUTCWithAM_PM(date: String?): String? {
        var date = date
        try {
            val localFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val utcFormat: DateFormat = SimpleDateFormat("dd-mm-yyyy hh:mm:ss:a")
            utcFormat.timeZone = TimeZone.getTimeZone("UTC")
            date = utcFormat.format(localFormat.parse(date))
        } catch (e: Exception) {
        }
        return date
    }

    fun Datechangeformat(date: String?): String? {
        var date = date
        Log.e("datw", date!!)
        try {
            val utcFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            val dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a")
            date = dateFormat.format(utcFormat.parse(date))
        } catch (e: Exception) {
        }
        return date
    }
}