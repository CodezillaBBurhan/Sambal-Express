package sambal.mydd.app.utils

import android.content.Context
import android.preference.PreferenceManager

object SharedPreferenceVariable {
    var amount: String? = null
    fun loadSavedPreferences(context: Context?, key: String?): String? {
        val sp = PreferenceManager
            .getDefaultSharedPreferences(context)
        val et = sp.edit()
        et.commit()
        amount = sp.getString(key, "")
        return amount
    }

    fun savePreferences(context: Context?, key: String?, value: String?) {
        if (context != null) {
            val sp = PreferenceManager
                .getDefaultSharedPreferences(context)
            val edit = sp.edit()
            edit.putString(key, value)
            edit.commit()
        }
    }

    fun deletePreferenceData(context: Context?, key: String?) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sp.edit()
        editor.remove(key)
        editor.commit()
    }

    @JvmStatic
    fun ClearSharePref(context: Context?) {
      /*  val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sp.edit()
        editor.clear()
        editor.commit()*/


        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val allEntries = sp.all // Get all entries in SharedPreferences
        val editor = sp.edit()

        for (entry in allEntries) {
            ErrorMessage.E("SavedData<><><>"+entry.key +"<><><>"+SavedData.countNearByDevicePermission)
            if (entry.key != SavedData.countNearByDevicePermission) {
                editor.remove(entry.key) // Remove all entries except the one to keep
            }
        }

        editor.commit() // Apply the changes
    }
}