package sambal.mydd.app.activity

import android.content.Context
import sambal.mydd.app.beans.FreeDealsList
import sambal.mydd.app.adapter.AdapterHomeSignUpDels
import androidx.recyclerview.widget.LinearLayoutManager
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.beans.Signupbanner
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.app.NavUtils
import androidx.recyclerview.widget.RecyclerView
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.MainActivity
import sambal.mydd.app.R
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.adapter.SignUpbanner_Adapter
import org.json.JSONException
import sambal.mydd.app.utils.ErrorMessage
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.databinding.NewhomesignupdealsBinding
import sambal.mydd.app.utils.StatusBarcolor
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception
import java.util.*

class ExclusiveDeals : BaseActivity() {
    private var binding: NewhomesignupdealsBinding? = null
    var isFirstTime = false
    val DELAY_MS: Long = 500 //delay in milliseconds before task is to be executed
    val PERIOD_MS: Long = 3000
    var context: Context? = null
    var offset = 0
    var mList = ArrayList<FreeDealsList>()
    var adap: AdapterHomeSignUpDels? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var dialogManager: DialogManager? = null
    var handler: Handler? = null
    var mBannerList = ArrayList<Signupbanner>()
    var currentPage = 0
    var timer: Timer? = null
    private var agentId: String? = ""
    private var return_back: String? = ""

