package com.jaychang.utils;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * This class provides empty implementations of the methods from TextWatcher.
 * Any custom listener that cares only about a subset of the methods of this listener can
 * simply subclass this adapter class instead of implementing the interface directly.
 */
public abstract class SimpleTextChangedListener implements TextWatcher {

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {
  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {
  }

  @Override
  public void afterTextChanged(Editable s) {
  }
}
