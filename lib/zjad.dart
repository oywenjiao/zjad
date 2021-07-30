
import 'dart:async';

import 'package:flutter/services.dart';

class Zjad {
  static const MethodChannel _channel =
      const MethodChannel('zjad');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
