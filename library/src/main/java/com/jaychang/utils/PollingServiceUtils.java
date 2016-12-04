package com.jaychang.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.text.TextUtils;

public class PollingServiceUtils {

  private PollingServiceUtils() {}

  public static void startPollingService(Context context,
                                         Class<?> service,
                                         int intervalInSec,
                                         String action) {
    Context appContext = context.getApplicationContext();
    AlarmManager manager = (AlarmManager) appContext
      .getSystemService(Context.ALARM_SERVICE);
    Intent intent = new Intent(appContext, service);
    if (!TextUtils.isEmpty(action)) {
      intent.setAction(action);
    }
    PendingIntent pendingIntent = PendingIntent.getService(
      appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    long triggerAtTime = SystemClock.elapsedRealtime();
    manager.setRepeating(AlarmManager.ELAPSED_REALTIME, triggerAtTime,
      intervalInSec * 1000, pendingIntent);
  }

  public static void startPollingService(Context context,
                                         Class<?> service,
                                         int intervalInSec) {
    startPollingService(context, service, intervalInSec, null);
  }

  public static void stopPollingService(Context context,
                                        Class<?> service,
                                        String action) {
    Context appContext = context.getApplicationContext();
    AlarmManager manager = (AlarmManager) appContext
      .getSystemService(Context.ALARM_SERVICE);
    Intent intent = new Intent(appContext, service);
    if (!TextUtils.isEmpty(action)) {
      intent.setAction(action);
    }
    PendingIntent pendingIntent = PendingIntent.getService(appContext, 0,
      intent, PendingIntent.FLAG_UPDATE_CURRENT);
    manager.cancel(pendingIntent);
  }

  public static void stopPollingService(Context context, Class<?> service) {
    stopPollingService(context, service, null);
  }
}
