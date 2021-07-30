import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:zjad/zjad.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion =
          await Zjad.platformVersion ?? 'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: [
              SizedBox(
                height: 100.0,
                child: Text('自定义插件库'),
              ),
              Padding(
                padding: EdgeInsets.symmetric(vertical: 30.0),
                child: InkWell(
                  onTap: () {
                    Zjad.showRewardVideoAd(
                      "zjad_3091624125775544",
                      onZjAdTradeId:(String id){
                        print("RewardVideoAd onZjAdTradeId");
                      },
                      onZjAdLoaded: (String id) {
                        print("RewardVideoAd onZjAdLoad");
                      },
                      onZjAdShow: (String id) {
                        print("RewardVideoAd onZjAdShow");
                      },
                      onZjAdReward: (String id) {
                        print("RewardVideoAd onReward");
                      },
                      onZjAdClick: (String id) {
                        print("RewardVideoAd onZjAdClick");
                      },
                      onZjAdVideoComplete: (String id) {
                        print("RewardVideoAd onVideoComplete");
                      },
                      onZjAdClose: (String id) {
                        print("RewardVideoAd onZjAdClose");
                      },
                      onZjAdError: (String id, int code, String message) {
                        print("RewardVideoAd onZjAdError");
                      }
                    );
                  },
                  child: Text('加载激励视频'),
                ),
              ),
              Text('Running on: $_platformVersion\n')
            ],
          ),
        ),
      ),
    );
  }
}
