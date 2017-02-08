package com.jaychang.utils;

import android.text.Layout;
import android.widget.TextView;

public final class TextViewUtils {

  private TextViewUtils() {
  }

  public static boolean isEllipsized(final TextView view) {
    Layout layout = view.getLayout();
    if (layout != null) {
      int lines = layout.getLineCount();
      return lines > 0 && layout.getEllipsisCount(lines - 1) > 0;
    }

    return false;
  }

}
