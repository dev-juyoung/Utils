package com.jaychang.demo.utils;

import android.app.Activity;

import com.jaychang.utils.AppStatusUtils;

public class TestAppStatusUtils {

  public static void test(Activity context) {
    AppStatusUtils.registerAppStatusCallback(context.getApplication(), new AppStatusUtils.Callback() {
      @Override
      public void onAppEnterBackground() {
        System.out.println("onAppEnterBackground");
      }

      @Override
      public void onAppEnterForeground() {
        System.out.println("onAppEnterForeground");
      }
    });
  }

}
