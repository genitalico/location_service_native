package com.g80bits.location_service.location_service_native

import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity(){

    private val locationChannel = LocationChannel(this)

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        locationChannel.createNotificationChannel()
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, ConstantsApp.FLUTTER_CHANNEL_LOCATION)
            .setMethodCallHandler(locationChannel)
        EventChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            ConstantsApp.FLUTTER_CHANNEL_LOCATION_UPDATES
        ).setStreamHandler(LocationStreamHandler)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray){
        this.locationChannel.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
