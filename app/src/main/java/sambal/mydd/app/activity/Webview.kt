package sambal.mydd.app.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.Window
import sambal.mydd.app.SplashActivity
import sambal.mydd.app.utils.StatusBarcolor
import android.webkit.WebViewClient
import sambal.mydd.app.utils.DialogManager
import android.webkit.WebView
import android.webkit.WebChromeClient
import sambal.mydd.app.R
import sambal.mydd.app.databinding.WebviewBinding
import java.lang.Exception

class Webview : AppCompatActivity() {
    var binding: WebviewBinding? = null
    var type: String? = "non_direct"
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.webview)
        val bundle = intent.extras
        if (bundle != null) {
            val url = bundle.getString("url")
            Log.e("Web url", "<><>$url")
            startWebView(url)
            try {
                if (bundle.getString("title") == "DD Kitchen" || bundle.getString("title") == "DD Grocer") {
                    binding!!.lbltitle.visibility = View.GONE
                    binding!!.ddGrocerHeader.visibility = View.VISIBLE
                } else {
                    binding!!.lbltitle.visibility = View.VISIBLE
                    binding!!.ddGrocerHeader.visibility = View.GONE
                    binding!!.tVtitle.text = bundle.getString("title")
                }
            } catch (e: Exception) {
                binding!!.tVtitle.text = ""
            }
            try {
                type = bundle.getString("type")
                Log.e("type", type!!)
            } catch (e: Exception) {
            }
            binding!!.ivBack.setOnClickListener {
                try {
                    if (type.equals("direct", ignoreCase = true)) {
                        try {
                        } catch (e: Exception) {
                        }
                        startActivity(Intent(this@Webview, SplashActivity::class.java))
                        finish()
                    } else {
                        finish()
                    }
                } catch (e: Exception) {
                }
            }
            binding!!.backTitleTv.setOnClickListener {
                try {
                    if (type.equals("direct", ignoreCase = true)) {
                        startActivity(Intent(this@Webview, SplashActivity::class.java))
                        finish()
                    } else {
                        finish()
                    }
                } catch (e: Exception) {
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        try {
            if (type.equals("direct", ignoreCase = true)) {
                startActivity(Intent(this@Webview, SplashActivity::class.java))
                finish()
            } else {
                finish()
            }
        } catch (e: Exception) {
        }
    }

    public override fun onResume() {
        super.onResume()
        try {
            StatusBarcolor.setStatusbarColor(this@Webview, "white")
        } catch (e: Exception) {
        }
    }

    private fun startWebView(url: String?) {
        binding!!.webView.webViewClient = object : WebViewClient() {
            var dialogManager: DialogManager? = null
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onLoadResource(view: WebView, url: String) {
                try {
                    if (dialogManager == null) {
                        dialogManager = DialogManager()
                        dialogManager!!.showProcessDialog(this@Webview, "", false, null)
                    }
                } catch (e: Exception) {
                }
            }

            override fun onPageFinished(view: WebView, url: String) {
                try {
                    dialogManager!!.stopProcessDialog()
                } catch (exception: Exception) {
                    exception.printStackTrace()
                    Log.e("exception >>", "" + exception)
                }
            }
        }
        binding!!.webView.settings.javaScriptEnabled = true
        binding!!.webView.settings.allowFileAccess = false
        binding!!.webView.isVerticalScrollBarEnabled = true
        binding!!.webView.isHorizontalScrollBarEnabled = true
        binding!!.webView.webChromeClient = WebChromeClient()
        binding!!.webView.settings.defaultTextEncodingName = "utf-8"
        binding!!.webView.settings.domStorageEnabled = true
        binding!!.webView.settings.allowUniversalAccessFromFileURLs = false
        binding!!.webView.settings.allowFileAccess = false
        binding!!.webView.settings.javaScriptEnabled = true
        binding!!.webView.settings.loadWithOverviewMode = true
        binding!!.webView.settings.useWideViewPort = true
        if (url!!.contains("pdf") || url.contains("PDF")) {
            Log.e("if is not working", "yes")
            binding!!.webView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=$url")
        } else {
            Log.e("if is not working", "no")
            binding!!.webView.loadUrl(url)
        }
    }
}