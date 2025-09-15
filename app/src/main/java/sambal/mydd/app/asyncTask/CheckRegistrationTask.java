package sambal.mydd.app.asyncTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import sambal.mydd.app.beans.User;
import sambal.mydd.app.constant.KeyConstant;
import sambal.mydd.app.constant.MessageConstant;
import sambal.mydd.app.constant.UrlConstant;
import sambal.mydd.app.utils.AsyncCallback;
import sambal.mydd.app.utils.ErrorMessage;
import sambal.mydd.app.utils.MyLog;
import sambal.mydd.app.utils.ProgressDialogUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class CheckRegistrationTask extends AsyncTask<Void, Void, Void> {

    MyLog myLog = new MyLog();
    private final String TAG = this.getClass().getSimpleName();
    private final String userPicture;
    private final User user;
    private final AsyncCallback asyncCallback;
    private final Context context;
    private final String socialIdToken;
    private final String referalCode;
    private final String VerificationId;


    public CheckRegistrationTask(Context context, String userPicture, String referalCode, User user, String socialIdToken, String verificationId, AsyncCallback asyncCallback) {
        this.userPicture = userPicture;
        this.user = user;
        this.asyncCallback = asyncCallback;
        this.context = context;
        this.socialIdToken = socialIdToken;
        this.referalCode = referalCode;
        this.VerificationId = verificationId;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            //ProgressDialogUtils.showProgressDialog(context, MessageConstant.MESSAGE_PLEASE_WAIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected Void doInBackground(Void... voids) {
        userRegistration(userPicture, user, referalCode);
        return null;
    }

    private String userRegistration(String userPicture, User user, String referalCode) {
        String fileName = "profile_pic" + ".png";
        String url = UrlConstant.BASE_URL + UrlConstant.URL_CHECK_REGISTER;

        myLog.logE(TAG, "userRegisteration URL :" + url);
        String responseStr = null;

        final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
        Response response = null;
        try {
            byte[] data = null;
            if (user != null && user.getUserPhoto() != null && user.getUserPhoto().length() > 0) {
                final Bitmap[] bitmap = new Bitmap[1];
                Glide.with(context)
                        .asBitmap().load(user.getUserPhoto()).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(new SimpleTarget<Bitmap>(100, 100) {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                bitmap[0] = resource;
                            }

                            @Override
                            public void onLoadCleared(Drawable placeholder) {
                            }

                            @Override
                            public void onLoadStarted(Drawable placeholder) {
                                super.onLoadStarted(placeholder);
                            }
                        });
               /* Bitmap bitmap = Glide.
                        with(context).
                        asBitmap().
                        load(user.getUserPhoto()).
                        into(100, 100). // Width and height
                        get();*/


                Log.d(TAG, "bitmap: " + bitmap[0].toString());
//                Bitmap bitmap = DateUtil.decodeBase64(encodedBitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap[0].compress(Bitmap.CompressFormat.PNG, 100, stream);
                data = stream.toByteArray();
            }

            OkHttpClient client = new OkHttpClient();

            if (user.getDeviceToken() == null) {
                user.setDeviceToken("");
            }

            client.setConnectTimeout(UrlConstant.CONNECTION_TIME_OUT, TimeUnit.SECONDS);
            client.setReadTimeout(UrlConstant.SOCKET_TIME_OUT, TimeUnit.SECONDS);
            RequestBody requestBody;

            ErrorMessage.E("aaawwww"+ "  "+socialIdToken+"  "+ KeyConstant.KEY_GRANT_TYPE_VALUE+ " "+KeyConstant.KEY_CLIENT_ID_VALUE+"  "+ KeyConstant.KEY_CLIENT_SEC_VALUE+

                    "  user.getUserCountryId()"+   user.getUserCountryId() + "  "+  user.getUserMobile()+ "  "+ user.getDeviceID()+ "  "+  user.getMobileType()+"  "+user.getUserType()+"  "+

                    user.getDeviceToken()+" "+referalCode + "   "+ user.getUserType() + "  "+UrlConstant.DEVICE_DEBUG_MODE
                    +"    "+VerificationId+ "   user.getUserName()"+ user.getUserName() + "   "+ user.getName()
            );

            ErrorMessage.E("countryCodeKey>>"+ user.getUserCountryId())  ;

            if (data == null) {


                requestBody = new MultipartBuilder()
                        .type(MultipartBuilder.FORM)
                        .addFormDataPart(KeyConstant.KEY_SOCIAL_LOGIN_INPUT_TOKEN, socialIdToken)
                        .addFormDataPart(KeyConstant.KEY_GRANT_TYPE, KeyConstant.KEY_GRANT_TYPE_VALUE)
                        .addFormDataPart(KeyConstant.KEY_CLIENT_ID, KeyConstant.KEY_CLIENT_ID_VALUE)
                        .addFormDataPart(KeyConstant.KEY_CLIENT_SEC, KeyConstant.KEY_CLIENT_SEC_VALUE)
                        .addFormDataPart(KeyConstant.KEY_USER_COUNTRY, user.getUserCountryId() + "")
                        .addFormDataPart(KeyConstant.KEY_USER_MOBILE, user.getUserMobile())
                        .addFormDataPart(KeyConstant.KEY_DEVICE_ID, user.getDeviceID())
                        .addFormDataPart(KeyConstant.KEY_MOBILE_TYPE, user.getMobileType())
                        .addFormDataPart(KeyConstant.KEY_USER_TYPE, user.getUserType() + "")
                        .addFormDataPart(KeyConstant.KEY_DEVICE_TOKEN, user.getDeviceToken())
                        .addFormDataPart(KeyConstant.KEY_DEVICE_MODE, UrlConstant.DEVICE_DEBUG_MODE + "")
                        .addFormDataPart(KeyConstant.KEY_PASSWORD, user.getPassword())
                        .addFormDataPart(KeyConstant.KEY_NAME, user.getName())
                        .addFormDataPart(KeyConstant.KEY_USER_NAME, user.getUserName())
                        .addFormDataPart("referalCode", referalCode)
                        .addFormDataPart(KeyConstant.KEY_EMAIL, user.getEmail())
                        .addFormDataPart(KeyConstant.KEY_POST_EMAIL, user.getEmail())
                        .addFormDataPart(KeyConstant.KEY_USER_FILE, "")
                        .addFormDataPart("verificationID", VerificationId)
                        .build();
                Log.e("Referra", user.getDeviceToken() + "");
                Log.e("getUserMobile>>>>",  user.getUserMobile() + "");

            } else {
                requestBody = new MultipartBuilder()
                        .type(MultipartBuilder.FORM)

                        .addFormDataPart(KeyConstant.KEY_SOCIAL_LOGIN_INPUT_TOKEN, socialIdToken)
                        .addFormDataPart(KeyConstant.KEY_GRANT_TYPE, KeyConstant.KEY_GRANT_TYPE_VALUE)
                        .addFormDataPart(KeyConstant.KEY_CLIENT_ID, KeyConstant.KEY_CLIENT_ID_VALUE)
                        .addFormDataPart(KeyConstant.KEY_CLIENT_SEC, KeyConstant.KEY_CLIENT_SEC_VALUE)
                        .addFormDataPart(KeyConstant.KEY_USER_COUNTRY, user.getUserCountryId() + "")
                        .addFormDataPart(KeyConstant.KEY_USER_MOBILE, user.getUserMobile())
                        .addFormDataPart(KeyConstant.KEY_DEVICE_ID, user.getDeviceID())
                        .addFormDataPart(KeyConstant.KEY_MOBILE_TYPE, user.getMobileType())
                        .addFormDataPart(KeyConstant.KEY_USER_TYPE, user.getUserType() + "")
                        .addFormDataPart(KeyConstant.KEY_DEVICE_TOKEN, user.getDeviceToken())
                        .addFormDataPart(KeyConstant.KEY_DEVICE_MODE, UrlConstant.DEVICE_DEBUG_MODE + "")
                        .addFormDataPart(KeyConstant.KEY_PASSWORD, user.getPassword())
                        .addFormDataPart(KeyConstant.KEY_NAME, user.getName())
                        .addFormDataPart(KeyConstant.KEY_USER_NAME, user.getUserName())
                        .addFormDataPart(KeyConstant.KEY_EMAIL, user.getEmail())
                        .addFormDataPart("referalCode", referalCode)
                        .addFormDataPart(KeyConstant.KEY_POST_EMAIL, user.getEmail())
                        .addFormDataPart(KeyConstant.KEY_USER_FILE, fileName, RequestBody.create(MEDIA_TYPE_JPG, data))
                        .addFormDataPart("verificationID", VerificationId)
                        .build();
                Log.e("Referra", user.getDeviceToken() + "");
            }

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            response = client.newCall(request).execute();
            responseStr = response.body().string();
            //Log.e("user reg resp", responseStr);
            //Log.d(TAG, "responseStr " + responseStr);
            asyncCallback.setResponse(response.code(), responseStr);
            //ProgressDialogUtils.hideProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
            //ProgressDialogUtils.hideProgressDialog();
            asyncCallback.setException(e.getMessage());
          Log.e("social login exp", e.toString() + "");
        } finally {
            try {
                //response.body().close();
                Log.e("finally", "block");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return responseStr;
    }
}