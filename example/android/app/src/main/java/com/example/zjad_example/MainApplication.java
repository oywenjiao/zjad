package com.example.zjad_example;

import android.app.Application;

import com.zj.zjsdk.ZjSdk;

import io.flutter.app.FlutterApplication;


public class MainApplication extends FlutterApplication {

    @Override
    public void onCreate()
    {
        super.onCreate();


        ZjSdk.init(this,"zj_11120200724001");

    }

}
