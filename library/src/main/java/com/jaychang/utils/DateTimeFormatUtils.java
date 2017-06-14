package com.jaychang.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public final class DateTimeFormatUtils {

  private DateTimeFormatUtils() {
  }

  public static String format(String timestampInMillis, String format) {
    SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
    return formatter.format(Long.parseLong(timestampInMillis));
  }

  /**
   * E.g. formatInDay(timestamp, "hh:mm a", "'Yesterday' hh:mm a", "'Tomorrow'", "yyyy-MM-dd");
   * return one of the following case:
   * 1. within today: 09:30 PM
   * 2. yesterday: Yesterday 10:20 PM
   * 3. other: 2016-01-20
   * Note: if the format contains other user defined strings, use '' to wrap the string.
   */
  public static String translate(String timestampInMillis,
                                 String todayFormat,
                                 String yesterdayFormat,
                                 String defaultFormat) {
    long thatDayMillis = Long.parseLong(timestampInMillis);
    long todayMillis = Calendar.getInstance().getTimeInMillis();
    Date thatDay = new Date(thatDayMillis);
    Date today = new Date(todayMillis);
    int diffDays = today.getDate() - thatDay.getDate();
    SimpleDateFormat formatter = new SimpleDateFormat();
    if (diffDays == 0) {
      formatter.applyPattern(todayFormat);
      return formatter.format(Long.parseLong(timestampInMillis));
    } else if (diffDays == 1) {
      formatter.applyPattern(yesterdayFormat);
      return formatter.format(Long.parseLong(timestampInMillis));
    } else {
      formatter.applyPattern(defaultFormat);
      return formatter.format(Long.parseLong(timestampInMillis));
    }
  }

  public static String translateElapsedTime(String timestampInMillis,
                                            String yearAgoFormat,
                                            String monthAgoFormat,
                                            String weekAgoFormat,
                                            String dayAgoFormat,
                                            String hourAgoFormat,
                                            String minuteAgoFormat,
                                            String nowFormat) {
    long fromMillis = Long.parseLong(timestampInMillis);
    long nowMillis = System.currentTimeMillis();
    long millisFromNow = nowMillis - fromMillis;

    long minutesFromNow = TimeUnit.MILLISECONDS.toMinutes(millisFromNow);
    if (minutesFromNow < 1) {
      return nowFormat;
    }
    long hoursFromNow = TimeUnit.MILLISECONDS.toHours(millisFromNow);
    if (hoursFromNow < 1) {
      return minutesFromNow + minuteAgoFormat;
    }
    long daysFromNow = TimeUnit.MILLISECONDS.toDays(millisFromNow);
    if (daysFromNow < 1) {
      return hoursFromNow + hourAgoFormat;
    }
    long weeksFromNow = TimeUnit.MILLISECONDS.toDays(millisFromNow) / 7;
    if (weeksFromNow < 1) {
      return daysFromNow + dayAgoFormat;
    }
    long monthsFromNow = TimeUnit.MILLISECONDS.toDays(millisFromNow) / 30;
    if (monthsFromNow < 1) {
      return weeksFromNow + weekAgoFormat;
    }
    long yearsFromNow = TimeUnit.MILLISECONDS.toDays(millisFromNow) / 365;
    if (yearsFromNow < 1) {
      return monthsFromNow + monthAgoFormat;
    }
    return yearsFromNow + yearAgoFormat;
  }

}
