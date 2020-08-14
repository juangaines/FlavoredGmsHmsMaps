package com.example.gmshmsdemo.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.graphics.Color
import androidx.core.app.ActivityCompat

class UtilsAndroid {

    companion object{

        const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
        const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
        const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
        const val LOCATION_PERMISSION_INDEX = 0
        const val BACKGROUND_LOCATION_PERMISSION_INDEX = 1

        private val TAG = UtilsAndroid::class.java.simpleName

        private val runningQOrLater =
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

        fun isPermissionGranted(context: Context) = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) === PackageManager.PERMISSION_GRANTED


        @TargetApi(29)
        fun foregroundAndBackgroundLocationPermissionApproved(
            activity: Activity
        ): Boolean {
            val foregroundLocationApproved = (
                    PackageManager.PERMISSION_GRANTED ==
                            ActivityCompat.checkSelfPermission(
                                activity,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ))
            val backgroundPermissionApproved =
                if (runningQOrLater) {
                    PackageManager.PERMISSION_GRANTED ==
                            ActivityCompat.checkSelfPermission(
                                activity,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            )
                } else {
                    true
                }
            return foregroundLocationApproved && backgroundPermissionApproved
        }

        @TargetApi(29)
        fun requestForegroundAndBackgroundLocationPermissions(
            activity: Activity
        ) {
            if (foregroundAndBackgroundLocationPermissionApproved(
                    activity
                )
            )
                return

            // Else request the permission
            // this provides the result[LOCATION_PERMISSION_INDEX]
            var permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

            val resultCode = when {
                runningQOrLater -> {
                    // this provides the result[BACKGROUND_LOCATION_PERMISSION_INDEX]
                    permissionsArray += Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
                }
                else -> REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            }
            ActivityCompat.requestPermissions(
                activity,
                permissionsArray,
                resultCode
            )
        }

        fun decideColor(type:String):Int{
            return when(type){
                "walking"->{Color.BLUE}
                "bicycling"->{Color.RED}
                "driving"->{Color.MAGENTA}
                else->{Color.WHITE}
            }
        }
    }
}