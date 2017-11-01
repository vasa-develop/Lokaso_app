package com.lokaso.util;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.lokaso.R;

import retrofit.RetrofitError;

public class MyToast {

    public static void dshort(Context context, String msg) {
        new AlertDialog.Builder(context)
                .setMessage(msg)
                .setCancelable(true)
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    public static void dshort(AlertDialog.Builder builder, String msg) {
        builder.setMessage(msg)
                .setCancelable(true)
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    public static void tshort(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void tshort(Context context, RetrofitError error) {

        String msg = "";
        if(error!=null) {
            if (error.getKind() == RetrofitError.Kind.NETWORK) {
                msg = "You are low on connectivity";
            }
            else if(error.getKind() == RetrofitError.Kind.HTTP) {
                msg = "Oops. There seems to be an issue. Please try again.";
            }
            else {
                msg = "Oops. Some unexpected error has come up.";
            }
        }
        else {
            msg = "Some unexpected error has occurred";
        }

        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void tlong(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void tdebug(Context context, String msg) {
        if(Constant.DEBUG)
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showInternet(Context context) {
        String msg = "Internet down";
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}
