package com.experiment.app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.experiment.app.view.AlphaVideoPlayer
import com.ss.ugc.android.alpha_player.IMonitor
import com.ss.ugc.android.alpha_player.IPlayerAction
import com.ss.ugc.android.alpha_player.model.ScaleType

/**
 * @version 1.0.0
 * @projectName: Experiment
 * @author: PRK
 * @description:
 * @date: 2024/2/1 2:21
 */
class AlphaPlayerFragment : Fragment() {
    private var player: AlphaVideoPlayer? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_alpha_player, container, false)
        player = view.findViewById(R.id.alpha_player)
        player!!.initPlayerController(viewLifecycleOwner, object : IPlayerAction {
            override fun endAction() {
                Log.d("AlphaPlayerFragment", "endAction")
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
        return view.rootView
    }

    override fun onStart() {
        super.onStart()
        player?.attachView()
        player?.startVideo("http://efx.media.dev/vapx/vap_demo.mp4")
    }

    override fun onPause() {
        super.onPause()
        player?.detachView()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.detachView()
    }
}