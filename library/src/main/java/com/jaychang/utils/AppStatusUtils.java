package com.jaychang.utils;

import android.app.Activity;
import android.app.Application;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;

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
        if (canInteractive(activity) && isInBackground) {
          callback.onAppEnterForeground();
        }

        isInBackground = false;
        state = "Resume";
      }

      @Override
      public void onActivityPaused(Activity activity) {
        if (!canInteractive(activity)) {
          isInBackground = true;
          callback.onAppEnterBackground();
        }

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
        if ("Pause".equals(state) || "Stop".equals(state) && !isInBackground) {
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

  private static boolean canInteractive(Context context) {
    PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    boolean isScreenAwake = Build.VERSION.SDK_INT < 20 ? powerManager.isScreenOn() : powerManager.isInteractive();

    KeyguardManager keyguardManager = (KeyguardManager) context.getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
    boolean isPhoneLocked = keyguardManager.inKeyguardRestrictedInputMode();

    return isScreenAwake && !isPhoneLocked;
  }

}
