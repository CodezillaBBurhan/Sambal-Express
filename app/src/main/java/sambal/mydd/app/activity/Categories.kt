package sambal.mydd.app.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.squareup.picasso.Picasso
import sambal.mydd.app.R
import sambal.mydd.app.adapter.AdapterViewAllCategories
import sambal.mydd.app.beans.CategoryModel
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.databinding.ViewallcatBinding
import sambal.mydd.app.utils.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Categories : BaseActivity() {

    private var Check: String? = ""
    private lateinit var binding: ViewallcatBinding
    private var mlist = ArrayList<CategoryModel>()
    private lateinit var adap: AdapterViewAllCategories
    internal lateinit var dialogManager: DialogManager

    override val contentResId: Int
        get() = R.layout.viewallcat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@Categories, R.layout.viewallcat)
        setToolbarWithBackButton_colorprimary("")
        title_txt!!.text = "Categories"
        var bundle: Bundle? = intent.extras
        if (bundle != null) {
            Check = bundle.getString("Check")
        } else {
            Check = "Deal"
        }
        init()

        getCatList()

        binding.card1.setOnClickListener {

            if (Check.equals("viewAll")) {

                val intent = Intent()
                intent.putExtra("Cat_id", mlist.get(0).id)
                intent.putExtra("Cat_Name", mlist.get(0).name)
                setResult(210, intent)
                finish()
            } else {
                val intent = Intent(this@Categories, DealByCat_IDActivity::class.java)
                intent.putExtra("Cat_id", mlist.get(0).id)
                intent.putExtra("Cat_Name", mlist.get(0).name)
                startActivity(intent)
            }
        }
        binding.card2.setOnClickListener {
            if (Check.equals("viewAll")) {

                val intent = Intent()
                intent.putExtra("Cat_id", mlist.get(1).id)
                intent.putExtra("Cat_Name", mlist.get(1).name)
                setResult(210, intent)
                finish()
            } else {
                val intent = Intent(this@Categories, DealByCat_IDActivity::class.java)
                intent.putExtra("Cat_id", mlist.get(1).id)
                intent.putExtra("Cat_Name", mlist.get(1).name)
                startActivity(intent)
            }
        }
        binding.card3.setOnClickListener {
            if (Check.equals("viewAll")) {
                val intent = Intent()
                intent.putExtra("Cat_id", mlist.get(2).id)
                intent.putExtra("Cat_Name", mlist.get(2).name)
                setResult(210, intent)
                finish()
            } else {
                val intent = Intent(this@Categories, DealByCat_IDActivity::class.java)
                intent.putExtra("Cat_id", mlist.get(2).id)
                intent.putExtra("Cat_Name", mlist.get(2).name)
                startActivity(intent)
            }

        }
        binding.card4.setOnClickListener {
            if (Check.equals("viewAll")) {
                val intent = Intent()
                intent.putExtra("Cat_id", mlist.get(3).id)
                intent.putExtra("Cat_Name", mlist.get(3).name)
                setResult(210, intent)
                finish()
            } else {
                val intent = Intent(this@Categories, DealByCat_IDActivity::class.java)
                intent.putExtra("Cat_id", mlist.get(3).id)
                intent.putExtra("Cat_Name", mlist.get(3).name)
                startActivity(intent)
            }
        }
        binding.card5.setOnClickListener {
            if (Check.equals("viewAll")) {
                val intent = Intent()
                intent.putExtra("Cat_id", mlist.get(4).id)
                intent.putExtra("Cat_Name", mlist.get(4).name)
                setResult(210, intent)
                finish()
            } else {
                val intent = Intent(this@Categories, DealByCat_IDActivity::class.java)
                intent.putExtra("Cat_id", mlist.get(4).id)
                intent.putExtra("Cat_Name", mlist.get(4).name)
                startActivity(intent)
            }
        }
        binding.card6.setOnClickListener {
            if (Check.equals("viewAll")) {
                val intent = Intent()
                intent.putExtra("Cat_id", mlist.get(5).id)
                intent.putExtra("Cat_Name", mlist.get(5).name)
                setResult(210, intent)
                finish()
            } else {
                val intent = Intent(this@Categories, DealByCat_IDActivity::class.java)
                intent.putExtra("Cat_id", mlist.get(5).id)
                intent.putExtra("Cat_Name", mlist.get(5).name)
                startActivity(intent)
            }
        }
        binding.card7.setOnClickListener {
            if (Check.equals("viewAll")) {
                val intent = Intent()
                intent.putExtra("Cat_id", mlist.get(6).id)
                intent.putExtra("Cat_Name", mlist.get(6).name)
                setResult(210, intent)
                finish()
            } else {
                val intent = Intent(this@Categories, DealByCat_IDActivity::class.java)
                intent.putExtra("Cat_id", mlist.get(6).id)
                intent.putExtra("Cat_Name", mlist.get(6).name)
                startActivity(intent)
            }
        }
        binding.card8.setOnClickListener {
            if (Check.equals("viewAll")) {
                val intent = Intent()
                intent.putExtra("Cat_id", mlist.get(7).id)
                intent.putExtra("Cat_Name", mlist.get(7).name)
                setResult(210, intent)
                finish()
            } else {
                val intent = Intent(this@Categories, DealByCat_IDActivity::class.java)
                intent.putExtra("Cat_id", mlist.get(7).id)
                intent.putExtra("Cat_Name", mlist.get(7).name)
                startActivity(intent)
            }
        }
        binding.card9.setOnClickListener {
            if (Check.equals("viewAll")) {
                val intent = Intent()
                intent.putExtra("Cat_id", mlist.get(8).id)
                intent.putExtra("Cat_Name", mlist.get(8).name)
                setResult(210, intent)
                finish()
            } else {
                val intent = Intent(this@Categories, DealByCat_IDActivity::class.java)
                intent.putExtra("Cat_id", mlist.get(8).id)
                intent.putExtra("Cat_Name", mlist.get(8).name)
                startActivity(intent)
            }
        }
        binding.card10.setOnClickListener {
            if (Check.equals("viewAll")) {
                val intent = Intent()
                intent.putExtra("Cat_id", mlist.get(9).id)
                intent.putExtra("Cat_Name", mlist.get(9).name)
                setResult(210, intent)
                finish()
            } else {
                val intent = Intent(this@Categories, DealByCat_IDActivity::class.java)
                intent.putExtra("Cat_id", mlist.get(9).id)
                intent.putExtra("Cat_Name", mlist.get(9).name)
                startActivity(intent)
            }
        }
    }

    private fun getCatList() {
        binding.shimmerViewContainer.setVisibility(View.VISIBLE)
        binding.shimmerViewContainer.startShimmerAnimation()

        dialogManager = DialogManager()

        if (AppUtil.isNetworkAvailable(this@Categories)) {

            if ( binding.shimmerViewContainer.visibility == View.VISIBLE) {
            } else {
                dialogManager.showProcessDialog(this@Categories, "", false, null)
            }
            val call = AppConfig.api_Interface().categoryList
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e(">>>>>>>>", resp.toString())
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)

                            if (errorType.equals(
                                    KeyConstant.KEY_RESPONSE_CODE_200,
                                    ignoreCase = true
                                )
                            ) {
                                val responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                var arr = responseObj.optJSONArray("categoryList")
                                if (arr.length() > 0) {
                                    for (i in 0 until arr.length()) {
                                        var objCat = arr.getJSONObject(i)
                                        var id = objCat.optString("categoryId")
                                        var categoryName = objCat.optString("categoryName")
                                        var categoryImage = objCat.optString("categoryImage")

                                        var mm = CategoryModel(id, categoryName, categoryImage)
                                        mlist.add(mm)

                                    }
                                    runOnUiThread {
                                        binding.shimmerViewContainer.setVisibility(View.GONE)
                                        if (mlist.size > 0 && mlist.size > 10) {
                                            binding.rv.visibility = View.VISIBLE
                                            binding.mainLayout.visibility = View.GONE
                                        } else if (mlist.size > 0 && mlist.size < 10) {
                                            binding.rv.visibility = View.GONE
                                            binding.mainLayout.visibility = View.VISIBLE
                                        }
                                        adap = AdapterViewAllCategories(
                                            mlist,
                                            Check.toString(),
                                            this@Categories
                                        )
                                        binding.rv.isLayoutFrozen == true
                                        binding.rv.layoutManager =
                                            GridLayoutManager(this@Categories, 2)
                                        //  binding.rv.layoutManager = SpanningLinearLayoutManager(this@Categories,2)
                                        binding.rv.adapter = adap
                                        adap.notifyDataSetChanged()

                                        if (mlist.size > 0) {
                                            if (mlist.size >= 1) {
                                                binding.mainLayout.visibility = View.VISIBLE
                                                binding.tvCatName1.text = "" + mlist.get(0).name
                                                /*roundplaceholder*/
                                                if(mlist.get(0).image !=null ){
                                                    Picasso.with(this@Categories)
                                                        .load(mlist.get(0).image)
                                                        .placeholder(R.drawable.roundplaceholder)
                                                        .error(R.drawable.roundplaceholder)
                                                        .into(binding.iv1)}
                                            }
                                            if (mlist.size >= 2) {
                                                binding.card2.visibility = View.VISIBLE
                                                binding.tvCatName2.text = "" + mlist.get(1).name
                                                if(mlist.get(1).image !=null && !mlist.get(1).image.equals("")){
                                                    Picasso.with(this@Categories)
                                                        .load(mlist.get(1).image)
                                                        .placeholder(R.drawable.roundplaceholder)
                                                        .error(R.drawable.roundplaceholder)
                                                        .into(binding.iv2)}
                                            } else {
                                                binding.card2.visibility = View.GONE
                                            }
                                            if (mlist.size >= 3) {
                                                binding.tvCatName3.text = "" + mlist.get(2).name
                                                if(mlist.get(2).image !=null && !mlist.get(2).image.equals("")){
                                                    Picasso.with(this@Categories)
                                                        .load(mlist.get(2).image)
                                                        .placeholder(R.drawable.roundplaceholder)
                                                        .error(R.drawable.roundplaceholder)
                                                        .into(binding.iv3)}
                                            } else {
                                                binding.card3.visibility = View.GONE
                                            }
                                            if (mlist.size >= 4) {
                                                binding.tvCatName4.text = "" + mlist.get(3).name
                                                if(mlist.get(3).image !=null && !mlist.get(3).image.equals("")){
                                                    Picasso.with(this@Categories)
                                                        .load(mlist.get(3).image)
                                                        .placeholder(R.drawable.roundplaceholder)
                                                        .error(R.drawable.roundplaceholder)
                                                        .into(binding.iv4)}
                                            } else {
                                                binding.card4.visibility = View.GONE

                                            }
                                            if (mlist.size >= 5) {
                                                binding.tvCatName5.text = "" + mlist.get(4).name
                                                if(mlist.get(4).image !=null && !mlist.get(4).image.equals("")){
                                                    Picasso.with(this@Categories)
                                                        .load(mlist.get(4).image)
                                                        .placeholder(R.drawable.roundplaceholder)
                                                        .error(R.drawable.roundplaceholder)
                                                        .into(binding.iv5)}
                                            } else {
                                                binding.card5.visibility = View.GONE

                                            }
                                            if (mlist.size >= 6) {
                                                binding.tvCatName6.text = "" + mlist.get(5).name
                                                if(mlist.get(5).image !=null && !mlist.get(5).image.equals("")){
                                                    Picasso.with(this@Categories)
                                                        .load(mlist.get(5).image)
                                                        .placeholder(R.drawable.roundplaceholder)
                                                        .error(R.drawable.roundplaceholder)
                                                        .into(binding.iv6)}
                                            } else {
                                                binding.card6.visibility = View.GONE

                                            }
                                            if (mlist.size >= 7) {
                                                binding.tvCatName7.text = "" + mlist.get(6).name
                                                if(mlist.get(6).image !=null && !mlist.get(6).image.equals("")){
                                                    Picasso.with(this@Categories)
                                                        .load(mlist.get(6).image)
                                                        .placeholder(R.drawable.roundplaceholder)
                                                        .error(R.drawable.roundplaceholder)
                                                        .into(binding.iv7)}
                                            } else {
                                                binding.card7.visibility = View.GONE

                                            }
                                            if (mlist.size >= 8) {
                                                binding.tvCatName8.text = "" + mlist.get(7).name
                                                if(mlist.get(7).image !=null && !mlist.get(7).image.equals("")){
                                                    Picasso.with(this@Categories)
                                                        .load(mlist.get(7).image)
                                                        .placeholder(R.drawable.roundplaceholder)
                                                        .error(R.drawable.roundplaceholder)
                                                        .into(binding.iv8)}
                                            } else {
                                                binding.card8.visibility = View.GONE

                                            }
                                            if (mlist.size >= 9) {
                                                binding.tvCatName9.text = "" + mlist.get(8).name
                                                if(mlist.get(8).image !=null && !mlist.get(8).image.equals("")){
                                                    Picasso.with(this@Categories)
                                                        .load(mlist.get(8).image)
                                                        .placeholder(R.drawable.roundplaceholder)
                                                        .error(R.drawable.roundplaceholder)
                                                        .into(binding.iv9)}
                                            } else {
                                                binding.card9.visibility = View.GONE

                                            }
                                            if (mlist.size >= 10) {
                                                binding.tvCatName10.text = "" + mlist.get(9).name
                                                if(mlist.get(9).image !=null && !mlist.get(9).image.equals("")){
                                                    Picasso.with(this@Categories)
                                                        .load(mlist.get(9).image)
                                                        .placeholder(R.drawable.roundplaceholder)
                                                        .error(R.drawable.roundplaceholder)
                                                        .into(binding.iv10)}
                                            } else {
                                                binding.card10.visibility = View.GONE
                                            }
                                        } else {
                                            binding.mainLayout.visibility = View.GONE
                                        }
                                    }
                                }

                                dialogManager.stopProcessDialog()

                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(
                                        resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true
                                    )
                                ) {
// swipeRefreshLayout.setRefreshing(false);
                                    dialogManager.stopProcessDialog()
                                    AppUtil.showMsgAlert(
                                        binding.ivBack,
                                        resp.optString(KeyConstant.KEY_MESSAGE)
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("Excep", e.toString())
                            e.printStackTrace()
// swipeRefreshLayout.setRefreshing(false);
                            dialogManager.stopProcessDialog()
                            AppUtil.showMsgAlert(
                                binding.ivBack,
                                MessageConstant.MESSAGE_SOMETHING_WRONG
                            )
                        }

                    } else {
                        dialogManager.stopProcessDialog()
                        AppUtil.showMsgAlert(binding.ivBack, MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(binding.ivBack, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(binding.ivBack, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }


    fun init() {
        binding.ivBack.setOnClickListener {
            finish()
        }

    }

    fun getId(id: String) {
        val returnIntent = Intent()
        returnIntent.putExtra("id", id)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        try {
            StatusBarcolor.setStatusbarColor(this@Categories, "")
        } catch (e: Exception) {
        }
    }
}