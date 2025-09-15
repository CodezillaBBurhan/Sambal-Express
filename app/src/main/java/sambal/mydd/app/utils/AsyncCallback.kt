package sambal.mydd.app.utils

interface AsyncCallback {
    fun setResponse(responseCode: Int?, responseStr: String?)
    fun setException(e: String?)
}