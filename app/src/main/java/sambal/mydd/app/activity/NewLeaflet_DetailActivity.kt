package sambal.mydd.app.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import sambal.mydd.app.constant.IntentConstant
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import com.google.gson.Gson
import com.bumptech.glide.Glide
import sambal.mydd.app.R
import sambal.mydd.app.databinding.ActivityNewLeafletDetailBinding
import sambal.mydd.app.models.Ads_Detail_Models.Example
import sambal.mydd.app.utils.ErrorMessage
import sambal.mydd.app.utils.StatusBarcolor
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class NewLeaflet_DetailActivity : AppCompatActivity() {
    var binding: ActivityNewLeafletDetailBinding? = null
    var productId: String? = ""
    var agentId: String? = ""
    var example: Example? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_leaflet__detail)
        val bundle = intent.extras
        if (bundle != null) {
            agentId = bundle.getString("agentId")
            productId = bundle.getString("productId")
            GetAdsDetails()
        }
        binding!!.cancelImgBtn.setOnClickListener { finish() }
        binding!!.clickHereLayout.setOnClickListener {
            try {
                if (example!!.response.userRefer[0].adsProfileType == 2) {
                    if (example!!.response.userRefer[0].adsExternalURL.contains("page.link") || example!!.response.userRefer[0].adsExternalURL.contains(
                            "links.passto.app")
                    ) {
                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(example!!.response.userRefer[0].adsExternalURL)
                        startActivity(i)
                    } else {
                        val intent = Intent(this@NewLeaflet_DetailActivity, Webview::class.java)
                        intent.putExtra("title", example!!.response.userRefer[0].agentCompanyName)
                        intent.putExtra("url", example!!.response.userRefer[0].adsExternalURL)
                        intent.putExtra("type", "non_direct")
                        startActivity(intent)
                    }
                } else if (example!!.response.userRefer[0].adsExternalURL == null || example!!.response.userRefer[0].adsExternalURL == "") {
                    val intent =
                        Intent(this@NewLeaflet_DetailActivity, New_AgentDetails::class.java)
                    intent.putExtra("direct", "")
                    intent.putExtra(IntentConstant.INTENT_KEY_AGENT_ID, agentId + "")
                    intent.putExtra("product_id", productId + "")
                    intent.putExtra("position", "")
                    startActivity(intent)
                } else {
                    val intent =
                        Intent(this@NewLeaflet_DetailActivity, New_AgentDetails::class.java)
                    intent.putExtra("agentId", agentId)
                    intent.putExtra("direct", "ActivityStorePoint")
                    intent.putExtra("position", "")
                    startActivityForResult(intent, 140)
                }
            } catch (e: Exception) {
            }
        }
        binding!!.clickHereBtn.setOnClickListener {
            try {
                if (example!!.response.userRefer[0].adsProfileType == 2) {
                    if (example!!.response.userRefer[0].adsExternalURL.contains("page.link") || example!!.response.userRefer[0].adsExternalURL.contains(
                            "links.passto.app")
                    ) {
                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(example!!.response.userRefer[0].adsExternalURL)
                        startActivity(i)
                    } else {
                        val intent = Intent(this@NewLeaflet_DetailActivity, Webview::class.java)
                        intent.putExtra("title", example!!.response.userRefer[0].agentCompanyName)
                        intent.putExtra("url", example!!.response.userRefer[0].adsExternalURL)
                        intent.putExtra("type", "non_direct")
                        startActivity(intent)
                    }
                } else if (example!!.response.userRefer[0].adsExternalURL == null || example!!.response.userRefer[0].adsExternalURL == "") {
                    val intent =
                        Intent(this@NewLeaflet_DetailActivity, New_AgentDetails::class.java)
                    intent.putExtra("direct", "")
                    intent.putExtra(IntentConstant.INTENT_KEY_AGENT_ID, agentId + "")
                    intent.putExtra("product_id", productId + "")
                    intent.putExtra("position", "")
                    startActivity(intent)
                } else {
                    val intent =
                        Intent(this@NewLeaflet_DetailActivity, New_AgentDetails::class.java)
                    intent.putExtra("agentId", agentId)
                    intent.putExtra("direct", "ActivityStorePoint")
                    intent.putExtra("position", "")
                    startActivityForResult(intent, 140)
                }
            } catch (e: Exception) {
            }
        }
        binding!!.playIconImgBtn.setOnClickListener {
            try {
                val intent = Intent(this@NewLeaflet_DetailActivity, PlayVideoActivity::class.java)
                intent.putExtra("videoUrl", example!!.response.userRefer[0].adsVideoURL)
                startActivity(intent)
            } catch (e: Exception) {
            }
        }
        binding!!.sponserTv.setOnClickListener {
            val intent = Intent(this@NewLeaflet_DetailActivity, New_AgentDetails::class.java)
            intent.putExtra("direct", "")
            intent.putExtra(IntentConstant.INTENT_KEY_AGENT_ID, agentId + "")
            intent.putExtra("product_id", productId + "")
            intent.putExtra("position", "")
            startActivity(intent)
        }
    }

    private fun GetAdsDetails() {
        if (AppUtil.isNetworkAvailable(this)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(this, "", false, null)
            val call = AppConfig.api_Interface().getAdsDetails(productId, agentId)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        Log.e("GetAdsDetails", response.toString())
                        try {
                            val obj = JSONObject(response.body()!!.string())
                            dialogManager.stopProcessDialog()
                            if (obj.optString("error_type") == "200") {
                                val gson = Gson()
                                example = gson.fromJson(obj.toString(), Example::class.java)
                                binding!!.agentNameTv.text =
                                    "" + example!!.getResponse().userRefer[0].agentCompanyName
                                binding!!.adsDescribitionTv.text =
                                    "" + example!!.getResponse().userRefer[0].adsDescription
                                Glide.with(this@NewLeaflet_DetailActivity)
                                    .load(example!!.getResponse().userRefer[0].agentImage)
                                    .error(R.drawable.mainimageplaceholder) // show error drawable if the image is not a gif
                                    .into(binding!!.cardBgImg)
                                Glide.with(this@NewLeaflet_DetailActivity)
                                    .load(example!!.getResponse().userRefer[0].adsImage)
                                    .error(R.drawable.mainimageplaceholder) // show error drawable if the image is not a gif
                                    .into(binding!!.videoImg)
                                binding!!.adsTitleTv.text =
                                    "" + example!!.getResponse().userRefer[0].adsTitle
                                if (example!!.getResponse().userRefer[0].type == 1) {
                                    binding!!.playIconImgBtn.visibility = View.GONE
                                } else if (example!!.getResponse().userRefer[0].type == 2) {
                                    binding!!.playIconImgBtn.visibility = View.GONE
                                } else if (example!!.getResponse().userRefer[0].type == 3) {
                                    binding!!.playIconImgBtn.visibility = View.VISIBLE
                                } else if (example!!.getResponse().userRefer[0].type == 4) {
                                    binding!!.playIconImgBtn.visibility = View.GONE
                                }
                            } else {
                                AppUtil.showMsgAlert(binding!!.agentNameTv,
                                    obj.optString("message"))
                            }
                        } catch (e: Exception) {
                            Log.e("Ex1", e.toString())
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(binding!!.agentNameTv, t.message)
                }
            })
        } else {
            ErrorMessage.T(this, "No Internet Found!")
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            StatusBarcolor.setStatusbarColor(this@NewLeaflet_DetailActivity, "black")
        } catch (e: Exception) {
        }
    }
}