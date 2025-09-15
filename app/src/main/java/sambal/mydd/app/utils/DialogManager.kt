package sambal.mydd.app.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AlertDialog
import java.lang.Exception

class DialogManager {
   /* var mProgressHUD: ProgressHUD? = null*/
    private var materialDialog: Dialog? = null
    fun showProcessDialog(
        context: Context?,
        title: String?,
        visible: Boolean,
        cancelButtonListener: DialogInteractionListener?
    ) {
        try {
//            (((context as Activity)).runOnUiThread {
            /*if (context is Activity && !context.isFinishing) {

                try {
                    dialog = 1
                   
                    mProgressHUD = ProgressHUD.show(context, title, visible, true, false,
                        { mProgressHUD!!.dismiss() }, cancelButtonListener)
                    mProgressHUD!!.setCanceledOnTouchOutside(false)
                    mProgressHUD!!.setCancelable(false)
                } catch (e: Exception) {
                    dialog = 0
                } finally {
                    
                }
            }*/
             materialDialog = ErrorMessage.initProgressDialog(context)
        } catch (e: Exception) {
            ErrorMessage.E("showProcessDialog>>>"+e.toString())
        }
    }

    
    fun stopProcessDialog() {
        
        try {
            dialog = 0
           /* if (mProgressHUD != null && mProgressHUD!!.isShowing) {
                mProgressHUD!!.dismiss()
                mProgressHUD!!.cancel()
            }*/
            if(materialDialog!=null && materialDialog!!.isShowing){
                materialDialog!!.dismiss();
            }
        } catch (e: Exception) {
            ErrorMessage.E("stopProcessDialog>>>"+e.toString())
        }
    }

    //calander
    interface DialogInteractionListener {
        fun onCancel()
    }

    companion object {
        var dialog = 0
        fun exitDialog(context: Activity) {
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle("Alert")
            alertDialogBuilder.setMessage("Are you sure want to exit?")
                .setCancelable(false)
                .setPositiveButton("Exit"
                ) { dialog, id -> context.finish() }
            alertDialogBuilder.setNegativeButton("Cancel"
            ) { dialog, id -> dialog.cancel() }
            val alert = alertDialogBuilder.create()
            alert.setCancelable(false)
            alert.show()
        }

        fun dobDialog(context: Activity) {
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle("Alert")
            alertDialogBuilder.setMessage("Date of Birth ")
                .setCancelable(false)
                .setPositiveButton("Exit"
                ) { dialog, id -> context.finish() }
            alertDialogBuilder.setNegativeButton("Cancel"
            ) { dialog, id -> dialog.cancel() }
            val alert = alertDialogBuilder.create()
            alert.setCancelable(false)
            alert.show()
        }
    }
}