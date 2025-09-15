package sambal.mydd.app.fragment

import androidx.appcompat.app.AppCompatActivity
import sambal.mydd.app.callback.RecyclerClickListener
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import org.json.JSONArray
import androidx.recyclerview.widget.LinearLayoutManager
import sambal.mydd.app.adapter.AdapterCategoriesNearMe
import sambal.mydd.app.beans.CategoryModel
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import sambal.mydd.app.utils.GPSTracker
import android.content.Intent
import sambal.mydd.app.utils.AppUtil
import sambal.mydd.app.authentication.SignUpActivity
import sambal.mydd.app.MainActivity
import android.text.TextWatcher
import android.text.Editable
import sambal.mydd.app.adapter.NearMeAdapter
import org.json.JSONObject
import sambal.mydd.app.utils.SavedData
import android.location.Geocoder
import android.content.Context
import android.location.Address
import android.util.Log
import android.view.View
import androidx.core.widget.NestedScrollView
import com.google.android.gms.location.*
import sambal.mydd.app.R
import sambal.mydd.app.activity.*
import sambal.mydd.app.utils.StatusBarcolor
import sambal.mydd.app.beans.NearMeModel
import sambal.mydd.app.constant.KeyConstant
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.AppConfig
import org.json.JSONException
import sambal.mydd.app.utils.ErrorMessage
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.constant.IntentConstant
import sambal.mydd.app.databinding.NearmemainBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception
import java.util.*

