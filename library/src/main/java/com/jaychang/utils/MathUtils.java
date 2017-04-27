package com.jaychang.utils;

import android.graphics.PointF;

public class MathUtils {

  public static float round(float number, int numberOfDecimal) {
    int pow = 10;
    for (int i = 1; i < numberOfDecimal; i++)
      pow *= 10;
    float tmp = number * pow;
    return (float) (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) / pow;
  }

  public static float getDistance(PointF p1, PointF p2) {
    float dx = Math.abs(p1.x - p2.x);
    float dy = Math.abs(p1.y - p2.y);
    return (float) Math.sqrt(dx * dx + dy * dy);
  }

}
