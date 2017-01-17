package com.jaychang.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Bundle;

public class AppStatusUtils {

  private static String state = "";
  private static boolean isInBackground;

  public interface Callback {
    void onAppEnterBackground();
    void onAppEnterForeground();
  }

  public static void registerAppStatusCallback(Application app, final Callback callback) {
    app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
      @Override
      public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        state = "Create";
      }

      @Override
      public void onActivityStarted(Activity activity) {
        state = "Start";
      }

      @Override
      public void onActivityResumed(Activity activity) {
        if (isInBackground) {
          callback.onAppEnterForeground();
        }

        isInBackground = false;
        state = "Resume";
      }

      @Override
      public void onActivityPaused(Activity activity) {
        state = "Pause";
      }

      @Override
      public void onActivityStopped(Activity activity) {
        state = "Stop";
      }

      @Override
      public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
      }

      @Override
      public void onActivityDestroyed(Activity activity) {
        isInBackground = false;
        state = "Destroy";
      }
    });

    app.registerComponentCallbacks(new ComponentCallbacks2() {
      @Override
      public void onTrimMemory(int level) {
        if (state.equals("Pause")) {
          isInBackground = true;
          callback.onAppEnterBackground();
        }
      }

      @Override
      public void onConfigurationChanged(Configuration newConfig) {
      }

      @Override
      public void onLowMemory() {
      }
    });
  }

}
