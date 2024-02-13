package com.experiment.app

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.experiment.app.utils.PreloadMediaSourceHelper
import com.experiment.app.view.AlphaVideoPlayer
import com.google.android.exoplayer2.Timeline.Window
import com.google.android.exoplayer2.offline.ProgressiveDownloader
import com.ss.ugc.android.alpha_player.IMonitor
import com.ss.ugc.android.alpha_player.IPlayerAction
import com.ss.ugc.android.alpha_player.model.ScaleType


class GiftPlayActivity : AppCompatActivity() {

    private var player: AlphaVideoPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_gift_play)
        player = findViewById(R.id.gift_player)
        player?.initPlayerController(this, object : IPlayerAction {
            override fun endAction() {
                Log.d("AlphaPlayerFragment", "endActionendAction")
                finish()
            }

            override fun onVideoSizeChanged(
                videoWidth: Int,
                videoHeight: Int,
                scaleType: ScaleType
            ) {
                Log.d("AlphaPlayerFragment", "onVideoSizeChanged $videoWidth, $videoHeight")
            }

            override fun startAction() {
                Log.d("AlphaPlayerFragment", "startAction")
            }

        }, object : IMonitor {
            override fun monitor(
                result: Boolean,
                playType: String,
                what: Int,
                extra: Int,
                errorInfo: String
            ) {
                Log.d("AlphaPlayerFragment", "monitor..$playType")
            }
        })
        player?.attachView()

        findViewById<Button>(R.id.close_btn).setOnClickListener{
            finish()
        }
        val bgImg = findViewById<ImageView>(R.id.bg_img)
        Glide.with(this)
            .applyDefaultRequestOptions(
                RequestOptions().centerCrop()
            )
            .load("https://picsum.photos/200/300")
            .into(bgImg)
    }

    override fun onStart() {
        super.onStart()
        player?.startVideo("http://efx.media.dev/vapx/vap_demo.mp4")
    }

}