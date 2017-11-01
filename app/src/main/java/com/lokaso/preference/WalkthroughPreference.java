package com.lokaso.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class WalkthroughPreference {

    private static final String TAG = WalkthroughPreference.class.getSimpleName();

	private static final String KEY                     = "LokasoPrefs2";
    private static final String KEY_WALKTHROUGH         = "isWalkthrough";

    public static boolean setWalkthroughSeen(Context context, boolean state) {
        SharedPreferences.Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_WALKTHROUGH, state);
        return editor.commit();
    }

    public static boolean isWalkthroughSeen(Context context) {
        SharedPreferences pref = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        return pref.getBoolean(KEY_WALKTHROUGH, false);
    }

    public static boolean clear(Context context) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.clear();
        return editor.commit();
    }
}
