package com.jaychang.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.v4.content.ContextCompat;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class ImageUtils {

  private ImageUtils() {
  }

  private static BitmapFactory.Options getBitmapOptions() {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
    return options;
  }

  public static Bitmap fileToBitmap(File file) {
    return BitmapFactory.decodeFile(file.getPath(), getBitmapOptions());
  }

  public static Bitmap bytesToBitmap(byte[] bytes) {
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, getBitmapOptions());
  }

  public static Bitmap drawableToBitmap(Drawable drawable) {
    return ((BitmapDrawable) drawable).getBitmap();
  }

  public static Bitmap drawableToBitmap(Context context, @DrawableRes int drawableRes) {
    return BitmapFactory.decodeResource(context.getResources(), drawableRes, getBitmapOptions());
  }

  public static Bitmap streamToBitmap(InputStream inputStream) {
    return BitmapFactory.decodeStream(inputStream, null, getBitmapOptions());
  }

  public static Bitmap viewToBitmap(View view) {
    view.setDrawingCacheEnabled(true);
    view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
      View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
    view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    view.buildDrawingCache(true);
    Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
    view.setDrawingCacheEnabled(false);
    return bitmap;
  }

  public static float[] getRGBA(Bitmap bitmap, int x, int y) {
    int color = bitmap.getPixel(x, y);
    int red = Color.red(color);
    int green = Color.green(color);
    int blue = Color.blue(color);
    int alpha = Color.alpha(color);
    float[] rgba = {red, green, blue, alpha};
    return rgba;
  }

  public static Bitmap contentViewToBitmap(Activity activity) {
    View view = activity.getWindow().getDecorView();
    view.setDrawingCacheEnabled(true);
    view.buildDrawingCache();
    Bitmap bmp = view.getDrawingCache();
    int statusBarHeight = AppUtils.getStatusBarHeightPixels(activity);
    int width = AppUtils.getScreenWidthPixels(activity);
    int height = AppUtils.getScreenHeightPixels(activity);
    Bitmap bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height - statusBarHeight);
    view.destroyDrawingCache();
    return bp;
  }

  public static byte[] bitmapToBytes(Bitmap bitmap) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
    return baos.toByteArray();
  }

  public static boolean bitmapToFile(Bitmap bitmap, File file) {
    OutputStream outputStream = null;
    try {
      outputStream = new FileOutputStream(file);
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
      outputStream.flush();
      outputStream.close();
      return true;
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return false;
  }

  public static Drawable bitmapToDrawable(Context context, Bitmap bitmap) {
    return new BitmapDrawable(context.getResources(), bitmap);
  }

  public static byte[] drawableToBytes(Drawable drawable) {
    return bitmapToBytes(drawableToBitmap(drawable));
  }

  public static byte[] drawableToBytes(Context context, @DrawableRes int drawableRes) {
    Drawable drawable = ContextCompat.getDrawable(context, drawableRes);
    return bitmapToBytes(drawableToBitmap(drawable));
  }

  public static Drawable bytesToDrawable(Context context, byte[] bytes) {
    return bitmapToDrawable(context, bytesToBitmap(bytes));
  }

  public static Uri bitmapToUri(Context context, Bitmap bitmap) {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
    String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
    return Uri.parse(path);
  }

  public static int getImageDegree(String imagePath) {
    try {
      ExifInterface exif = new ExifInterface(imagePath);
      int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
      if (rotation == ExifInterface.ORIENTATION_ROTATE_90) {
        return 90;
      } else if (rotation == ExifInterface.ORIENTATION_ROTATE_180) {
        return 180;
      } else if (rotation == ExifInterface.ORIENTATION_ROTATE_270) {
        return 270;
      }
      return 0;
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static Bitmap rotateImage(Bitmap src, int degree) {
    Matrix matrix = new Matrix();
    matrix.postRotate(degree);
    return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
  }

  public static Bitmap scale(Bitmap src, int scaleWidth, int scaleHeight) {
    return Bitmap.createScaledBitmap(src, scaleWidth, scaleHeight, false);
  }

  public static Bitmap clip(Bitmap src, int x, int y, int width, int height) {
    Bitmap newBitmap = Bitmap.createBitmap(src, x, y, width, height);
    if (!src.isRecycled()) {
      src.recycle();
    }
    return newBitmap;
  }

  public static Bitmap skew(Bitmap src, float kx, float ky, float px, float py) {
    Matrix matrix = new Matrix();
    matrix.setSkew(kx, ky, px, py);
    Bitmap newBitmap = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);

    if (!src.isRecycled()) {
      src.recycle();
    }

    return newBitmap;
  }

  public static Bitmap toCircle(Bitmap src) {
    int width = src.getWidth();
    int height = src.getHeight();
    int radius = Math.min(width, height) >> 1;

    Bitmap newBitmap = Bitmap.createBitmap(width, height, src.getConfig());
    Paint paint = new Paint();
    Canvas canvas = new Canvas(newBitmap);
    Rect rect = new Rect(0, 0, width, height);
    paint.setAntiAlias(true);
    canvas.drawARGB(0, 0, 0, 0);
    canvas.drawCircle(width >> 1, height >> 1, radius, paint);
    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    canvas.drawBitmap(src, rect, rect, paint);

    if (!src.isRecycled()) {
      src.recycle();
    }

    return newBitmap;
  }

  public static Bitmap toRound(Bitmap src, float radius) {
    int width = src.getWidth();
    int height = src.getHeight();

    Bitmap newBitmap = Bitmap.createBitmap(width, height, src.getConfig());
    Paint paint = new Paint();
    Canvas canvas = new Canvas(newBitmap);
    Rect rect = new Rect(0, 0, width, height);
    paint.setAntiAlias(true);
    canvas.drawRoundRect(new RectF(rect), radius, radius, paint);
    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    canvas.drawBitmap(src, rect, rect, paint);

    if (!src.isRecycled()) {
      src.recycle();
    }

    return newBitmap;
  }

  public static Bitmap toAlpha(Bitmap src, @IntRange(from = 0, to = 100) int alpha) {
    Bitmap newBitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(newBitmap);
    Paint alphaPaint = new Paint();
    alphaPaint.setAlpha(alpha);
    canvas.drawBitmap(src, 0, 0, alphaPaint);

    if (!src.isRecycled()) {
      src.recycle();
    }

    return newBitmap;
  }

  public static Bitmap toGray(Bitmap src) {
    Bitmap newBitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(newBitmap);
    Paint paint = new Paint();
    ColorMatrix colorMatrix = new ColorMatrix();
    colorMatrix.setSaturation(0);
    ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(colorMatrix);
    paint.setColorFilter(colorMatrixColorFilter);
    canvas.drawBitmap(src, 0, 0, paint);

    if (!src.isRecycled()) {
      src.recycle();
    }

    return newBitmap;
  }

  public static Bitmap addBorder(Bitmap src, int borderWidth, int color) {
    int doubleBorder = borderWidth << 1;
    int newWidth = src.getWidth() + doubleBorder;
    int newHeight = src.getHeight() + doubleBorder;

    Bitmap newBitmap = Bitmap.createBitmap(newWidth, newHeight, src.getConfig());
    Canvas canvas = new Canvas(newBitmap);
    Rect rect = new Rect(0, 0, newWidth, newHeight);
    Paint paint = new Paint();
    paint.setColor(color);
    paint.setStyle(Paint.Style.STROKE);
    // setStrokeWidth是居中画的，所以要两倍的宽度才能画，否则有一半的宽度是空的
    paint.setStrokeWidth(doubleBorder);
    canvas.drawRect(rect, paint);
    canvas.drawBitmap(src, borderWidth, borderWidth, null);

    if (!src.isRecycled()) {
      src.recycle();
    }

    return newBitmap;
  }

  public static ImageDimension getImageDimensionFromUri(Uri uri) {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
    return new ImageDimension(options.outWidth, options.outHeight);
  }

}
