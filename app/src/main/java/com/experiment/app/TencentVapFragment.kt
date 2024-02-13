package com.experiment.app

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.experiment.app.utils.PreloadMediaSourceHelper
import com.ss.ugc.android.alpha_player.model.DataSource

import com.tencent.qgame.animplayer.AnimConfig
import com.tencent.qgame.animplayer.AnimView
import com.tencent.qgame.animplayer.inter.IAnimListener
import com.tencent.qgame.animplayer.util.ScaleType
import com.tencent.qgame.animplayer.inter.IFetchResource
import com.tencent.qgame.animplayer.inter.OnResourceClickListener
import com.tencent.qgame.animplayer.mix.Resource
import java.io.File
import java.lang.ref.ReferenceQueue
import java.util.Random


/**
 * @version 1.0.0
 * @projectName: Experiment
 * @author: PRK
 * @description:
 * @date: 2024/2/3 13:44
 */
class TencentVapFragment(private val  url: String) : Fragment(), IAnimListener {
    private val TAG = TencentVapFragment::javaClass.name

    private lateinit var bg_cover:ImageView
    // 动画View
    private lateinit var animView: AnimView

    private var lastToast: Toast? = null
    private var fileUri = ""
    private var isOldVersion = false

    private val uiHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tencent_vap, container, false)
        animView = view.findViewById(R.id.playerView)
        bg_cover =  view.findViewById(R.id.bg_cover)
        return view.rootView
    }

    override fun onStart() {
        super.onStart()
        Glide.with(this)
            .applyDefaultRequestOptions(RequestOptions().centerCrop())
            .load("https://picsum.photos/200/300")
            .into(bg_cover)

        // 居中（根据父布局按比例居中并全部显示，默认fitXY）
        animView.setScaleType(ScaleType.FIT_CENTER)

        uiHandler.post {
            // 注册动画监听
            animView.setAnimListener(this)
            /**
             * 注册资源获取类
             */
            animView.setFetchResource(object : IFetchResource {
                /**
                 * 获取图片资源
                 * 无论图片是否获取成功都必须回调 result 否则会无限等待资源
                 */
                override fun fetchImage(resource: Resource, result: (Bitmap?) -> Unit) {
                    /**
                     * srcTag是素材中的一个标记，在制作素材时定义
                     * 解析时由业务读取tag决定需要播放的内容是什么
                     * 比如：一个素材里需要显示多个头像，则需要定义多个不同的tag，表示不同位置，需要显示不同的头像，文字类似
                     */
//                    val srcTag = resource.tag
//                    if (srcTag.isNotEmpty()) {
//                        val drawableId = if (srcTag == "head1") R.drawable.head1 else R.drawable.head2
//                        val options = BitmapFactory.Options()
//                        options.inScaled = false
//                        result(BitmapFactory.decodeResource(resources, drawableId, options))
//                    } else {
//                        result(null)
//                    }
                    result(null)
                }

                /**
                 * 获取文字资源
                 */
                override fun fetchText(resource: Resource, result: (String?) -> Unit) {
                    val str = "恭喜 No.${1000 + Random().nextInt(8999)}用户 升神"
                    result(str)
//                    val srcTag = resource.tag
//                    if (srcTag.isNotEmpty()) { // 此tag是已经写入到动画配置中的tag
//                        result(str)
//                    } else {
//                        result(null)
//                    }
                }

                /**
                 * 播放完毕后的资源回收
                 */
                override fun releaseResource(resources: List<Resource>) {
                    resources.forEach {
                        it.bitmap?.recycle()
                    }
                }
            })
        }


        // 注册点击事件监听
        animView.setOnResourceClickListener(object : OnResourceClickListener {
            override fun onClick(resource: Resource) {
                lastToast?.cancel()
                lastToast = Toast.makeText(
                    context,
                    "srcTag=${resource.tag} onClick ${resource.curPoint}",
                    Toast.LENGTH_LONG
                )
                lastToast?.show()
            }
        })
        /**
         * 开始播放主流程
         * ps: 主要流程都是对AnimView的操作，其它比如队列，或改变窗口大小等操作都不是必须的
         */
        PreloadMediaSourceHelper.load(url, object:
            PreloadMediaSourceHelper.CompleteMediaDataSource {
            override fun result(source: DataSource) {
                Thread {
                    fileUri = "${source.baseDir}${source.portPath}"
                    if(isOldVersion){
                        animView.enableVersion1(false)
                    }
                    animView.startPlay(File(fileUri))
                }.start()
            }
        })

    }


    /**
     * 视频信息准备好后的回调，用于检查视频准备好后是否继续播放
     * @return true 继续播放 false 停止播放
     */
    override fun onVideoConfigReady(config: AnimConfig): Boolean {
        return true
    }

    override fun onFailed(errorType: Int, errorMsg: String?) {
        //TODO("Not yet implemented")
        Log.d(TAG, "onFailed: $errorType, $errorMsg")
        if(errorType == 10005 && fileUri.isNotEmpty()){
            isOldVersion = true
            animView.enableVersion1(true)
             animView.stopPlay()
            animView.startPlay(File(fileUri))
        }
    }

    override fun onVideoComplete() {
        //TODO("Not yet implemented")
        Log.d(TAG, "onVideoComplete")
    }

    override fun onVideoDestroy() {
        //TODO("Not yet implemented")
        Log.d(TAG, "onVideoDestroy")
    }

    override fun onVideoRender(frameIndex: Int, config: AnimConfig?) {
        //TODO("Not yet implemented")
        Log.d(TAG, "onVideoRender: $frameIndex")
    }

    override fun onVideoStart() {
        //TODO("Not yet implemented")
        Log.d(TAG, "onVideoStart")
        fileUri = ""
    }


}