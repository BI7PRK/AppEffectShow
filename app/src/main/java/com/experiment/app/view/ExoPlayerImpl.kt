package com.experiment.app.view

import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.Surface
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Renderer
import com.google.android.exoplayer2.RenderersFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.audio.AudioRendererEventListener
import com.google.android.exoplayer2.audio.MediaCodecAudioRenderer
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector
import com.google.android.exoplayer2.metadata.MetadataOutput
import com.google.android.exoplayer2.source.LoopingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.text.TextOutput
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.MediaCodecVideoRenderer
import com.google.android.exoplayer2.video.VideoRendererEventListener
import com.google.android.exoplayer2.video.VideoSize
import com.ss.ugc.android.alpha_player.model.VideoInfo
import com.ss.ugc.android.alpha_player.player.AbsPlayer
import java.lang.Exception


/**
 * @version 1.0.0
 * @projectName: My Application
 * @author: PRK
 * @description:
 * @date: 2024/1/31 23:51
 */
class ExoPlayerImpl(private val context: Context) : AbsPlayer(context) {
    private lateinit var exoPlayer: ExoPlayer
    private var isLooping: Boolean = false
    private var currVideoWidth: Int = 0
    private var currVideoHeight: Int = 0
    private val dataSourceFactory: DefaultDataSourceFactory
    private var videoSource: MediaSource? = null

    init {
        dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, "player"))
    }

    private val exoPlayerListener: Player.Listener = object: Player.Listener {

        override fun onRenderedFirstFrame() {
            super.onRenderedFirstFrame()
            firstFrameListener?.onFirstFrame()
        }
        override fun onVideoSizeChanged(videoSize: VideoSize) {
            super.onVideoSizeChanged(videoSize)
            currVideoWidth = videoSize.width
            currVideoHeight = videoSize.height
        }
        override fun onPlayerError(error: PlaybackException) {
            errorListener?.onError(0, 0, "ExoPlayer on error: " + Log.getStackTraceString(error))
        }
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            Log.d("ExoPlayerImpl", "onPlaybackStateChanged: $playbackState")
            if(playbackState == 3){ //开始播放
                preparedListener?.onPrepared()
            }
            if(playbackState == 4){ //播放完毕
                completionListener?.onCompletion()
            }
        }

    }


    override fun getPlayerType(): String {
        return "ExoPlayerImpl"
    }

    override fun getVideoInfo(): VideoInfo {
        return VideoInfo(currVideoWidth, currVideoHeight)
    }

    override fun initMediaPlayer() {
        exoPlayer = ExoPlayer.Builder(context).build()
        exoPlayer.addListener(exoPlayerListener)
    }

    override fun pause() {
        exoPlayer.playWhenReady = false
    }

    override fun prepareAsync() {
        videoSource?.let { exoPlayer.prepare(it) }
        exoPlayer.playWhenReady = true
    }

    override fun release() {
        exoPlayer.release()
    }

    override fun reset() {
        exoPlayer.stop()
        exoPlayer.play()
        exoPlayer.playWhenReady = true
    }

    override fun setDataSource(dataPath: String) {
        Log.d("ExoPlayerImpl", dataPath)
        val extractorMediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(dataPath))
        videoSource = if (isLooping) {
            LoopingMediaSource(extractorMediaSource)
        } else {
            extractorMediaSource
        }
        reset()
    }

    override fun setLooping(looping: Boolean) {
        this.isLooping = looping
    }

    override fun setScreenOnWhilePlaying(onWhilePlaying: Boolean) {
        exoPlayer.playWhenReady = onWhilePlaying
    }

    override fun setSurface(surface: Surface) {
        Log.d("ExoPlayerImpl", "surface: ${surface.isValid}")
        if(!surface.isValid) {
           throw Exception("The surface is invalid")
        }
        exoPlayer.setVideoSurface(surface)
    }

    override fun start() {
        exoPlayer.playWhenReady = true
        exoPlayer.play()
    }

    override fun stop() {
        exoPlayer.stop()
    }


}