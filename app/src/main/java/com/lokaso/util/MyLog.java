package com.lokaso.util;

public class MyLog {

    public static void d(String tag, String message) {
        if (Constant.DEBUG) {
            android.util.Log.d(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (Constant.DEBUG) {
            android.util.Log.e(tag, message);
        }
    }

    public static void e(String tag, String message, String data) {
        if (Constant.DEBUG) {
            android.util.Log.e(tag, message + " : " + data);
        }
    }

    public static void i(String tag, String message) {
        if (Constant.DEBUG) {
            android.util.Log.i(tag, message);
        }
    }


    public static void w(String tag, String message) {
        if (Constant.DEBUG) {
            android.util.Log.w(tag, message);
        }
    }
}
