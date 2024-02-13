package com.experiment.app.utils

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import java.security.Permission


/**
 * @version 1.0.0
 * @projectName: Experiment
 * @author: PRK
 * @description:
 * @date: 2024/2/1 3:06
 */
 object PermissionUtils {
    private val PERMISSIONS_STORAGE = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )

    private val REQUEST_EXTERNAL_STORAGE = 1

     fun verifyStoragePermissions(activity: Activity, permission: String) : Boolean {
        try {
            //检测是否有写的权限
            val permission = ActivityCompat.checkSelfPermission(
                activity,
                permission
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
                )
                return false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
         return true
    }
}