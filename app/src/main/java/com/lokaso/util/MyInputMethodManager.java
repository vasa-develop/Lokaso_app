package com.lokaso.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class MyInputMethodManager {

    public InputMethodManager imm;

    public MyInputMethodManager(Context context, View view) {
        this.imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        this.imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public MyInputMethodManager(Context context, View view, int flag) {
        this.imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        this.imm.toggleSoftInputFromWindow(view.getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
    }
}
