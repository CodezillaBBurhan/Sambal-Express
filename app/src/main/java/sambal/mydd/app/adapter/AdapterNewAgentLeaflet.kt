package sambal.mydd.app.adapter

import sambal.mydd.app.activity.New_AgentDetails
import org.json.JSONArray
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import com.squareup.picasso.Picasso
import android.content.Intent
import sambal.mydd.app.activity.Webview
import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import com.shockwave.pdfium.PdfiumCore
import android.graphics.Bitmap
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.View
import androidx.core.content.FileProvider
import sambal.mydd.app.R
import sambal.mydd.app.databinding.AdapleafletBinding
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class AdapterNewAgentLeaflet(private val context: New_AgentDetails, private val arr: JSONArray) :
    RecyclerView.Adapter<AdapterNewAgentLeaflet.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = AdapleafletBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val obj = arr.optJSONObject(position)
        Log.e("Leafler", obj.optString("leafLetImage"))
        if (obj.optString("leafLetImage").contains("pdf") || obj.optString("leafLetImage")
                .contains("PDF")
        ) {
            holder.binding.progressBar.visibility = View.VISIBLE
            try {
                DownloadTask(context, obj.optString("leafLetImage"), holder)
            } catch (e: Exception) {
            }
        } else {
            Log.e("if is working", " no")
            try {
                Picasso.with(context)
                    .load(obj.optString("leafLetImage"))
                    .placeholder(context.resources.getDrawable(R.drawable.mainimageplaceholder))
                    .error(context.resources.getDrawable(R.drawable.mainimageplaceholder))
                    .into(holder.binding.iv)
                holder.binding.progressBar.visibility = View.GONE
            } catch (e: Exception) {
            }
        }
        holder.binding.iv.setOnClickListener {
            if (obj.optString("leafLetImage").contains("pdf") || obj.optString("leafLetImage")
                    .contains("PDF")
            ) {
                context.startActivity(Intent(context, Webview::class.java)
                    .putExtra("url", obj.optString("leafLetImage"))
                    .putExtra("title", "Leaflet")
                    .putExtra("type", "non_direct"))
            } else {
                context.fullscreenPager(arr, position)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return arr.length()
    }

    inner class MyViewHolder(var binding: AdapleafletBinding) : RecyclerView.ViewHolder(
        binding.root)

    inner class DownloadTask(
        private val context: Context,
        downloadUrl: String,
        holder: MyViewHolder
    ) {
        private var downloadUrl = ""
        private var downloadFileName = ""

        // private ProgressDialog progressDialog;
        var holder1: MyViewHolder

        init {
            this.downloadUrl = downloadUrl
            holder1 = holder
            downloadFileName =
                downloadUrl.substring(downloadUrl.lastIndexOf('/')) //Create file name by picking download file name from URL
            if (downloadFileName.contains(".doc") || downloadFileName.contains(".docx") || downloadFileName.contains(
                    ".pdf") || downloadFileName.contains(".PDF")
            ) {
            } else {
                downloadFileName = "$downloadFileName.pdf"
            }
            Log.e("doc", downloadFileName)

            //Start Downloading Task
            DownloadingTask().execute()
        }

        private inner class DownloadingTask : AsyncTask<Void?, Void?, Void?>() {
            var apkStorage: File? = null
            var outputFile: File? = null
            override fun onPreExecute() {
                super.onPreExecute()
                holder1.binding.progressBar.visibility = View.VISIBLE
            }

            override fun onPostExecute(result: Void?) {
                try {
                    if (outputFile != null) {
                        holder1.binding.progressBar.visibility = View.GONE
                        val path: Uri
                        val pdfFile: File
                        pdfFile = if (Build.VERSION.SDK_INT >= 29) {
                            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/CodePlayon/" + downloadFileName)
                        } else {
                            File(Environment.getExternalStorageDirectory().absolutePath + "/CodePlayon/" + downloadFileName)
                        }
                        path = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            FileProvider.getUriForFile(context,
                                "sambal.mydd.app.provider",
                                pdfFile)
                        } else {
                            Uri.fromFile(pdfFile)
                        }
                        generateImageFromPdf(path, holder1)
                    } else {
                        Handler().postDelayed({ }, 3000)
                        Log.e(Companion.TAG, "Download Failed")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    //Change button text if exception occurs
                    Handler().postDelayed({ }, 3000)
                    Log.e(Companion.TAG, "Download Failed with Exception - " + e.localizedMessage)
                }
                super.onPostExecute(result)
            }

            @SuppressLint("WrongThread")
            protected override fun doInBackground(vararg p0: Void?): Void? {
                try {
                    val url = URL(downloadUrl) //Create Download URl
                    val c = url.openConnection() as HttpURLConnection //Open Url Connection
                    c.requestMethod = "GET" //Set Request Method to "GET" since we are grtting data
                    c.connect() //connect the URL Connection

                    //If Connection response is not OK then show Logs
                    if (c.responseCode != HttpURLConnection.HTTP_OK) {
                        Log.e(Companion.TAG,
                            "Server returned HTTP " + c.responseCode + " " + c.responseMessage)
                    }


                    //Get File if SD card is present
                    if (CheckForSDCard().isSDCardPresent) {

                        //apkStorage = new File(Environment.getExternalStorageDirectory() + "/" + "CodePlayon");
                        apkStorage = if (Build.VERSION.SDK_INT >= 29) {
                            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                .toString() + "/" + "CodePlayon")
                        } else {
                            File(Environment.getExternalStorageDirectory()
                                .toString() + "/" + "CodePlayon")
                        }
                    } else Toast.makeText(context,
                        "Oops!! There is no SD Card.",
                        Toast.LENGTH_SHORT).show()

                    //If File is not present create directory
                    if (!apkStorage!!.exists()) {
                        apkStorage!!.mkdir()
                        Log.e(Companion.TAG, "Directory Created.")
                    }
                    outputFile =
                        File(apkStorage, downloadFileName) //Create Output file in Main File

                    //Create New File if not present
                    if (!outputFile!!.exists()) {
                        outputFile!!.createNewFile()
                        Log.e(Companion.TAG, "File Created")
                    }
                    val fos = FileOutputStream(outputFile) //Get OutputStream for NewFile Location
                    val `is` = c.inputStream //Get InputStream for connection
                    val buffer = ByteArray(1024) //Set buffer type
                    var len1 = 0 //init length
                    while (`is`.read(buffer).also { len1 = it } != -1) {
                        fos.write(buffer, 0, len1) //Write new file
                    }

                    //Close all connection after doing task
                    fos.close()
                    `is`.close()
                } catch (e: Exception) {

                    //Read exception if something went wrong
                    e.printStackTrace()
                    outputFile = null
                    Log.e(Companion.TAG, "Download Error Exception " + e.message)
                    /*progressDialog.dismiss();*/try {
                        holder1.binding.progressBar.visibility = View.GONE
                    } catch (e1: Exception) {
                    }
                }
                return null
            }
        }
    }
    companion object {
        private const val TAG = "Download Task"
    }

    inner class CheckForSDCard {
        //Check If SD Card is present or not method
        val isSDCardPresent: Boolean
            get() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    fun generateImageFromPdf(pdfUri: Uri?, holder1: MyViewHolder) {
        val pageNumber = 0
        val pdfiumCore = PdfiumCore(context)
        try {
            //http://www.programcreek.com/java-api-examples/index.php?api=android.os.ParcelFileDescriptor
            val fd = context.contentResolver.openFileDescriptor(pdfUri!!, "r")
            val pdfDocument = pdfiumCore.newDocument(fd)
            pdfiumCore.openPage(pdfDocument, pageNumber)
            val width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber)
            val height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber)
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height)
            saveImage(bmp, holder1)
            pdfiumCore.closeDocument(pdfDocument) // important!
        } catch (e: Exception) {
            //todo with exception
        }
    }

    var FOLDER = ""
    private fun saveImage(bmp: Bitmap, holder1: MyViewHolder) {
        var out: FileOutputStream? = null
        FOLDER = if (Build.VERSION.SDK_INT >= 29) {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .toString() + "/PDF"
        } else {
            Environment.getExternalStorageDirectory().toString() + "/PDF"
        }
        try {
            val folder = File(FOLDER)
            if (!folder.exists()) folder.mkdirs()
            val file = File(folder, "PDF.png")
            out = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.PNG, 80, out) // bmp is your Bitmap instance
            holder1.binding.iv.setImageBitmap(bmp)
        } catch (e: Exception) {
            //todo with exception
        } finally {
            try {
                out?.close()
            } catch (e: Exception) {
                //todo with exception
            }
        }
    }
}