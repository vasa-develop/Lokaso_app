package com.lokaso.util;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.SwitchCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MyFont {

    private Context context;
    private Typeface mTypeface;

    public static final int LEELAWAD1 = 1;
    public static final int LEELAWUI2 = 2;
    public static final int LEELAWBOLD3 = 3;
    public static final int CENTURY_GOTHIC1 = 4;
    public static final int CENTURY_GOTHIC_BOLD1 = 5;

    public static final String CENTURY_GOTHIC = "century-gothic.ttf";
    public static final String CENTURY_GOTHIC_BOLD = "century-gothic-bold.ttf";

    public static final String DEFAULT_FONT = CENTURY_GOTHIC;

    private final String folder = "fonts/";

    public MyFont(Context context, int type) {
        this.context = context;
        switch (type){
            case LEELAWAD1:
                mTypeface = Typeface.createFromAsset(context.getAssets(), folder + DEFAULT_FONT);
                break;

            case LEELAWUI2:
                mTypeface = Typeface.createFromAsset(context.getAssets(), folder + DEFAULT_FONT);
                break;

            case LEELAWBOLD3:
                mTypeface = Typeface.createFromAsset(context.getAssets(), folder + DEFAULT_FONT);
                break;

            case CENTURY_GOTHIC1:
                mTypeface = Typeface.createFromAsset(context.getAssets(), folder + DEFAULT_FONT);
                break;

            case CENTURY_GOTHIC_BOLD1:
                mTypeface = Typeface.createFromAsset(context.getAssets(), folder + CENTURY_GOTHIC_BOLD);
                break;
        }
    }

    public MyFont(Context context) {
        this.context = context;
        mTypeface = Typeface.createFromAsset(context.getAssets(), folder + DEFAULT_FONT);
    }

    public void setTypeface(TextView textView) {
        textView.setTypeface(mTypeface);
    }

    public void setTypeface(EditText textView) {
        textView.setTypeface(mTypeface);
    }

    public void setTypeface(Button textView) {
        textView.setTypeface(mTypeface);
    }

    public void setTypeface(SwitchCompat textView) {
        textView.setTypeface(mTypeface);
    }

    public void setTypeface(MenuItem mi) {
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , mTypeface), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }
}
