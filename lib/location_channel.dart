import 'package:flutter/services.dart';

class LocationChannel {
  static const _channel = MethodChannel('com.g80bits/location');

  static Future<void> startService() async {
    await _channel.invokeMethod('startLocationService');
  }

  static Future<void> stopService() async {
    await _channel.invokeMethod('stopLocationService');
  }

  static Future<void> requestPermission() async {
    await _channel.invokeMethod('requestLocationPermission');
  }

  static Stream<dynamic> get locationStream {
    return EventChannel(
      'com.g80bits/location_updates',
    ).receiveBroadcastStream();
  }
}
