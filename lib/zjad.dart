
import 'dart:async';

import 'package:flutter/services.dart';

typedef AdCallback = void Function(String id);
typedef AdErrorCallback = void Function(String id, int code, String message);

class Zjad {
  static int _channelId = 0;
  static const MethodChannel _channel =
      const MethodChannel('com_cat_zjad');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static void showRewardVideoAd(String adId, {
    AdCallback? onZjAdTradeId,
    AdCallback? onZjAdLoaded,
    AdCallback? onZjAdShow,
    AdCallback? onZjAdReward,
    AdCallback? onZjAdClick,
    AdCallback? onZjAdVideoComplete,
    AdCallback? onZjAdClose,
    AdErrorCallback? onZjAdError
  }) {
    _channel.invokeMethod("showRewardVideoAd", {"_channelId": ++_channelId, "adId": adId});

    EventChannel eventChannel = EventChannel("com_cat_zjad/event_$_channelId");
    eventChannel.receiveBroadcastStream().listen((event) {
      switch (event["event"]) {
        case "onZjAdTradeId":
          onZjAdTradeId?.call(event["id"]);
          break;
        case "onZjAdLoaded":
          onZjAdLoaded?.call(event["id"]);
          break;

        case "onZjAdShow":
          onZjAdShow?.call(event["id"]);
          break;

        case "onZjAdReward":
          onZjAdReward?.call(event["id"]);
          break;

        case "onZjAdClick":
          onZjAdClick?.call(event["id"]);
          break;

        case "onZjAdVideoComplete":
          onZjAdVideoComplete?.call(event["id"]);
          break;

        case "onZjAdClose":
          onZjAdClose?.call(event["id"]);
          break;

        case "onZjAdError":
          onZjAdError?.call(event["id"], event["code"], event["message"]);
          break;
      }
    });
  }
}
