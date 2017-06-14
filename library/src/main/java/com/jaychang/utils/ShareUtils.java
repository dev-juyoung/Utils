package com.jaychang.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.util.ArrayList;

public class ShareUtils {

  public static final String FACEBOOK = "com.facebook.katana";
  public static final String WHATSAPP = "com.whatsapp";
  public static final String LINE = "jp.naver.line.android";
  public static final String WECHAT = "com.tencent.mm";
  public static final String GOOGLE_PLUS = "com.google.android.apps.plus";
  public static final String INSTAGRAM = "com.instagram.android";

  private ShareUtils() {
  }

  public static void shareText(Context context, String text, String packageName) {
    Intent intent = new Intent(Intent.ACTION_SEND);
    if (packageName != null) {
      intent.setPackage(packageName);
    }
    intent.setType("text/plain");
    intent.putExtra(Intent.EXTRA_TEXT, text);
    context.startActivity(Intent.createChooser(intent, ""));
  }

  public static void shareText(Context context, String text) {
    shareText(context, text, null);
  }

  public static void shareVideo(Context context, File file, String packageName) {
    Intent intent = new Intent(Intent.ACTION_SEND);
    if (packageName != null) {
      intent.setPackage(packageName);
    }
    intent.setType("video/*");
    Uri uri = Uri.fromFile(file);
    intent.putExtra(Intent.EXTRA_STREAM, uri);
    context.startActivity(Intent.createChooser(intent, ""));
  }

  public static void shareVideo(Context context, File file) {
    shareVideo(context, file, null);
  }

  public static void shareVideos(Context context, ArrayList<File> files, String packageName) {
    Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
    if (packageName != null) {
      intent.setPackage(packageName);
    }
    intent.setType("video/*");
    ArrayList<Uri> uris = new ArrayList<>();
    for (File file : files) {
      uris.add(Uri.fromFile(file));
    }
    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
    context.startActivity(Intent.createChooser(intent, ""));
  }

  public static void shareVideo(Context context, ArrayList<File> files) {
    shareVideos(context, files, null);
  }

  public static void shareImage(Context context, File file, String packageName) {
    Intent intent = new Intent(Intent.ACTION_SEND);
    if (packageName != null) {
      intent.setPackage(packageName);
    }
    intent.setType("image/*");
    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
    context.startActivity(Intent.createChooser(intent, ""));
  }

  public static void shareImage(Context context, File file) {
    shareImage(context, file, null);
  }

  public static void shareImages(Context context, ArrayList<File> files, String packageName) {
    Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
    if (packageName != null) {
      intent.setPackage(packageName);
    }
    intent.setType("image/*");
    ArrayList<Uri> uris = new ArrayList<>();
    for (File file : files) {
      uris.add(Uri.fromFile(file));
    }
    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
    context.startActivity(Intent.createChooser(intent, ""));
  }

  public static void shareImages(Context context, ArrayList<File> files) {
    shareImages(context, files, null);
  }

  public static void shareTextAndImage(Context context, String text, File file, String packageName) {
    Intent intent = new Intent(Intent.ACTION_SEND);
    if (packageName != null) {
      intent.setPackage(packageName);
    }
    intent.setType("image/*");
    intent.putExtra(Intent.EXTRA_TEXT, text);
    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
    context.startActivity(Intent.createChooser(intent, ""));
  }

  public static void shareTextAndImage(Context context, String text, File file) {
    shareTextAndImage(context, text, file, null);
  }

  public static void shareTextAndImages(Context context, String text, ArrayList<File> files, String packageName) {
    Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
    if (packageName != null) {
      intent.setPackage(packageName);
    }
    intent.setType("image/*");
    ArrayList<Uri> uris = new ArrayList<>();
    for (File file : files) {
      uris.add(Uri.fromFile(file));
    }
    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
    intent.putExtra(Intent.EXTRA_TEXT, text);
    context.startActivity(Intent.createChooser(intent, ""));
  }

  public static void shareTextAndImages(Context context, String text, ArrayList<File> files) {
    shareTextAndImages(context, text, files, null);
  }

}
