package sambal.mydd.app.utils

import android.annotation.SuppressLint
import sambal.mydd.app.DealDioApplication
import sambal.mydd.app.utils.PreferenceHelper.Companion.getInstance
import sambal.mydd.app.utils.ErrorMessage.E
import retrofit2.Retrofit
import sambal.mydd.app.constant.UrlConstant
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.lang.Exception
import java.util.concurrent.TimeUnit

@SuppressLint("StaticFieldLeak")
object AppConfig {
    private var retrofit: Retrofit? = null
    var loadInterface: LoadInterface? = null
        get() {
            if (field == null) field = client!!.create(
                LoadInterface::class.java)
            return field
        }
    // Request customization: add request headers

    val client: Retrofit?
        get() {
            try {
                if (retrofit == null) {
                    val okHttpClient =
                        OkHttpClient.Builder().addInterceptor { chain: Interceptor.Chain ->
                            val original = chain.request()
                            // Request customization: add request headers
                                        ErrorMessage.E("tokan>>" +"Bearer " +getInstance(DealDioApplication.appContext)!!.accessToken.toString());
                            val requestBuilder = original.newBuilder()
//                                .addHeader("Content-Type", "application/json")
                                .addHeader("Content-Type", "application/json")
                                .addHeader("Authorization",
                                    "Bearer " + getInstance(DealDioApplication.appContext)!!.accessToken.toString())


                            val request = requestBuilder.build()
                            chain.proceed(request)
                        }
                            .connectTimeout(100, TimeUnit.SECONDS)
                            .readTimeout(100, TimeUnit.SECONDS)
                            .writeTimeout(100, TimeUnit.SECONDS)
                            .cache(null)
                            .build()
                    retrofit = Retrofit.Builder()
                        .baseUrl(UrlConstant.BASE_URL)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                }
            } catch (e: Exception) {
                ErrorMessage.E("Exception>>>><<<${e.toString()}")
            }
            return retrofit
        }

//    @JvmStatic
//    fun api_Interface(): LoadInterface {
//        return client!!.create(LoadInterface::class.java)
//    }

    @JvmStatic
    fun api_Interface(): LoadInterface {
        return try {
            client?.create(LoadInterface::class.java)
                ?: throw IllegalStateException("HTTP client is not initialized")
        } catch (e: Exception) {
            ErrorMessage.E("Error>>>><<<${e.toString()}")
            throw e
        }
    }



}