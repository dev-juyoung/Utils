package com.jaychang.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;

import java.util.Locale;

import static com.jaychang.utils.AppUtils.getLauncherIntent;

public class LangUtils {

  private static final String KEY_CURRENT_LANG = LangUtils.class.getName() + "_CURRENT_LANG";
  private static final String KEY_CURRENT_COUNTRY = LangUtils.class.getName() + "_KEY_CURRENT_COUNTRY";

  private static void changeLanguage(Context context, Locale locale, boolean restart) {
    PreferenceUtils.saveString(context, KEY_CURRENT_LANG, locale.getLanguage());
    PreferenceUtils.saveString(context, KEY_CURRENT_COUNTRY, locale.getCountry());

    Locale.setDefault(locale);
    Context appContext = context.getApplicationContext();
    Resources resources = appContext.getResources();
    Configuration config = new Configuration(resources.getConfiguration());
    config.locale = locale;
    resources.updateConfiguration(config, resources.getDisplayMetrics());
    if (restart) {
      Intent refresh = getLauncherIntent(context);
      refresh.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
      context.startActivity(refresh);
    }
  }

  public static void changeLanguage(Context context, Locale locale) {
    changeLanguage(context, locale, true);
  }

  public static void restoreLanguage(Context context) {
    changeLanguage(context, getLocale(context), false);
  }

  private static Locale getLocale(Context context) {
    Locale result;
    String lang = PreferenceUtils.getString(context, KEY_CURRENT_LANG);
    String country = PreferenceUtils.getString(context, KEY_CURRENT_COUNTRY);
    if (!TextUtils.isEmpty(lang)) {
      result = new Locale(lang, country);
    } else {
      result = Locale.getDefault();
    }
    return result;
  }

}
