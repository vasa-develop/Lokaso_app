package com.lokaso.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.lokaso.custom.SharedPreferencesCustom;
import com.lokaso.gcm.QuickstartPreferences;
import com.lokaso.util.MyLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyPreferencesManager {

    private static final String PREF_NAME = "LokasoPrefs";
    private static final String PREF_NAME2 = "LokasoPrefs2";
    private static final String PREF_LOKASO_OFFICIAL = "LOKASO_OFFICIAL_PREF";


    private static final String KEY_IS_LOGIN = "isLoggedIn";
    private static final String KEY_GPS = "GPS";
    private static final String KEY_GPS_MODE = "GPS_mode";
    private static final String KEY_GPS_TYPE = "GPS_type";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LNG = "lng";
    private static final String KEY_ID = "id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAME = "name";
    private static final String KEY_REFER_CODE = "refer_code";
    private static final String KEY_PROFESSION = "profession";
    private static final String KEY_PROFESSION_ID = "profession_id";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_USER_LATITUDE = "user_latitude";
    private static final String KEY_USER_LONGITUDE = "user_longitude";
    private static final String KEY_ABOUT_ME = "about_me";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_OTP = "otp";
    private static final String KEY_DISCOVERY_COUNT = "discovery_count";
    private static final String KEY_QUERY_COUNT = "query_count";
    private static final String KEY__FOLLOWERS_COUNT = "followers_count";
    private static final String KEY_PROFESSION_LIST = "profession_list";
    private static final String KEY_CHAT_UNREAD_COUNT = "chat_unread_count";
    private static final String KEY_INTEREST_LIST = "interest_list";
    private static final String KEY_USER_INTEREST_LIST = "user_interest_list";
    private static final String KEY_USER_FILTER_INTEREST_LIST = "key_user_filter_interest_list";

    private static final String KEY_LOCATION_SELECTED = "location_selected";
    private static final String KEY_FIRST_CHAT_NOTIFICATION = "first_chat_notification";

    private static final String KEY_QUERY_FILTER = "key_query_filter";
    private static final String KEY_SUGGESTION_FILTER = "key_suggestion_filter";

    public static boolean saveLoginState(Context context, boolean state) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_IS_LOGIN, state);
        return editor.commit();
    }

    public static boolean isLoggedIn(Context context) {
        //SharedPreferencesCustom pref = new SharedPreferencesCustom(context, "my-preferences", "SometopSecretKey1235", true);
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(KEY_IS_LOGIN, false);
    }

    public static boolean saveGPS(Context context, boolean state) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_GPS, state);
        return editor.commit();
    }

    public static boolean isGPS(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(KEY_GPS, false);
    }

    public static boolean saveLocationModeSelected(Context context, boolean state) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_GPS_MODE, state);
        return editor.commit();
    }

    public static boolean isLocationModeSelected(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(KEY_GPS_MODE, false);
    }

    public static boolean saveLocationModeManual(Context context, boolean state) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_GPS_TYPE, state);
        return editor.commit();
    }

    public static boolean isLocationModeManual(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(KEY_GPS_TYPE, false);
    }

    public static boolean saveSentTokenToServer(Context context, boolean state) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, state);
        return editor.commit();
    }

    public static boolean sentTokenToServer(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
    }

    public static boolean logOutUser(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.clear();
        return editor.commit();
    }

    public static boolean saveId(Context context, int id) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_ID, id);
        return editor.commit();
    }

    public static int getId(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getInt(KEY_ID, 0);
    }


    public static boolean saveLat(Context context, String lat) {
        double latitude = Double.parseDouble(lat);
        return saveLat(context, latitude);
    }
    public static boolean saveLat(Context context, double lat) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putLong(KEY_LAT, Double.doubleToRawLongBits(lat));
        return editor.commit();
    }

    public static double getLat(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return Double.longBitsToDouble(pref.getLong(KEY_LAT, Double.doubleToLongBits(0)));
    }

    public static boolean saveLng(Context context, String lng) {
        double longitude = Double.parseDouble(lng);
        return saveLng(context, longitude);
    }
    public static boolean saveLng(Context context, double lng) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putLong(KEY_LNG, Double.doubleToRawLongBits(lng));
        return editor.commit();
    }

    public static double getLng(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return Double.longBitsToDouble(pref.getLong(KEY_LNG, Double.doubleToLongBits(0)));
    }

    public static boolean saveLocationSelected(Context context, String image) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_LOCATION_SELECTED, image);
        return editor.commit();
    }

    public static String getLocationSelected(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString(KEY_LOCATION_SELECTED, "");
    }


    public static boolean saveImage(Context context, String image) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_IMAGE, image);
        return editor.commit();
    }

    public static String getImage(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString(KEY_IMAGE, "");
    }

    public static boolean saveEmail(Context context, String email) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_EMAIL, email);
        return editor.commit();
    }

    public static String getEmail(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString(KEY_EMAIL, "");
    }

    public static boolean saveName(Context context, String name) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_NAME, name);
        return editor.commit();
    }

    public static String getName(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString(KEY_NAME, "");
    }

    public static boolean setAboutMe(Context context, String aboutMe) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_ABOUT_ME, aboutMe);
        return editor.commit();
    }

    public static String getAboutMe(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString(KEY_ABOUT_ME, "");
    }

    public static boolean saveReferCode(Context context, String code) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_REFER_CODE, code);
        return editor.commit();
    }

    public static String getReferCode(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString(KEY_REFER_CODE, "");
    }

    public static boolean saveProfession(Context context, String phone) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_PROFESSION, phone);
        return editor.commit();
    }

    public static String getProfession(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString(KEY_PROFESSION, "");
    }

    public static boolean saveProfessionId(Context context, int id) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_PROFESSION_ID, id);
        return editor.commit();
    }

    public static int getProfessionId(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getInt(KEY_PROFESSION_ID, 0);
    }

    public static boolean setUserLocation(Context context, String city) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_LOCATION, city);
        return editor.commit();
    }

    public static String getUserLocation(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString(KEY_LOCATION, "");
    }

    public static boolean setUserLatitude(Context context, double lat) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putFloat(KEY_USER_LATITUDE, (float) lat);
        return editor.commit();
    }

    public static double getUserLatitude(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getFloat(KEY_USER_LATITUDE, 0);
    }

    public static boolean setUserLongitude(Context context, double longitide) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putFloat(KEY_USER_LONGITUDE, (float) longitide);
        return editor.commit();
    }

    public static double getUserLongitude(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getFloat(KEY_USER_LONGITUDE, 0);
    }

    public static boolean saveChatUnreadCount(Context context, int count) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_CHAT_UNREAD_COUNT, count);
        return editor.commit();
    }

    public static int getChatUnreadCount(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getInt(KEY_CHAT_UNREAD_COUNT, 0);
    }


    public static boolean saveOTP(Context context, String otp) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_OTP, otp);
        MyLog.e(KEY_OTP, otp);
        return editor.commit();
    }

    public static String getOTP(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        MyLog.e(KEY_OTP, pref.getString(KEY_OTP, ""));
        return pref.getString(KEY_OTP, "");
    }

    public static void resetOTP(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.remove(KEY_OTP);
        editor.commit();
    }

    public static boolean saveDiscovery_count(Context context, int id) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_DISCOVERY_COUNT, id);
        return editor.commit();
    }

    public static int getDiscovery_count(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getInt(KEY_DISCOVERY_COUNT, 0);
    }

    public static boolean saveQuery_count(Context context, int id) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_QUERY_COUNT, id);
        return editor.commit();
    }

    public static int getQuery_count(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getInt(KEY_QUERY_COUNT, 0);
    }

    public static boolean saveFollowing_count(Context context, int id) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY__FOLLOWERS_COUNT, id);
        return editor.commit();
    }

    public static int getFollowing_count(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getInt(KEY__FOLLOWERS_COUNT, 0);
    }


    public static boolean saveInterestList(Context context, Set<String> set) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putStringSet(KEY_INTEREST_LIST, set);
        return editor.commit();
    }

    public static Set<String> getInterestList(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> mySet = new HashSet<>();
        return pref.getStringSet(KEY_INTEREST_LIST, mySet);
    }

    public static boolean saveUserInterestList(Context context, Set<String> set) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putStringSet(KEY_USER_INTEREST_LIST, set);
        return editor.commit();
    }

    public static Set<String> getUserInterestList(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> mySet = new HashSet<>();
        return pref.getStringSet(KEY_USER_INTEREST_LIST, mySet);
    }

    public static boolean saveUserFilterInterestList(Context context, List<String> interestList) {

        Set<String> set = new HashSet<>(interestList);

        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putStringSet(KEY_USER_FILTER_INTEREST_LIST, set);
        return editor.commit();
    }

    public static List<String> getUserFilterInterestList(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> mySet1 = new HashSet<>();
        Set<String> set = pref.getStringSet(KEY_USER_FILTER_INTEREST_LIST, mySet1);


        List<String> interestIdList = new ArrayList<>();
        Object[] arrayInterest = set.toArray();

        for (int i = 0; i < arrayInterest.length; i++) {
            int interestIntId = Integer.parseInt("" + arrayInterest[i]);
            interestIdList.add(""+interestIntId);
        }
        return interestIdList;
    }

    public static boolean setQueryFilter(Context context, int type) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_QUERY_FILTER, type);
        return editor.commit();
    }

    public static int getQueryFilter(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getInt(KEY_QUERY_FILTER, 0);
    }

    public static boolean setSuggestionFilter(Context context, int type) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_SUGGESTION_FILTER, type);
        return editor.commit();
    }

    public static int getSuggestionFilter(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getInt(KEY_SUGGESTION_FILTER, 0);
    }


    public static boolean setFirstChatNotificationSeen(Context context, boolean state) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_LOKASO_OFFICIAL, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_FIRST_CHAT_NOTIFICATION, state);
        return editor.commit();
    }

    public static boolean isFirstChatNotificationSeen(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_LOKASO_OFFICIAL, Context.MODE_PRIVATE);
        return pref.getBoolean(KEY_FIRST_CHAT_NOTIFICATION, false);
    }


}
