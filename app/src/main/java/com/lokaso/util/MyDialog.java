package com.lokaso.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.lokaso.R;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

/**
 * Created by Androcid-6 on 06-01-2016.
 */
public class MyDialog {

    private Context context;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;

    private static MyDialog instance;

    private static final String DEFAULT_POSITIVE_TEXT = "OK";
    private static final String DEFAULT_NEGATIVE_TEXT = "CANCEL";

    public MyDialog(Context ctx) {
        context = ctx;
        init(context);
    }

    public static MyDialog with(Context context) {
        instance = new MyDialog(context);
        return instance;
    }

    private void init(Context context) {
        builder = new AlertDialog.Builder(context);
    }

    private OnClickListener onClickListener;
//    private OnClickListener onPositiveClickListener;
//    private OnClickListener onNegativeClickListener;
    private OnClickListener onDismissClickListener;
    public interface OnClickListener {
        public void onClick(MyDialog tour);
    }

    /***
     * This will be called for all the listeners.
     *
     *
     * @param listener
     * @param <T>
     * @return
     */
    public <T> MyDialog setOnClickListener(OnClickListener listener) {
        this.onClickListener = listener;

        if(onDismissClickListener==null) {
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    onClickListener.onClick(instance);
                }
            });
        }

        return this;
    }



    public <T> MyDialog positive() {
        positive(DEFAULT_POSITIVE_TEXT, null);
        return this;
    }

    public <T> MyDialog positive(String name) {
        positive(name, null);
        return this;
    }

    public <T> MyDialog positive(final OnClickListener listener) {
        positive(DEFAULT_POSITIVE_TEXT, listener);
        return this;
    }

    public <T> MyDialog positive(String name, final OnClickListener listener) {
        if(builder!=null) {
            if(name!=null && name.length()>0)
                builder.setPositiveButton(name, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(listener!=null)
                            listener.onClick(instance);
                    }
                });
        }
        return this;
    }

    public <T> MyDialog negative() {
        negative(DEFAULT_NEGATIVE_TEXT, null);
        return this;
    }

    public <T> MyDialog negative(String name) {
        negative(name, null);
        return this;
    }

    public <T> MyDialog negative(final OnClickListener listener) {
        negative(DEFAULT_NEGATIVE_TEXT, listener);
        return this;
    }

    public <T> MyDialog negative(String name, final OnClickListener listener) {
        if(builder!=null) {
            if(name!=null && name.length()>0)
                builder.setNegativeButton(name, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(listener!=null)
                            listener.onClick(instance);
                    }
                });
        }
        return this;
    }


    public <T> MyDialog dismiss(final OnClickListener listener) {
        if(builder!=null) {
            onDismissClickListener = listener;
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    if(listener!=null)
                        listener.onClick(instance);
                }
            });
        }
        return this;
    }


    public <T> MyDialog title(String title) {
        if(builder!=null) {
            if(title!=null && title.length()>0)
                builder.setTitle(title);
        }
        return this;
    }
    public <T> MyDialog message(String message) {
        if(builder!=null) {
            builder.setMessage(message);
        }
        return this;
    }

    public <T> MyDialog show() {
        if(builder!=null) {
            alertDialog = builder.create();
            alertDialog.show();
        }
        return this;
    }
/*
    public <T> void dismiss() {
        if(alertDialog!=null) {
            alertDialog.dismiss();
        }
    }*/
}
