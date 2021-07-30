package com.example.zjad;

import android.content.Context;
import android.app.ProgressDialog;
import android.os.Build;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.zj.zjsdk.ZjH5ContentListener;
import com.zj.zjsdk.ZjUser;
import com.zj.zjsdk.ad.ZjAdError;
import com.zj.zjsdk.ad.ZjH5Ad;
import com.zj.zjsdk.ad.ZjInterstitialAd;
import com.zj.zjsdk.ad.ZjInterstitialAdListener;
import com.zj.zjsdk.ad.ZjRewardVideoAd;
import com.zj.zjsdk.ad.ZjRewardVideoAdListener;
import com.zj.zjsdk.core.DeviceId.ZjDeviceId;


import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.platform.PlatformViewRegistry;

/** ZjadPlugin */
public class ZjadPlugin implements FlutterPlugin, ActivityAware, MethodCallHandler {
  private static final String TAG = "catZjAdPlugin";
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;

  private Context applicationContext;
  private final static ZjadPlugin plugin = new ZjadPlugin();
  private BinaryMessenger mBinaryMessenger;
  private Activity activity;
  private FlutterPluginBinding pluginBinding;

  public static void registerWith(Registrar registrar) {
    if (registrar.activity() == null) {
      return;
    }
    plugin.initializePlugin(registrar.context(), registrar.activity(), registrar.messenger(), registrar.platformViewRegistry());
  }

  private void initializePlugin(Context applicationContext, Activity activity, BinaryMessenger messenger, PlatformViewRegistry registry) {
    this.activity = activity;
    this.applicationContext = applicationContext;
    mBinaryMessenger = messenger;
    channel = new MethodChannel(mBinaryMessenger, "com_cat_zjad");
    channel.setMethodCallHandler(this);
//    registry.registerViewFactory("flutter_adzj_plugin/splash", new SplashAdViewFactory(mBinaryMessenger,activity));
//    registry.registerViewFactory("flutter_adzj_plugin/ADView", new ADViewFactory(mBinaryMessenger,activity));
//    registry.registerViewFactory("flutter_adzj_plugin/banner", new BannerAdViewFactory(mBinaryMessenger,activity));
//    registry.registerViewFactory("flutter_adzj_plugin/native_express", new NativeExpressAdViewFactory(mBinaryMessenger,activity));
  }

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    pluginBinding = flutterPluginBinding;
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    /// 这里可以处理初始化等事件
    Log.e(TAG, "调用了方法：onMethodCall: call.method==" + call.method);
    switch (call.method) {
      case "getPlatformVersion":
        result.success("Android " + android.os.Build.VERSION.RELEASE);
        break;
      case "showRewardVideoAd":
        String adId = call.argument("adId");
        loadReward(adId, call.argument("_channelId")+"");
      default:
        result.notImplemented();
        break;
    }
  }

  // 进度条
  private ProgressDialog pb;
  private void showPb(){
    if(null == pb){
      pb = new ProgressDialog(activity);
    }
    pb.setMessage("加载中...");
    pb.show();
  }

  private void cancel(){
    if(null != pb){
      pb.cancel();
      pb = null;
    }
  }

  // 激励视频
  EventChannel.EventSink eventSink;
  ZjRewardVideoAd zjRewardVideoAd = null;
  boolean isLoad = false;
  private void loadReward(String adId, String channelId) {
    EventChannel eventChannel = new EventChannel(mBinaryMessenger, "com_cat_zjad/event_" + channelId);
    eventChannel.setStreamHandler(new EventChannel.StreamHandler() {
      @RequiresApi(api = Build.VERSION_CODES.N)
      @Override
      public void onListen(Object o, EventChannel.EventSink es) {
        eventSink = es;
      }
      @Override
      public void onCancel(Object o) {
      }
    });
    Log.e(TAG, "onZjAdLoaded.isLoad="+isLoad);
    if(isLoad){
      return;
    }
    Log.e(TAG, "onMethodCall: call.loadReward==");
    //可以加个进度等待。。
    showPb();
    zjRewardVideoAd = new ZjRewardVideoAd(activity, adId, new ZjRewardVideoAdListener() {
      @Override
      public void onZjAdTradeId(String s, String key, boolean isVerity) {
        Log.d("test","onZjAdTradeId.s="+s);
        Map<String, Object> result = new HashMap<>();
        result.put("id", s);
        result.put("event", "onZjAdTradeId");
        eventSink.success(result);
      }

      @Override
      public void onZjAdLoaded(String s) {
        Log.e(TAG, "onZjAdLoaded");
        isLoad = false;
        cancel();
        zjRewardVideoAd.showAD();
        Map<String, Object> result = new HashMap<>();
        result.put("id", s);
        result.put("event", "onZjAdLoaded");
        eventSink.success(result);
      }

      @Override
      public void onZjAdVideoCached() {
//        zjRewardVideoAd.showAD();
        Map<String, Object> result = new HashMap<>();
        result.put("event", "onZjAdVideoCached");
        eventSink.success(result);
      }

      @Override
      public void onZjAdShow() {
        Map<String, Object> result = new HashMap<>();
        result.put("event", "onZjAdShow");
        eventSink.success(result);
      }

      @Override
      public void onZjAdShowError(ZjAdError zjAdError) {
        isLoad = false;
        cancel();
        Map<String, Object> result = new HashMap<>();

        result.put("event", "onZjAdError");
        result.put("code", zjAdError.getErrorCode());
        result.put("message", zjAdError.getErrorMsg());
        eventSink.success(result);
        eventSink.endOfStream();
      }

      @Override
      public void onZjAdClick() {
        Map<String, Object> result = new HashMap<>();
        result.put("event", "onZjAdClick");
        eventSink.success(result);
      }

      @Override
      public void onZjAdVideoComplete() {
        Map<String, Object> result = new HashMap<>();
        result.put("event", "onZjAdVideoComplete");
        eventSink.success(result);
      }

      @Override
      public void onZjAdExpose() {

      }

      @Override
      public void onZjAdReward(String s) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", s);
        result.put("event", "onZjAdReward");
        eventSink.success(result);
      }

      @Override
      public void onZjAdClose() {
        isLoad = false;
        Map<String, Object> result = new HashMap<>();
        result.put("event", "onZjAdClose");
        eventSink.success(result);
        eventSink.endOfStream();
      }

      @Override
      public void onZjAdError(ZjAdError zjAdError) {
        isLoad = false;
        cancel();
        Toast.makeText(activity,zjAdError.getErrorMsg(),Toast.LENGTH_SHORT).show();
        Log.d("main","zjAdError="+zjAdError.getErrorCode()+",,msg=="+zjAdError.getErrorMsg());
        Map<String, Object> result = new HashMap<>();

        result.put("event", "onZjAdError");
        result.put("code", zjAdError.getErrorCode());
        result.put("message", zjAdError.getErrorMsg());
        eventSink.success(result);
        eventSink.endOfStream();
      }

    });
    zjRewardVideoAd.loadAd();
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    pluginBinding = null;
  }


  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    initializePlugin(
      pluginBinding.getApplicationContext(),
      binding.getActivity(),
      pluginBinding.getBinaryMessenger(),
      pluginBinding.getPlatformViewRegistry()
    );
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    activity = null;
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    initializePlugin(
      pluginBinding.getApplicationContext(),
      binding.getActivity(),
      pluginBinding.getBinaryMessenger(),
      pluginBinding.getPlatformViewRegistry()
    );
  }

  @Override
  public void onDetachedFromActivity() {
    activity = null;
  }
}
