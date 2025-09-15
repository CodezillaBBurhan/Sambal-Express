package sambal.mydd.app.activity

import sambal.mydd.app.beans.CountryDao
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.constant.MessageConstant
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONException
import android.app.Activity
import sambal.mydd.app.adapter.CountryDialogAdapter
import sambal.mydd.app.callback.CountryCallback
import android.widget.AdapterView.OnItemClickListener
import android.text.TextWatcher
import android.text.Editable
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.AppConfig
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.utils.ErrorMessage
import android.text.TextUtils
import sambal.mydd.app.beans.UpdateUserNewDataModel
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import sambal.mydd.app.MainActivity
import sambal.mydd.app.R
import sambal.mydd.app.databinding.EditprofileBinding
import sambal.mydd.app.utils.StatusBarcolor
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception
import java.nio.charset.StandardCharsets
import java.util.*

class EditProfile() : BaseActivity(), View.OnClickListener {
    private var binding: EditprofileBinding? = null
    private var mCountryListFinal: ArrayList<CountryDao>? = null
    var gender = ""
    var dob = ""
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private var userCountryCode = 44
    private var countryCodeKey = "228"
    private var selectedCountry = "United Kingdom"

    override val contentResId: Int
        get() = R.layout.editprofile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.editprofile)
        setToolbarWithBackButton_colorprimary("Edit Profile")
        initWidgets()
        if (!AppUtil.isNetworkAvailable(this@EditProfile)) {
            AppUtil.showMsgAlert(binding!!.exMobile, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        } else {
            myProfile
        }
    }

    private fun initWidgets() {
        exCountry = findViewById(R.id.exCountry)
        //spinner_country = findViewById(R.id.spinner_country);
        binding!!.btnSave.setOnClickListener(this)
        binding!!.exDOB.setOnClickListener(this)
        binding!!.rb.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, i -> // find the radio button by returned id
            val selectedId = binding!!.rb.checkedRadioButtonId
            val radioButton = findViewById<RadioButton>(selectedId)
            gender = radioButton.text.toString().trim { it <= ' ' }
        })
        jsonParser()
    }

    private fun jsonParser() {
        mCountryListFinal = ArrayList()
        try {
            var jArray: JSONArray? = null
            jArray = JSONArray(loadJSONFromAsset())
            for (i in 0 until jArray.length()) {
                val jObjects = jArray.getJSONObject(i)
                mCountryListFinal!!.add(CountryDao(jObjects))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun loadJSONFromAsset(): String? {
        var json: String? = null
        try {
            val `is` = application.assets.open("country.json")
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            json = String(buffer, StandardCharsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnSave -> if (checkValidation()) {
                if (!AppUtil.isNetworkAvailable(this@EditProfile)) {
                    AppUtil.showMsgAlert(binding!!.exMobile,
                        MessageConstant.MESSAGE_INTERNET_CONNECTION)
                } else {
                    updateProfile()
                }
            }
            R.id.exCountry ->                 // CountryCodeDialog.openCountryCodeDialog(spinner_country);
                showDialog(this@EditProfile, loadCountryJSONFromAsset())
            R.id.exDOB -> setDate(binding!!.exDOB)
        }
    }

    private fun showDialog(activity: Activity, list: JSONArray?) {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_country)
        val searchText = dialog.findViewById<EditText>(R.id.country_dialog_search_text)
        val searchBtn = dialog.findViewById<ImageButton>(R.id.country_dialog_search_btn)
        searchBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                //requestFocus(searchText);
            }
        })
        val listView = dialog.findViewById<ListView>(R.id.country_dialog_list_view)
        val adapter = CountryDialogAdapter(this@EditProfile, list!!, object : CountryCallback {
            override fun setItemList(position: Int, jsonArray: JSONArray) {
                userCountryCode =
                    jsonArray.optJSONObject(position).optString("country_code").toInt()
                countryCodeKey =
                    jsonArray.optJSONObject(position).optString("id").toInt().toString() + ""
                selectedCountry = jsonArray.optJSONObject(position).optString("country_name")
                exCountry!!.setText(selectedCountry)
                dialog.dismiss()
            }
        })
        listView.adapter = adapter
        dialog.show()
        listView.onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {}
        }
        searchText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun loadCountryJSONFromAsset(): JSONArray? {
        var json: String? = null
        var countryArray: JSONArray? = null
        try {
            val `is` = application.assets.open("country.json")
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            json = String(buffer, StandardCharsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        if (json != null) {
            try {
                countryArray = JSONArray(json)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return countryArray
    }

    private fun updateProfile() {
        if (AppUtil.isNetworkAvailable(this)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", false, null)
            val call = AppConfig.api_Interface()
                .updateProfileV1(updateUserDataObject().mobileNumber,
                    updateUserDataObject().name,
                    updateUserDataObject().lastName,
                    updateUserDataObject().email,
                    updateUserDataObject().doorNumber,
                    updateUserDataObject().streetName,
                    updateUserDataObject().city,
                    updateUserDataObject().postCode,
                    updateUserDataObject().country,
                    "",
                    updateUserDataObject().dob)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("ResponseUpdate", resp.toString() + "")
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            val message = resp.optString(KeyConstant.KEY_MESSAGE)
                            if ((errorType == KeyConstant.KEY_RESPONSE_CODE_201)) {
                                try {
                                    val responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                    runOnUiThread(object : Runnable {
                                        override fun run() {
                                            dialogManager.stopProcessDialog()
                                            AppUtil.showMsgAlert(binding!!.exMobile, message)
                                            finish()
                                        }
                                    })
                                } catch (e: Exception) {
                                    dialogManager.stopProcessDialog()
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true)
                                ) {
                                    dialogManager.stopProcessDialog()
                                    AppUtil.showMsgAlert(binding!!.exMobile,
                                        resp.optString(KeyConstant.KEY_MESSAGE))
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(binding!!.exMobile,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(binding!!.exMobile,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(binding!!.exCityName, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(binding!!.exMobile, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    private fun checkValidation(): Boolean {
        if (TextUtils.isEmpty(binding!!.exFirstName.text.toString().trim { it <= ' ' })) {
            AppUtil.showMsgAlert(binding!!.exMobile, "Enter first name")
            return false
        } else if (TextUtils.isEmpty(binding!!.exMobile.text.toString().trim { it <= ' ' })) {
            AppUtil.showMsgAlert(binding!!.exMobile, "Enter mobile number")
            return false
        }
        return true
    }

    private fun updateUserDataObject(): UpdateUserNewDataModel {
        val model = UpdateUserNewDataModel()
        model.name = binding!!.exFirstName.text.toString().trim { it <= ' ' }
        model.lastName = binding!!.exLastName.text.toString().trim { it <= ' ' }
        model.email = binding!!.exEmailId.text.toString().trim { it <= ' ' }
        model.doorNumber = binding!!.exFlatNo.text.toString().trim { it <= ' ' }
        model.streetName = binding!!.exStreetName.text.toString().trim { it <= ' ' }
        model.city = binding!!.exCityName.text.toString().trim { it <= ' ' }
        model.postCode = binding!!.exPostcode.text.toString().trim { it <= ' ' }
        model.country = countryCodeKey
        model.mobileNumber = binding!!.exMobile.text.toString()
        model.dob = dob
        model.gender = gender
        Log.e("Couss", countryCode)
        return model
    }

    private fun setDate(ex: EditText) {
        val c = Calendar.getInstance()
        mYear = c[Calendar.YEAR]
        mMonth = c[Calendar.MONTH]
        mDay = c[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(this, R.style.DialogTheme,
            object : OnDateSetListener {
                override fun onDateSet(
                    view: DatePicker, year: Int,
                    monthOfYear: Int, dayOfMonth: Int
                ) {
                    ex.setText(String.format("%02d", dayOfMonth) + "-" + String.format("%02d",
                        (monthOfYear + 1)) + "-" + year)
                    dob = year.toString() + "-" + String.format("%02d",
                        (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth)
                    Log.e("dob", dob)
                }
            }, mYear, mMonth, mDay)
        datePickerDialog.show()
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
    }

    private val myProfile: Unit
        private get() {
            if (AppUtil.isNetworkAvailable(this)) {
                val dialogManager = DialogManager()
                dialogManager.showProcessDialog(this@EditProfile, "", false, null)
                val lat = MainActivity.userLat.toString() + ""
                val lng = MainActivity.userLang.toString() + ""
                val call = AppConfig.api_Interface().getMyProfileV1(lat, lng)
                call!!.enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(
                        call: Call<ResponseBody?>,
                        response: Response<ResponseBody?>
                    ) {
                        if (response.isSuccessful) {
                            try {
                                dialogManager.stopProcessDialog()
                                val resp = JSONObject(response.body()!!.string())
                                Log.e("ProfileResponse", resp.toString())
                                val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                                if (errorType.equals(KeyConstant.KEY_RESPONSE_CODE_200,
                                        ignoreCase = true)
                                ) {
                                    val responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                    val arrDetails = responseObj.optJSONArray("userDetails")
                                    for (i in 0 until arrDetails.length()) {
                                        val ob = arrDetails.optJSONObject(i)
                                        runOnUiThread(object : Runnable {
                                            override fun run() {
                                                binding!!.exFirstName.setText(ob.optString("userName"))
                                                binding!!.exLastName.setText(ob.optString("userLastName"))
                                                binding!!.exEmailId.setText(ob.optString("userEmail"))
                                                binding!!.exMobile.setText(ob.optString("userMobile"))
                                                exCountry!!.setText(ob.optString("userCountry"))
                                                countryCode = ob.optString("userCountryId")
                                                binding!!.exFlatNo.setText(ob.optString("userDoorNumber"))
                                                binding!!.exStreetName.setText(ob.optString("userStreetName"))
                                                binding!!.exCityName.setText(ob.optString("userCity"))
                                                binding!!.exPostcode.setText(ob.optString("userZipCode"))
                                                countryCodeKey = ob.optString("userCountryId")
                                                val dobs =
                                                    ob.optString("userDOB").split("-".toRegex())
                                                        .toTypedArray()
                                                binding!!.exDOB.setText(dobs[2] + "-" + dobs[1] + "-" + dobs[0])
                                                dob = ob.optString("userDOB")
                                            }
                                        })
                                    }
                                } else {
                                    if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(
                                            KeyConstant.KEY_STATUS), ignoreCase = true)
                                    ) {
                                        dialogManager.stopProcessDialog()
                                        //AppUtil.showMsgAlert(tvTitle, resp.optString(KeyConstant.KEY_MESSAGE));
                                    }
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                Log.e("eeee", e.toString())
                                dialogManager.stopProcessDialog()
                                //AppUtil.showMsgAlert(tvTitle, MessageConstant.MESSAGE_SOMETHING_WRONG);
                            } catch (e: IOException) {
                                e.printStackTrace()
                                Log.e("eeee", e.toString())
                                dialogManager.stopProcessDialog()
                            }
                        } else {
                            dialogManager.stopProcessDialog()
                            Log.e("sendToken", "else is working" + response.code().toString())
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        ErrorMessage.E("ON FAILURE > " + t.message)
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert(binding!!.exCityName, t.message)
                    }
                })
            } else {
                AppUtil.showMsgAlert(binding!!.exMobile,
                    MessageConstant.MESSAGE_INTERNET_CONNECTION)
            }
        }

    public override fun onResume() {
        super.onResume()
        try {
            StatusBarcolor.setStatusbarColor(this@EditProfile, "")
        } catch (e: Exception) {
        }
    }

    companion object {
        @JvmField
        var exCountry: EditText? = null
        @JvmField
        var countryCode = "228"
    }
}