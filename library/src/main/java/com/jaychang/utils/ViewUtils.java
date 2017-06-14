package com.jaychang.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public final class ViewUtils {

  private ViewUtils() {}

  public static boolean isTouchInView(MotionEvent ev, View view) {
    int[] viewLoc = new int[2];
    view.getLocationOnScreen(viewLoc);
    float motionX = ev.getRawX();
    float motionY = ev.getRawY();
    return motionX >= viewLoc[0]
      && motionX <= (viewLoc[0] + view.getWidth())
      && motionY >= viewLoc[1]
      && motionY <= (viewLoc[1] + view.getHeight());
  }

  public static void measureView(View view) {
    ViewGroup.LayoutParams p = view.getLayoutParams();
    if (p == null) {
      p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
    int lpHeight = p.height;
    int childHeightSpec;
    if (lpHeight > 0) {
      childHeightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight, View.MeasureSpec.EXACTLY);
    } else {
      childHeightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
    }
    view.measure(childWidthSpec, childHeightSpec);
  }

  public static int getViewWidth(View view) {
    measureView(view);
    return view.getMeasuredWidth();
  }

  public static int getViewHeight(View view) {
    measureView(view);
    return view.getMeasuredHeight();
  }

  public static void setViewWidth(View view, int widthInDp) {
    view.getLayoutParams().width = AppUtils.dp2px(view.getContext(), widthInDp);
    view.setLayoutParams(view.getLayoutParams());
  }

  public static void setViewHeight(View view, int heightInDp) {
    view.getLayoutParams().height = AppUtils.dp2px(view.getContext(), heightInDp);
    view.setLayoutParams(view.getLayoutParams());
  }

  public static void setViewWidthHeight(View view, int widthInDp, int heightInDp) {
    view.getLayoutParams().width = AppUtils.dp2px(view.getContext(), widthInDp);
    view.getLayoutParams().height = AppUtils.dp2px(view.getContext(), heightInDp);
    view.setLayoutParams(view.getLayoutParams());
  }

  public static void setViewMargin(View view, int leftInDp, int topInDp, int rightInDp, int bottomInDp) {
    Context context = view.getContext();
    int leftMargin = AppUtils.dp2px(context, leftInDp);
    int topMargin = AppUtils.dp2px(context, topInDp);
    int rightMargin = AppUtils.dp2px(context, rightInDp);
    int bottomMargin = AppUtils.dp2px(context, bottomInDp);
    if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
      ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
      p.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
      view.setLayoutParams(p);
    }
  }

  public static Drawable setDrawableTint(Context context, Drawable drawable, @ColorRes int color) {
    Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
    DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(context, color));
    return wrappedDrawable;
  }

}
