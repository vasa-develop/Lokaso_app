package com.lokaso.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.lokaso.util.AppUtil;
import com.lokaso.util.MyLog;

public class SettingPreference {

    private static final String TAG = SettingPreference.class.getSimpleName();

	private static final String KEY                     = "SettingPreference";
    private static final String VERSION_CODE            = "version_code";
    private static final String VERSION_NAME            = "version_name";
    private static final String UPDATE_TYPE             = "update_type";
    private static final String UPDATE_SEEN             = "update_seen";
    private static final String UPDATE_SEEN_VERSION     = "update_seen_version";
    private static final String LAST_FORCE_UPDATE_VERSION     = "last_force_update_version";

    private static final int UPDATE_TYPE_NONE           = 0;
    private static final int UPDATE_TYPE_NORMAL        = 1;
    public static final int UPDATE_TYPE_FORCE          = 2;

    private static int getVersionCode(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        return preferences.getInt(VERSION_CODE, 0);
    }

    public static boolean setVersionCode(Context context, int code) {

        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putInt(VERSION_CODE, code);
        return editor.commit();
    }

    private static String getVersionName(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        return preferences.getString(VERSION_NAME, "1.0");
    }

    public static boolean setVersionName(Context context, String name) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putString(VERSION_NAME, name);
        return editor.commit();
    }

    private static int getUpdateType(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        return preferences.getInt(UPDATE_TYPE, UPDATE_TYPE_NONE);
    }

    public static boolean setVersionUpdateType(Context context, int updateType) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putInt(UPDATE_TYPE, updateType);
        return editor.commit();
    }

    private static boolean isUpdateSeen(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        return preferences.getBoolean(UPDATE_SEEN, false);
    }

    public static boolean setUpdateSeen(Context context, boolean isSeen) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(UPDATE_SEEN, isSeen);
        return editor.commit();
    }

    private static int getUpdateSeenVersion(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        return preferences.getInt(UPDATE_SEEN_VERSION, 0);
    }
    private static boolean setUpdateSeenVersion(Context context, int version_code) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putInt(UPDATE_SEEN_VERSION, version_code);
        return editor.commit();
    }

    private static int getLastForceUpdateVersion(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        return preferences.getInt(LAST_FORCE_UPDATE_VERSION, UPDATE_TYPE_NONE);
    }

    public static boolean setLastForceUpdateVersion(Context context, int version_code) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putInt(LAST_FORCE_UPDATE_VERSION, version_code);
        return editor.commit();
    }


    public static boolean resetUpdateSeenIfNewerVersion(Context context, int version_code) {

        boolean isReset = false;
        if(version_code>getUpdateSeenVersion(context)) {
            isReset = setUpdateSeen(context, false);
            setUpdateSeenVersion(context,version_code);
        }

        MyLog.e(TAG, "isReset Version seen : "+isReset+" ("+version_code+")");
        return isReset;
    }


    public static boolean resetLastForceUpdateVersionIfSameChangedToNormalUpdate(Context context, int version_code, int version_update_type) {

        boolean isReset = false;
        if(version_code==getLastForceUpdateVersion(context) && version_update_type==UPDATE_TYPE_NORMAL) {
            //Just set it to the previous version, which is actually wrong, but would work.

            setLastForceUpdateVersion(context, version_code-1);
        }

        MyLog.e(TAG, "isReset Version seen : "+isReset+" ("+version_code+")");
        return isReset;
    }

    public static boolean isNewVersionAvailable(Context context) {
        //float versionLatest = Float.parseFloat(getVersionName(context));
        //float versionApp = Float.parseFloat(AppUtil.getVersionName(context));
        float versionLatest = getVersionCode(context);
        float versionApp = AppUtil.getVersionCode(context);

        MyLog.e(TAG, "versionLatest : "+versionLatest+" , versionApp : "+versionApp);
        return versionLatest>versionApp;
    }




    public static boolean isForceUpdateAvailable(Context context) {

        boolean isForceUpdateAvailable = false;
        if(isNewVersionAvailable(context)) {
            if(getUpdateType(context)==UPDATE_TYPE_FORCE) {
                isForceUpdateAvailable = true;
            }
            // Here check the last force update version
            else {
                float versionApp = AppUtil.getVersionCode(context);
                if(getLastForceUpdateVersion(context)>versionApp) {
                    isForceUpdateAvailable = true;
                }
            }
        }
        return isForceUpdateAvailable;
    }


    public static boolean isNormalUpdateSeen(Context context) {

        boolean isUpdateSeen = true;
        if(isNewVersionAvailable(context)) {
            if(getUpdateType(context)==UPDATE_TYPE_NORMAL) {
                if(!isUpdateSeen(context)) {
                    isUpdateSeen = false;
                }
            }
        }
        return isUpdateSeen;
    }

    public static boolean clear(Context context) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.clear();
        return editor.commit();
    }
}
