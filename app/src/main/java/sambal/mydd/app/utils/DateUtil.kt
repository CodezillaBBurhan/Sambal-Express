package sambal.mydd.app.utils

import android.util.Log
import java.lang.Exception
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtil {
    fun getLocalDate(serverDate: String?): String {
        var localDate: String
        try {
            val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm")
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            val value = formatter.parse(serverDate)
            formatter.timeZone = TimeZone.getDefault()
            localDate = formatter.format(value)
        } catch (e: Exception) {
            localDate = "00-00-0000 00:00"
        }
        return localDate
    }

    fun convertDateToMS(serverDate: String?): String {
        val localDate = getLocalDate(serverDate)
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm")
        try {
            val date = sdf.parse(localDate)
            val localMS = date.time
            val currentMS = System.currentTimeMillis()
            val difference = localMS - currentMS
            return convertSecondsToHMmSs(difference)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

    fun getDateOnlyFromServerDate(serverDate: String?): String {
        val localDate = getLocalDate(serverDate)
        val date = localDate.split(" ".toRegex()).toTypedArray()
        return date[0]
    }

    fun getTimeOnlyFromServerDate(serverDate: String?): String {
        val localDate = getLocalDate(serverDate)
        val date = localDate.split(" ".toRegex()).toTypedArray()
        return date[1]
    }

    fun convertSecondsToHMmSs(milisec: Long): String {
        val seconds = milisec / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        return if (days == 1L) {
            "$days DAY"
        } else if (days > 1) {
            "$days DAYS"
        } else {
            (hours % 24).toString() + ":" + minutes % 60
        }
    }

    fun getDifferenceBtwTime(startDate: String?, endDate: String?): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm")
        return try {
            val date1 = sdf.parse(startDate)
            val date2 = sdf.parse(endDate)
            val startDateMS = date1.time
            val endDateMS = date2.time
            val difference = endDateMS - startDateMS
            val time = convertMSToMinHourDay(difference)
            if (time.contains("-")) {
                //return "0mins";
                "0"
            } else {
                time
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            Log.e("getDifferenceBtwTime", e.toString() + "")
            "-"
        }
    }

    fun convertMSToMinHourDay(millis: Long): String {
        val seconds = millis / 1000
        var min = seconds / 60
        var hour = min / 60
        val days = hour / 24
        min = min % 60
        hour = hour % 24
        return if (days == 0L && min == 0L && hour == 0L) {
            "0mins"
        } else if (min == 0L && hour == 0L && days != 0L) {
            days.toString() + "days"
        } else if (min == 0L && hour != 0L && days != 0L) {
            days.toString() + "days" + " " + hour + "hours "
        } else if (min != 0L && hour != 0L && days != 0L) {
            days.toString() + "days" + " " + hour + "hours " + " " + min + "mins "
        } else if (min == 0L && days == 0L) {
            hour.toString() + "hours"
        } else if (hour == 0L && days == 0L) {
            min.toString() + "mins"
        } else if (days == 0L) {
            hour.toString() + "hours" + " " + min + "mins "
        } else {
            "0mins"
        }
    }

    fun convertMSToHourMinSec(millis: Long): String {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
            TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(
                millis)),
            TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(
                millis)))
    }

    fun getDay(dateStr: String): String {
        var dateStr = dateStr
        dateStr = getLocalDate(dateStr)
        val date = dateStr.split("-".toRegex()).toTypedArray()
        return date[0]
    }

    fun getMonth1(dateStr: String): String {
        var dateStr = dateStr
        dateStr = getLocalDate(dateStr)
        val monthArray = arrayOf("January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December")
        val date = dateStr.split("-".toRegex()).toTypedArray()
        val month = date[1].replace("0", "").toInt()
        return monthArray[month - 1]
    }

    fun getMonth(dateStr: String): String {
        var dateStr = dateStr
        dateStr = getLocalDate(dateStr)
        val monthArray = arrayOf("January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December")
        var date = dateStr.split("-".toRegex()).toTypedArray()
        //Integer month = Integer.parseInt(date[1].replace("0", ""));
        var month = date[1]
        date = month.split("\\s+".toRegex()).toTypedArray()
        if (date[0].equals("0", ignoreCase = true)) {
            month = date[1].replace("0", "")
        }
        val monthInt = month.toInt()
        return monthArray[monthInt - 1]
    }

    fun getMonthNum(dateStr: String): Int {
        var dateStr = dateStr
        dateStr = getLocalDate(dateStr)
        val date = dateStr.split("-".toRegex()).toTypedArray()
        val month = date[1].toInt()
        return month - 1
    }

    fun getYear(dateStr: String): String {
        var dateStr = dateStr
        dateStr = getLocalDate(dateStr)
        val date = dateStr.split("-".toRegex()).toTypedArray()
        return date[2]
    }

    fun getTime(dateStr: String): String {
        var dateStr = dateStr
        dateStr = getLocalDate(dateStr)
        val date = dateStr.split(" ".toRegex()).toTypedArray()
        return date[1]
    }

    fun getDateInGMT2(localDate: String?): String {
        var serverDate: String
        try {
            val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm")
            formatter.timeZone = TimeZone.getDefault()
            val value = formatter.parse(localDate)
            val utcFormatter = SimpleDateFormat("dd-MM-yyyy HH:mm")
            utcFormatter.timeZone = TimeZone.getTimeZone("GMT")
            serverDate = utcFormatter.format(value)
        } catch (e: Exception) {
            serverDate = "0000-00-00"
        }
        Log.d("date", "getDateInGMT : $serverDate")
        return serverDate
    }

    fun getDateInGMT(localDate: String?): String {
        var serverDate: String
        try {
            val formatter = SimpleDateFormat("dd-MM-yyyy")
            formatter.timeZone = TimeZone.getDefault()
            val value = formatter.parse(localDate)
            val utcFormatter = SimpleDateFormat("dd-MM-yyyy")
            utcFormatter.timeZone = TimeZone.getTimeZone("GMT")
            serverDate = utcFormatter.format(value)
        } catch (e: Exception) {
            serverDate = "0000-00-00"
        }
        Log.d("date", "getDateInGMT : $serverDate")
        return serverDate
    }

    fun getTimeInGMT(localDate: String?): String {
        var serverDate: String
        try {
            val formatter = SimpleDateFormat("HH:mm")
            formatter.timeZone = TimeZone.getDefault()
            val value = formatter.parse(localDate)
            val utcFormatter = SimpleDateFormat("HH:mm")
            utcFormatter.timeZone = TimeZone.getTimeZone("GMT")
            serverDate = utcFormatter.format(value)
        } catch (e: Exception) {
            serverDate = "00:00"
        }
        return serverDate
    }

    fun isEndDateGreater(startDate: String?, endDate: String?): Boolean {
        return try {
            val formatter = SimpleDateFormat("dd-MM-yyyy")
            val date1 = formatter.parse(startDate)
            val date2 = formatter.parse(endDate)
            if (date1.compareTo(date2) <= 0) {
                Log.d("date2", " is Greater than my date1")
                true
            } else {
                false
            }
        } catch (e1: ParseException) {
            e1.printStackTrace()
            false
        }
    }

    fun convertDateTimeFormat(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm")
        return sdf.format(Calendar.getInstance().time)
    }

    fun convertDateTimeWithSeconFormat(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        var format = sdf.format(Date())
        try {
            val mDate = sdf.parse(format)
            format = mDate.time.toString() + ""
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return format
    }

    fun getMilliSecFromDuration(duration: String): Long {
        val array = duration.split(":".toRegex()).toTypedArray()
        val time = Integer.valueOf(array[0])
        val min = Integer.valueOf(array[1])
        val sec = Integer.valueOf(array[2])
        return (((time * 60 + min) * 60 + sec) * 1000).toLong()
    }

    fun isTimeForGoLive(serverDate: String?): String {
        val localDate = getLocalDate(serverDate)
        val currentTime = Date().time
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm")
        return try {
            val date = sdf.parse(localDate)
            val localTime = date.time
            val difference = localTime - currentTime
            val time = convertMSToHourMinSec(difference)
            if (time.contains("-")) {
                "GOLIVE"
            } else {
                convertMSToHourMinSec(difference)
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            "GOLIVE"
        }
    }

    fun getTimerMiliSec(serverDate: String): String {
        var serverDate = serverDate
        return try {
            Log.e("Date", serverDate)
            serverDate = "$serverDate 23:59"
            val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val outputFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm")
            var date1: Date? = null
            date1 = inputFormat.parse(serverDate)
            val outputDateStr = outputFormat.format(date1)

            //outputDateStr = getLocalDate(outputDateStr);

            //String localDate = getLocalDate(outputDateStr);
            val currentTime = Date().time
            val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm")
            try {
                val date = sdf.parse(outputDateStr)
                val localTime = date.time
                val difference = localTime - currentTime
                val seconds = difference / 1000
                val minutes = seconds / 60
                val hours = minutes / 60
                val days = hours / 24
                if (days == 1L) {
                    "$days day left"
                } else if (days > 1) {
                    //days = days - 1;
                    "$days days left"
                } else {
                    val time = convertMSToHourMinSec(difference)
                    if (time.contains("-")) {
                        "00:00:00"
                    } else {
                        difference.toString()
                    }
                }
            } catch (e: ParseException) {
                e.printStackTrace()
                "00:00:00"
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            "00:00:00"
        }
    }

    fun getMillis(duration: String?): Date? {
        val sdf = SimpleDateFormat("HH:mm:ss")
        var milles: Date? = null
        try {
            milles = sdf.parse(duration)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return milles
    }

    fun UTCToLocal(date: String?): String? {
        var date = date
        Log.e("datw", date!!)
        try {
            val utcFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)
            utcFormat.timeZone = TimeZone.getTimeZone("UTC")
            val dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a")
            dateFormat.timeZone = TimeZone.getDefault()
            date = dateFormat.format(utcFormat.parse(date))
        } catch (e: Exception) {
        }
        return date
    }

    fun UTCToLocalReport(date: String?): String? {
        var date = date
        Log.e("datw", date!!)
        try {
            val utcFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            utcFormat.timeZone = TimeZone.getTimeZone("UTC")
            val dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a")
            dateFormat.timeZone = TimeZone.getDefault()
            date = dateFormat.format(utcFormat.parse(date))
        } catch (e: Exception) {
        }
        return date
    }

    @JvmStatic
    fun UTCToLocalSec(date: String?): String? {
        var date = date
        Log.e("date", date!!)
        try {
            val utcFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            utcFormat.timeZone = TimeZone.getTimeZone("UTC")
            val dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a")
            dateFormat.timeZone = TimeZone.getDefault()
            date = dateFormat.format(utcFormat.parse(date))
            Log.e("date1", date)
        } catch (e: Exception) {
        }
        return date
    }

    fun localToUTC(date: String?): String? {
        var date = date
        try {
            val localFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm")
            localFormat.timeZone = TimeZone.getDefault()
            val utcFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            utcFormat.timeZone = TimeZone.getTimeZone("UTC")
            date = utcFormat.format(localFormat.parse(date))
            Log.e("Date", date)
        } catch (e: Exception) {
        }
        return date
    }

    fun localToUTCTime(date: String): String {
        var date = date
        try {
            Log.e("DAteeeUTV", date + "")
            val utcFormat: DateFormat = SimpleDateFormat("dd MMM, yyyy hh:mm a", Locale.ENGLISH)
            utcFormat.timeZone = TimeZone.getTimeZone("UTC")
            val dateFormat: DateFormat = SimpleDateFormat("hh:mm a")
            dateFormat.timeZone = TimeZone.getDefault()
            date = dateFormat.format(utcFormat.parse(date))
            Log.e("date1", date)
        } catch (e: Exception) {
        }
        return date
    }
}