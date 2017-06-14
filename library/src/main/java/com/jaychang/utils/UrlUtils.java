package com.jaychang.utils;

import android.net.Uri;
import android.util.Patterns;

public class UrlUtils {

  private UrlUtils() {}

  public static String getValue(String url, String key) {
    Uri uri = Uri.parse(url);
    return uri.getQueryParameter(key);
  }

  public static boolean isValid(String url) {
    return Patterns.WEB_URL.matcher(url).matches();
  }

}
