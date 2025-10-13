package com.g80bits.location_service.location_service_native

import android.Manifest
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority


class LocationService: Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.locations.forEach { location ->
                    Log.d("LocationService", "Lat: ${location.latitude}, Lon: ${location.longitude}")

                    LocationStreamHandler.emit(
                        lat = location.latitude,
                        lon = location.longitude,
                        acc = if (location.hasAccuracy()) location.accuracy else null,
                        bearing = if (location.hasBearing()) location.bearing else null,
                        speed = if (location.hasSpeed()) location.speed else null,
                        timeMillis = location.time
                    )
                }
            }
        }

        startLocationUpdates()
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun startLocationUpdates() {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, ConstantsApp.INTERVAL_LOCATION_UPDATES)
            .setMinUpdateIntervalMillis(ConstantsApp.INTERVAL_LOCATION_UPDATES)
            .build()

        fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, ConstantsApp.ID_NOTIFICATION_LOCATION_SERVICE)
            .setContentTitle(ConstantsApp.TXT_LOCATION_TRACKING)
            .setContentText(ConstantsApp.TXT_LOCATION_NOTIFICATION_CONTENT)
            .setSmallIcon(android.R.drawable.ic_dialog_map)
            .build()

        startForeground(1, notification)
        return START_STICKY
    }

    override fun onDestroy() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}