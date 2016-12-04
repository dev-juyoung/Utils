package com.jaychang.utils;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {

  private StringUtils() {
  }

  public static boolean isBlank(String text) {
    return text.trim().length() == 0;
  }

  public static boolean isEmpty(String text) {
    return text == null || text.length() == 0;
  }

  public static String reverse(String source) {
    return new StringBuilder(source).reverse().toString();
  }

  public static String replaceLast(String source, String pattern, String to) {
    int index = source.lastIndexOf(pattern);
    if (index >= 0) {
      source = new StringBuilder(source).replace(index, index + pattern.length(), to).toString();
    }

    return source;
  }

  public static List<Integer> indexesOf(String src, String target) {
    List<Integer> positions = new ArrayList<>();
    for (int index = src.indexOf(target);
         index >= 0;
         index = src.indexOf(target, index + 1)) {
      positions.add(index);
    }
    return positions;
  }

}
