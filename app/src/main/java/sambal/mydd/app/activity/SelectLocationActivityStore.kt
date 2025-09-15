package sambal.mydd.app.activity

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.SupportMapFragment
import org.json.JSONArray
import com.google.android.gms.maps.GoogleMap
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import sambal.mydd.app.database.DatabaseHandler
import sambal.mydd.app.beans.LocationModel
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import sambal.mydd.app.utils.SharedPreferenceVariable
import sambal.mydd.app.constant.KeyConstant
import com.google.android.libraries.places.api.Places
import sambal.mydd.app.utils.StatusBarcolor
import android.annotation.SuppressLint
import android.app.Dialog
import android.location.LocationManager
import android.location.Geocoder
import android.os.Build
import com.google.android.libraries.places.api.model.Place
import android.content.Intent
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.SeekBar
import android.content.Context
import sambal.mydd.app.utils.AppUtil
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.android.libraries.places.widget.AutocompleteActivity
import sambal.mydd.app.adapter.SearchLocationDBAdapter
import android.text.TextUtils
import sambal.mydd.app.MainActivity
import sambal.mydd.app.utils.SavedData
import android.content.pm.PackageManager
import android.graphics.Canvas
import com.google.android.gms.location.LocationServices
import kotlin.jvm.Synchronized
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.maps.CameraUpdateFactory
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Location
import android.net.Uri
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import org.json.JSONException
import sambal.mydd.app.utils.ErrorMessage
import sambal.mydd.app.constant.MessageConstant
import com.google.android.gms.maps.model.*
import sambal.mydd.app.R
import sambal.mydd.app.SplashActivity
import sambal.mydd.app.constant.IntentConstant
import sambal.mydd.app.databinding.ActivitySelectLocationBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception
import java.util.*

