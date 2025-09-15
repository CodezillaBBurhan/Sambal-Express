package sambal.mydd.app.asyncTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import sambal.mydd.app.authentication.SignUpActivity;
import sambal.mydd.app.beans.User;
import sambal.mydd.app.constant.KeyConstant;
import sambal.mydd.app.constant.UrlConstant;
import sambal.mydd.app.utils.ErrorMessage;
import sambal.mydd.app.utils.MyLog;
import sambal.mydd.app.utils.PreferenceHelper;
import sambal.mydd.app.utils.ProgressDialogUtils;
import sambal.mydd.app.utils.SharedPreferenceVariable;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by codezilla-11 on 31/1/18.
 */

public class MyOkHttp {

    Context context;
    OkHttpClient client;
    MyLog myLog = new MyLog();

    public Response executeAPI(RequestBody requestBody, String url, final Context context) throws IOException {

        User user = PreferenceHelper.getInstance(context).getUserDetail();
        // System.out.println("header>>" + "Bearer " + PreferenceHelper.getInstance(context).getAccessToken());

        //PreferenceHelper.getInstance(context).setAccessToken("");

        //Log.e("hit api access token", PreferenceHelper.getInstance(context).getAccessToken());
        final String accessToken = PreferenceHelper.getInstance(context).getAccessToken();
        //myLog.logE("device", user.getDeviceID());

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //Toast.makeText(context, "okHttp " + accessToken, Toast.LENGTH_SHORT).show();
            }
        });
        Log.e("token", ">>" + PreferenceHelper.getInstance(context).getAccessToken());
        Request request = null;
        if (user != null) {
            request = new Request.Builder()
                    .url(url)

                    //    .addHeader(UrlConstant.HEADER_AUTHORIZATION, "Bearer" + " " + "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjQ4YTc1YjRiYWE1YWJlNzRlZmUzYWZjZGYzNTAxNTZkZDc1MzgxODViODBkMDIxYzNjNmYyOWM0MDYzMmJlMzRiN2U4NDU4OWM3YjQ5ZjdkIn0.eyJhdWQiOiJkZWFsRGlvIiwianRpIjoiNDhhNzViNGJhYTVhYmU3NGVmZTNhZmNkZjM1MDE1NmRkNzUzODE4NWI4MGQwMjFjM2M2ZjI5YzQwNjMyYmUzNGI3ZTg0NTg5YzdiNDlmN2QiLCJpYXQiOjE1NjcwODAxMzksIm5iZiI6MTU2NzA4MDEzOSwiZXhwIjoxNTY3NjgwMTM5LCJzdWIiOiI4OTMiLCJzY29wZXMiOlsiYmFzaWMiLCJlbWFpbCJdfQ.efZb3_fDudufoH0gPs_eQh3kfgrfGMV_Da2m8FDoLpt94krqwZBUFELIRBPbOukdVA_mo6G9UXw8Vqec1kep15P4lO7iKmVCMnuSPhE4kgnxfaj4wLhoi6N4FHfGleyDyHvhVjTbjz4h637GYTZm_-NbNDNoCEY_5LclEpndkw161zAcLoa9pGqG-Z9ZliDf-qQe-QRLydSJfbf9JUBL36eT8J2LGe02FWWvFSL6KXZefjrY1c2ySUgjUMIJ-_FsiBBtJh7bIpEKbEs10KH7kRiLbITaAzdllxjUWqS9dfU7JdczlYCOc4vpqqeaeI9NTpWkqtIPpMKnGVLudIYxaw")
                    .addHeader(UrlConstant.HEADER_AUTHORIZATION, "Bearer " + PreferenceHelper.getInstance(context).getAccessToken())
                    .addHeader(UrlConstant.HEADER_CONTENT_TYPE, UrlConstant.CONTENT_TYPE_X_WWW_VALUE)
                    .addHeader(UrlConstant.HEADER_DEVICE_ID, user.getDeviceID())
                    .post(requestBody)
                    .build();
        } else {
            request = new Request.Builder()
                    .url(url)

                    //.addHeader(UrlConstant.HEADER_AUTHORIZATION,"Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6ImJiZDBjODZmMGZhZGE2ZWZhYWQyNjU2M2ZjZDM1ZTQ0MjMyM2Q3M2VjNDBjODUxMjYxZWYwYWMzODlkNjFlYjQyNGEyNTI5OGE4MDU5M2M2In0.eyJhdWQiOiJkZWFsRGlvIiwianRpIjoiYmJkMGM4NmYwZmFkYTZlZmFhZDI2NTYzZmNkMzVlNDQyMzIzZDczZWM0MGM4NTEyNjFlZjBhYzM4OWQ2MWViNDI0YTI1Mjk4YTgwNTkzYzYiLCJpYXQiOjE1ODY5MzQ2OTcsIm5iZiI6MTU4NjkzNDY5NywiZXhwIjoxNTg3NTM0Njk3LCJzdWIiOiIyODk3Iiwic2NvcGVzIjpbImJhc2ljIiwiZW1haWwiXX0.ZBM_0Pzg6cpKwdxFfFT6MOlkrItjt7rEsWeoVmzk45EUkPVDvZizVkSIK0Tmuo37NzmhoLsbhM9kY0BBBQ4glGvug0S1dGIZC2AnfYLF09BdzfIgJhRBJiH-xSeQzap0IKcsdaUTTM_fbsndblikxY-6szYPDYWRC68RdaEYpyADqig9nFugYc7hr-Kij4lsLkrzxXFAEN_B5c5ri2M4zgUgKi2FQq-BUXK5EeFtGD4Ip8rpEnDd4lbgC2sW4s-T-K0iy9MiJrmM85mIDA3FpFvLDSpG0q1g_KmmTjvsWFUNjDneD3W85pK5b5RGksDHrYni-pmqQRD0eZq-Jw2G9A")
                    .addHeader(UrlConstant.HEADER_AUTHORIZATION, "Bearer " + PreferenceHelper.getInstance(context).getAccessToken())
                    .addHeader(UrlConstant.HEADER_CONTENT_TYPE, UrlConstant.CONTENT_TYPE_X_WWW_VALUE)
                    .addHeader(UrlConstant.HEADER_DEVICE_ID, user.getDeviceID())
                    .post(requestBody)
                    .build();
        }
        //   Log.e("header req", request.headers() + "");

        client = new OkHttpClient();
        client.setConnectTimeout(UrlConstant.CONNECTION_TIME_OUT, TimeUnit.SECONDS);
        client.setReadTimeout(UrlConstant.SOCKET_TIME_OUT, TimeUnit.SECONDS);

        //TODO: Here this code call asynchronously need to make it synchronous
        //new BackgroundTask().execute(request);

        Response response = client.newCall(request).execute();
        //Response returnValue = response;
        //String result = response.body().string().toString();
        //Log.e("response code", result);

        try {

            //myLog.logE("code------", response.code() + "");
            myLog.logE("code------", response.toString() + "");
            if (response.code() == 401) {
                Log.e("code is 401", "**************");
                String refreshToken = PreferenceHelper.getInstance(context).getRefreshToken();
                if (refreshToken == null) {
                    return null;
                }
                getAuthTokenAgain(context);
                return executeAPI(requestBody, url, context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                //response.body().close();
                Log.e("finally", "block");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return response;

    }

    public String getAuthTokenAgain(final Context context) {

        //String url = UrlConstant.API_URL + UrlConstant.URL_GET_REFRESH_AUTH_TOKEN;
        String url = UrlConstant.BASE_URL + UrlConstant.URL_GET_REFRESH_AUTH_TOKEN;
        Response response = null;
        try {
            OkHttpClient client = new OkHttpClient();

            client.setConnectTimeout(UrlConstant.CONNECTION_TIME_OUT, TimeUnit.SECONDS);
            client.setReadTimeout(UrlConstant.SOCKET_TIME_OUT, TimeUnit.SECONDS);
            RequestBody requestBody = null;

            Log.e("old access token", PreferenceHelper.getInstance(context).getAccessToken());
            Log.e("refresh token1", PreferenceHelper.getInstance(context).getRefreshToken());

            requestBody = new FormEncodingBuilder()
                    .add(KeyConstant.KEY_GRANT_TYPE, KeyConstant.KEY_REFRESH_TOKEN)
                    .add(KeyConstant.KEY_CLIENT_ID, KeyConstant.KEY_CLIENT_ID_VALUE)
                    .add(KeyConstant.KEY_CLIENT_SEC, KeyConstant.KEY_CLIENT_SEC_VALUE)
                    .add(KeyConstant.KEY_REFRESH_TOKEN, PreferenceHelper.getInstance(context).getRefreshToken())
                    //.add(KeyConstant.KEY_REFRESH_TOKEN, "")
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            response = client.newCall(request).execute();
            String result = response.body().string();

            if (response.code() == 401) {
                myLog.logE("need to logout user", "401 when token refresh");
            } else {
                //Log.e("refresh token", result + "");
                JSONObject responseObj = new JSONObject(result);
                myLog.logE("need to logout user", responseObj.toString());
                try {
                    if (responseObj.optString("error_code").equals("401")) {
                        SharedPreferenceVariable.ClearSharePref(context);
                        PreferenceHelper.getInstance(context).getLogout();
                        // Intent in = new Intent(context, Login.class);
                        Intent in = new Intent(context, SignUpActivity.class);
                        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(in);
                    } else if (responseObj.optString("error_code").equals("201")) {
                        SharedPreferenceVariable.ClearSharePref(context);
                        PreferenceHelper.getInstance(context).getLogout();
                        //Intent in = new Intent(context, Login.class);
                        Intent in = new Intent(context, SignUpActivity.class);
                        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(in);
                    } else {
                        JSONObject obj = responseObj.optJSONObject(KeyConstant.KEY_RESPONSE);
                        final String accessToken = obj.optString(KeyConstant.KEY_ACCESS_TOKEN);
                        final String refreshToken = obj.optString(KeyConstant.KEY_REFRESH_TOKEN);
                        //  Log.e("Accesstoken", accessToken);
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                //Toast.makeText(context, "call refresh1 " + accessToken, Toast.LENGTH_SHORT).show();
                                //Toast.makeText(context, "call refresh2 " + refreshToken, Toast.LENGTH_SHORT).show();
                            }
                        });

                        PreferenceHelper.getInstance(context).setAccessToken(accessToken);
                        PreferenceHelper.getInstance(context).setRefreshToken(refreshToken);
                    }
                } catch (Exception e1) {
                    try {
                        if (responseObj.getString("error_type").equals("201")) {
                            JSONObject obj = responseObj.optJSONObject(KeyConstant.KEY_RESPONSE);
                            final String accessToken = obj.optString(KeyConstant.KEY_ACCESS_TOKEN);
                            final String refreshToken = obj.optString(KeyConstant.KEY_REFRESH_TOKEN);
                            //  Log.e("Accesstoken", accessToken);
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    //Toast.makeText(context, "call refresh1 " + accessToken, Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(context, "call refresh2 " + refreshToken, Toast.LENGTH_SHORT).show();
                                }
                            });

                            PreferenceHelper.getInstance(context).setAccessToken(accessToken);
                            PreferenceHelper.getInstance(context).setRefreshToken(refreshToken);
                        }
                    } catch (Exception e) {
                    }
                }
                //   myLog.logE("new access token", PreferenceHelper.getInstance(context).getAccessToken());
                //  myLog.logE("refresh token2", PreferenceHelper.getInstance(context).getRefreshToken());
            }
            myLog.logE("responseStr ", result);

            ProgressDialogUtils.hideProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
            ProgressDialogUtils.hideProgressDialog();
        } finally {
            try {
                //response.body().close();
                //   Log.e("finally", "block");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;

    }

}