class NearMeHomeFragment : AppCompatActivity(), RecyclerClickListener, OnRefreshListener {
    var view: View? = null
    var agentListArray: JSONArray? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var adapNEarMeCat: AdapterCategoriesNearMe? = null
    var binding: NearmemainBinding? = null
    private var lastKnownLocation = ""
    private var context: Context? = null
    private var listLoadMore = 0
    private var count = "10"
    private var offset = 0
    private var catId: String? = "0"
    private var isCat = true
    var mCatlist = ArrayList<CategoryModel>()
    private var userLat = 0.0
    private var userLang = 0.0
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private var return_back: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  binding = DataBindingUtil.inflate(inflater, R.layout.nearmemain, container, false);
        binding = DataBindingUtil.setContentView(this, R.layout.nearmemain)
        view = binding!!.getRoot()
        context = this@NearMeHomeFragment
        binding!!.msg.text = "No Data"
        val mGPS = GPSTracker()
        val args = intent.extras
        if (args != null) {
            catId = args.getString("Cat_id")
            binding!!.titleTv.text = args.getString("Cat_Name")
            binding!!.calegoryLayout.visibility = View.VISIBLE
            binding!!.searchImg.visibility = View.VISIBLE
            binding!!.llLocation.visibility = View.GONE

            if (args.getString("return_back")!=null){
                return_back=args.getString("return_back");
            }
        }
        binding!!.nearLocationLayout.setOnClickListener { v: View? ->
            val intent = Intent(context, SelectLocationActivity::class.java)
            intent.putExtra("clickedBottomTab", "12")
            startActivityForResult(intent, 190)
        }
        binding!!.tvAll.setOnClickListener {
            startActivityForResult(Intent(this@NearMeHomeFragment, Categories::class.java).putExtra(
                "Check",
                "viewAll")
                .putExtra("list", mCatlist), 210)
        }
        binding!!.ivAll.setOnClickListener { binding!!.tvAll.performLongClick() }
        binding!!.searchImg.setOnClickListener {
            val intent = Intent(context, SearchAllNewActivity::class.java)
            intent.putExtra("page", "Near Me Search")
            startActivityForResult(intent, 90)
        }
        binding!!.ivNoti.setOnClickListener {
            if (!AppUtil.isNetworkAvailable(this@NearMeHomeFragment)) {
                startActivity(Intent(this@NearMeHomeFragment, SignUpActivity::class.java))
            } else {
                startActivity(Intent(this@NearMeHomeFragment, NewNotification::class.java))
            }
        }
        binding!!.ivHumburger.setOnClickListener {
            if (return_back.equals("")){
            finish()}
            else{
                finish()
               // ErrorMessage.I_clear(this@NearMeHomeFragment, MainActivity::class.java,null)
            }
        }
        try {
            MainActivity.tvLocation!!.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                    lastKnownLocation = MainActivity.tvLocation!!.text.toString()
                 //   Log.e("last known loc", lastKnownLocation)
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    if (!MainActivity.tvLocation!!.text.toString()
                            .equals("Location", ignoreCase = true)
                    ) {
                        if (lastKnownLocation.equals("Location", ignoreCase = true)) {
                            binding!!.shimmerViewContainer.visibility = View.VISIBLE
                            binding!!.shimmerViewContainer.startShimmerAnimation()
                            getAllMerchantsList(true, 0)
                        }
                    }
                }
            })
        } catch (e: Exception) {
          //  Log.e("fdsfdfdf", "" + e.toString())
        }
        linearLayoutManager =
            LinearLayoutManager(this@NearMeHomeFragment, LinearLayoutManager.VERTICAL, false)
        binding!!.recyclerView.setHasFixedSize(false)
        binding!!.recyclerView.layoutManager = linearLayoutManager
        listLoadMore = 0
        modelList.clear()
        count = "10"
        offset = 0
        adapter = null
        adapter = NearMeAdapter(context!!,
            modelList,
            binding!!.recyclerView,
            binding!!.nestedscrollview,
            this@NearMeHomeFragment) { jsonObject, eventHasMultipleParts -> }
        binding!!.recyclerView.setItemViewCacheSize(modelList.size)
        binding!!.recyclerView.adapter = adapter
        binding!!.recyclerView.isNestedScrollingEnabled = false
        binding!!.nestedscrollview.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (v.getChildAt(v.childCount - 1) != null) {
                if (scrollY >= v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight &&
                    scrollY > oldScrollY
                ) {
                    //code to fetch more data for endless scrolling
                    offset++
                    isCat = false
                    listLoadMore = 1
                    binding!!.shimmerViewContainer.visibility = View.GONE
                    getAllMerchantsList(true, 0)
                }
            }
        })
        listLoadMore = 0
        try {
            if (MainActivity.userLat != 0.0 || SavedData.getLatitude() != "0") {
                userLat = MainActivity.userLat
                userLang = MainActivity.userLang
                binding!!.tvToolbarLocation.text =
                    if (MainActivity.address == "") if (SavedData.getAddress() == "") "Location" else SavedData.getAddress() else MainActivity.address
                if (MainActivity.userLat != 0.0) {
                    userLat = MainActivity.userLat
                } else if (SavedData.getLatitude() != "0") {
                    userLat = SavedData.getLatitude()?.toDouble()!!
                }
                if (MainActivity.userLang != 0.0) {
                    userLang = MainActivity.userLang
                } else if (SavedData.getLongitude() != "0") {
                    userLang = SavedData.getLongitude()?.toDouble()!!
                }
            } else {
                try {
                    GPSTracker.requestSingleUpdate(this@NearMeHomeFragment,
                        object : GPSTracker.LocationCallback {
                            override fun onNewLocationAvailable(location: GPSTracker.GPSCoordinates?) {
                                if (location != null) {
                                    userLat = location.latitude.toDouble()
                                    userLang = location.longitude.toDouble()
                                    val geocoder: Geocoder
                                    val addresses: List<Address>?
                                    geocoder =
                                        Geocoder(this@NearMeHomeFragment, Locale.getDefault())
                                    try {
                                        addresses =
                                            geocoder.getFromLocation(location.latitude.toDouble(),
                                                location.longitude.toDouble(),
                                                1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                        val address =
                                            addresses!![0].locality // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                        binding!!.tvToolbarLocation.text = address
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                        })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
        }
        if (!catId.equals("0", ignoreCase = true)) {
            offset = 0
            isCat = true
            binding!!.shimmerViewContainer.visibility = View.VISIBLE
            binding!!.shimmerViewContainer.startShimmerAnimation()
           // Log.e("IF IS WORKING iiiiii", "")
            getAllMerchantsList(true, 0)
            for (i in mCatlist.indices) {
                if (mCatlist[i].id.equals(catId, ignoreCase = true)) {
                    AdapterCategoriesNearMe.poss = i
                }
            }
        } else {
            isCat = true
            catId = "0"
            AdapterCategoriesNearMe.poss = 0
            binding!!.shimmerViewContainer.visibility = View.VISIBLE
            binding!!.shimmerViewContainer.startShimmerAnimation()
          //  Log.e("ELSE IS WORKING iiiiii", "")
            getAllMerchantsList(true, 0)
        }

        binding!!.nearMePageErrorLayout.plsTryAgain.setOnClickListener {
            if (AppUtil.isNetworkAvailable(this)) {
                binding!!.nearMePageErrorLayout.someThingWentWrongLayout.visibility = View.GONE
                getAllMerchantsList(true, 0)
            } else {
                AppUtil.showMsgAlert(binding!!.titleTv, MessageConstant.MESSAGE_INTERNET_CONNECTION)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 1 || requestCode == 1) {
            try {
                refreshDetals(data!!.getStringExtra("result"))
                binding!!.rvTabs.scrollToPosition(data.getStringExtra("position")!!.toInt())
            } catch (e: Exception) {
            }
        } else if (resultCode == RESULT_OK && requestCode == 80 || resultCode == RESULT_OK && requestCode == 90) {
            catId = data!!.getStringExtra("id")
            for (i in mCatlist.indices) {
                if (mCatlist[i].id.equals(catId, ignoreCase = true)) {
                    AdapterCategoriesNearMe.poss = i
                    adapNEarMeCat!!.notifyDataSetChanged()
                    try {
                        binding!!.rvCat.scrollToPosition(i)
                    } catch (e: Exception) {
                    }
                    break
                }
            }
            isCat = false
            binding!!.shimmerViewContainer.visibility = View.VISIBLE
            binding!!.shimmerViewContainer.startShimmerAnimation()
            getAllMerchantsList(true, 0)
        } else if (resultCode == RESULT_OK && requestCode == 105) {
            linearLayoutManager =
                LinearLayoutManager(this@NearMeHomeFragment, LinearLayoutManager.VERTICAL, false)
            binding!!.recyclerView.setHasFixedSize(false)
            binding!!.recyclerView.layoutManager = linearLayoutManager
            listLoadMore = 0
            modelList.clear()
            count = "10"
            offset = 0
            adapter!!.notifyDataSetChanged()
            adapter = NearMeAdapter(context!!,
                modelList,
                binding!!.recyclerView,
                binding!!.nestedscrollview,
                this@NearMeHomeFragment) { jsonObject, eventHasMultipleParts -> }
            binding!!.recyclerView.setItemViewCacheSize(modelList.size)
            binding!!.recyclerView.adapter = adapter
            binding!!.recyclerView.isNestedScrollingEnabled = false
            isCat = true
            binding!!.shimmerViewContainer.visibility = View.VISIBLE
            binding!!.shimmerViewContainer.startShimmerAnimation()
            getAllMerchantsList(true, data!!.getIntExtra("position", 0))
            try {
            } catch (e: Exception) {
            }
        } else if (requestCode == 190 && resultCode == 2) {
            userLat = data!!.getStringExtra("latitude")!!.toDouble()
            userLang = data.getStringExtra("longitude")!!.toDouble()
            //  binding.tvLocation.setText(data.getStringExtra("locationName"));
            binding!!.tvToolbarLocation.text = data.getStringExtra("locationName")

            modelList.clear()
            try {
                MainActivity.userLat = userLat
                MainActivity.userLang = userLang
                MainActivity.address = data.getStringExtra("locationName")
            } catch (e: Exception) {
            }
            offset = 0
            binding!!.shimmerViewContainer.visibility = View.VISIBLE
            binding!!.shimmerViewContainer.startShimmerAnimation()
            getAllMerchantsList(true, 0)
        } else if (requestCode == 210 && resultCode == 210) {
         //   Log.e("Cat_id", "" + data!!.getStringExtra("Cat_id"))
            try {
                refresh_background(data!!.getStringExtra("Cat_id"))
                refreshDetals(data!!.getStringExtra("Cat_id")!!)
            } catch (e: Exception) {}
        }
    }

    public override fun onResume() {
        super.onResume()
        try {
            binding!!.toolBarFragment.visibility = View.VISIBLE
        } catch (e: Exception) {
        }
        if (adapter != null) {
            adapter!!.notifyDataSetChanged()
            adapter!!.setLoaded()
        }
        try {
            StatusBarcolor.setStatusbarColor(this@NearMeHomeFragment, "")
        } catch (e: Exception) {
        }
        try {
            if (MainActivity.address == "") {
                location
                GPSTracker.requestSingleUpdate(this@NearMeHomeFragment,
                    object : GPSTracker.LocationCallback {
                        override fun onNewLocationAvailable(location: GPSTracker.GPSCoordinates?) {
                            if (location != null) {
                                getLocationAddress(location.latitude.toDouble(),
                                    location.longitude.toDouble())
                            }
                        }
                    })
            }
            if (binding!!.tvToolbarLocation.text.toString().contains("Location")) {
                location
                GPSTracker.requestSingleUpdate(this@NearMeHomeFragment, object : GPSTracker.LocationCallback {
                    override fun onNewLocationAvailable(location: GPSTracker.GPSCoordinates?) {
                        if (location != null) {
                            getLocationAddress(location.latitude.toDouble(),
                                location.longitude.toDouble())
                        }
                    }
                })
            }
        } catch (e: Exception) {
        }
    }

    override fun setCellClicked(newsJSONObject: JSONObject, eventHasMultipleParts: String) {}
    private fun initView() {
        val loaderPosition = modelList.size
        if (agentListArray != null && agentListArray!!.length() > 0) {
            if (listLoadMore == 0) {
                modelList.clear()
            }
            for (i in 0 until agentListArray!!.length()) {
                val jsonObject = agentListArray!!.optJSONObject(i)
                val model = NearMeModel()
                model.agentAdsEnable =
                    jsonObject.optInt(KeyConstant.KEY_AGENT_ADS_ENABLE).toString() + ""
                model.agentImage = jsonObject.optString(KeyConstant.KEY_AGENT_IMAGE)
                model.agentId = jsonObject.optInt(KeyConstant.KEY_AGENT_ID).toString() + ""
                model.agentCompanyName = jsonObject.optString(KeyConstant.KEY_AGENT_COMPANY_NAME)
                model.agentDescription = jsonObject.optString(KeyConstant.KEY_AGENT_DESCRIPTION)
                model.agentRating = jsonObject.optInt(KeyConstant.KEY_AGENT_RATING).toString() + ""
                model.agentEnableDescription =
                    jsonObject.optString(KeyConstant.KEY_AGENT_ENABLE_DESCRIPTION)
                model.agentDistance = jsonObject.optString(KeyConstant.KEY_AGENT_DISTANCE)
                model.agentDealEnabled =
                    jsonObject.optInt(KeyConstant.KEY_AGENT_DEAL_ENABLED).toString() + ""
                model.agentVoucherEnabled =
                    jsonObject.optInt(KeyConstant.KEY_AGENT_VOUCHER_ENABLED).toString() + ""
                model.agentAddress = jsonObject.optString("agentAddress")
                model.moreVouchers = jsonObject.optString(KeyConstant.KEY_MORE_VOUCHERS)
                model.dealButtonEnable = jsonObject.optString(KeyConstant.KEY_DEAL_BUTTON_ENABLE)
                model.moreProduct = jsonObject.optString(KeyConstant.KEY_MORE_PRODUCT)
                model.productId = jsonObject.optInt(KeyConstant.KEY_PRODUCT_ID).toString() + ""
                model.ddPointsEnabled = jsonObject.optString("ddPointsEnabled")
                model.agentExternalUrlEnable =
                    jsonObject.optInt(KeyConstant.KEY_AGENT_EXTERNAL_URL_ENABLE).toString() + ""
                model.agentExternalUrl = jsonObject.optString(KeyConstant.KEY_AGENT_EXTERNAL_URL)
                modelList.add(model)
                if (listLoadMore == 1) {
                    adapter!!.notifyItemInserted(modelList.size)
                }
            }
            if (listLoadMore == 0) {
                binding!!.recyclerView.adapter = adapter
                adapter!!.notifyDataSetChanged()
            } else {
                adapter!!.setLoaded()
            }
        }
        if (modelList.size > 0) {
            binding!!.msg.visibility = View.GONE
            binding!!.recyclerView.visibility = View.VISIBLE
        } else {
            if (listLoadMore == 0) {
                binding!!.msg.visibility = View.VISIBLE
                binding!!.recyclerView.visibility = View.GONE
            }
        }
    }

    public override fun onPause() {
        binding!!.shimmerViewContainer.stopShimmerAnimation()
        super.onPause()
    }

    /*private void getAllMerchantsListOLD(boolean isShowingLoader, final int position) {
        if (listLoadMore == 0) {
            modelList.clear();
        }
        agentListArray = null;
        binding.loadMoreProgressBar.setVisibility(View.GONE);
        if (AppUtil.isNetworkAvailable(NearMeHomeFragment.this)) {
            final DialogManager dialogManager = new DialogManager();
            if (isShowingLoader) {
                dialogManager.showProcessDialog(context, "", false, null);
                if (binding.shimmerViewContainer.getVisibility() == View.VISIBLE) {
                    dialogManager.stopProcessDialog();
                }
            }
            String url = UrlConstant.BASE_URL + UrlConstant.URL_GET_ALL_MERCHANTS_LIST;
            Log.e("url", ">>" + url);
            StringRequest stringRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("NEarMeRes", response + "");
                            binding.shimmerViewContainer.stopShimmerAnimation();
                            binding.shimmerViewContainer.setVisibility(View.GONE);
                            if (response != null) {

                                try {
                                    agentListArray = null;
                                    JSONObject resp = new JSONObject(response.toString());
                                    String errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE);
                                    if (errorType.equals(KeyConstant.KEY_RESPONSE_CODE_200)) {
                                        if (resp.has(KeyConstant.KEY_RESPONSE)) {
                                            JSONObject responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE);
                                            if (isCat) {
                                                JSONArray arrCatList = responseObj.optJSONArray("categoryList");
                                                mCatlist.clear();
                                                for (int i = 0; i < arrCatList.length(); i++) {
                                                    JSONObject ojCat = arrCatList.optJSONObject(i);
                                                    String categoryId = ojCat.optString("categoryId");
                                                    String categoryName = ojCat.optString("categoryName");
                                                    String categoryImage = ojCat.optString("categoryImage");
                                                    CategoryModel cm = new CategoryModel(categoryId, categoryName, categoryImage);
                                                    mCatlist.add(cm);
                                                }
                                                adapNEarMeCat = new AdapterCategoriesNearMe(NearMeHomeFragment.this, mCatlist, NearMeHomeFragment.this, "-2");
                                                binding.rvCat.setLayoutManager(new LinearLayoutManager(NearMeHomeFragment.this, LinearLayoutManager.HORIZONTAL, false));
                                                binding.rvCat.setAdapter(adapNEarMeCat);
                                                binding.rvCat.setNestedScrollingEnabled(false);
                                                adapNEarMeCat.notifyDataSetChanged();
                                            }


                                            agentListArray = responseObj.optJSONArray(KeyConstant.KEY_AGENT_LIST);

//                                    swipeRefreshLayout.setRefreshing(false);
                                            dialogManager.stopProcessDialog();
                                            initView();
                                            Log.e("posssss", position + "");
                                            binding.recyclerView.scrollToPosition(position);

                                            //adapter.notifyDataSetChanged();

                                        } else {

                                            //  swipeRefreshLayout.setRefreshing(false);
                                            dialogManager.stopProcessDialog();
                                            initView();
                                            //adapter.setLoaded();

                                        }

                                    } else if (errorType.equals(KeyConstant.KEY_RESPONSE_CODE_202)) {

                                        dialogManager.stopProcessDialog();
                                        initView();
                                        if (listLoadMore == 0) {
                                            binding.msg.setVisibility(View.VISIBLE);
                                            binding.recyclerView.setVisibility(View.GONE);
                                        }

                                    } else {
                                        if (KeyConstant.KEY_MESSAGE_FALSE.equalsIgnoreCase(resp.optString(KeyConstant.KEY_STATUS))) {
                                            //swipeRefreshLayout.setRefreshing(false);
                                            dialogManager.stopProcessDialog();
                                            //AppUtil.showMsgAlert(binding.msg, resp.optString(KeyConstant.KEY_MESSAGE));
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    //   swipeRefreshLayout.setRefreshing(false);
                                    dialogManager.stopProcessDialog();
                                    //AppUtil.showMsgAlert(binding.msg, MessageConstant.MESSAGE_SOMETHING_WRONG);
                                }
                            } else {
                                Log.e("eeee", "error");
                                //  swipeRefreshLayout.setRefreshing(false);
                                dialogManager.stopProcessDialog();
                                //AppUtil.showMsgAlert(binding.msg, MessageConstant.MESSAGE_SOMETHING_WRONG);
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            NetworkResponse networkResponse = error.networkResponse;
                            if (networkResponse != null && networkResponse.data != null) {
                                String jsonError = new String(networkResponse.data);
                                Log.e("Errror", jsonError);
                                dialogManager.stopProcessDialog();

                            }

                        }

                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");

                    if (PreferenceHelper.getInstance(context).getIsLogin() == true) {
                        headers.put(UrlConstant.HEADER_AUTHORIZATION, "Bearer" + " " + PreferenceHelper.getInstance(context).getAccessToken());
                    }
                    headers.put(UrlConstant.HEADER_DEVICE_ID, SharedPreferenceVariable.loadSavedPreferences(context, KeyConstant.Shar_DeviceID));

                    return headers;
                }

                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(KeyConstant.KEY_LATITUDE, String.valueOf(userLat));
                    params.put(KeyConstant.KEY_LONG, String.valueOf(userLang));
                    params.put(KeyConstant.KEY_CHAT_OFFSET, offset + "");
                    params.put(KeyConstant.KEY_CHAT_COUNT, "10");
                    params.put("categoryId", catId);
                    Log.e("getAllMerchant params", ">>" + params.toString());
                    return params;
                }

            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            // Access the RequestQueue through singleton class.
            MySingleton.getInstance(context).addToRequestQueue(stringRequest);
        } else {
            AppUtil.showMsgAlert(binding.titleTv, MessageConstant.MESSAGE_INTERNET_CONNECTION);
        }
    }*/
    private fun getAllMerchantsList(isShowingLoader: Boolean, position: Int) {
        if (listLoadMore == 0) {
            modelList.clear()
        }
        agentListArray = null
        binding!!.loadMoreProgressBar.visibility = View.GONE
        if (AppUtil.isNetworkAvailable(this@NearMeHomeFragment)) {
            binding!!.nearMePageErrorLayout.someThingWentWrongLayout.visibility = View.GONE
            val dialogManager = DialogManager()
            if (isShowingLoader) {
                dialogManager.showProcessDialog(context, "", false, null)
                if (binding!!.shimmerViewContainer.visibility == View.VISIBLE) {
                    dialogManager.stopProcessDialog()
                }
            }
            val call = AppConfig.api_Interface().getAllMerchantsList(userLat.toString(),
                userLang.toString(),
                offset.toString(),
                "10",
                catId)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>,
                ) {
                    binding!!.shimmerViewContainer.stopShimmerAnimation()
                    binding!!.shimmerViewContainer.visibility = View.GONE
                    if (response.isSuccessful) {
                        try {
                            binding!!.nearMePageErrorLayout.someThingWentWrongLayout.visibility = View.GONE
                            agentListArray = null
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("NearMeList", resp.toString() + "")
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                if (resp.has(KeyConstant.KEY_RESPONSE)) {
                                    val responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                    if (isCat) {
                                        val arrCatList = responseObj.optJSONArray("categoryList")
                                        mCatlist.clear()
                                        for (i in 0 until arrCatList.length()) {
                                            val ojCat = arrCatList.optJSONObject(i)
                                            val categoryId = ojCat.optString("categoryId")
                                            val categoryName = ojCat.optString("categoryName")
                                            val categoryImage = ojCat.optString("categoryImage")
                                            val cm = CategoryModel(categoryId,
                                                categoryName,
                                                categoryImage)
                                            mCatlist.add(cm)
                                        }
                                        adapNEarMeCat =
                                            AdapterCategoriesNearMe(this@NearMeHomeFragment,
                                                mCatlist,
                                                this@NearMeHomeFragment,
                                                "-2")
                                        binding!!.rvCat.layoutManager =
                                            LinearLayoutManager(this@NearMeHomeFragment,
                                                LinearLayoutManager.HORIZONTAL,
                                                false)
                                        binding!!.rvCat.adapter = adapNEarMeCat
                                        binding!!.rvCat.isNestedScrollingEnabled = false
                                        adapNEarMeCat!!.notifyDataSetChanged()
                                    }
                                    agentListArray =
                                        responseObj.optJSONArray(KeyConstant.KEY_AGENT_LIST)

//                                    swipeRefreshLayout.setRefreshing(false);
                                    dialogManager.stopProcessDialog()
                                    initView()
                                    Log.e("posssss", position.toString() + "")
                                    binding!!.recyclerView.scrollToPosition(position)

                                    //adapter.notifyDataSetChanged();
                                } else {

                                    //  swipeRefreshLayout.setRefreshing(false);
                                    dialogManager.stopProcessDialog()
                                    initView()
                                    //adapter.setLoaded();
                                }
                            } else if (errorType == KeyConstant.KEY_RESPONSE_CODE_202) {
                                dialogManager.stopProcessDialog()
                                initView()
                                if (listLoadMore == 0) {
                                    binding!!.msg.visibility = View.VISIBLE
                                    binding!!.recyclerView.visibility = View.GONE
                                }
                            } else {
                                if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(KeyConstant.KEY_STATUS),
                                        ignoreCase = true)
                                ) {
                                    //swipeRefreshLayout.setRefreshing(false);
                                    dialogManager.stopProcessDialog()
                                    //AppUtil.showMsgAlert(binding.msg, resp.optString(KeyConstant.KEY_MESSAGE));
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            //   swipeRefreshLayout.setRefreshing(false);
                            dialogManager.stopProcessDialog()
                            binding!!.nearMePageErrorLayout.someThingWentWrongLayout.visibility = View.VISIBLE
                            //AppUtil.showMsgAlert(binding.msg, MessageConstant.MESSAGE_SOMETHING_WRONG);
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        Log.e("sendToken", "else is working" + response.code().toString())
                        binding!!.nearMePageErrorLayout.someThingWentWrongLayout.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    AppUtil.showMsgAlert(binding!!.tvAll, t.message)
                    binding!!.nearMePageErrorLayout.someThingWentWrongLayout.visibility = View.VISIBLE

                }
            })
        } else {

            binding!!.nearMePageErrorLayout.someThingWentWrongLayout.visibility = View.VISIBLE

            AppUtil.showMsgAlert(binding!!.titleTv, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    override fun onRefresh() {
        offset = 0
        listLoadMore = 0
        adapter!!.setLoaded()
        binding!!.shimmerViewContainer.visibility = View.VISIBLE
        binding!!.shimmerViewContainer.startShimmerAnimation()
        getAllMerchantsList(false, 0)
        adapter!!.notifyDataSetChanged()
    }

    fun refreshDetals(catIds: String?) {
        if (AppUtil.isNetworkAvailable(context)) {
            listLoadMore = 0
            count = "10"
            offset = 0
            catId = catIds
            linearLayoutManager =
                LinearLayoutManager(this@NearMeHomeFragment, LinearLayoutManager.VERTICAL, false)
            binding!!.recyclerView.setHasFixedSize(false)
            binding!!.recyclerView.layoutManager = linearLayoutManager
            listLoadMore = 0
            modelList.clear()
            adapter = null
            adapter = NearMeAdapter(context!!,
                modelList,
                binding!!.recyclerView,
                binding!!.nestedscrollview,
                this@NearMeHomeFragment) { jsonObject, eventHasMultipleParts -> }
            binding!!.recyclerView.setItemViewCacheSize(modelList.size)
            binding!!.recyclerView.adapter = adapter
            adapter!!.setLoaded()
            isCat = false
            binding!!.shimmerViewContainer.visibility = View.VISIBLE
            binding!!.shimmerViewContainer.startShimmerAnimation()
            getAllMerchantsList(true, 0)
        } else {
            AppUtil.showMsgAlert(binding!!.titleTv, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }

    fun gotoAgentDetails(agentId: String, productId: String, adapterPosition: Int) {
        val intent = Intent(context, New_AgentDetails::class.java)
        intent.putExtra("direct", "")
        intent.putExtra(IntentConstant.INTENT_KEY_AGENT_ID, agentId + "")
        intent.putExtra("product_id", productId + "")
        intent.putExtra("position",
            (binding!!.recyclerView.layoutManager as LinearLayoutManager?)!!.findFirstCompletelyVisibleItemPosition())
        startActivity(intent)
        // startActivityForResult(intent, 105);
    }

    fun refresh_background(id: String?) {
        adapNEarMeCat =
            AdapterCategoriesNearMe(this@NearMeHomeFragment, mCatlist, this@NearMeHomeFragment,
                id.toString())
        binding!!.rvCat.layoutManager =
            LinearLayoutManager(this@NearMeHomeFragment, LinearLayoutManager.HORIZONTAL, false)
        binding!!.rvCat.adapter = adapNEarMeCat
        binding!!.rvCat.isNestedScrollingEnabled = false
        adapNEarMeCat!!.notifyDataSetChanged()
    }

    private fun getLocationAddress(latitude: Double, longi: Double) {
        Log.e("LcoationsAa", "LocationaAssa")
        val geocoder = Geocoder(this, Locale.getDefault())
        var addresses: List<Address>? = null
        try {
            addresses = geocoder.getFromLocation(latitude, longi, 1)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            if (addresses != null && addresses.size > 0) {
                val cityName = addresses[0].getAddressLine(0)
                val fullAddress = addresses[0].getAddressLine(1)
                val city = addresses[0].locality
                val countryName = addresses[0].getAddressLine(2)
                Log.e("City", "$city,$cityName")
                if (binding!!.tvToolbarLocation.text.toString().trim { it <= ' ' }
                        .equals("Location", ignoreCase = true)) {
                    if (city == null) {
                        binding!!.tvToolbarLocation.text = cityName
                        MainActivity.address = cityName
                    } else {
                        binding!!.tvToolbarLocation.text = city
                        MainActivity.address = city
                    }
                }
                userLat = latitude
                userLang = longi
                getAllMerchantsList(true, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val location: Unit
        private get() {
            locationRequest = LocationRequest()
                .setInterval(2000).setFastestInterval(2000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest!!)
            val client = LocationServices.getSettingsClient(this)
            val task = client.checkLocationSettings(builder.build())
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    for (location in locationResult.locations) {
                        try {
                            Log.e("Location on Splash is ", location.toString())
                            userLat = location.latitude
                            userLang = location.longitude
                            getLocationAddress(location.latitude, location.longitude)
                        } catch (ew: Exception) {
                        }
                    }
                }
            }
        }

    companion object {
        @JvmField
        var adapter: NearMeAdapter? = null

        @JvmField
        var modelList: MutableList<NearMeModel> = ArrayList()
    }

    override fun onBackPressed() {
        if (!return_back.equals("")){
            finish()
           // ErrorMessage.I_clear(this@NearMeHomeFragment, MainActivity::class.java,null)
        }
        super.onBackPressed()
    }
}