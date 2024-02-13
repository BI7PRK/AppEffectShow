package com.experiment.app.utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url
import java.io.File
import java.io.IOException

/**
 * @version 1.0.0
 * @projectName: Experiment
 * @author: PRK
 * @description:
 * @date: 2024/2/1 16:44
 */
object DownloadManager {
    suspend fun download(url: String, file: File): Flow<DownloadState> {
        return flow {
            val response = Retrofit.Builder()
                .baseUrl(getBaseUrl(url))
                .build()
                .create(DownloadService::class.java)
                .download(url)
                .execute()
            if (response.isSuccessful) {
                try {
                    saveToFile(response.body()!!, file) {
                        emit(DownloadState.InProgress(it))
                    }
                    emit(DownloadState.Success(file))
                }catch (io:IOException) {
                    io.printStackTrace()
                    emit(DownloadState.Error(io))
                }
            } else {
                emit(DownloadState.Error(IOException(response.toString())))
            }
        }.catch {
            emit(DownloadState.Error(it))
        }.flowOn(Dispatchers.IO)
    }
    private fun mkDirs(file: File){
        if(file.parent !=null) {
            val dirs = file.parent!!.split(File.separator)
            var mkDir = ""
            for (d in dirs){
                mkDir = "$mkDir$d${File.separator}"
                if(mkDir == File.separator) continue
                val dirInfo = File(mkDir)
                if (!dirInfo.exists()) {
                    dirInfo.mkdir()
                }
            }
        }
    }
    private inline fun saveToFile(responseBody: ResponseBody, file: File, progressListener: (Int) -> Unit) {
        val total = responseBody.contentLength()
        var bytesCopied = 0
        var emittedProgress = 0
        mkDirs(file)
        file.outputStream().use { output ->
            val input = responseBody.byteStream()
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var bytes = input.read(buffer)
            while (bytes >= 0) {
                output.write(buffer, 0, bytes)
                bytesCopied += bytes
                bytes = input.read(buffer)
                val progress = (bytesCopied * 100 / total).toInt()
                if (progress - emittedProgress > 0) {
                    progressListener(progress)
                    emittedProgress = progress
                }
            }
        }
    }

    private fun getBaseUrl(url:String):String{
        var strUrl = url
        var xIndex = strUrl.indexOf("://")
        val prefix = strUrl.substring(0, xIndex + 3)
        if(xIndex >=0){
            strUrl = strUrl.substring(xIndex + 3)
        }
        xIndex = strUrl.indexOf("/")
        if(xIndex >=0){
            strUrl = strUrl.substring(0, xIndex)
        }
        return prefix + strUrl
    }
}

interface DownloadService {
    @Streaming
    @GET
    fun download(@Url url: String): Call<ResponseBody>
}

sealed class DownloadState {
    data class InProgress(val progress: Int) : DownloadState()
    data class Success(val file: File) : DownloadState()
    data class Error(val throwable: Throwable) : DownloadState()
}