class SelectLocationActivityStore : AppCompatActivity(), OnMapReadyCallback,
    GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,
    View.OnClickListener, LocationListener, android.location.LocationListener {
    private var binding: ActivitySelectLocationBinding? = null
    private val MY_PERMISSIONS_REQUEST_LOCATION = 99

    /*private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(
            new LatLng(-0, 0), new LatLng(0, 0));*/
    var mapFrag: SupportMapFragment? = null
    var mLocationRequest: LocationRequest? = null
    var mLastLocation: Location? = null
    var mCurrLocationMarker: Marker? = null
    var agentJSONArrayList: JSONArray? = null
    var context: Context? = null
    var handler: Handler? = null
    var markerOptions: MarkerOptions? = null
    var clickedBottomTab = 1 //lastSelectedTabOnHomeScreen = 1
    var location: Location? = null
    var MIN_TIME_BW_UPDATES = 10000
    var MIN_DISTANCE_CHANGE_FOR_UPDATES = 10000
    var AUTOCOMPLETE_REQUEST_CODE = 1
    private var map: GoogleMap? = null
    private var rlLocationList: RelativeLayout? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var llm: LinearLayoutManager? = null
    private var setLocationFirstTime = 0
    private var latitude = 0.0
    private var longitude = 0.0
    private var milesValue = "1"
    private var strCityName: String? = ""
    private var locationFullName: String? = ""
    private var db: DatabaseHandler? = null
    private var dbLocationList: List<LocationModel>? = ArrayList()
    private var circle: Circle? = null
    private var lastMiles = 1
    var gpsAlertDialog: Dialog? = null
    var permissionDialog: Dialog? = null
    var countStoragePermission = 0
    var locationcheckingpermission=0
    var checkloadingmerchantlist=1

    private val LOCATION_PERMISSION_REQUEST_CODE = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_location)
        context = this
        db = DatabaseHandler(this)
        if (!Places.isInitialized()) {
            if (SharedPreferenceVariable.loadSavedPreferences(this@SelectLocationActivityStore,
                    KeyConstant.KEY_GOOGLE_API_KEY) != null && SharedPreferenceVariable.loadSavedPreferences(
                    this@SelectLocationActivityStore,
                    KeyConstant.KEY_GOOGLE_API_KEY) != ""
            ) {
                Places.initialize(applicationContext,
                    SharedPreferenceVariable.loadSavedPreferences(this@SelectLocationActivityStore,
                        KeyConstant.KEY_GOOGLE_API_KEY).toString())
            }
        }

        handler = Handler()
        if (intent.extras != null) {
            clickedBottomTab = intent.getIntExtra("clickedBottomTab", 1)
        }
        mapFrag = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFrag!!.getMapAsync(this)
        initToolBar()
        init()
        setLocFromDb()
        if (!getLocation()) {
        }

    }



    public override fun onResume() {
        super.onResume()
        try {
            StatusBarcolor.setStatusbarColor(this@SelectLocationActivityStore, "colorPrimary")
        } catch (e: Exception) {
        }
    }




    @SuppressLint("MissingPermission")
    fun getLocation(): Boolean {
        var isLocation = false
        try {
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (isNetworkEnabled) {
                Log.d("Network", "Network Enabled")

                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES.toLong(),
                    MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                Log.d("Network", "Network Enabled")
                if (locationManager != null) {
                    location = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    if (location != null) {
                        latitude = location!!.latitude
                        longitude = location!!.longitude


                        locationManager.removeUpdates(this)
                        locationAddress
                    }
                }
            }
            isLocation = latitude != 0.0
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return isLocation
    }

    private val locationAddress: Unit
        private get() {
            val geocoder = Geocoder(this, Locale.getDefault())
            var addresses: List<Address>? = null
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                if (addresses != null && addresses.size > 0) {
                    val cityName = addresses[0].getAddressLine(0)
                    val fullAddress = addresses[0].getAddressLine(1)
                    val city = addresses[0].locality
                    val countryName = addresses[0].getAddressLine(2)
                    if (city == null) {
                        binding!!.tvPlace.visibility = View.VISIBLE
                        binding!!.tvCurrentLocation.text = ""
                        binding!!.etLocationSearch.clearFocus()
                        binding!!.tvPlace.text = cityName
                        binding!!.tvPlace.text = cityName
                        binding!!.tvCurrentLocation.text = ""
                        locationFullName = cityName
                        strCityName = cityName
                    } else {
                        binding!!.tvPlace.visibility = View.VISIBLE
                        binding!!.etLocationSearch.clearFocus()
                        binding!!.tvPlace.text = cityName
                        binding!!.tvCurrentLocation.text = ""
                        binding!!.tvCurrentLocation.text = ""
                        locationFullName = city
                        strCityName = city
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            ErrorMessage.E("searchMerchants1")
            if(checkloadingmerchantlist==1) {
                checkloadingmerchantlist=0;
                searchMerchants
            }
        }

    private fun initToolBar() {
        binding!!.toolbarTitle.text = "Select Location"
        binding!!.toolbarLeftImage.visibility = View.VISIBLE
        binding!!.toolbarRightImage.isClickable = true
        binding!!.toolbarRightImage.visibility = View.GONE
        binding!!.tvVisitUs.visibility = View.INVISIBLE
        binding!!.tvVisitUs.setOnClickListener { finish() }
        binding!!.toolbarLeftImage.setOnClickListener { finish() }
        setSupportActionBar(binding!!.toolbar)
    }

    private fun init() {
        binding!!.btnSearch.setOnClickListener(this)
        binding!!.recyclerView.setHasFixedSize(true)
        binding!!.recyclerView.layoutManager = LinearLayoutManager(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding!!.seekbarSetMiles.min = 1
        }
        binding!!.llCurrentLocation.setOnClickListener(this)
        rlLocationList = findViewById(R.id.rl_location_list)
        rlLocationList!!.setVisibility(View.GONE)
        binding!!.locationRecyclerView.setHasFixedSize(true)
        llm = LinearLayoutManager(context)
        binding!!.locationRecyclerView.layoutManager = llm
        binding!!.etLocationSearch.setOnClickListener {
            val fields = Arrays.asList(Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG)
            try {
                val intent = Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, fields)
                    .build(this@SelectLocationActivityStore)
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
            } catch (e: Exception) {
            }
        }
        binding!!.seekbarSetMiles.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                var progress = progress
                progress = progress + 1
                lastMiles = progress
                map!!.clear()
                try {
                    setRadius(lastMiles, latitude, longitude)
                } catch (e: Exception) {
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    binding!!.seekbarSetMiles.min = 1
                }
                binding!!.tvSearchMiles.text = "$progress miles"
                milesValue = progress.toString() + ""
                ErrorMessage.E("searchMerchants2")

                if(checkloadingmerchantlist==1) {
                    checkloadingmerchantlist=0;
                    searchMerchants
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val places = Autocomplete.getPlaceFromIntent(data!!)
                Log.e("Success", "Place: " + places.name + ", " + places.id)
                val address = places.address
                val place = places.name
                val lat = places.latLng!!.latitude
                val lng = places.latLng!!.longitude
                latitude = lat
                longitude = lng
                val latLng = LatLng(lat, lng)
                locationFullName = address
                binding!!.tvCurrentLocation.text = ""
                binding!!.tvPlace.visibility = View.VISIBLE
                binding!!.tvPlace.text = address
                binding!!.etLocationSearch.setText(address)
                binding!!.etLocationSearch.clearFocus()
                strCityName = place
                rlLocationList!!.visibility = View.GONE
                AppUtil.hideSoftKeyboard(context)
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker!!.remove()
                }

                ErrorMessage.E("searchMerchants3")

                if(checkloadingmerchantlist==1) {
                    checkloadingmerchantlist=0;
                    searchMerchants
                }

                setRadius(lastMiles, lat, lng)
                var smallMarkerIcon: BitmapDescriptor? = null
                if (map != null) {
                    val height = 50
                    val width = 50
                    val b = BitmapFactory.decodeResource(resources, R.drawable.marker)
                    val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
                    smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker)
                }
                try {
                    markerOptions!!.position(latLng)
                    markerOptions!!.icon(smallMarkerIcon)
                    val markerOptions = MarkerOptions()
                    markerOptions.position(latLng)
                    //markerOptions.title("Current Position");
                    markerOptions.icon(smallMarkerIcon)
                    mCurrLocationMarker = map!!.addMarker(markerOptions)
                } catch (e: Exception) {
                }
            } else {
            }
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            // TODO: Handle the error.
            val status = Autocomplete.getStatusFromIntent(data!!)
            Log.e("Error", status.statusMessage!!)
        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.
        }
    }

    private fun setLocFromDb() {
        dbLocationList = db!!.favLocationList
        if (dbLocationList != null && dbLocationList!!.size > 0) {
            val adapter = SearchLocationDBAdapter(context!!, dbLocationList!!) { model ->
                binding!!.etLocationSearch.hint = "SEARCH LOCATION"
                binding!!.tvPlace.text = model.locationName
                binding!!.etLocationSearch.setText(model.locationName)
                rlLocationList!!.visibility = View.GONE
                binding!!.seekbarSetMiles.progress = model.distance.toInt()
                val latLng = LatLng(model.latitude.toDouble(), model.longitude.toDouble())
                latitude = model.latitude.toDouble()
                longitude = model.longitude.toDouble()
                try {
                    val height = 50
                    val width = 50
                    val b = BitmapFactory.decodeResource(resources, R.drawable.marker)
                    val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
                    val smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker)
                    markerOptions!!.position(latLng)
                    markerOptions!!.icon(smallMarkerIcon)
                    val markerOptions = MarkerOptions()
                    markerOptions.position(latLng)
                    markerOptions.icon(smallMarkerIcon)
                    mCurrLocationMarker = map!!.addMarker(markerOptions)
                } catch (e: Exception) {
                }
                setRadius(lastMiles, latitude, longitude)
                val geocoder = Geocoder(this@SelectLocationActivityStore, Locale.getDefault())
                var addresses: List<Address>? = null
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                try {
                    if (addresses != null && addresses.size > 0) {
                        val cityName = addresses[0].getAddressLine(0)
                        val fullAddress = addresses[0].getAddressLine(0)
                        val city = addresses[0].locality
                        val countryName = addresses[0].getAddressLine(2)
                        if (city == null) {
                            locationFullName = fullAddress
                            strCityName = cityName
                        } else {
                            locationFullName = fullAddress
                            strCityName = city
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            binding!!.recyclerView.adapter = adapter
            if (dbLocationList!!.size > 0) {
                binding!!.recyclerView.visibility = View.VISIBLE
            } else {
                binding!!.recyclerView.visibility = View.GONE
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ll_current_location -> {

                requestPermission()


                val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                }
                else {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                            MY_PERMISSIONS_REQUEST_LOCATION)
                    }

                    else{
                        alertPopupForGps()
                    }

                }



                if (binding!!.tvCurrentLocation.text.toString().trim { it <= ' ' }.length == 0) {
                    binding!!.etLocationSearch.clearFocus()
                    myCurrentLocation
                }
            }


            R.id.ivBack -> finish()
            R.id.btn_search -> if (latitude == 0.0) {
                AppUtil.showMsgAlert(binding!!.tvCurrentLocation, "Location cannot be empty")
                return
            } else if (TextUtils.isEmpty(binding!!.etLocationSearch.text.toString()
                    .trim { it <= ' ' }) && TextUtils.isEmpty(
                    binding!!.tvPlace.text.toString().trim { it <= ' ' })
            ) {
                AppUtil.showMsgAlert(binding!!.tvCurrentLocation, "Location cannot be empty")
                return
            } else {
                try {
                    MainActivity.userLat = latitude
                    MainActivity.userLang = longitude
                    MainActivity.tvLocation!!.text =
                        "" + binding!!.tvPlace.text.toString().trim { it <= ' ' }
                } catch (e: Exception) {
                }
                SavedData.saveLatitude(latitude.toString())
                SavedData.saveLongitude(longitude.toString())
                val returnIntent = Intent()
                returnIntent.putExtra("lat", latitude.toString())
                returnIntent.putExtra("lng", longitude.toString())
                returnIntent.putExtra("name", binding!!.tvPlace.text.toString().trim { it <= ' ' })
                setResult(RESULT_OK, returnIntent)
                finish()
            }
        }
    }


    // TODO: Consider calling
    //    ActivityCompat#requestPermissions
    // here to request the missing permissions, and then overriding
    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
    //                                          int[] grantResults)
    // to handle the case where the user grants the permission. See the documentation
    // for ActivityCompat#requestPermissions for more details.

    private val myCurrentLocation: Unit
        private get() {
            getLocation()
//            locationAddress
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            map!!.isMyLocationEnabled = true

//            searchMerchants
            if (mCurrLocationMarker != null) {
                mCurrLocationMarker!!.remove()
            }
            setRadius(lastMiles, latitude, longitude)
            binding!!.etLocationSearch.setText("")
        }

    override fun onPause() {
        super.onPause()
        try {
            if (mGoogleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient!!, this)
            }
        } catch (e: Exception) {
        }
    }

    public override fun onStart() {
        super.onStart()
    }

    public override fun onStop() {
        super.onStop()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map!!.mapType = GoogleMap.MAP_TYPE_NORMAL
        map!!.uiSettings.isMyLocationButtonEnabled = false
        map!!.uiSettings.isScrollGesturesEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
            ) {
                //Location Permission already granted
                buildGoogleApiClient()
                map!!.isMyLocationEnabled = true
            } else {
            }
        } else {
            buildGoogleApiClient()
            map!!.isMyLocationEnabled = true
        }
    }

    @Synchronized
    protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        mGoogleApiClient!!.connect()
    }

    override fun onLocationChanged(location: Location) {
        run {
            mLastLocation = location
            val latLng = LatLng(location.latitude, location.longitude)
            if (setLocationFirstTime == 0) {
                latitude = location.latitude
                longitude = location.longitude

                ErrorMessage.E("searchMerchants4")

                if(checkloadingmerchantlist==1) {
                    checkloadingmerchantlist=0;
                    searchMerchants
                }

                setRadius(lastMiles, latitude, longitude)
            }
            setLocationFirstTime = 1
        }
    }

    override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
    override fun onProviderEnabled(s: String) {}
    override fun onProviderDisabled(s: String) {}

    @SuppressLint("RestrictedApi")
    override fun onConnected(bundle: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 1000
        mLocationRequest!!.fastestInterval = 1000
        mLocationRequest!!.priority =
            LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient!!,
                mLocationRequest!!,
                this)
        }
    }

    override fun onConnectionSuspended(i: Int) {}
    override fun onConnectionFailed(connectionResult: ConnectionResult) {}
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray,
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient()
                        }
                        map!!.isMyLocationEnabled = true
                    }
                } else {
                }
                return
            }
        }
    }

    fun setRadius(miles: Int, lat: Double, lang: Double) {
        if (markerOptions != null) {
            markerOptions!!.visible(false)
        }
        if (map != null && circle != null) {
            circle!!.remove()
        }
        // Instantiates a new CircleOptions object and defines the center and radius
        if (map != null) {
            circle = map!!.addCircle(CircleOptions()
                .center(LatLng(lat, lang))
                .radius(miles * 1609.34)
                .strokeWidth(0f)
                .strokeColor(resources.getColor(R.color.green_color_overlay))
                .fillColor(resources.getColor(R.color.green_color_overlay))
                .clickable(false)) // In meters
            circle!!.isVisible
            map!!.uiSettings.isScrollGesturesEnabled = true
            map!!.uiSettings.isZoomControlsEnabled = false
            val currentZoomLevel = getZoomLevel(circle)
            map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lang), currentZoomLevel))
            map!!.animateCamera(CameraUpdateFactory.zoomTo(currentZoomLevel), 2000, null)
            handler!!.postDelayed({
                if (circle != null) {
                    circle!!.isVisible = false
                    val circleDrawable = resources.getDrawable(R.drawable.circle)
                    val markerIcon = getMarkerIconFromDrawable(circleDrawable)
                    map!!.clear()

//                    ErrorMessage.E("searchMerchants6")

//                    searchMerchants
                    val height = 50
                    val width = 50
                    val b = BitmapFactory.decodeResource(resources, R.drawable.marker)
                    val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
                    val smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker)
                    markerOptions = MarkerOptions()
                    markerOptions!!.position(LatLng(lat, lang))
                    markerOptions!!.anchor(0.5f, 0.5f)
                    map!!.animateCamera(CameraUpdateFactory.newLatLng(LatLng(lat, lang)))
                    markerOptions!!.icon(markerIcon)
                    map!!.addMarker(markerOptions!!)
                    markerOptions!!.visible(true)
                }
            }, 500)
        }
    }

    fun getZoomLevel(circle: Circle?): Float {
        val i = AppUtil.getDeviceWidth(this@SelectLocationActivityStore)
        var zoomLevel = 0f
        if (circle != null) {
            val radius = circle.radius
            val scale = radius / i
            zoomLevel = (15 - Math.log(scale) / Math.log(2.0)).toInt().toFloat()
        }
        return zoomLevel + .0f
    }

    private fun getMarkerIconFromDrawable(drawable: Drawable): BitmapDescriptor {
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888)
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }//dialogManager.stopProcessDialog();//dialogManager.stopProcessDialog();//dialogManager.stopProcessDialog();

    //AppUtil.showMsgAlert(tvTitle, MessageConstant.MESSAGE_SOMETHING_WRONG);
