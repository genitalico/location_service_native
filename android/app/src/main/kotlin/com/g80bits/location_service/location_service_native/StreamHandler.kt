package com.g80bits.location_service.location_service_native

import android.os.Handler
import android.os.Looper
import io.flutter.plugin.common.EventChannel

object LocationStreamHandler : EventChannel.StreamHandler {
    private val main = Handler(Looper.getMainLooper())
    @Volatile private var sink: EventChannel.EventSink? = null

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) { sink = events }
    override fun onCancel(arguments: Any?) { sink = null }

    fun emit(lat: Double, lon: Double, acc: Float?, bearing: Float?, speed: Float?, timeMillis: Long) {
        val payload = mapOf(
            "latitude" to lat,
            "longitude" to lon,
            "accuracy" to acc,
            "bearing" to bearing,
            "speed" to speed,
            "timestamp" to timeMillis
        )

        main.post { sink?.success(payload) }
    }

    fun emitError(code: String, message: String) {
        main.post { sink?.error(code, message, null) }
    }
}
