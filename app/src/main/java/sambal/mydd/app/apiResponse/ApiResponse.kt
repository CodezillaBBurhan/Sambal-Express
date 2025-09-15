package sambal.mydd.app.apiResponse

import android.content.Context
import android.util.Log
import android.view.View
import sambal.mydd.app.MainActivity
import sambal.mydd.app.activity.NewNotification
import sambal.mydd.app.adapter.AdapterStorePoints
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.utils.*
import sambal.mydd.app.utils.AppUtil.isNetworkAvailable
import sambal.mydd.app.utils.AppConfig.api_Interface
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class ApiResponse {
    fun followAgent(
        context: Context?,
        agentId: String?,
        view: View?,
        responseListener: ResponseListener,
    ) {
        if (isNetworkAvailable(context)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(context, "", false, null)
            val call = api_Interface().updateFollowAgent(agentId)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>,
                ) {
                    if (response.isSuccessful) {
                        responseListener.onSuccess(response.body())
                        dialogManager.stopProcessDialog()
                    } else {
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert(view, MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    dialogManager.stopProcessDialog()
                    responseListener.onFailure(t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(view, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    fun deleteComment(
        context: Context,
        commentId: String,
        view: View?,
        responseListener: ResponseListener,
    ) {
        if (isNetworkAvailable(context)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(context, "", false, null)
            val call = api_Interface().deleteAgentRating(commentId)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>,
                ) {
                    if (response.isSuccessful) {
                        responseListener.onSuccess(response.body())
                        dialogManager.stopProcessDialog()
                    } else {
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert(view, MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    dialogManager.stopProcessDialog()
                    responseListener.onFailure(t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(view, MessageConstant.MESSAGE_SOMETHING_WRONG)
        }
    }

    fun getCharityList(
        context: Context,
        agentId: String?,
        view: View?,
        responseListener: ResponseListener,
    ) {
        if (isNetworkAvailable(context)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(context, "", false, null)
            val call = api_Interface().getCharityList(agentId)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>,
                ) {
                    if (response.isSuccessful) {
                        responseListener.onSuccess(response.body())
                        dialogManager.stopProcessDialog()
                    } else {
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert(view,
                            MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    dialogManager.stopProcessDialog()
                    responseListener.onFailure(t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(view, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    fun doCharity(
        context: Context,
        agentId: String?,
        charityId: String,
        view: View?,
        responseListener: ResponseListener,
    ) {
        if (isNetworkAvailable(context)) {

            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(context, "", false, null)

            Log.e("Agent", "$agentId,$charityId")
            val call = api_Interface().addMyCharity(agentId, charityId)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>,
                ) {
                    if (response.isSuccessful) {
                        responseListener.onSuccess(response.body())
                        dialogManager.stopProcessDialog()
                    } else {
                        try {
                            AdapterStorePoints.isChange = false
                        } catch (e: Exception) {
                        }
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert(view, MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    dialogManager.stopProcessDialog()
                    responseListener.onFailure(t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(view, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    fun getNotifications(
        context: Context,
        offset: String,
        count: String,
        responseListener: ResponseListener,
    ) {
        if (isNetworkAvailable(context)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(context, "", false, null)
            val call = api_Interface().getNotifications(
                MainActivity.userLat.toString(),
                MainActivity.userLang.toString(),
                NewNotification.agentId,
                offset,
                count
            )
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>,
                ) {
                    if (response.isSuccessful) {
                        responseListener.onSuccess(response.body())
                        dialogManager.stopProcessDialog()
                    } else {
                        dialogManager.stopProcessDialog()
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    dialogManager.stopProcessDialog()
                    responseListener.onFailure(t.message)
                }
            })
        } else {
            ErrorMessage.T(context, "No Internet Found!")
        }
    }

}