package com.g80bits.location_service.location_service_native

object ConstantsApp {
    const val FLUTTER_CHANNEL_LOCATION = "com.g80bits/location"
    const val FLUTTER_CHANNEL_LOCATION_UPDATES = "com.g80bits/location_updates"
    const val FLUTTER_METHOD_REQUEST_LOCATION_PERMISSION = "requestLocationPermission"
    const val FLUTTER_METHOD_START_LOCATION_SERVICE = "startLocationService"
    const val FLUTTER_METHOD_STOP_LOCATION_SERVICE = "stopLocationService"
    const val ID_NOTIFICATION_LOCATION_SERVICE = "location_channel";
    const val INTERVAL_LOCATION_UPDATES = 1 * 60 * 1000L
    const val TXT_LOCATION_TRACKING = "Servicio de ubicación iniciado"
    const val TXT_LOCATION_NOTIFICATION_CONTENT = "Enviando ubicación"

    const val REQ_FOREGROUND_LOCATION = 1001
    const val REQ_BACKGROUND_LOCATION = 1002

}