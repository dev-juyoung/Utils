package com.jaychang.utils;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;

public class ViewAnimationUtils {

  private static final int DEFAULT_EXPAND_ANIMATION_DURATION = 150;

  public static ValueAnimator expandVertically(final View view, int duration) {
    view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    final int targetHeight = view.getMeasuredHeight();

    ValueAnimator anim = ValueAnimator.ofInt(0, targetHeight);
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

  public static ValueAnimator expandVertically(final View v) {
    return expandVertically(v, DEFAULT_EXPAND_ANIMATION_DURATION);
  }

  public static ValueAnimator expandHorizontally(final View view, int duration) {
    view.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    final int targetWidth = view.getMeasuredWidth();

    ValueAnimator anim = ValueAnimator.ofInt(0, targetWidth);
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

  public static ValueAnimator expandHorizontally(final View v) {
    return expandHorizontally(v, DEFAULT_EXPAND_ANIMATION_DURATION);
  }

  public static ValueAnimator collapseVertically(final View view, int duration) {
    view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    final int targetHeight = view.getMeasuredHeight();

    ValueAnimator anim = ValueAnimator.ofInt(targetHeight, 0);
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

  public static ValueAnimator collapseVertically(final View v) {
    return collapseVertically(v, DEFAULT_EXPAND_ANIMATION_DURATION);
  }

  public static ValueAnimator collapseHorizontally(final View view, int duration) {
    view.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    final int targetWidth = view.getMeasuredWidth();

    ValueAnimator anim = ValueAnimator.ofInt(targetWidth, 0);
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

  public static ValueAnimator collapseHorizontally(final View v) {
    return collapseHorizontally(v, DEFAULT_EXPAND_ANIMATION_DURATION);
  }

}