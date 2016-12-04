package com.jaychang.utils;

import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;

public class FragmentUtils {

  public interface OnBackPressListener {
    void onBackPressed();
  }

  public static void setOnBackPressListener(Fragment fragment, final OnBackPressListener onBackPressListener) {
    if (fragment.getView() == null) {
      return;
    }

    fragment.getView().setFocusableInTouchMode(true);
    fragment.getView().requestFocus();
    fragment.getView().setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
          onBackPressListener.onBackPressed();
          return true;
        }
        return false;
      }
    });
  }

}
