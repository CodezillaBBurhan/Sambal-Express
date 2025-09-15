package sambal.mydd.app.activity

import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.annotation.RequiresApi
import android.os.Build
import android.os.Bundle
import android.widget.LinearLayout
import android.app.Activity
import android.graphics.Color
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import sambal.mydd.app.R
import java.util.*

abstract class BaseActivity : AppCompatActivity() {
    protected var toolbar: Toolbar? = null
    protected var title_txt: TextView? = null
    protected abstract val contentResId: Int
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(contentResId)
    }

    private fun setAppLocale(localeCode: String) {
        val resources = resources
        val dm = resources.displayMetrics
        val config = resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(Locale(localeCode.lowercase(Locale.getDefault())))
        } else {
            config.locale = Locale(localeCode.lowercase(Locale.getDefault()))
        }
        resources.updateConfiguration(config, dm)
    }

    protected fun setToolbarWithBackButton_colorprimary(title: String) {
        initToolbar()
        supportActionBar!!.title = ""
        title_txt!!.text = "" + title
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_circle_back_arrow)
    }
    protected fun setToolbarWithWhiteBackButton(title: String) {
        initToolbar()
        supportActionBar!!.title = ""
        title_txt!!.text = "" + title
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
    }

    protected fun initToolbar() {
        toolbar = findViewById(R.id.tool_bar)
        title_txt = findViewById(R.id.title_txt)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
    }

    protected fun initTitleToolbar(title: String?) {
        initToolbar()
        toolbar = findViewById(R.id.tool_bar)
        supportActionBar!!.title = "0"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.title = title
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        //        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
    }

    protected fun initTitlewithoutbackgroundToolbar(title: String?) {
        initToolbar()
        toolbar = findViewById(R.id.tool_bar)
        val main_layout = findViewById<LinearLayout>(R.id.main_layout)
        main_layout.setBackgroundColor(Color.TRANSPARENT)
        supportActionBar!!.title = "0"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.title = title
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        //        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        //        getMenuInflater().inflate(R.menu.main, menu);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> navigateToParent()
        }
        return true
    }

    private fun navigateToParent() {
        val intent = NavUtils.getParentActivityIntent(this)
        if (intent == null) {
            finish()
        } else {
            NavUtils.navigateUpFromSameTask(this)
        }
        onAnim(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        onAnim(this)
    }

    companion object {
        fun onAnim(activity: Activity) {
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}