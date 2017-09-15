package com.jaychang.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.Locale;

import static com.jaychang.utils.AppUtils.getLauncherIntent;

public class LangUtils {

  private static final String KEY_CURRENT_LANG = LangUtils.class.getName() + "_CURRENT_LANG";
  private static final String KEY_CURRENT_COUNTRY = LangUtils.class.getName() + "_KEY_CURRENT_COUNTRY";

  public static void changeLanguage(Context context, Locale locale) {
    saveString(context, KEY_CURRENT_LANG, locale.getLanguage());
    saveString(context, KEY_CURRENT_COUNTRY, locale.getCountry());

    wrap(context);

    Intent refresh = getLauncherIntent(context);
    refresh.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    context.startActivity(refresh);
  }

  public static Context wrap(Context context) {
    Locale locale = getLocale(context);
    Locale.setDefault(locale);

    Resources res = context.getResources();
    Configuration config = new Configuration(res.getConfiguration());
    if (Build.VERSION.SDK_INT >= 17) {
      config.setLocale(locale);
      context = context.createConfigurationContext(config);
    } else {
      config.locale = locale;
      res.updateConfiguration(config, res.getDisplayMetrics());
    }
    return context;
  }

  private static Locale getLocale(Context context) {
    Locale result;
    String lang = getString(context, KEY_CURRENT_LANG);
    String country = getString(context, KEY_CURRENT_COUNTRY);
    if (!TextUtils.isEmpty(lang)) {
      result = new Locale(lang, country);
    } else {
      result = Locale.getDefault();
    }
    return result;
  }

  @SuppressLint("ApplySharedPref")
  private static void saveString(Context context, String key, String value) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    prefs.edit().putString(key, value).commit();
  }

  private static String getString(Context context, String key) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    return prefs.getString(key, "");
  }

}