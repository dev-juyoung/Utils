package com.jaychang.utils;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;

public class ViewAnimationUtils {

  private static final int DEFAULT_EXPAND_ANIMATION_DURATION = 150;

  public static ValueAnimator expandVertically(final View view, int duration, int fromHeight) {
    view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    final int targetHeight = view.getMeasuredHeight();

    ValueAnimator anim = ValueAnimator.ofInt(fromHeight, targetHeight);
    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator valueAnimator) {
        int val = (Integer) valueAnimator.getAnimatedValue();
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = val;
        view.setLayoutParams(layoutParams);
      }
    });
    anim.setDuration(duration);
    anim.start();

    return anim;
  }

  public static ValueAnimator expandVertically(final View view, int duration) {
    return expandVertically(view, duration, 0);
  }

  public static ValueAnimator expandVertically(final View view) {
    return expandVertically(view, DEFAULT_EXPAND_ANIMATION_DURATION, 0);
  }

  public static ValueAnimator expandHorizontally(final View view, int duration, int fromWidth) {
    view.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    final int targetWidth = view.getMeasuredWidth();

    ValueAnimator anim = ValueAnimator.ofInt(fromWidth, targetWidth);
    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator valueAnimator) {
        int val = (Integer) valueAnimator.getAnimatedValue();
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = val;
        view.setLayoutParams(layoutParams);
      }
    });
    anim.setDuration(duration);
    anim.start();

    return anim;
  }

  public static ValueAnimator expandHorizontally(final View view, int duration) {
    return expandHorizontally(view, duration, 0);
  }

  public static ValueAnimator expandHorizontally(final View view) {
    return expandHorizontally(view, DEFAULT_EXPAND_ANIMATION_DURATION, 0);
  }

  public static ValueAnimator collapseVertically(final View view, int duration, int toHeight) {
    view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    final int targetHeight = view.getMeasuredHeight();

    ValueAnimator anim = ValueAnimator.ofInt(targetHeight, toHeight);
    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator valueAnimator) {
        int val = (Integer) valueAnimator.getAnimatedValue();
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = val;
        view.setLayoutParams(layoutParams);
      }
    });
    anim.setDuration(duration);
    anim.start();

    return anim;
  }

  public static ValueAnimator collapseVertically(final View view, int duration) {
    return collapseVertically(view, duration, 0);
  }

  public static ValueAnimator collapseVertically(final View v) {
    return collapseVertically(v, DEFAULT_EXPAND_ANIMATION_DURATION, 0);
  }

  public static ValueAnimator collapseHorizontally(final View view, int duration, int toWidth) {
    view.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    final int targetWidth = view.getMeasuredWidth();

    ValueAnimator anim = ValueAnimator.ofInt(targetWidth, toWidth);
    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator valueAnimator) {
        int val = (Integer) valueAnimator.getAnimatedValue();
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = val;
        view.setLayoutParams(layoutParams);
      }
    });
    anim.setDuration(duration);
    anim.start();

    return anim;
  }

  public static ValueAnimator collapseHorizontally(final View view, int duration) {
    return collapseHorizontally(view, duration, 0);
  }

    public static ValueAnimator collapseHorizontally(final View view) {
    return collapseHorizontally(view, DEFAULT_EXPAND_ANIMATION_DURATION, 0);
  }

}