package sambal.mydd.app.activity

import androidx.appcompat.app.AppCompatActivity
import sambal.mydd.app.callback.GroceryBottomSheet
import sambal.mydd.app.beans.AgentList
import sambal.mydd.app.adapter.AdapterGroceryList
import sambal.mydd.app.utils.DialogManager
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import sambal.mydd.app.MainActivity
import android.content.Intent
import sambal.mydd.app.SplashActivity
import sambal.mydd.app.utils.WrapContentLinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import sambal.mydd.app.utils.StatusBarcolor
import android.view.WindowManager
import android.widget.TextView
import android.view.WindowManager.BadTokenException
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.adapter.Groceryimage_Adapter
import sambal.mydd.app.constant.MessageConstant
import org.json.JSONException
import sambal.mydd.app.utils.ErrorMessage
import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import sambal.mydd.app.R
import sambal.mydd.app.databinding.NewgroceryBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception
import java.util.*

class ActivityGroceryList : AppCompatActivity(), GroceryBottomSheet, View.OnClickListener {
    var binding: NewgroceryBinding? = null
    var mList = ArrayList<AgentList>()
    var adapterGroceryList: AdapterGroceryList? = null
    var context: Context? = null
    var offset = 0
    var dialogManager: DialogManager? = null
    var count = "10"
    var isFirstTym = true
    var handler: Handler? = null
    var timer: Timer? = null
    var lat: String? = null
    var lng: String? = null
    var type = "0"
    private var callback: GroceryBottomSheet? = null
    var types: String? = "non_direct"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.newgrocery)
        context = this@ActivityGroceryList
        callback = this
        val bundle = intent.extras
        if (bundle != null) {
            types = bundle.getString("type")
            try {
                binding!!.tvToolbarLocation.text = bundle.getString("name")
            } catch (e: Exception) {
                binding!!.tvToolbarLocation.text = "Location"
            }
        } else {
        }
        try {
            binding!!.tvToolbarLocation.text =
                MainActivity.tvLocation!!.text.toString().trim { it <= ' ' }
        } catch (e: Exception) {
        }
        lat = MainActivity.userLat.toString() + ""
        lng = MainActivity.userLang.toString() + ""
        handler = Handler()
        dialogManager = DialogManager()
        try {
        } catch (e: Exception) {
        }
        binding!!.ivBack.setOnClickListener {
            if (types.equals("direct", ignoreCase = true)) {
                startActivity(Intent(this@ActivityGroceryList, SplashActivity::class.java))
                finish()
            } else {
                finish()
            }
        }
        mList.clear()
        binding!!.rv.setHasFixedSize(false)
        binding!!.rv.layoutManager = WrapContentLinearLayoutManager(context)
        adapterGroceryList =
            AdapterGroceryList(context!!, mList, binding!!.rv, true, binding!!.nestedscrollview)
        binding!!.rv.isNestedScrollingEnabled = false
        binding!!.rv.adapter = adapterGroceryList
        adapterGroceryList!!.notifyDataSetChanged()
        try {
            binding!!.tvGrocery.setTextColor(context!!.getResources().getColor(R.color.grocerygreen))
            binding!!.viewgrocery.setBackgroundColor(context!!.getResources()
                .getColor(R.color.grocerygreen))
            binding!!.tvUpcomig.setTextColor(context!!.getResources().getColor(R.color.black))
            binding!!.viewupcoming.setBackgroundColor(context!!.getResources()
                .getColor(R.color.light_grey))
            binding!!.viewgrocery.visibility = View.VISIBLE
            binding!!.llFilter.visibility = View.VISIBLE
            binding!!.viewupcoming.visibility = View.INVISIBLE
            binding!!.tvAll.setTextColor(context!!.getResources().getColor(R.color.white))
            binding!!.tvClick.setTextColor(context!!.getResources().getColor(R.color.black))
            binding!!.tvDelivery.setTextColor(context!!.getResources().getColor(R.color.black))
            binding!!.llAll.background = resources.getDrawable(R.drawable.llgrocerybordergreen)
            binding!!.llClick.background = resources.getDrawable(R.drawable.llgroceryborder)
            binding!!.llDelivery.background = resources.getDrawable(R.drawable.llgroceryborder)
            binding!!.rv.visibility = View.VISIBLE
            binding!!.rvUpcomig.visibility = View.GONE
        } catch (e: Exception) {
        }
        isFirstTym = true
        getList(offset, true)
        binding!!.rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                try {
                    if (!recyclerView.canScrollVertically(1) && dy > 0) {
                        offset++
                        isFirstTym = false
                        getList(offset, false)
                    }
                } catch (e: Exception) {
                }
            }
        })
        binding!!.llAll.setOnClickListener { showFilterDialog() }
        binding!!.llLocationLayout.setOnClickListener {
            startActivityForResult(Intent(context,
//                SelectLocationActivityStore::class.java), 80)
                SelectLocationActivity::class.java), 80)
        }
        binding!!.ivNoti.setOnClickListener {
            startActivity(Intent(context,
                NewNotification::class.java))
        }
        binding!!.llGrocery.setOnClickListener(this)
        binding!!.llUpcoming.setOnClickListener(this)
        binding!!.llAll.setOnClickListener(this)
        binding!!.llClick.setOnClickListener(this)
        binding!!.llDelivery.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        try {
            StatusBarcolor.setStatusbarColor(this@ActivityGroceryList, "")
        } catch (e: Exception) {
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.llGrocery -> {
                binding!!.tvGrocery.setTextColor(resources.getColor(R.color.grocerygreen))
                binding!!.viewgrocery.setBackgroundColor(resources.getColor(R.color.grocerygreen))
                binding!!.tvUpcomig.setTextColor(resources.getColor(R.color.grocertytext))
                binding!!.viewupcoming.setBackgroundColor(resources.getColor(R.color.grocertytext))
                binding!!.viewgrocery.visibility = View.VISIBLE
                binding!!.llFilter.visibility = View.VISIBLE
                binding!!.viewupcoming.visibility = View.INVISIBLE
                binding!!.nestedscrollview.fullScroll(View.FOCUS_UP)
                binding!!.nestedscrollview.scrollTo(0, 0)
                mList.clear()
                adapterGroceryList = null
                binding!!.rv.setHasFixedSize(false)
                binding!!.rv.layoutManager = WrapContentLinearLayoutManager(context)
                adapterGroceryList = AdapterGroceryList(context!!,
                    mList,
                    binding!!.rv,
                    true,
                    binding!!.nestedscrollview)
                binding!!.rv.isNestedScrollingEnabled = false
                binding!!.rv.adapter = adapterGroceryList
                adapterGroceryList!!.notifyDataSetChanged()
                isFirstTym = true
                type = "0"
                offset = 0
                getList(offset, true)
                binding!!.nestedscrollview.scrollTo(0, 0)
            }
            R.id.llUpcoming -> {
                binding!!.nestedscrollview.fullScroll(View.FOCUS_UP)
                binding!!.nestedscrollview.scrollTo(0, 0)
                type = ""
                binding!!.viewgrocery.visibility = View.INVISIBLE
                binding!!.llFilter.visibility = View.GONE
                binding!!.viewupcoming.visibility = View.VISIBLE
                binding!!.tvUpcomig.setTextColor(resources.getColor(R.color.grocerygreen))
                binding!!.viewupcoming.setBackgroundColor(resources.getColor(R.color.grocerygreen))
                binding!!.tvGrocery.setTextColor(resources.getColor(R.color.grocertytext))
                binding!!.viewgrocery.setBackgroundColor(resources.getColor(R.color.grocerygreen))
                binding!!.tvAll.setTextColor(context!!.resources.getColor(R.color.white))
                binding!!.tvClick.setTextColor(context!!.resources.getColor(R.color.grocertytext))
                binding!!.tvDelivery.setTextColor(context!!.resources.getColor(R.color.grocertytext))
                binding!!.llAll.background =
                    resources.getDrawable(R.drawable.llgrocerybordergreen)
                binding!!.llClick.background =
                    resources.getDrawable(R.drawable.llgroceryborder)
                binding!!.llDelivery.background =
                    resources.getDrawable(R.drawable.llgroceryborder)
                mList.clear()
                adapterGroceryList!!.notifyDataSetChanged()
                adapterGroceryList = null
                binding!!.rv.setHasFixedSize(false)
                binding!!.rv.layoutManager = WrapContentLinearLayoutManager(context)
                adapterGroceryList = AdapterGroceryList(context!!,
                    mList,
                    binding!!.rv,
                    false,
                    binding!!.nestedscrollview)
                binding!!.rv.isNestedScrollingEnabled = false
                binding!!.rv.adapter = adapterGroceryList
                adapterGroceryList!!.notifyDataSetChanged()
                offset = 0
                isFirstTym = true
                getUpcomingList(offset, true)
            }
            R.id.llAll -> {
                binding!!.nestedscrollview.fullScroll(View.FOCUS_UP)
                binding!!.nestedscrollview.scrollTo(0, 0)
                binding!!.tvAll.setTextColor(context!!.resources.getColor(R.color.white))
                binding!!.tvClick.setTextColor(context!!.resources.getColor(R.color.grocertytext))
                binding!!.tvDelivery.setTextColor(context!!.resources.getColor(R.color.grocertytext))
                binding!!.llAll.background =
                    resources.getDrawable(R.drawable.llgrocerybordergreen)
                binding!!.llClick.background =
                    resources.getDrawable(R.drawable.llgroceryborder)
                binding!!.llDelivery.background =
                    resources.getDrawable(R.drawable.llgroceryborder)
                setClicked("0")
            }
            R.id.llClick -> {
                binding!!.nestedscrollview.fullScroll(View.FOCUS_UP)
                binding!!.nestedscrollview.scrollTo(0, 0)
                binding!!.tvAll.setTextColor(context!!.resources.getColor(R.color.grocertytext))
                binding!!.tvClick.setTextColor(context!!.resources.getColor(R.color.white))
                binding!!.tvDelivery.setTextColor(context!!.resources.getColor(R.color.grocertytext))
                binding!!.llAll.background = resources.getDrawable(R.drawable.llgroceryborder)
                binding!!.llClick.background =
                    resources.getDrawable(R.drawable.llgrocerybordergreen)
                binding!!.llDelivery.background =
                    resources.getDrawable(R.drawable.llgroceryborder)
                setClicked("1")
            }
            R.id.llDelivery -> {
                binding!!.nestedscrollview.fullScroll(View.FOCUS_UP)
                binding!!.nestedscrollview.scrollTo(0, 0)
                binding!!.tvAll.setTextColor(context!!.resources.getColor(R.color.grocertytext))
                binding!!.tvClick.setTextColor(context!!.resources.getColor(R.color.grocertytext))
                binding!!.tvDelivery.setTextColor(context!!.resources.getColor(R.color.white))
                binding!!.llAll.background =
                    resources.getDrawable(R.drawable.llgroceryborder)
                binding!!.llClick.background =
                    resources.getDrawable(R.drawable.llgroceryborder)
                binding!!.llDelivery.background =
                    resources.getDrawable(R.drawable.llgrocerybordergreen)
                setClicked("2")
            }
        }
    }

    private fun showFilterDialog() {
        val dialog1 = Dialog(context!!, R.style.NewDialog)
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog1.setContentView(R.layout.filtergrocery)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog1.window!!.attributes)
        lp.width = WindowManager.LayoutParams.FILL_PARENT
        lp.height = WindowManager.LayoutParams.FILL_PARENT
        dialog1.window!!.attributes = lp
        val ivClose = dialog1.findViewById<ImageView>(R.id.ivClose)
        val tVAll = dialog1.findViewById<TextView>(R.id.tvAll)
        val tvCollect = dialog1.findViewById<TextView>(R.id.tvClick)
        val tvDelivery = dialog1.findViewById<TextView>(R.id.tvDelivery)
        ivClose.setOnClickListener { dialog1.dismiss() }
        tVAll.setOnClickListener {
            dialog1.dismiss()
            setClicked("0")
        }
        tvCollect.setOnClickListener {
            dialog1.dismiss()
            setClicked("1")
        }
        tvDelivery.setOnClickListener {
            dialog1.dismiss()
            setClicked("2")
        }
        try {
            dialog1.show()
        } catch (e: BadTokenException) {
            Log.e("EXxx", e.toString())
        }
    }

    private fun getList(offset: Int, showLoader: Boolean) {
        if (AppUtil.isNetworkAvailable(context)) {
            //"productId"    agentId
            if (showLoader) {
                dialogManager!!.showProcessDialog(this@ActivityGroceryList, "", false, null)
            }
            val call = AppConfig.api_Interface()
                .getAgentStoreList(lat, lng, offset.toString(), count, type)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("AgentList", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200 && resp.optString("status")
                                    .equals("true", ignoreCase = true)
                            ) {
                                val responseObj = resp.getJSONObject(KeyConstant.KEY_RESPONSE)
                                if (responseObj != null) {
                                    runOnUiThread {
                                        val arrRev = responseObj.optJSONArray("agentList")
                                        val arrImage = responseObj.optJSONArray("bannerList")
                                        if (arrImage.length() == 0) {
                                            binding!!.llBanner.visibility = View.GONE
                                        } else {
                                            binding!!.llBanner.visibility = View.VISIBLE
                                        }
                                        try {
                                            binding!!.viewpagerBannerImage.adapter =
                                                Groceryimage_Adapter(this@ActivityGroceryList,
                                                    arrImage)
                                            if (arrImage.length() > 1) {
                                                val timerTask: TimerTask = object : TimerTask() {
                                                    override fun run() {
                                                        binding!!.viewpagerBannerImage.post {
                                                            try {
                                                                binding!!.viewpagerBannerImage.currentItem =
                                                                    (binding!!.viewpagerBannerImage.currentItem + 1) % arrImage.length()
                                                            } catch (e: Exception) {
                                                            }
                                                        }
                                                    }
                                                }
                                                timer = Timer()
                                                timer!!.schedule(timerTask, 3000, 3000)
                                            }
                                        } catch (e: Exception) {
                                        }
                                        if (arrRev.length() == 0 && isFirstTym) {
                                            binding!!.rv.visibility = View.GONE
                                            binding!!.tvMsg.visibility = View.VISIBLE
                                        } else {
                                            binding!!.rv.visibility = View.VISIBLE
                                            binding!!.tvMsg.visibility = View.GONE
                                        }
                                        for (i in 0 until arrRev.length()) {
                                            val obj = arrRev.optJSONObject(i)
                                            val agentId = obj.optString("agentId")
                                            val agentName = obj.optString("agentName")
                                            val agentAddress = obj.optString("agentAddress")
                                            val agentPostcode = obj.optString("agentPostcode")
                                            val agentDistance = obj.optString("agentDistance")
                                            val agentImage = obj.optString("agentImage")
                                            val agentStoreClickAndCollect =
                                                obj.optString("agentStoreClickAndCollect")
                                            val agentStoreDelivery =
                                                obj.optString("agentStoreDelivery")
                                            val agentStoreURL = obj.optString("agentStoreURL")
                                            val dc = AgentList(agentId,
                                                agentName,
                                                agentAddress,
                                                agentPostcode,
                                                agentDistance,
                                                agentImage,
                                                agentStoreClickAndCollect,
                                                agentStoreDelivery,
                                                agentStoreURL)
                                            mList.add(dc)
                                            adapterGroceryList!!.notifyItemInserted(mList.size)
                                        }
                                        if (mList.size > 9) {
                                            adapterGroceryList!!.setLoaded()
                                        }
                                        if (showLoader) {
                                            dialogManager!!.stopProcessDialog()
                                        }
                                    }
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true)
                                ) {
                                    dialogManager!!.stopProcessDialog()
                                    if (isFirstTym) {
                                        AppUtil.showMsgAlert(binding!!.tvToolbarLocation,
                                            MessageConstant.MESSAGE_SOMETHING_WRONG)
                                    }
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager!!.stopProcessDialog()
                            AppUtil.showMsgAlert(binding!!.tvToolbarLocation,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            dialogManager!!.stopProcessDialog()
                            AppUtil.showMsgAlert(binding!!.tvToolbarLocation,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        }
                    } else {
                        dialogManager!!.stopProcessDialog()
                        AppUtil.showMsgAlert(binding!!.tvToolbarLocation,
                            MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager!!.stopProcessDialog()
                    AppUtil.showMsgAlert(binding!!.tvMsg, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(binding!!.tvToolbarLocation,
                MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    private fun getUpcomingList(offset: Int, showLoader: Boolean) {
        if (AppUtil.isNetworkAvailable(context)) {
            //"productId"    agentId
            if (showLoader) {
                dialogManager!!.showProcessDialog(this@ActivityGroceryList, "", false, null)
            }
            val call = AppConfig.api_Interface()
                .getAgentStoreList(lat, lng, offset.toString(), count, type)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("AgentList", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200 && resp.optString("status")
                                    .equals("true", ignoreCase = true)
                            ) {
                                val responseObj = resp.getJSONObject(KeyConstant.KEY_RESPONSE)
                                if (responseObj != null) {
                                    runOnUiThread {
                                        val arrRev = responseObj.optJSONArray("upcomingAgentList")
                                        val arrImage = responseObj.optJSONArray("bannerList")
                                        if (arrImage.length() == 0) {
                                            binding!!.llBanner.visibility = View.GONE
                                        } else {
                                            binding!!.llBanner.visibility = View.VISIBLE
                                        }
                                        try {
                                            binding!!.viewpagerBannerImage.adapter =
                                                Groceryimage_Adapter(this@ActivityGroceryList,
                                                    arrImage)
                                            if (arrImage.length() > 1) {
                                                val timerTask: TimerTask = object : TimerTask() {
                                                    override fun run() {
                                                        binding!!.viewpagerBannerImage.post {
                                                            try {
                                                                binding!!.viewpagerBannerImage.currentItem =
                                                                    (binding!!.viewpagerBannerImage.currentItem + 1) % arrImage.length()
                                                            } catch (e: Exception) {
                                                            }
                                                        }
                                                    }
                                                }
                                                timer = Timer()
                                                timer!!.schedule(timerTask, 3000, 3000)
                                            }
                                        } catch (e: Exception) {
                                        }
                                        if (arrRev.length() == 0 && isFirstTym) {
                                            binding!!.rv.visibility = View.GONE
                                            binding!!.tvMsg.visibility = View.VISIBLE
                                        } else {
                                            binding!!.rv.visibility = View.VISIBLE
                                            binding!!.tvMsg.visibility = View.GONE
                                        }
                                        for (i in 0 until arrRev.length()) {
                                            val obj = arrRev.optJSONObject(i)
                                            val agentId = obj.optString("agentId")
                                            val agentName = obj.optString("agentName")
                                            val agentAddress = obj.optString("agentAddress")
                                            val agentPostcode = obj.optString("agentPostcode")
                                            val agentDistance = obj.optString("agentDistance")
                                            val agentImage = obj.optString("agentImage")
                                            val agentStoreClickAndCollect =
                                                obj.optString("agentStoreClickAndCollect")
                                            val agentStoreDelivery =
                                                obj.optString("agentStoreDelivery")
                                            val agentStoreURL = obj.optString("agentStoreURL")
                                            val dc = AgentList(agentId,
                                                agentName,
                                                agentAddress,
                                                agentPostcode,
                                                agentDistance,
                                                agentImage,
                                                agentStoreClickAndCollect,
                                                agentStoreDelivery,
                                                agentStoreURL)
                                            mList.add(dc)
                                            adapterGroceryList!!.notifyItemInserted(mList.size)
                                        }
                                        if (mList.size > 9) {
                                            adapterGroceryList!!.setLoaded()
                                        }
                                        if (showLoader) {
                                            dialogManager!!.stopProcessDialog()
                                        }
                                    }
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true)
                                ) {
                                    dialogManager!!.stopProcessDialog()
                                    if (isFirstTym) {
                                        AppUtil.showMsgAlert(binding!!.tvToolbarLocation,
                                            MessageConstant.MESSAGE_SOMETHING_WRONG)
                                    }
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager!!.stopProcessDialog()
                            AppUtil.showMsgAlert(binding!!.tvToolbarLocation,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            dialogManager!!.stopProcessDialog()
                            AppUtil.showMsgAlert(binding!!.tvToolbarLocation,
                                MessageConstant.MESSAGE_SOMETHING_WRONG)
                        }
                    } else {
                        dialogManager!!.stopProcessDialog()
                        AppUtil.showMsgAlert(binding!!.tvToolbarLocation,
                            MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager!!.stopProcessDialog()
                    AppUtil.showMsgAlert(binding!!.tvMsg, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(binding!!.tvToolbarLocation,
                MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    override fun setClicked(types: String) {
        type = types
        binding!!.nestedscrollview.fullScroll(View.FOCUS_UP)
        binding!!.nestedscrollview.scrollTo(0, 0)
        if (type.equals("0", ignoreCase = true)) {
            mList.clear()
            adapterGroceryList = null
            binding!!.rv.setHasFixedSize(false)
            binding!!.rv.layoutManager = WrapContentLinearLayoutManager(context)
            adapterGroceryList =
                AdapterGroceryList(context!!, mList, binding!!.rv, true, binding!!.nestedscrollview)
            binding!!.rv.isNestedScrollingEnabled = false
            binding!!.rv.adapter = adapterGroceryList
            adapterGroceryList!!.notifyDataSetChanged()
            offset = 0
            isFirstTym = true
            getList(offset, true)
        } else if (type.equals("1", ignoreCase = true)) {
            mList.clear()
            adapterGroceryList = null
            binding!!.rv.setHasFixedSize(false)
            binding!!.rv.layoutManager = WrapContentLinearLayoutManager(context)
            adapterGroceryList =
                AdapterGroceryList(context!!, mList, binding!!.rv, true, binding!!.nestedscrollview)
            binding!!.rv.isNestedScrollingEnabled = false
            binding!!.rv.adapter = adapterGroceryList
            adapterGroceryList!!.notifyDataSetChanged()
            offset = 0
            isFirstTym = true
            getList(offset, true)
        } else if (type.equals("2", ignoreCase = true)) {
            mList.clear()
            adapterGroceryList = null
            binding!!.rv.setHasFixedSize(false)
            binding!!.rv.layoutManager = WrapContentLinearLayoutManager(context)
            adapterGroceryList =
                AdapterGroceryList(context!!, mList, binding!!.rv, true, binding!!.nestedscrollview)
            binding!!.rv.isNestedScrollingEnabled = false
            binding!!.rv.adapter = adapterGroceryList
            adapterGroceryList!!.notifyDataSetChanged()
            offset = 0
            isFirstTym = true
            getList(offset, true)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 80) {
            try {
                val name = data!!.getStringExtra("name")
                lat = data.getStringExtra("lat")
                lng = data.getStringExtra("lng")
                Log.e("Lat", "$name,$lat,$lng")
                binding!!.tvToolbarLocation.text = data.getStringExtra("name")
                mList.clear()
                binding!!.nestedscrollview.fullScroll(View.FOCUS_UP)
                binding!!.nestedscrollview.scrollTo(0, 0)
                offset = 0
                binding!!.rv.setHasFixedSize(false)
                binding!!.rv.layoutManager = WrapContentLinearLayoutManager(context)
                adapterGroceryList = AdapterGroceryList(context!!,
                    mList,
                    binding!!.rv,
                    true,
                    binding!!.nestedscrollview)
                binding!!.rv.isNestedScrollingEnabled = false
                binding!!.rv.adapter = adapterGroceryList
                adapterGroceryList!!.notifyDataSetChanged()
                isFirstTym = true
                getList(offset, true)
            } catch (e: Exception) {
                Log.e("Ex", e.toString())
            }
        }
    }

    override fun onBackPressed() {
        Log.e("Typeee", types!!)
        super.onBackPressed()
        if (types.equals("direct", ignoreCase = true)) {
            startActivity(Intent(this@ActivityGroceryList, SplashActivity::class.java))
            finish()
        } else {
            finish()
        }
    }
}