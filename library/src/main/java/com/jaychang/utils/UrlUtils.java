package com.jaychang.utils;

import android.net.Uri;

public class UrlUtils {

  private UrlUtils() {}

  public static String getValue(String url, String key) {
    Uri uri = Uri.parse(url);
    return uri.getQueryParameter(key);
  }
}
