package sambal.mydd.app.asyncTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import sambal.mydd.app.constant.KeyConstant;
import sambal.mydd.app.constant.UrlConstant;
import sambal.mydd.app.utils.AsyncCallback;
import sambal.mydd.app.utils.ProgressDialogUtils;

import java.io.ByteArrayOutputStream;

public class Chat_Image_Task extends AsyncTask<Void, Void, Void> {

    private String TAG = this.getClass().getSimpleName();

    private AsyncCallback asyncCallback;
    private Context context;
    private Bitmap bitmap;

    public Chat_Image_Task(Context context, Bitmap bitmap, AsyncCallback asyncCallback) {
        this.asyncCallback = asyncCallback;
        this.context = context;
        this.bitmap = bitmap;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        try {
            ProgressDialogUtils.hideProgressDialog();
        } catch (Exception e){}
    }

    @Override
    protected Void doInBackground(Void... voids) {
        getData();
        return null;
    }

    public String getData() {
        String url = UrlConstant.BASE_URL + UrlConstant.URL_UPLOAD_IMAGE_CHAT;
        Log.e(TAG, "url " + url);
        Response response = null;
        final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
        try {
            byte[] data = null;
            if (bitmap != null && bitmap != null) {


                Log.d(TAG, "bitmap: " + bitmap.toString());
                Log.e("bitmap ", bitmap + "");
//              Bitmap bitmap = DateUtil.decodeBase64(encodedBitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                data = stream.toByteArray();
            }

            RequestBody requestBody;

            requestBody = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    //.addFormDataPart(KeyConstant.KEY_TITLE, bitmap.getTitle())

                    .addFormDataPart(KeyConstant.KEY_USER_FILE, "image.jpg", RequestBody.create(MEDIA_TYPE_JPG, data))
                    .build();


            MyOkHttp myOkHttp = new MyOkHttp();
            response = myOkHttp.executeAPI(requestBody, url, context);

            String result = response.body().string();

            Log.d(TAG, "responseStr " + result);
            Log.d(TAG, "response.code() :" + response.code());
            asyncCallback.setResponse(response.code(), result);
            //ProgressDialogUtils.hideProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "exception " + e.getMessage());
            //ProgressDialogUtils.hideProgressDialog();
            asyncCallback.setException(e.getMessage());
        } finally {
            try {
                //response.body().close();
                Log.e("finally", "block");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;


    }

}