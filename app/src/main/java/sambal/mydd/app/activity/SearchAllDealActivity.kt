package sambal.mydd.app.activity

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import sambal.mydd.app.MainActivity
import android.text.TextWatcher
import android.text.Editable
import android.widget.TextView.OnEditorActionListener
import android.view.inputmethod.EditorInfo
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import com.google.gson.Gson
import sambal.mydd.app.adapter.SignUp_Deal_Adapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import sambal.mydd.app.adapter.Daily_Deal_Adapter
import sambal.mydd.app.adapter.Favourite_deal_Adapter
import sambal.mydd.app.utils.ErrorMessage
import android.content.Intent
import sambal.mydd.app.constant.IntentConstant
import sambal.mydd.app.utils.StatusBarcolor
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import sambal.mydd.app.R
import sambal.mydd.app.databinding.ActivitySearchAllDealBinding
import sambal.mydd.app.models.MyDeal_Models.Example
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class SearchAllDealActivity : BaseActivity() {
    private var binding: ActivitySearchAllDealBinding? = null
    private var lat: String? = ""
    private var lng: String? = ""

    override val contentResId: Int
        get() = R.layout.activity_search_all_deal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_all_deal)
        setToolbarWithBackButton_colorprimary("Search in products")
        val bundle = intent.extras
        //showSoftKeyboard(SearchAllDealActivity.this);
        if (bundle != null) {
            lat = bundle.getString("lat")
            lng = bundle.getString("lng")
            if (lat == "" && lng == "") {
                lat = MainActivity.userLat.toString() + ""
                lng = MainActivity.userLang.toString() + ""
            }
        } else {
            lat = MainActivity.userLat.toString() + ""
            lng = MainActivity.userLang.toString() + ""
        }
        binding!!.searchEtv.isFocusable = true
        binding!!.searchEtv.isCursorVisible = true
        binding!!.searchEtv.isFocusableInTouchMode = true
        binding!!.searchEtv.isFocusable = true
        binding!!.searchEtv.requestFocus()
        binding!!.searchEtv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding!!.searchEtv.text.toString().length > 0) {
                    binding!!.cancelImg.visibility = View.VISIBLE
                } else {
                    binding!!.cancelImg.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding!!.searchEtv.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding!!.nestedScolling.visibility = View.VISIBLE
                GetAllSearch_Deal(binding!!.searchEtv.text.toString())
                return@OnEditorActionListener true
            }
            false
        })
        binding!!.cancelImg.setOnClickListener {
            binding!!.searchEtv.setText("")
            binding!!.nestedScolling.visibility = View.GONE
        }
    }

    private fun GetAllSearch_Deal(s: String) {
        if (AppUtil.isNetworkAvailable(this@SearchAllDealActivity)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", false, null)
            val call = AppConfig.api_Interface()
                .searchAllDeals(lat, lng, binding!!.searchEtv.text.toString())
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        Log.e("ResssPor", response.toString())
                        try {
                            val obj = JSONObject(response.body()!!.string())
                            dialogManager.stopProcessDialog()
                            Log.e(">>>", obj.toString())
                            if (obj.optString("error_type") == "200") {
                                val gson = Gson()
                                val example = gson.fromJson(obj.toString(), Example::class.java)
                                if (example.response.signupDealsList.size > 0) {
                                    binding!!.signUpDealLayout.visibility = View.VISIBLE
                                    binding!!.signUpDealRcv.visibility = View.VISIBLE
                                    val side_rv_adapter =
                                        SignUp_Deal_Adapter(this@SearchAllDealActivity,
                                            example.response.signupDealsList,
                                            "Search")
                                    binding!!.signUpDealRcv.layoutManager =
                                        LinearLayoutManager(this@SearchAllDealActivity,
                                            RecyclerView.VERTICAL,
                                            false)
                                    binding!!.signUpDealRcv.isNestedScrollingEnabled = false
                                    binding!!.signUpDealRcv.setItemViewCacheSize(example.response.signupDealsList.size)
                                    binding!!.signUpDealRcv.adapter = side_rv_adapter
                                    side_rv_adapter.notifyDataSetChanged()
                                } else {
                                    binding!!.signUpDealLayout.visibility = View.GONE
                                    binding!!.signUpDealRcv.visibility = View.GONE
                                }
                                if (example.response.dailyDealsList.size > 0) {
                                    binding!!.dailyDealLayout.visibility = View.VISIBLE
                                    binding!!.dailyDealRcv.visibility = View.VISIBLE
                                    val side_rv_adapter =
                                        Daily_Deal_Adapter(this@SearchAllDealActivity,
                                            example.response.dailyDealsList,
                                            "DailyDeal",
                                            "Search")
                                    binding!!.dailyDealRcv.layoutManager =
                                        LinearLayoutManager(this@SearchAllDealActivity,
                                            RecyclerView.VERTICAL,
                                            false)
                                    binding!!.dailyDealRcv.isNestedScrollingEnabled = false
                                    binding!!.dailyDealRcv.setItemViewCacheSize(example.response.dailyDealsList.size)
                                    binding!!.dailyDealRcv.adapter = side_rv_adapter
                                    side_rv_adapter.notifyDataSetChanged()
                                } else {
                                    binding!!.dailyDealLayout.visibility = View.GONE
                                    binding!!.dailyDealRcv.visibility = View.GONE
                                }
                                if (example.response.latestDealsList.size > 0) {
                                    binding!!.latestDealLayout.visibility = View.VISIBLE
                                    binding!!.latestDealRcv.visibility = View.VISIBLE
                                    val side_rv_adapter =
                                        Daily_Deal_Adapter(this@SearchAllDealActivity,
                                            example.response.latestDealsList,
                                            "",
                                            "Search")
                                    binding!!.latestDealRcv.layoutManager =
                                        LinearLayoutManager(this@SearchAllDealActivity,
                                            RecyclerView.VERTICAL,
                                            false)
                                    binding!!.latestDealRcv.isNestedScrollingEnabled = false
                                    binding!!.latestDealRcv.setItemViewCacheSize(example.response.latestDealsList.size)
                                    binding!!.latestDealRcv.adapter = side_rv_adapter
                                    side_rv_adapter.notifyDataSetChanged()
                                } else {
                                    binding!!.latestDealLayout.visibility = View.GONE
                                    binding!!.latestDealRcv.visibility = View.GONE
                                }
                                if (example.response.favouriteDealsList.size > 0) {
                                    binding!!.favouriteDealRcv.visibility = View.VISIBLE
                                    binding!!.favouriteDealLayout.visibility = View.VISIBLE
                                    val side_rv_adapter =
                                        Favourite_deal_Adapter(this@SearchAllDealActivity,
                                            example.response.favouriteDealsList,
                                            "Search")
                                    binding!!.favouriteDealRcv.layoutManager = LinearLayoutManager(
                                        this@SearchAllDealActivity,
                                        RecyclerView.VERTICAL,
                                        false)
                                    binding!!.favouriteDealRcv.isNestedScrollingEnabled = false
                                    binding!!.favouriteDealRcv.setItemViewCacheSize(example.response.favouriteDealsList.size)
                                    binding!!.favouriteDealRcv.adapter = side_rv_adapter
                                    side_rv_adapter.notifyDataSetChanged()
                                } else {
                                    binding!!.favouriteDealRcv.visibility = View.GONE
                                    binding!!.favouriteDealLayout.visibility = View.GONE
                                }
                            } else {
                                AppUtil.showMsgAlert(binding!!.searchEtv, obj.optString("message"))
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            Log.e("Exception", e.toString())
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                }
            })
        } else {
            ErrorMessage.T(this@SearchAllDealActivity, "No Internet Found!")
        }
    }

    fun moveToDetails(productId: String?, agentId: String?, pos: Int) {
        val intent = Intent(this@SearchAllDealActivity, LatestProductDetails::class.java)
        intent.putExtra(IntentConstant.INTENT_KEY_PRODUCT_ID, productId)
        intent.putExtra(IntentConstant.INTENT_KEY_AGENT_ID, agentId)
        intent.putExtra("type", "non_direct")
        intent.putExtra("pos", pos)
        startActivityForResult(intent, 80)
    }

    override fun onResume() {
        super.onResume()
        try {
            StatusBarcolor.setStatusbarColor(this@SearchAllDealActivity, "")
        } catch (e: Exception) {
        }
    }

    companion object {
        fun showSoftKeyboard(context: Context) {
            val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 1)
        }

        fun hideSoftKeyboard(context: Context) {
            val activity = context as Activity
            if (activity.currentFocus != null) {
                val inputMethodManager =
                    context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
            }
        }
    }
}