package com.jaychang.utils;

import android.text.TextUtils;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ValidationUtils {

  private ValidationUtils() {
  }

  public static boolean startsWith(String value, String... start) {
    for (String s : start) {
      if (value.startsWith(s)) {
        return true;
      }
    }
    return false;
  }

  public static boolean isEmail(String value) {
    Pattern pattern = Pattern
      .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-\\+]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    Matcher matcher = pattern.matcher(value);
    return matcher.matches();
  }

  public static boolean isNumber(String value) {
    String expr = "^[0-9]+$";
    return value.matches(expr);
  }

  public static boolean isLetter(String value) {
    String expr = "^[A-Za-z]+$";
    return value.matches(expr);
  }

  public static boolean isNumberOrLetter(String value) {
    String expr = "^[A-Za-z0-9]+$";
    return value.matches(expr);
  }

  public static boolean isChinese(String value) {
    String expr = "^[\u0391-\uFFE5]+$";
    return value.matches(expr);
  }

  public static boolean containChinese(String value) {
    String chinese = "[\u0391-\uFFE5]";
    if (value != null && value.length() > 0) {
      for (int i = 0; i < value.length(); i++) {
        String temp = value.substring(i, i + 1);
        boolean flag = temp.matches(chinese);
        if (flag) {
          return true;
        }
      }
    }
    return false;
  }

  public static boolean isDecimal(String value) {
    String expr = "^[1-9][0-9]*\\.[0-9]+$";
    return value.matches(expr);
  }

  public static boolean isDecimal(String value, int length) {
    String expr = "^[1-9][0-9]*\\.[0-9]{" + length + "}$";
    return value.matches(expr);
  }

  public static boolean isLength(String value, int length) {
    return value != null && value.length() == length;
  }

  public static boolean isDate(int year, int month, int day) {
    if (year < 0) return false;
    if ((month < 1) || (month > 12)) return false;
    if ((day < 1) || (day > 31)) return false;
    switch (month) {
      case 1:
        return true;
      case 2:
        return (isLeap(year) ? day <= 29 : day <= 28);
      case 3:
        return true;
      case 4:
        return day < 31;
      case 5:
        return true;
      case 6:
        return day < 31;
      case 7:
        return true;
      case 8:
        return true;
      case 9:
        return day < 31;
      case 10:
        return true;
      case 11:
        return day < 31;
      default:
        return true;
    }
  }

  private static boolean isLeap(int year) {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, year);
    return cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365;
  }

  public static boolean testRegex(String value, String regex) {
    return Pattern.compile(regex).matcher(value).matches();
  }
}
