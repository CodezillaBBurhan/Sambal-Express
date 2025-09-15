package sambal.mydd.app.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import sambal.mydd.app.R
import sambal.mydd.app.databinding.ActivityWebViewBinding
import sambal.mydd.app.utils.ErrorMessage
import sambal.mydd.app.utils.StatusBarcolor
import java.lang.Exception

class WebViewActivity : AppCompatActivity() {
    var binding: ActivityWebViewBinding? = null
    private var title: String? = ""
    private var url: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web_view)
        if (intent.extras != null) {
            title = intent.getStringExtra("title")
            url = intent.getStringExtra("url")
        }

        initToolBar()
        initView()
    }

    private fun initView() {
        binding!!.webView.settings.javaScriptEnabled = true
        binding!!.webView.webViewClient = WebViewClient()
        binding!!.webView.settings.loadWithOverviewMode = true
        binding!!.webView.settings.useWideViewPort = true
        val webSettings = binding!!.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.allowUniversalAccessFromFileURLs = true
        webSettings.domStorageEnabled = true
        binding!!.webView.webChromeClient = WebChromeClient()
        binding!!.webView.loadUrl(url!!)
    }

    public override fun onResume() {
        super.onResume()
        /*try {
            StatusBarcolor.setStatusbarColor(this@WebViewActivity, "")
        } catch (e: Exception) {
        }*/
    }

    private fun initToolBar() {
        binding!!.tabLayout.toolbarRightFavoriteIcon.visibility = View.GONE
        binding!!.tabLayout.toolbarRightUnfavoriteIcon.visibility = View.GONE
        setSupportActionBar(binding!!.tabLayout.toolbar)
        binding!!.tabLayout.toolbarLeftImage.visibility = View.VISIBLE
        //rightImage.setClickable(true);
        binding!!.tabLayout.toolbarLeftImage.setImageResource(R.drawable.back)
        binding!!.tabLayout.toolbarRightImage.visibility = View.INVISIBLE
        setSupportActionBar(binding!!.tabLayout.toolbar)
        binding!!.tabLayout.toolbarTitle.text = title
        binding!!.tabLayout.toolbarLeftImage.setOnClickListener { finish() }
    }
}