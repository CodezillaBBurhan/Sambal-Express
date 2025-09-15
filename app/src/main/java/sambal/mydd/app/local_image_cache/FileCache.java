package sambal.mydd.app.local_image_cache;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class FileCache {
    private final File cacheDir;

    public FileCache(Context context){

        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            if (Build.VERSION.SDK_INT >=  29) {
                cacheDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DD_cache");
            } else {
                cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "DD_cache");
            }


        else
            cacheDir = context.getCacheDir();
        if (!cacheDir.exists())
            cacheDir.mkdirs();
    }

    public File getFile(String url) {
        Log.e("filename>>>","<url>"+url);
        //I identify images by hashcode. Not a perfect solution, good for the demo.
        //  String filename=String.valueOf(url.hashCode());
        String filename = url.hashCode() + url.substring(url.lastIndexOf('.'));
        //Another possible solution (thanks to grantland)
        Log.e("filename>>>","<>"+filename);
        //String filename = URLEncoder.encode(url);
        File f = new File(cacheDir, filename);
        return f;

    }

    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }
}
