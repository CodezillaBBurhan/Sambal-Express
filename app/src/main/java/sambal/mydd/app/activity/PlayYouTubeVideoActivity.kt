package sambal.mydd.app.activity

/*import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener
import com.google.android.youtube.player.YouTubePlayerView*/

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import sambal.mydd.app.R
import sambal.mydd.app.utils.ErrorMessage


class PlayYouTubeVideoActivity :AppCompatActivity() /*YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener */{
    private var videoId: String? = null
    private var videoUrl: String? = null
    private val API_KEY = "AIzaSyBaL-lMYwCzkhD_D2DSoGzFlVaL7gENQ-k"
    private var youTubePlayerView: YouTubePlayerView? = null
    private var context: Context? = null
    private var ibBackIcon: ImageButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_you_tube_video)
        context = this
       // youTubePlayerView = findViewById<View>(R.id.youtube_view) as YouTubePlayerView
        ibBackIcon = findViewById<View>(R.id.ib_back_icon) as ImageButton

        /*if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }*/window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if (intent.extras != null) {
            videoId = intent.getStringExtra("videoId")
            videoUrl = intent.getStringExtra("videoUrl")
        }
        ErrorMessage.E("videoId>>>>>"+videoId)
        ErrorMessage.E("videoUrl>>>>>"+videoUrl)
        ibBackIcon!!.setOnClickListener { finish() }
       // episode_type_youtube()


        youTubePlayerView = findViewById<View>(R.id.youtube_player_view) as YouTubePlayerView

        lifecycle.addObserver(youTubePlayerView!!)

        youTubePlayerView!!.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
               // val videoId = "S0Q4gqBUs7c"
                youTubePlayer.loadVideo(videoId!!, 0f)
            }
        })
    }

   /* private fun episode_type_youtube() {
        youTubePlayerView!!.initialize(API_KEY, this)
    }

    override fun onInitializationSuccess(
        provider: YouTubePlayer.Provider,
        youTubePlayer: YouTubePlayer,
        b: Boolean
    ) {
        youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener)
        youTubePlayer.setPlaybackEventListener(playbackEventListener)
        youTubePlayer.setFullscreen(true)
        *//** Start buffering  *//*
        if (!b) {
            youTubePlayer.cueVideo(videoId);
           // youTubePlayer.loadVideo(videoId)
        }
    }

    override fun onInitializationFailure(
        provider: YouTubePlayer.Provider,
        youTubeInitializationResult: YouTubeInitializationResult
    ) {
    }

    private val playerStateChangeListener: PlayerStateChangeListener =
        object : PlayerStateChangeListener {
            override fun onLoading() {}
            override fun onLoaded(s: String) {}
            override fun onAdStarted() {}
            override fun onVideoStarted() {}
            override fun onVideoEnded() {}
            override fun onError(errorReason: YouTubePlayer.ErrorReason) {}
        }
    private val playbackEventListener: PlaybackEventListener = object : PlaybackEventListener {
        override fun onPlaying() {}
        override fun onPaused() {}
        override fun onStopped() {}
        override fun onBuffering(b: Boolean) {}
        override fun onSeekTo(i: Int) {}
    }*/
}