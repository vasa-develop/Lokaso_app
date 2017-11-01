package com.lokaso.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class TourPreference {

    private static final String TAG = TourPreference.class.getSimpleName();

	private static final String KEY                     = "LokasoTourPref";

    private static final String KEY_DRAWER_INVITE       = "key_drawer_invite";

    private static final String KEY_HOME_ADD_TIP        = "key_home_add_tip";
    private static final String KEY_HOME_DRAWER        = "key_home_drawer";
    private static final String KEY_HOME_LOCATION       = "key_home_location";
    private static final String KEY_HOME_FILTER         = "key_home_filter";

    private static final String KEY_SUGGESTION_WISHLIST    = "key_suggestion_wishlist";
    private static final String KEY_SUGGESTION_SHARE       = "key_suggestion_share";
    private static final String KEY_SUGGESTION_LOCATION    = "key_suggestion_location";

    private static final String KEY_PROFILE_FOLLOW      = "key_profile_follow";
    private static final String KEY_PROFILE_CHAT        = "key_profile_chat";

    private static final String KEY_SEARCH_FILTER          = "key_search_filter";

    public static boolean setDrawerInvite(Context context, boolean state) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_DRAWER_INVITE, state);
        return editor.commit();
    }
    public static boolean isDrawerInviteSeen(Context context) {
        SharedPreferences pref = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        return pref.getBoolean(KEY_DRAWER_INVITE, false);
    }


    public static boolean setHomeAddTipSeen(Context context, boolean state) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_HOME_ADD_TIP, state);
        return editor.commit();
    }
    public static boolean isHomeAddTipSeen(Context context) {
        SharedPreferences pref = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        return pref.getBoolean(KEY_HOME_ADD_TIP, false);
    }
    public static boolean setHomeDrawerSeen(Context context, boolean state) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_HOME_DRAWER, state);
        return editor.commit();
    }
    public static boolean isHomeDrawerSeen(Context context) {
        SharedPreferences pref = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        return pref.getBoolean(KEY_HOME_DRAWER, false);
    }
    public static boolean setHomeLocationSeen(Context context, boolean state) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_HOME_LOCATION, state);
        return editor.commit();
    }
    public static boolean isHomeLocationSeen(Context context) {
        SharedPreferences pref = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        return pref.getBoolean(KEY_HOME_LOCATION, false);
    }

    public static boolean setHomeFilterSeen(Context context, boolean state) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_HOME_FILTER, state);
        return editor.commit();
    }
    public static boolean isHomeFilterSeen(Context context) {
        SharedPreferences pref = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        return pref.getBoolean(KEY_HOME_FILTER, false);
    }



    public static boolean setSuggestionWishListSeen(Context context, boolean state) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_SUGGESTION_WISHLIST, state);
        return editor.commit();
    }
    public static boolean isSuggestionWishListSeen(Context context) {
        SharedPreferences pref = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        return pref.getBoolean(KEY_SUGGESTION_WISHLIST, false);
    }
    public static boolean setSuggestionShareSeen(Context context, boolean state) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_SUGGESTION_SHARE, state);
        return editor.commit();
    }
    public static boolean isSuggestionShareSeen(Context context) {
        SharedPreferences pref = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        return pref.getBoolean(KEY_SUGGESTION_SHARE, false);
    }
    public static boolean setSuggestionLocationSeen(Context context, boolean state) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_SUGGESTION_LOCATION, state);
        return editor.commit();
    }
    public static boolean isSuggestionLocationSeen(Context context) {
        SharedPreferences pref = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        return pref.getBoolean(KEY_SUGGESTION_LOCATION, false);
    }



    public static boolean setProfileFollowSeen(Context context, boolean state) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_PROFILE_FOLLOW, state);
        return editor.commit();
    }
    public static boolean isProfileFollowSeen(Context context) {
        SharedPreferences pref = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        return pref.getBoolean(KEY_PROFILE_FOLLOW, false);
    }
    public static boolean setProfileChatSeen(Context context, boolean state) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_PROFILE_CHAT, state);
        return editor.commit();
    }
    public static boolean isProfileChatSeen(Context context) {
        SharedPreferences pref = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        return pref.getBoolean(KEY_PROFILE_CHAT, false);
    }


    //
    public static boolean setSearchFilterSeen(Context context, boolean state) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_SEARCH_FILTER, state);
        return editor.commit();
    }
    public static boolean isSearchFilterSeen(Context context) {
        SharedPreferences pref = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        return pref.getBoolean(KEY_SEARCH_FILTER, false);
    }

    public static boolean clear(Context context) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.clear();
        return editor.commit();
    }
}
