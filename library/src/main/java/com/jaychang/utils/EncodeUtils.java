package com.jaychang.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class EncodeUtils {

  private EncodeUtils() {
  }

  public static String encodeUrl(String url) {
    return encodeUrl(url, "UTF-8");
  }

  public static String encodeUrl(String url, String charset) {
    try {
      return URLEncoder.encode(url, charset);
    } catch (UnsupportedEncodingException e) {
      return url;
    }
  }

  public static String decodeUrl(String url) {
    return decodeUrl(url, "UTF-8");
  }

  public static String decodeUrl(String url, String charset) {
    try {
      return URLDecoder.decode(url, charset);
    } catch (UnsupportedEncodingException e) {
      return url;
    }
  }

  public static String encodeBase64(String input) {
    try {
      return Base64.encodeToString(input.getBytes("UTF-8"), Base64.DEFAULT);
    } catch (UnsupportedEncodingException e) {
      return "";
    }
  }

  public static String decodeBase64(String input) {
    try {
      byte[] data = Base64.decode(input, Base64.DEFAULT);
      return new String(data, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      return "";
    }
  }

}
