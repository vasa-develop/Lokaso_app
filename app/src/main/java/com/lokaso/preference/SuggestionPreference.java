package com.lokaso.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SuggestionPreference {
	
	private static final String KEY         = "SuggestionPreference";
    private static final String OFFSET          = "offset";


    public static boolean isSuggestionSeen(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        return preferences.getBoolean(OFFSET, false);
    }

    public static boolean setSuggestionSeen(Context context, boolean seen) {

        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(OFFSET, seen);
        return editor.commit();
    }

    public static boolean clear(Context context) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.clear();
        return editor.commit();
    }
}
