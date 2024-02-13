package com.experiment.app.utils


import android.os.Environment
import android.util.Log
import com.ss.ugc.android.alpha_player.model.DataSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.IOException


/**
 * @version 1.0.0
 * @projectName: Experiment
 * @author: PRK
 * @description:
 * @date: 2024/2/1 15:17
 */
object PreloadMediaSourceHelper  {
    private val TAG = "PreloadMediaSourceHelper"
    private var mediaFile:String = ""
    private var baseDir:String;

    init {
        baseDir = getLocalResourceDir()
    }
    private fun getLocalResourceDir() : String {
        val rootDir = Environment.getExternalStorageDirectory().absolutePath
        val sp = File.separator
        return "${rootDir}${sp}data${sp}kaixuan_app${sp}gifts${sp}"
    }

    fun load(url:String, completeMediaDataSource: CompleteMediaDataSource){
        val urlIndex = url.replace("\\", "/").lastIndexOf("/")
        mediaFile = if(urlIndex >= 0){
            url.substring(urlIndex + 1)
        }else {
            url
        }
        val source =  getMediaDataSource(mediaFile)
        val checker = checkLocalSource(source)
        if(checker.exists){
            completeMediaDataSource.result(source)
            return
        }
        runBlocking {
            try{
                DownloadManager.download(url, checker.file).collect {
                    when (it) {
                        is DownloadState.InProgress -> {
                            Log.d(TAG, "download in progress: ${it.progress}.")
                        }
                        is DownloadState.Success -> {
                            Log.d(TAG, "download finished.")
                            completeMediaDataSource.result(source)
                        }
                        is DownloadState.Error -> {
                            Log.e(TAG, "download error: ${it.throwable}.")
                            try {
                                val f = File(checker.file.path)
                                if(f.exists()) f.delete()
                            } catch (io:IOException){
                                io.printStackTrace()
                            }
                        }
                    }
                }
            } catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    private fun checkLocalSource(source:DataSource):  CheckResult {
        val fileInfo = File(source.baseDir, source.portPath)
        if(fileInfo.exists() && fileInfo.isFile && fileInfo.length() > 0){
            return CheckResult(fileInfo, true)
        }
        return  CheckResult(fileInfo, false)
    }

    private fun getMediaDataSource(file:String): DataSource {
        val dataSource = DataSource()
            .setBaseDir(baseDir)
            .setPortraitPath(file, 2)
            .setLandscapePath(file, 2)
        if(!dataSource.isValid()){
            Log.e(TAG, "The DataSource is invalid")
        }
        return dataSource
    }

    private class CheckResult(f: File, e: Boolean){
        var file = f
        val exists = e
    }

    interface CompleteMediaDataSource{
        fun result(source: DataSource)
    }
}

