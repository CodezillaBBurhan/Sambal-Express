package sambal.mydd.app.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.databinding.DataBindingUtil
import android.webkit.WebViewClient
import android.webkit.WebView
import sambal.mydd.app.R
import sambal.mydd.app.databinding.WebviewstoreBinding
import sambal.mydd.app.utils.StatusBarcolor
import java.lang.Exception

class WebviewStore : AppCompatActivity() {
    var binding: WebviewstoreBinding? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DataBindingUtil.setContentView(this, R.layout.webviewstore)
        binding!!.ivBack.setOnClickListener { finish() }
        val bundle = intent.extras
        if (bundle != null) {
            val url = bundle.getString("url")
            try {
                binding!!.tVtitle.text = bundle.getString("title")
            } catch (e: Exception) {
                binding!!.tVtitle.text = ""
            }
            binding!!.webView.settings.javaScriptEnabled = true
            binding!!.webView.settings.loadWithOverviewMode = true
            binding!!.webView.settings.useWideViewPort = true
            binding!!.webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    view.loadUrl(url)
                    return true
                }

                override fun onPageFinished(view: WebView, url: String) {}
            }
            binding!!.webView.loadUrl(url!!)
            Log.e("E", "2")
        }
    }

    public override fun onResume() {
        super.onResume()
        try {
            StatusBarcolor.setStatusbarColor(this@WebviewStore, "")
        } catch (e: Exception) {
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}