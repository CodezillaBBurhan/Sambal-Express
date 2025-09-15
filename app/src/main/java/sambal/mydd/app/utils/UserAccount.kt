package sambal.mydd.app.utils

import android.content.Context
import android.text.InputFilter
import android.widget.EditText
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.text.TextUtils
import android.util.Patterns

class UserAccount(
    private val mCont: Context,
    private val userName: EditText,
    private val password: EditText
) {
    init {
        isLoginInit(userName, password)
    }

    companion object {
        //for EditText Refrance
        var EditTextPointer: EditText? = null
        var errorMessage: String? = null
        private fun isLoginInit(userName: EditText, password: EditText) {
            val maxLength = 10
            val fArray = arrayOfNulls<InputFilter>(1)
            fArray[0] = LengthFilter(maxLength)
            //this is for userName
            userName.hint = "Enter Email / Contact No"
            userName.isSingleLine = true
            userName.maxLines = 1
            password.hint = "Enter Passwrod"
            password.isSingleLine = true
            password.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            password.maxLines = 1
            password.filters = fArray
        }

        fun isEmailValid(tv: EditText): Boolean {
            return if (TextUtils.isEmpty(tv.text)) {
                EditTextPointer = tv
                errorMessage = "This field can't be empty.!"
                false
            } else {
                if (Patterns.EMAIL_ADDRESS.matcher(tv.text).matches()) {
                    true
                } else {
                    EditTextPointer = tv
                    errorMessage = "Invalid Email Id"
                    false
                }
            }
        }

        fun isPhoneNumberLength(tv: EditText): Boolean {
            return if (tv.text.toString().length == 10) {
                true
            } else {
                EditTextPointer = tv
                errorMessage = "Enter 10 digits number"
                false
            }
        }

        fun isSaudiPhoneNumberLength(tv: EditText): Boolean {
            return if (tv.text.toString().length == 9) {
                true
            } else {
                EditTextPointer = tv
                errorMessage = "Enter 10 digits number"
                false
            }
        }

        fun isPasswordLength(tv: EditText): Boolean {
            //add your own logic
            return if (tv.text.toString().length >= 6) {
                true
            } else {
                EditTextPointer = tv
                errorMessage = "Enter 6 digits number"
                false
            }
        }

        fun isPIN(tv: EditText): Boolean {
            //add your own logic
            return if (tv.text.toString().length == 16) {
                true
            } else {
                EditTextPointer = tv
                errorMessage = "Enter 16 digits number"
                false
            }
        }

        fun isValidPhoneNumber(tv: EditText): Boolean {
            return if (tv.text == null || TextUtils.isEmpty(tv.text)) {
                false
            } else {
                if (Patterns.PHONE.matcher(tv.text).matches()) {
                    true
                } else {
                    EditTextPointer = tv
                    errorMessage = "Invalid Mobile No."
                    false
                }
            }
        }

        fun isEmpty(vararg arg: EditText): Boolean {
            for (i in arg.indices) {
                if (arg[i].text.length <= 0) {
                    EditTextPointer = arg[i]
                    EditTextPointer!!.requestFocus()
                    return false
                }
            }
            return true
        } /* public static boolean validateAadharNumber(String aadharNumber) {
        Pattern aadharPattern = Pattern.compile("\\d{11}");
        boolean isValidAadhar = aadharPattern.matcher(aadharNumber).matches();
        if (isValidAadhar) {
            isValidAadhar = Verhoeff.validateVerhoeff(aadharNumber);
        }
        return isValidAadhar;
    }*/
    }
}