//dialogManager.stopProcessDialog();
    //dialogManager.stopProcessDialog();


    private val searchMerchants: Unit
        private get() {
            if (AppUtil.isNetworkAvailable(context)) {
                if (map != null) {
                    map!!.clear()
                }
                val call = AppConfig.api_Interface()
                    .searchMerchants(latitude.toString().trim(), longitude.toString().trim())
                call!!.enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(
                        call: Call<ResponseBody?>,
                        response: Response<ResponseBody?>,
                    ) {
                        if (response.isSuccessful &&  response.body() != null) {



//                            ErrorMessage.E("axxxxx"+ JSONObject(response.body().toString()))
                            if( response.body()!!.toString()!=null && !response.body()!!.equals("") &&
                                response.body()!!.toString().length > 0) {


                                try {


                                    val resp = JSONObject(response.body()!!.string())
                                    Log.e("searchMerchant", "" + resp.toString())
                                    val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                                    if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                        if (resp.has(KeyConstant.KEY_RESPONSE)) {
                                            val responseObj =
                                                resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                            agentJSONArrayList =
                                                responseObj.optJSONArray(KeyConstant.KEY_AGENT_LIST)
                                            //dialogManager.stopProcessDialog();


                                                checkloadingmerchantlist=1;


                                            if(agentJSONArrayList!=null && agentJSONArrayList!!.length()>0) {
                                                updateUI(agentJSONArrayList)
                                            }
                                        }
                                    } else {
                                        if (KeyConstant.KEY_MESSAGE_FALSE.equals(resp.optString(
                                                KeyConstant.KEY_STATUS), ignoreCase = true)
                                        ) {
                                            //dialogManager.stopProcessDialog();
                                            AppUtil.showMsgAlert(binding!!.tvSearchMiles,
                                                resp.optString(KeyConstant.KEY_MESSAGE))
                                        }
                                    }
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                    //dialogManager.stopProcessDialog();
                                    //AppUtil.showMsgAlert(tvTitle, MessageConstant.MESSAGE_SOMETHING_WRONG);
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            }



                        } else {
                            //dialogManager.stopProcessDialog();
                            Log.e("sendToken", "else is working" + response.code().toString())
                        }
                    }


                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        ErrorMessage.E("ON FAILURE > " + t.message)
                        //dialogManager.stopProcessDialog();
                    }
                })
            }
            else {
                AppUtil.showMsgAlert(binding!!.tvSearchMiles,
                    MessageConstant.MESSAGE_INTERNET_CONNECTION)
            }


        }

    private fun updateUI(jsonArray: JSONArray?) {
        if (map != null) {
            if (jsonArray != null && jsonArray.length() > 0) {
                for (i in 0 until jsonArray.length()) {
                    val `object` = jsonArray.optJSONObject(i)
                    val agentId = `object`.optInt(KeyConstant.KEY_AGENT_ID).toString() + ""
                    val agentCompanyName = `object`.optString(KeyConstant.KEY_AGENT_COMPANY_NAME)
                    val agentAddress = `object`.optString(KeyConstant.KEY_AGENT_ADDRESS)
                    val agentEmail = `object`.optString(KeyConstant.KEY_AGENT_EMAIL)
                    val agentMobile = `object`.optString(KeyConstant.KEY_AGENT_MOBILE)
                    val agentCountry = `object`.optString(KeyConstant.KEY_AGENT_COUNTRY)
                    val agentCountryCode =
                        `object`.optInt(KeyConstant.KEY_AGENT_COUNTRY_CODE).toString() + ""
                    val agentURL = `object`.optString(KeyConstant.KEY_AGENT_URL)
                    val agentDescription = `object`.optString(KeyConstant.KEY_AGENT_DESCRIPTION)
                    val agentLatitude = `object`.optString(KeyConstant.KEY_AGENT_LATITUDE)
                    val agentLongitude = `object`.optString(KeyConstant.KEY_AGENT_LONGITUDE)
                    val agentDistance = `object`.optString(KeyConstant.KEY_AGENT_DISTANCE)
                    val agentExternalUrlEnable =
                        `object`.optInt(KeyConstant.KEY_AGENT_EXTERNAL_URL_ENABLE)
                    val agentExternalUrl = `object`.optString(KeyConstant.KEY_AGENT_EXTERNAL_URL)
                    val markerOptions = MarkerOptions()


//                    var latLng= LatLng(0.0,0.0)
//
//                    if (agentLatitude != null && agentLongitude != null && agentLatitude.toDouble() != 0.0 && agentLongitude.toDouble() != 0.0) {
//                        latLng = LatLng(agentLatitude.toDouble(), agentLongitude.toDouble())
//                    } else {
//                        latLng = LatLng(0.0, 0.0)
//                    }

                    var latLng: LatLng

                    if (!agentLatitude.isNullOrBlank() && !agentLongitude.isNullOrBlank()) {
                        try {
                            val latitude = agentLatitude.toDouble()
                            val longitude = agentLongitude.toDouble()
                            latLng = LatLng(latitude, longitude)
                        } catch (e: NumberFormatException) {
                            // Handle the case where conversion to Double fails
                            latLng = LatLng(0.0, 0.0)
                        }
                    } else {
                        // Handle the case where one or both strings are null or empty
                        latLng = LatLng(0.0, 0.0)
                    }

//                    val latLng = LatLng(agentLatitude.toDouble(), agentLongitude.toDouble())
                    val height = 50
                    val width = 50
                    val b = BitmapFactory.decodeResource(resources, R.drawable.marker)
                    val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
                    val smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker)
                    markerOptions.position(latLng)
                    markerOptions.title(agentCompanyName)
                    markerOptions.icon(smallMarkerIcon)
                    map!!.addMarker(markerOptions)!!.tag = `object`
                    map!!.setOnInfoWindowClickListener { marker ->
                        val `object` = marker.tag as JSONObject?
                        val agentId = `object`!!.optInt(KeyConstant.KEY_AGENT_ID).toString() + ""
                        val agentCompanyName =
                            `object`.optString(KeyConstant.KEY_AGENT_COMPANY_NAME)
                        val agentExternalUrlEnable =
                            `object`.optInt(KeyConstant.KEY_AGENT_EXTERNAL_URL_ENABLE)
                        val agentExternalUrl =
                            `object`.optString(KeyConstant.KEY_AGENT_EXTERNAL_URL)
                        if (agentExternalUrlEnable == 1) {
                            val intent = Intent(context, WebViewActivity::class.java)
                            intent.putExtra("title", agentCompanyName)
                            intent.putExtra("url", agentExternalUrl)
                            context!!.startActivity(intent)
                        } else {
                            val intent = Intent(this@SelectLocationActivityStore,
                                LatestProductDetails::class.java)
                            intent.putExtra("direct", "false")
                            intent.putExtra(IntentConstant.INTENT_KEY_AGENT_ID, agentId)
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    fun getItem(jsonArray: JSONArray, position: Int): JSONObject {
        return jsonArray.optJSONObject(position)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
//            mGoogleApiClient!!.disconnect()
            finish()
        } catch (e: Exception) {
        }

        val serviceIntent = Intent(this, SelectLocationActivityStore::class.java)
        stopService(serviceIntent)

//        android.os.Process.killProcess(android.os.Process.myPid());

    }


    private fun alertPopupForGps() {

        try {
            if (gpsAlertDialog != null) {
                gpsAlertDialog!!.dismiss()
            }

//            ErrorMessage.E("mayu"+gpsAlertDialog!!.isShowing)

            gpsAlertDialog = Dialog(this@SelectLocationActivityStore)
            gpsAlertDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            gpsAlertDialog!!.setContentView(R.layout.popup_common)
            gpsAlertDialog!!.setCanceledOnTouchOutside(false)
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(gpsAlertDialog!!.window!!.attributes)
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            gpsAlertDialog!!.window!!.attributes = lp
            val tvTitle = gpsAlertDialog!!.findViewById<TextView>(R.id.popup_content_inbold)
            tvTitle.visibility = View.VISIBLE
            tvTitle.text = "Location Access Disabled"
            val contentText = gpsAlertDialog!!.findViewById<TextView>(R.id.popup_content)
            contentText.text = "In order to search nearby deals we need your location"
            val btnNo = gpsAlertDialog!!.findViewById<TextView>(R.id.popup_no_btn)
            btnNo.text = "Cancel"
            val btnOk = gpsAlertDialog!!.findViewById<TextView>(R.id.popup_yes_btn)
            btnOk.text = "Open Settings"
            val view = gpsAlertDialog!!.findViewById<View>(R.id.view_btw_btn)
            view.visibility = View.VISIBLE
            //Button btnOk = (Button) dialog1.findViewById(R.id.mg_ok_btn);
            gpsAlertDialog!!.setCancelable(true)
            gpsAlertDialog!!.show()
            try {
                btnOk.setOnClickListener {
                    try {
                        gpsAlertDialog!!.dismiss()
                    } catch (e: Exception) {
                    }
//                    startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
//                        MY_PERMISSIONS_REQUEST_LOCATION)

                    val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && locationcheckingpermission==1) {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }

                    else {


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        } else {
                            startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                                MY_PERMISSIONS_REQUEST_LOCATION)
                        }

                    }
                }
                btnNo.setOnClickListener {
                    try {
                        gpsAlertDialog!!.dismiss()

                        SavedData.saveLocationPermission("false")
                    } catch (e: Exception) {
                    }
                }
            }
            catch (e: Exception) {
            }
        } catch (e: Exception) {
        }
    }


    private fun requestPermission() {
        countStoragePermission++

        ErrorMessage.E("mayurr" + countStoragePermission)
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permissions
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
             locationcheckingpermission=1
            alertPopupForGps()

        }


        else{
            locationcheckingpermission=0

        }

    }

}