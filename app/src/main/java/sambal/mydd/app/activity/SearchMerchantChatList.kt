package sambal.mydd.app.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import sambal.mydd.app.callback.RecyclerClickListener
import sambal.mydd.app.callback.ItemClickedPositionCallback
import org.json.JSONArray
import sambal.mydd.app.adapter.SearchChatNewAdapter
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import sambal.mydd.app.MainActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.TextView.OnEditorActionListener
import android.view.inputmethod.EditorInfo
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.StatusBarcolor
import org.json.JSONObject
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.AppConfig
import sambal.mydd.app.constant.KeyConstant
import org.json.JSONException
import sambal.mydd.app.utils.ErrorMessage
import sambal.mydd.app.constant.MessageConstant
import android.text.TextWatcher
import android.text.Editable
import android.util.Log
import android.view.KeyEvent
import android.view.View
import sambal.mydd.app.R
import sambal.mydd.app.databinding.ActivitySearchAllNewBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception

class SearchMerchantChatList : AppCompatActivity(), RecyclerClickListener, View.OnClickListener,
    ItemClickedPositionCallback {
    private var binding: ActivitySearchAllNewBinding? = null
    var isFirstTime = true
    private var dealJsonArray: JSONArray? = null
    private var voucherJsonArray: JSONArray? = null
    private var context: Context? = null
    private var searchNewAdapter: SearchChatNewAdapter? = null
    private var catId = "0"
    private var clickedPosition = 0
    var publishKey: String? = null
    var subscribeKey: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_all_new)
        context = this
        initToolBar()
        catId = MainActivity.categoryId.toString() + ""
        try {
            if (intent.extras != null) {
                binding!!.toolbarSearch.etToolbarSearch.hint = intent.extras!!.getString("page")
            }
        } catch (e: Exception) {
        }
        binding!!.dealRecyclerView.setHasFixedSize(true)
        binding!!.dealRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding!!.dealRecyclerView.isNestedScrollingEnabled = false
    }

    private fun initToolBar() {
        setSupportActionBar(binding!!.toolbarSearch.toolbar)
        binding!!.toolbarSearch.etToolbarSearch.addTextChangedListener(MyTextWatcher(
            binding!!.toolbarSearch.etToolbarSearch))
        binding!!.toolbarSearch.toolbar.visibility = View.VISIBLE
        binding!!.toolbarSearch.toolbarLeftImage.setOnClickListener { finish() }
        binding!!.toolbarSearch.etToolbarSearch.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                AppUtil.hideSoftKeyboard(context)
                performSearch()
                return@OnEditorActionListener true
            }
            false
        })
        setSupportActionBar(binding!!.toolbarSearch.toolbar)
    }

    public override fun onResume() {
        super.onResume()
        try {
            StatusBarcolor.setStatusbarColor(this@SearchMerchantChatList, "")
        } catch (e: Exception) {
        }
    }

    override fun onClick(v: View) {}
    override fun setCellClicked(newsJSONObject: JSONObject, eventHasMultipleParts: String) {
        Log.e("s", "asasasa")
    }

    private fun dealSearchUpdateUI(searchKeyword: String, publish: String?, subscribekey: String?) {
        Log.e("sss", "100")
        if (dealJsonArray != null && dealJsonArray!!.length() > 0) {
            binding!!.msg.visibility = View.GONE
            binding!!.dealRecyclerView.visibility = View.VISIBLE
        } else {
            binding!!.msg.visibility = View.VISIBLE
            binding!!.dealRecyclerView.visibility = View.GONE
        }
        searchNewAdapter = SearchChatNewAdapter(this,
            searchKeyword,
            dealJsonArray!!,
            publish!!,
            subscribekey!!) { jsonObject, eventHasMultipleParts -> Log.e("clic", "111") }
        binding!!.dealRecyclerView.adapter = searchNewAdapter
        searchNewAdapter!!.notifyDataSetChanged()
        //etSearch.requestFocus();
    }

    override fun onStop() {
        super.onStop()
        //AppUtil.hideSoftKeyboard(context);
    }

    override fun onPause() {
        super.onPause()
        //AppUtil.hideSoftKeyboard(context);
    }

    private fun searchDeals(keyword: String, showMsg: Boolean) {
        dealJsonArray = null
        var lat = MainActivity.userLat.toString() + ""
        var lang = MainActivity.userLang.toString() + ""
        val distance = MainActivity.distance + ""
        if (lat.equals(null, ignoreCase = true)) {
            lat = ""
        }
        if (lang.equals(null, ignoreCase = true)) {
            lang = ""
        }
        if (AppUtil.isNetworkAvailable(context)) {
            val dialogManager = DialogManager()
            if (showMsg) {
                dialogManager.showProcessDialog(this@SearchMerchantChatList, "", false, null)
            } else {
                binding!!.toolbarSearch.progressBar.visibility = View.VISIBLE
            }
            Log.e("search screen distance", "$distance, $lat, $lang")
            val call = AppConfig.api_Interface().searchMerchantChatList(keyword, lat, lang)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            val status = resp.optBoolean(KeyConstant.KEY_STATUS)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                dialogManager.stopProcessDialog()
                                binding!!.toolbarSearch.progressBar.visibility = View.GONE
                                Log.e("search resp", "$keyword,  $resp")
                                if (status) {
                                    val responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                    publishKey = responseObj.optString("publishKey")
                                    subscribeKey = responseObj.optString("subscribeKey")
                                    dealJsonArray =
                                        responseObj.optJSONArray(KeyConstant.KEY_AGENT_LIST)
                                    if (dealJsonArray != null && dealJsonArray!!.length() > 0) {
                                        dealSearchUpdateUI(keyword, publishKey, subscribeKey)
                                    }
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true)
                                ) {
                                    dialogManager.stopProcessDialog()
                                    binding!!.toolbarSearch.progressBar.visibility = View.GONE
                                    if (dealJsonArray != null && dealJsonArray!!.length() > 0) {
                                        binding!!.msg.visibility = View.GONE
                                        binding!!.dealRecyclerView.visibility = View.VISIBLE
                                    } else {
                                        binding!!.msg.visibility = View.VISIBLE
                                        binding!!.dealRecyclerView.visibility = View.GONE
                                    }

                                    //AppUtil.showMsgAlert(msg, resp.optString(KeyConstant.KEY_MESSAGE));
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            binding!!.toolbarSearch.progressBar.visibility = View.GONE
                            //AppUtil.showMsgAlert(
                            // msg, MessageConstant.MESSAGE_SOMETHING_WRONG);
                        } catch (e: IOException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            binding!!.toolbarSearch.progressBar.visibility = View.GONE
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        binding!!.toolbarSearch.progressBar.visibility = View.GONE
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    binding!!.toolbarSearch.progressBar.visibility = View.GONE
                }
            })
        } else {
            AppUtil.showMsgAlert(binding!!.msg, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    private fun getMyGiftVoucher(keyword: String, showMsg: Boolean) {
        val lat = MainActivity.userLat.toString() + ""
        val lang = MainActivity.userLang.toString() + ""
        val agentId = ""
        val catId = MainActivity.categoryId.toString() + ""
        if (AppUtil.isNetworkAvailable(this@SearchMerchantChatList)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(context, "", false, null)
            val call =
                AppConfig.api_Interface().getMyGiftVouchersV2(agentId, lat, lang, catId, keyword)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            voucherJsonArray = null
                            val resp = JSONObject(response.body()!!.string())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                val responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                voucherJsonArray =
                                    responseObj.optJSONArray(KeyConstant.KEY_AGENT_LIST)
                                runOnUiThread {
                                    dialogManager.stopProcessDialog()
                                    initView(responseObj)
                                }
                            } else if (errorType.equals(KeyConstant.KEY_RESPONSE_CODE_202,
                                    ignoreCase = true)
                            ) {
                                runOnUiThread {
                                    dialogManager.stopProcessDialog()
                                    binding!!.msg.visibility = View.VISIBLE
                                    binding!!.dealRecyclerView.visibility = View.GONE
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true)
                                ) {
                                    dialogManager.stopProcessDialog()
                                    //AppUtil.showMsgAlert(msg, resp.optString(KeyConstant.KEY_MESSAGE));
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager.stopProcessDialog()
                            //AppUtil.showMsgAlert(msg, MessageConstant.MESSAGE_SOMETHING_WRONG);
                        } catch (e: IOException) {
                            e.printStackTrace()
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
                }
            })
        } else {
            AppUtil.showMsgAlert(binding!!.msg, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    private fun initView(responseObj: JSONObject) {
        if (voucherJsonArray != null && voucherJsonArray!!.length() > 0) {
            binding!!.msg.visibility = View.GONE
            binding!!.dealRecyclerView.visibility = View.GONE
            binding!!.voucherRecyclerView.visibility = View.VISIBLE
        } else {
            binding!!.msg.visibility = View.VISIBLE
            binding!!.dealRecyclerView.visibility = View.GONE
            binding!!.voucherRecyclerView.visibility = View.GONE
        }
        binding!!.toolbarSearch.etToolbarSearch.setOnEditorActionListener { v, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding!!.toolbarSearch.etToolbarSearch.text.toString() != null && binding!!.toolbarSearch.etToolbarSearch.text.toString().length >= 1) {
                    searchDeals(binding!!.toolbarSearch.etToolbarSearch.text.toString(), true)
                } else {
                    binding!!.msg.visibility = View.VISIBLE
                    //AppUtil.showMsgAlert(search, getResources().getString(R.string.err_msg_search_key));
                }
            }
            false
        }
    }

    private fun performSearch() {
        if (binding!!.toolbarSearch.etToolbarSearch.text.toString().length > 0) {
            searchDeals(binding!!.toolbarSearch.etToolbarSearch.text.toString(), true)
        } else {
            binding!!.msg.visibility = View.VISIBLE
            //AppUtil.showMsgAlert(search, getResources().getString(R.string.err_msg_search_key));
        }
    }

    override fun itemClickedPosition(position: Int, isFavourite: Int) {
        clickedPosition = position
    }

    private inner class MyTextWatcher(private val view: View) : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            if (isFirstTime && binding!!.toolbarSearch.etToolbarSearch.text.toString().length == 1) {
                isFirstTime = false
            }
            if (binding!!.toolbarSearch.etToolbarSearch.text.toString().length == 0) {
                binding!!.msg.visibility = View.VISIBLE
                //cross.setVisibility(View.GONE);
            } else if (binding!!.toolbarSearch.etToolbarSearch.text.toString().length >= 3) {
                searchDeals(binding!!.toolbarSearch.etToolbarSearch.text.toString(), false)
            }
        }
    }
}