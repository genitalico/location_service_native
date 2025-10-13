package com.g80bits.location_service.location_service_native

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class LocationChannel(val activity:FlutterActivity) : MethodChannel.MethodCallHandler {



    override fun onMethodCall(
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        when (call.method) {
            ConstantsApp.FLUTTER_METHOD_REQUEST_LOCATION_PERMISSION -> {
                requestForegroundLocation()
                result.success(null)
            }
            ConstantsApp.FLUTTER_METHOD_START_LOCATION_SERVICE -> {
                if (hasForegroundLocation()) {
                    val intent = Intent(this.activity, LocationService::class.java)
                    ContextCompat.startForegroundService(activity, intent)
                    result.success("Servicio iniciado")
                } else {
                    result.error("PERMISSIONS_DENIED", "Los permisos de ubicación no fueron concedidos.", null)
                }
            }
            ConstantsApp.FLUTTER_METHOD_STOP_LOCATION_SERVICE -> {
                val intent = Intent(this.activity, LocationService::class.java)
                this.activity.stopService(intent)
                result.success("Servicio detenido")
            }
            else -> result.notImplemented()
        }
    }

    private fun hasForegroundLocation(): Boolean {
        val fine =
            ActivityCompat.checkSelfPermission(this.activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse =
            ActivityCompat.checkSelfPermission(this.activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    private fun hasBackgroundLocation(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.checkSelfPermission(
                this.activity,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    private fun requestForegroundLocation() {
        if (hasForegroundLocation()) return
        val perms = buildList {
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }.toTypedArray()

        ActivityCompat.requestPermissions(this.activity, perms, ConstantsApp.REQ_FOREGROUND_LOCATION)
    }

    private fun requestBackgroundLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            when {
                Build.VERSION.SDK_INT == Build.VERSION_CODES.Q -> {
                    ActivityCompat.requestPermissions(
                        this.activity,
                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                        ConstantsApp.REQ_BACKGROUND_LOCATION
                    )
                }
                else -> {
                    openAppLocationSettings()
                }
            }
        }
    }

    private fun openAppLocationSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", this.activity.packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        this.activity.startActivity(intent)
    }

    // Manejo de resultados:
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        fun granted(p: String): Boolean {
            val idx = permissions.indexOf(p)
            return idx >= 0 && grantResults.getOrNull(idx) == PackageManager.PERMISSION_GRANTED
        }

        when (requestCode) {
            ConstantsApp.REQ_FOREGROUND_LOCATION -> {
                val fineOk = granted(Manifest.permission.ACCESS_FINE_LOCATION)
                val coarseOk = granted(Manifest.permission.ACCESS_COARSE_LOCATION)
                val notifOk = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    granted(Manifest.permission.POST_NOTIFICATIONS) else true

                Log.d("MainActivity", "Foreground perms -> fine=$fineOk, coarse=$coarseOk, notif=$notifOk")

                if (fineOk || coarseOk) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        requestBackgroundLocation()
                    }
                } else {
                    Log.w("MainActivity", "Permisos foreground denegados")
                }
            }

            ConstantsApp.REQ_BACKGROUND_LOCATION -> {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                    val bgOk = granted(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    Log.d("MainActivity", "Background (API29) -> $bgOk")
                }
            }
        }
    }




    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ConstantsApp.ID_NOTIFICATION_LOCATION_SERVICE,
                ConstantsApp.TXT_LOCATION_TRACKING,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = ConstantsApp.TXT_LOCATION_NOTIFICATION_CONTENT
            val manager = this.activity.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}