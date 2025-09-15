package sambal.mydd.app.activity

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import sambal.mydd.app.MainActivity
import android.content.Intent
import sambal.mydd.app.utils.MyLog
import sambal.mydd.app.utils.StatusBarcolor
import android.util.Log
import android.view.View
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import com.google.gson.Gson
import sambal.mydd.app.adapter.My_Promotion_List_Adapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import sambal.mydd.app.R
import sambal.mydd.app.databinding.ActivityMYPromotionBinding
import sambal.mydd.app.models.MyPromotion.Example
import sambal.mydd.app.utils.ErrorMessage
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class MY_PromotionActivity : BaseActivity() {
    private var binding: ActivityMYPromotionBinding? = null
    private var lat: String? = ""
    private var lng: String? = ""

    override val contentResId: Int
        get() = R.layout.activity_m_y__promotion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_m_y__promotion)
        setToolbarWithBackButton_colorprimary("My Promotions")
        lat = MainActivity.userLat.toString() + ""
        lng = MainActivity.userLang.toString() + ""
        try {
            binding!!.tvLocation.text = MainActivity.address
        } catch (e: Exception) {
        }
        GetTenData()
        binding!!.llLocation.setOnClickListener {
            binding!!.llLocation.isEnabled = false
//            val intent1 = Intent(this@MY_PromotionActivity, SelectLocationActivityStore::class.java)
            val intent1 = Intent(this@MY_PromotionActivity, SelectLocationActivity::class.java)

            startActivityForResult(intent1, 40)
        }
        binding!!.downArrowImg.setOnClickListener {
            binding!!.llLocation.isEnabled = false
            //            val intent1 = Intent(this@MY_PromotionActivity, SelectLocationActivityStore::class.java)
            val intent1 = Intent(this@MY_PromotionActivity, SelectLocationActivity::class.java)
            startActivityForResult(intent1, 40)
        }
        binding!!.searchEtv.setOnClickListener {
            val i = Intent(this@MY_PromotionActivity, SearchAllDealActivity::class.java)
            i.putExtra("lat", lat)
            i.putExtra("lng", lng)
            startActivityForResult(i, 101)
        }
        binding!!.allCategoryImg.setOnClickListener {
            startActivity(Intent(this@MY_PromotionActivity, Categories::class.java))
            MyLog.onAnim(this@MY_PromotionActivity)
        }
        binding!!.notificationImg.setOnClickListener {
            startActivity(Intent(this@MY_PromotionActivity, NewNotification::class.java)
                .putExtra("agentId", "")
                .putExtra("title", "Notifications"))
            MyLog.onAnim(this@MY_PromotionActivity)
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            StatusBarcolor.setStatusbarColor(this@MY_PromotionActivity, "")
        } catch (e: Exception) {
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("Ress", requestCode.toString() + "")
        if (requestCode == 40 && resultCode == RESULT_OK) {
            try {
                val name = data!!.getStringExtra("name")
                lat = data.getStringExtra("lat")
                lng = data.getStringExtra("lng")
                GetTenData()
                Log.e("Lat", "$name,$lat,$lng")
                binding!!.tvLocation.text = name
                try {
                    MainActivity.address = name
                    MainActivity.userLat = data.getStringExtra("lat")!!.toDouble()
                    MainActivity.userLang = data.getStringExtra("lng")!!.toDouble()
                } catch (e: Exception) {
                }
            } catch (e: Exception) {
                Log.e("Ex", e.toString())
            }
        }
    }

    private fun GetTenData() {
        if (AppUtil.isNetworkAvailable(this)) {
            binding!!.shimmerViewContainer.visibility = View.VISIBLE
            binding!!.shimmerViewContainer.startShimmerAnimation()
            binding!!.myPramotionRcv.visibility = View.GONE
            val dialogManager = DialogManager()
            if (binding!!.shimmerViewContainer.visibility == View.GONE) {
                dialogManager.showProcessDialog(this, "", false, null)
            }
            val call = AppConfig.api_Interface().getPromotionDealsList(lat, lng, "0", "10")
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        Log.e("ResssPor", response.toString())
                        try {
                            val obj = JSONObject(response.body()!!.string())
                            binding!!.shimmerViewContainer.visibility = View.GONE
                            binding!!.shimmerViewContainer.stopShimmerAnimation()
                            binding!!.myPramotionRcv.visibility = View.VISIBLE
                            dialogManager.stopProcessDialog()
                            Log.e(">>>", obj.toString())
                            if (obj.optString("error_type") == "200") {
                                val gson = Gson()
                                val example = gson.fromJson(obj.toString(), Example::class.java)
                                if (example.response.promotionDealsList.size > 0) {
                                    binding!!.myPramotionRcv.visibility = View.VISIBLE
                                    val side_rv_adapter =
                                        My_Promotion_List_Adapter(this@MY_PromotionActivity,
                                            example.response.promotionDealsList,
                                            example.response.promotionDealsList.size)
                                    binding!!.myPramotionRcv.layoutManager = LinearLayoutManager(
                                        this@MY_PromotionActivity,
                                        RecyclerView.VERTICAL,
                                        false)
                                    binding!!.myPramotionRcv.isNestedScrollingEnabled = false
                                    binding!!.myPramotionRcv.setItemViewCacheSize(example.response.promotionDealsList.size)
                                    binding!!.myPramotionRcv.adapter = side_rv_adapter
                                    side_rv_adapter.notifyDataSetChanged()
                                } else {
                                    binding!!.myPramotionRcv.visibility = View.GONE
                                }
                            } else {
                                AppUtil.showMsgAlert(binding!!.tvLocation, obj.optString("message"))
                            }
                        } catch (e: Exception) {
                            Log.e("Ex1", e.toString())
                            binding!!.shimmerViewContainer.visibility = View.GONE
                            binding!!.shimmerViewContainer.stopShimmerAnimation()
                            binding!!.myPramotionRcv.visibility = View.VISIBLE
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(binding!!.tvLocation, t.message)
                }
            })
        } else {
            ErrorMessage.T(this, "No Internet Found!")
        }
    }
}