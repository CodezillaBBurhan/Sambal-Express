package sambal.mydd.app.utils

import okhttp3.ResponseBody

interface ResponseListener {
    fun onSuccess(response: ResponseBody?)
    fun onFailure(text: String?)
}