    override val contentResId: Int
        get() = R.layout.newhomesignupdeals

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.newhomesignupdeals)
        setToolbarWithBackButton_colorprimary("Sign Up Deal")
        context = this@ExclusiveDeals
        handler = Handler()
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding!!.rv.setHasFixedSize(false)
        binding!!.rv.layoutManager = linearLayoutManager
        mList.clear()
        offset = 0
        adap = AdapterHomeSignUpDels(this@ExclusiveDeals, mList, binding!!.rv)
        binding!!.rv.setItemViewCacheSize(mList.size)
        binding!!.rv.adapter = adap
        adap!!.notifyDataSetChanged()
        try {
            val bundle = intent.extras
            if (bundle != null) {
                agentId = bundle.getString("agentId")
               // Log.e("agentId", ">>$agentId")

                if(bundle.getString("return_back")!=null){
                    return_back=bundle.getString("return_back")
                }
            }
        } catch (e: Exception) {
        }
        isFirstTime = true
        getDeals(true)
        binding!!.llSearch.setOnClickListener {
            val intent = Intent(this@ExclusiveDeals, SearchAllNewActivity::class.java)
            startActivity(intent)
        }
        binding!!.rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                try {
                    if (!recyclerView.canScrollVertically(1) && dy > 0) {
                        isFirstTime = false
                        offset = offset + 1
                        getDeals(true)
                    }
                } catch (e: Exception) {
                }
            }
        })

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> navigateToParent()
        }
        return true
    }
    private fun navigateToParent() {
       if(return_back.equals("")){
        val intent = NavUtils.getParentActivityIntent(this)
        if (intent == null) {
            finish()
        } else {
            NavUtils.navigateUpFromSameTask(this)
        }}
       else{
           finish()
         //   ErrorMessage.I_clear(this@ExclusiveDeals, MainActivity::class.java,null)
        }
        onAnim(this)
    }
    override fun onBackPressed() {
        if (!return_back.equals("")) {
            finish()
           // ErrorMessage.I_clear(this@ExclusiveDeals, MainActivity::class.java,null)
        }
        super.onBackPressed()

        onAnim(this)
    }

    private fun getDeals(showLoader: Boolean) {
        if (AppUtil.isNetworkAvailable(this)) {
            if (showLoader) {
                dialogManager = DialogManager()
                if (isFirstTime) {
                    binding!!.shimmerViewContainer.visibility = View.VISIBLE
                    binding!!.shimmerViewContainer.startShimmerAnimation()
                }
                if (binding!!.shimmerViewContainer.visibility == View.GONE) {
                    dialogManager!!.showProcessDialog(this@ExclusiveDeals, "", false, null)
                }
            } else {
                if (binding!!.shimmerViewContainer.visibility == View.GONE) {
                    dialogManager = DialogManager()
                    dialogManager!!.showProcessDialog(this@ExclusiveDeals, "", false, null)
                }
            }
            val lat = MainActivity.userLat.toString()
            val lng = MainActivity.userLang.toString()
            val call =
                AppConfig.api_Interface().getFreeDeals(lat, lng, offset.toString(), "10", agentId)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            if (isFirstTime) {
                                binding!!.shimmerViewContainer.visibility = View.GONE
                                binding!!.shimmerViewContainer.stopShimmerAnimation()
                            }
                            val resp = JSONObject(response.body()!!.string())
                          //  Log.e("ExlusiveDeals", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType.equals(KeyConstant.KEY_RESPONSE_CODE_200,
                                    ignoreCase = true)
                            ) {
                                val objRes = resp.optJSONObject("response")
                                val arr = objRes.optJSONArray("freeDealsList")
                                for (i in 0 until arr.length()) {
                                    val objArr = arr.optJSONObject(i)
                                    val productId = objArr.optString("productId")
                                    val productName = objArr.optString("productName")
                                    val productDistance = objArr.optString("productDistance")
                                    val productAgentId = objArr.optString("productAgentId")
                                    val productAgentName = objArr.optString("productAgentName")
                                    val productAgentImage = objArr.optString("productAgentImage")
                                    val productImage = objArr.optString("productImage")
                                    val dealExpiredDate = objArr.optString("dealExpiredDate")
                                    val productType = objArr.optString("productType")
                                    val productTypeColor = objArr.optString("productTypeColor")
                                    val redeemedText = objArr.optString("redeemedText")
                                    val fl = FreeDealsList(productId,
                                        productName,
                                        productDistance,
                                        productAgentId,
                                        productAgentName,
                                        productAgentImage,
                                        productImage,
                                        dealExpiredDate,
                                        productType,
                                        productTypeColor,
                                        redeemedText)
                                    mList.add(fl)
                                    adap!!.notifyItemInserted(mList.size)
                                }
                                adap!!.setLoaded()
                                val arrBanner = objRes.optJSONArray("BannerList")
                                if (arrBanner.length() == 0 && mList.size == 0) {
                                    binding!!.llNoData.visibility = View.VISIBLE
                                    binding!!.llBanner.visibility = View.GONE
                                    binding!!.rv.visibility = View.GONE
                                } else if (arrBanner.length() > 0 && mList.size == 0) {
                                    binding!!.llNoData.visibility = View.GONE
                                    binding!!.llBanner.visibility = View.VISIBLE
                                    binding!!.rv.visibility = View.GONE
                                } else if (arrBanner.length() == 0 && mList.size > 0) {
                                    binding!!.llNoData.visibility = View.GONE
                                    binding!!.llBanner.visibility = View.GONE
                                    binding!!.rv.visibility = View.VISIBLE
                                } else if (arrBanner.length() > 0 && mList.size > 0) {
                                    binding!!.llNoData.visibility = View.GONE
                                    binding!!.llBanner.visibility = View.VISIBLE
                                    binding!!.rv.visibility = View.VISIBLE
                                }
                                if (arrBanner.length() > 0) {
                                    mBannerList.clear()
                                    binding!!.llBanner.visibility = View.VISIBLE
                                    for (i in 0 until arrBanner.length()) {
                                        val ob = arrBanner.optJSONObject(i)
                                        val bannerImage = ob.optString("bannerImage")
                                        val sb = Signupbanner(bannerImage)
                                        mBannerList.add(sb)
                                    }
                                    binding!!.viewPager.adapter =
                                        SignUpbanner_Adapter(this@ExclusiveDeals, mBannerList)
                                    if (mBannerList.size > 1) {
                                        /*After setting the adapter use the timer */
                                        val handler = Handler()
                                        val Update = Runnable {
                                            if (currentPage == mBannerList.size - 1) {
                                                currentPage = 0
                                            }
                                            binding!!.viewPager.setCurrentItem(currentPage++, true)
                                        }
                                        timer = Timer() // This will create a new Thread
                                        timer!!.schedule(object : TimerTask() {
                                            // task to be scheduled
                                            override fun run() {
                                                handler.post(Update)
                                            }
                                        }, DELAY_MS, PERIOD_MS)
                                    }
                                } else {
                                    binding!!.llBanner.visibility = View.GONE
                                }
                                try {
                                    dialogManager!!.stopProcessDialog()
                                } catch (e: Exception) {
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true)
                                ) {
                                    dialogManager!!.stopProcessDialog()
                                    //AppUtil.showMsgAlert(tvTitle, resp.optString(KeyConstant.KEY_MESSAGE));
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager!!.stopProcessDialog()
                            //AppUtil.showMsgAlert(tvTitle, MessageConstant.MESSAGE_SOMETHING_WRONG);
                        } catch (e: IOException) {
                            e.printStackTrace()
                            dialogManager!!.stopProcessDialog()
                        }
                    } else {
                        dialogManager!!.stopProcessDialog()
                        if (isFirstTime) {
                            binding!!.shimmerViewContainer.visibility = View.GONE
                            binding!!.shimmerViewContainer.stopShimmerAnimation()
                        }
                       // Log.e("sendToken", "else is working " + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager!!.stopProcessDialog()
                    if (isFirstTime) {
                        binding!!.shimmerViewContainer.visibility = View.GONE
                        binding!!.shimmerViewContainer.stopShimmerAnimation()
                    }
                }
            })
        } else {
            AppUtil.showMsgAlert(binding!!.llSearch, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    override fun onResume() {
        super.onResume()
        StatusBarcolor.setStatusbarColor(this@ExclusiveDeals, "")
        try {
            StatusBarcolor.setStatusbarColor(this@ExclusiveDeals, "")
        } catch (e: Exception) {
        }
    }
}