package com.jaychang.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Bundle;

public class AppStatusUtils {

  private static String state = "";
  private static boolean isInBackground;
  private static Application.ActivityLifecycleCallbacks lifecycleCallbacks;
  private static ComponentCallbacks2 componentCallbacks;

  public interface Callback {
    void onAppEnterBackground();
    void onAppEnterForeground();
  }

  public static void registerAppStatusCallback(Application app, final Callback callback) {
    lifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
      @Override
      public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
      }

      @Override
      public void onActivityStarted(Activity activity) {
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
      }
    };
    app.registerActivityLifecycleCallbacks(lifecycleCallbacks);

    componentCallbacks = new ComponentCallbacks2() {
      @Override
      public void onTrimMemory(int level) {
        if ("Pause".equals(state) || "Stop".equals(state)) {
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
    };
    app.registerComponentCallbacks(componentCallbacks);
  }

  public static void unregisterAppStatusCallback(Application app) {
    app.unregisterActivityLifecycleCallbacks(lifecycleCallbacks);
    app.unregisterComponentCallbacks(componentCallbacks);
    lifecycleCallbacks = null;
    componentCallbacks = null;
  }

}
