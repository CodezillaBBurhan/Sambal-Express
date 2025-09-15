package sambal.mydd.app.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import sambal.mydd.app.utils.DialogManager
import android.view.WindowManager
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import sambal.mydd.app.R
import sambal.mydd.app.utils.StatusBarcolor
import java.lang.Exception

class PlayVideoActivity : AppCompatActivity() {
    private var videoUrl: String? = null
    private var videoView: VideoView? = null
    private var ibBackIcon: ImageButton? = null
    private var tvFromTime: TextView? = null
    private var tvToTime: TextView? = null
    private var seekBarSeekVideo: SeekBar? = null
    private val mHandler = Handler()
    private var songTime: String? = null
    private var totalTime: String? = null
    private val updateTimeTask: Runnable = object : Runnable {
        override fun run() {
            val totalDuration = videoView!!.duration.toLong()
            val currentDuration = videoView!!.currentPosition.toLong()
            val Currentpos = videoView!!.currentPosition
            val TotalTime = videoView!!.duration
            seekBarSeekVideo!!.progress = Currentpos
            val hrs = (currentDuration / 3600000).toInt()
            val mns = (currentDuration / 60000 % 60000).toInt()
            val scs = (currentDuration % 60000 / 1000).toInt()
            songTime = if (hrs == 0) {
                String.format("%02d:%02d", mns, scs)
            } else {
                String.format("%02d:%02d:%02d", hrs, mns, scs)
            }
            val timee = videoView!!.duration - videoView!!.currentPosition
            val hrss = timee / 3600000
            val mnss = timee / 60000 % 60000
            val scss = timee % 60000 / 1000
            totalTime = if (hrss == 0) {
                String.format("%02d:%02d", mnss, scss)
            } else {
                String.format("%02d:%02d:%02d", hrss, mnss, scss)
            }
            tvToTime!!.text = "- $totalTime"
            tvFromTime!!.text = songTime
            mHandler.postDelayed(this, 100)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_video)
        val dialogManager = DialogManager()
        dialogManager.showProcessDialog(this, "", false, null)
        videoView = findViewById(R.id.video_view)
        ibBackIcon = findViewById(R.id.ib_back_icon)
        tvFromTime = findViewById(R.id.tvFromTime)
        tvToTime = findViewById(R.id.tvToTime)
        seekBarSeekVideo = findViewById(R.id.seekbarProgress)
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView!!.setMediaController(mediaController)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if (intent.extras != null) {
            videoUrl = intent.getStringExtra("videoUrl")
        }
        ibBackIcon!!.setOnClickListener(View.OnClickListener { finish() })
        val uri = Uri.parse(videoUrl)
        Log.e("video url", videoUrl!!)
        //Uri uri = Uri.parse("http://techslides.com/demos/sample-videos/small.mp4");
        //Uri uri = Uri.parse("http://qthttp.apple.com.edgesuite.net/1010qwoeiuryfg/sl.m3u8");
        videoView!!.setVideoURI(uri)
        videoView!!.start()
        updateProgressBar()
        videoView!!.setOnCompletionListener(OnCompletionListener { videoView!!.pause() })
        videoView!!.setOnPreparedListener(OnPreparedListener {
            dialogManager.stopProcessDialog()
            seekBarSeekVideo!!.setMax(videoView!!.getDuration())
        })
        seekBarSeekVideo!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (b) {
                    videoView!!.seekTo(i)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun updateProgressBar() {
        mHandler.postDelayed(updateTimeTask, 100)
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacks(updateTimeTask)
    }

    override fun onResume() {
        super.onResume()
        try {
            StatusBarcolor.setStatusbarColor(this@PlayVideoActivity, "")
        } catch (e: Exception) {
        }
    }
}