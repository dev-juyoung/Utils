package com.jaychang.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.ColorRes;
import android.support.annotation.RequiresPermission;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.google.common.base.Optional;
import com.jaychang.toolbox.Toolbox;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public final class AppUtils {

  public static String getVersionName(Context context) {
    try {
      Context appContext = context.getApplicationContext();
      PackageInfo pInfo = appContext.getPackageManager().getPackageInfo(appContext.getPackageName(), 0);
      return pInfo.versionName;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return "";
  }

  public static int getVersionCode(Context context) {
    try {
      Context appContext = context.getApplicationContext();
      PackageInfo pInfo = appContext.getPackageManager().getPackageInfo(appContext.getPackageName(), 0);
      return pInfo.versionCode;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return -1;
  }

  /**
   * Remember to add this to activity manifest
   * <code>android:configChanges="locale|orientation"</code>
   */
  public static Class getLauncherActivity(Context context) {
    Intent intent = new Intent(Intent.ACTION_MAIN, null);
    intent.addCategory(Intent.CATEGORY_LAUNCHER);
    PackageManager pm = context.getPackageManager();
    List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
    for (ResolveInfo info : infos) {
      if (info.activityInfo.packageName.equals(context.getPackageName())) {
        return info.activityInfo.getClass();
      }
    }
    return null;
  }

  public static void changeLanguage(Context context, Locale locale) {
    Locale.setDefault(locale);
    Context appContext = context.getApplicationContext();
    Resources resources = appContext.getResources();
    Configuration config = new Configuration(resources.getConfiguration());
    config.locale = locale;
    resources.updateConfiguration(config, resources.getDisplayMetrics());
    Intent refresh = new Intent(appContext, getLauncherActivity(context));
    refresh.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    context.startActivity(refresh);
  }

  //region Screen functions
  public static int dp2px(Context context, int dp) {
    float density = context.getApplicationContext().getApplicationContext().getResources().getDisplayMetrics().density;
    return (int) (dp * density + 0.5f);
  }

  public static int px2dp(Context context, int px) {
    float density = context.getApplicationContext().getResources().getDisplayMetrics().density;
    return (int) (px / density + 0.5f);
  }

  public static int sp2px(Context context, float sp) {
    float fontScale = context.getApplicationContext().getResources().getDisplayMetrics().scaledDensity;
    return (int) (sp * fontScale + 0.5f);
  }

  public static int px2sp(Context context, float px) {
    float fontScale = context.getApplicationContext().getResources().getDisplayMetrics().scaledDensity;
    return (int) (px / fontScale + 0.5f);
  }

  public static double getScreenInch(Context context) {
    DisplayMetrics metrics = context.getApplicationContext().getResources().getDisplayMetrics();
    int widthPixels = metrics.widthPixels;
    int heightPixels = metrics.heightPixels;
    float widthDpi = metrics.xdpi;
    float heightDpi = metrics.ydpi;
    float widthInches = widthPixels / widthDpi;
    float heightInches = heightPixels / heightDpi;
    return Math.sqrt((widthInches * widthInches) + (heightInches * heightInches));
  }

  public static int getScreenWidthPixels(Context context) {
    return context.getApplicationContext().getResources().getDisplayMetrics().widthPixels;
  }

  /**
   * without height of status bar & bottom virtual navigation bar
   */
  public static int getScreenHeightPixels(Context context) {
    return context.getApplicationContext().getResources().getDisplayMetrics().heightPixels;
  }

  /**
   * useful for full screen mode
   */
  @TargetApi(17)
  public static int getFullScreenHeightPixels(Context context) {
    int height;
    WindowManager winMgr = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
    Display display = winMgr.getDefaultDisplay();
    DisplayMetrics dm = new DisplayMetrics();
    if (Build.VERSION.SDK_INT >= 17) {
      display.getRealMetrics(dm);
      height = dm.heightPixels;
    } else {
      try {
        Method method = Class.forName("android.view.Display").getMethod("getRealMetrics", DisplayMetrics.class);
        method.invoke(display, dm);
        height = dm.heightPixels;
      } catch (Exception e) {
        display.getMetrics(dm);
        height = dm.heightPixels;
      }
    }
    return height;
  }

  public static int getStatusBarHeightPixels(Context context) {
    int result = -1;
    Resources resources = context.getApplicationContext().getResources();
    int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      result = resources.getDimensionPixelSize(resourceId);
    }
    return result;
  }

  public static int getNavigationBarHeightPixels(Context context) {
    int result = -1;
    Resources resources = context.getApplicationContext().getResources();
    int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
    if (resourceId > 0) {
      result = resources.getDimensionPixelSize(resourceId);
    }
    return result;
  }
  //endregion

  //region System Ui function
  public static void showKeyboard(Context context, View view) {
    if (view.requestFocus()) {
      InputMethodManager imm = (InputMethodManager) context.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }
  }

  public static void hideKeyboard(Context context, View view) {
    InputMethodManager imm = (InputMethodManager) context.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
  }

  public static void hideKeyboardWhenTouchOutside(final Activity context) {
    if (context.getWindow() == null) {
      return;
    }

    context.getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (context.getCurrentFocus() != null) {
          imm.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), 0);
        } else {
          imm.hideSoftInputFromWindow((context.findViewById(android.R.id.content)).getWindowToken(), 0);
        }
        return false;
      }
    });
  }

  @TargetApi(19)
  public static void setStatusBarTranslucent(Activity activity) {
    if (Build.VERSION.SDK_INT < 19 || activity.getWindow() == null) {
      return;
    }

    activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
  }

  @TargetApi(19)
  public static void clearStatusBarTranslucent(Activity activity) {
    if (Build.VERSION.SDK_INT < 19 || activity.getWindow() == null) {
      return;
    }

    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
  }

  @TargetApi(21)
  public static void setStatusBarColor(Activity activity, @ColorRes int color) {
    if (Build.VERSION.SDK_INT < 21 || activity.getWindow() == null) {
      return;
    }

    activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, color));
  }

  @TargetApi(21)
  public static void setContentBehindStatusBar(Activity activity) {
    if (Build.VERSION.SDK_INT < 21 || activity.getWindow() == null) {
      return;
    }

    View decorView = activity.getWindow().getDecorView();
    int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
    decorView.setSystemUiVisibility(option);
  }

  @TargetApi(19)
  public static void setNavigationBarTranslucent(Activity activity) {
    if (Build.VERSION.SDK_INT < 19 || activity.getWindow() == null) {
      return;
    }

    activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
  }

  @TargetApi(19)
  public static void clearNavigationBarTranslucent(Activity activity) {
    if (Build.VERSION.SDK_INT < 19 || activity.getWindow() == null) {
      return;
    }

    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
  }

  @TargetApi(21)
  public static void setNavigationBarColor(Activity activity, @ColorRes int color) {
    if (Build.VERSION.SDK_INT < 21 || activity.getWindow() == null) {
      return;
    }

    activity.getWindow().setNavigationBarColor(ContextCompat.getColor(activity, color));
  }

  @TargetApi(16)
  public static void setFullscreen(Activity activity) {
    if (Build.VERSION.SDK_INT < 16 || activity.getWindow() == null) {
      return;
    }

    View decorView = activity.getWindow().getDecorView();
    int option = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
    decorView.setSystemUiVisibility(option);
  }

  //region Intent functions
  public static void shareText(Context context, String chooserTitle, String text) {
    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("text/plain");
    intent.putExtra(Intent.EXTRA_TEXT, text);
    context.startActivity(Intent.createChooser(intent, chooserTitle));
  }

  public static void shareText(Context context, String text) {
    shareText(context, "", text);
  }

  public static void shareFile(Context context, String chooserTitle, File file) {
    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("*/*");
    Uri uri = Uri.fromFile(file);
    intent.putExtra(Intent.EXTRA_STREAM, uri);
    context.startActivity(Intent.createChooser(intent, chooserTitle));
  }

  public static void shareFile(Context context, File file) {
    shareFile(context, "", file);
  }

  @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
  public static void shareImage(Context context, String chooserTitle, Bitmap bitmap) {
    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("image/*");
    intent.putExtra(Intent.EXTRA_STREAM, ImageUtils.bitmapToUri(context, bitmap));
    context.startActivity(Intent.createChooser(intent, chooserTitle));
  }

  @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
  public static void shareImage(Context context, Bitmap bitmap) {
    shareImage(context, "", bitmap);
  }

  @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
  public static void shareImages(Context context, String chooserTitle, ArrayList<Bitmap> bitmaps) {
    Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
    intent.setType("image/*");
    ArrayList<Uri> uris = new ArrayList<>();
    for (Bitmap bitmap : bitmaps) {
      uris.add(ImageUtils.bitmapToUri(context, bitmap));
    }
    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
    context.startActivity(Intent.createChooser(intent, chooserTitle));
  }

  @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
  public static void shareImages(Context context, ArrayList<Bitmap> bitmaps) {
    shareImages(context, "", bitmaps);
  }

  @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
  public static void shareTextAndImage(Context context, String chooserTitle, String text, Bitmap bitmap) {
    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("image/*");
    intent.putExtra(Intent.EXTRA_TEXT, text);
    intent.putExtra(Intent.EXTRA_STREAM, ImageUtils.bitmapToUri(context, bitmap));
    context.startActivity(Intent.createChooser(intent, chooserTitle));
  }

  @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
  public static void shareTextAndImage(Context context, String text, Bitmap bitmap) {
    shareTextAndImage(context, "", text, bitmap);
  }

  @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
  public static void shareTextAndImages(Context context, String chooserTitle, String text, ArrayList<Bitmap> bitmaps) {
    Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
    intent.setType("image/*");
    ArrayList<Uri> uris = new ArrayList<>();
    for (Bitmap bitmap : bitmaps) {
      uris.add(ImageUtils.bitmapToUri(context, bitmap));
    }
    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
    intent.putExtra(Intent.EXTRA_TEXT, text);
    context.startActivity(Intent.createChooser(intent, chooserTitle));
  }

  @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
  public static void shareTextAndImages(Context context, String text, ArrayList<Bitmap> bitmaps) {
    shareTextAndImages(context, "", text, bitmaps);
  }

  public static void openUrl(Context context, String url) {
    Uri uri = Uri.parse(url);
    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    context.startActivity(intent);
  }

  public static void openLocalImage(Context context, String imagePath) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.addCategory(Intent.CATEGORY_DEFAULT);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    Uri uri = Uri.fromFile(new File(imagePath));
    intent.setDataAndType(uri, "image/*");
    context.startActivity(intent);
  }

  public static void openLocalVideo(Context context, String videoPath) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.putExtra("oneshot", 0);
    intent.putExtra("configchange", 0);
    Uri uri = Uri.fromFile(new File(videoPath));
    intent.setDataAndType(uri, "video/*");
    context.startActivity(intent);
  }

  private static File createImageFile(Context context) throws IOException {
    String imageFileName = "Image_" + UUID.randomUUID() + ".jpg";
    // start from api 19, app can write file to its app folder without requiring WRITE_EXTERNAL_STORAGE
    return new File(context.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + imageFileName);
  }

  @TargetApi(18)
  public static void pickPhotoFromAlbum(Activity context, int requestCode) {
    Intent intent = new Intent();
    if (Build.VERSION.SDK_INT >= 18) {
      intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
    }
    intent.setType("image/*");
    intent.setAction(Intent.ACTION_GET_CONTENT);
    context.startActivityForResult(Intent.createChooser(intent, ""), requestCode);
  }

  @TargetApi(18)
  public static List<Uri> getPhotosFromAlbum(Context context, Intent data) {
    if (Build.VERSION.SDK_INT < 18) {
      return Collections.emptyList();
    }

    ClipData clipData = data.getClipData();

    if (clipData == null) {
      return Collections.emptyList();
    }

    String[] filePathColumn = {MediaStore.Images.Media.DATA};
    ArrayList<Uri> uris = new ArrayList<>();
    for (int i = 0; i < clipData.getItemCount(); i++) {
      ClipData.Item item = clipData.getItemAt(i);
      Uri uri = item.getUri();
      uris.add(uri);
      Cursor cursor = context.getApplicationContext().getContentResolver().query(uri, filePathColumn, null, null, null);
      cursor.moveToFirst();
      cursor.close();
    }
    return uris;
  }

  public static void takeVideo(Activity activity, int requestCode) {
    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
    if (intent.resolveActivity(activity.getPackageManager()) != null) {
      activity.startActivityForResult(intent, requestCode);
    }
  }

  public static void takeVideo(Fragment fragment, int requestCode) {
    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
    if (intent.resolveActivity(fragment.getActivity().getPackageManager()) != null) {
      fragment.startActivityForResult(intent, requestCode);
    }
  }

  public static Uri getTakenVideoUri(Intent intent) {
    return intent.getData();
  }
  //endregion

  public static void saveImageToAlbum(final Activity activity, final File file, final OnImageSaveListener listener) {
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        try {
          String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
          final File imageFile = new File(root, UUID.randomUUID().toString() + ".jpg");

          if (!imageFile.exists()) {
            imageFile.createNewFile();
          }
          FileUtils.copyFile(file, imageFile);
          MediaScannerConnection.scanFile(activity.getApplicationContext(), new String[]{imageFile.toString()}, null, null);

          if (listener != null) {
            activity.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                listener.onImageSaved(imageFile);
              }
            });
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
  }

  public static void saveImageToAppInternalDir(final Activity activity, final File file, final OnImageSaveListener listener) {
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        try {
          String root = Environment.getExternalStorageDirectory().toString();
          final File imageFile = new File(root, UUID.randomUUID().toString() + ".jpg");

          if (!imageFile.exists()) {
            imageFile.createNewFile();
          }
          FileUtils.copyFile(file, imageFile);

          if (listener != null) {
            activity.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                listener.onImageSaved(imageFile);
              }
            });
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
  }

  public interface OnImageSaveListener {
    void onImageSaved(File file);
  }

  public static void copyText(Context context, CharSequence text) {
    ClipboardManager clipboard = (ClipboardManager) context.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
    ClipData clip = ClipData.newPlainText("text", text);
    clipboard.setPrimaryClip(clip);
  }

}
