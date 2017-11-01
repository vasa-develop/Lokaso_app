package com.lokaso.util;

import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class MyProgressBar {

    public static void setProgress(LinearLayout layout, ProgressBar progressBar) {
        layout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        layout.setBackgroundColor(Color.argb(100, 0, 0, 0));
    }

    public static void removeProgress(LinearLayout layout, ProgressBar progressBar) {
        progressBar.setVisibility(View.INVISIBLE);
        layout.setBackgroundColor(Color.TRANSPARENT);
        layout.setVisibility(View.GONE);
    }
}
