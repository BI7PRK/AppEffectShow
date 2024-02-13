package com.experiment.app.view

import android.content.Context
import android.os.Environment
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import com.experiment.app.R
import com.experiment.app.utils.PermissionUtils
import com.experiment.app.utils.PreloadMediaSourceHelper

import com.ss.ugc.android.alpha_player.IMonitor
import com.ss.ugc.android.alpha_player.IPlayerAction
import com.ss.ugc.android.alpha_player.controller.IPlayerController
import com.ss.ugc.android.alpha_player.controller.PlayerController
import com.ss.ugc.android.alpha_player.model.AlphaVideoViewType
import com.ss.ugc.android.alpha_player.model.Configuration
import com.ss.ugc.android.alpha_player.model.DataSource
import java.io.File

/**
 * @version 1.0.0
 * @projectName: My Application
 * @author: PRK
 * @description:
 * @date: 2024/1/31 21:11
 */
class AlphaVideoPlayer(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs){

    companion object {
        const val TAG = "AlphaVideoPlayerView"
    }
    private val mVideoContainer: RelativeLayout
    private var mPlayerController: IPlayerController? = null

    init {
        LayoutInflater.from(context).inflate(getResourceLayout(), this)
        mVideoContainer = findViewById(R.id.video_view)
    }
    private fun getResourceLayout(): Int {
        return R.layout.view_alpha_video_player
    }

    private fun getLocalResourceDir() : String {
        val rootDir = Environment.getExternalStorageDirectory().absolutePath
        val sp = File.separator
        return "${rootDir}${sp}data${sp}kaixuan_app${sp}gifts${sp}"
    }

    fun initPlayerController(owner: LifecycleOwner, playerAction: IPlayerAction, monitor: IMonitor) {
        val configuration = Configuration(context, owner)
        configuration.alphaVideoViewType = AlphaVideoViewType.GL_TEXTURE_VIEW
        mPlayerController = PlayerController.get(configuration, ExoPlayerImpl(context))
        mPlayerController?.let {
            it.setPlayerAction(playerAction)
            it.setMonitor(monitor)
        }
    }

    fun startVideo(url: String) {
        if (TextUtils.isEmpty(url)) {
            return
        }

        PreloadMediaSourceHelper.load(url, object:
            PreloadMediaSourceHelper.CompleteMediaDataSource {
            override fun result(source: DataSource) {
                mPlayerController?.start(source)
            }
        })
    }

    fun attachView() {
        mPlayerController?.attachAlphaView(mVideoContainer)
        Toast.makeText(context, "attach alphaVideoView", Toast.LENGTH_SHORT).show()
    }

    fun detachView() {
        mPlayerController?.detachAlphaView(mVideoContainer)
        Toast.makeText(context, "detach alphaVideoView", Toast.LENGTH_SHORT).show()
    }

    fun release() {
        mPlayerController?.let {
            it.detachAlphaView(mVideoContainer)
            it.release()
        }

    }

}