package com.lokaso.util;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Point;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.lokaso.application.MyApplication;
import com.lokaso.model.Chat;
import com.lokaso.model.DiscoveryComment;
import com.lokaso.model.Interest;
import com.lokaso.model.Profession;
import com.lokaso.model.QueryResponse;
import com.lokaso.model.ResponseComment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper extends Application {

    private static boolean commonLog = false;
    static Context context;
    static String TAG = "Helper";
    public static List<Interest> interestList = new ArrayList<>();
    public static List<Profession> professionList = new ArrayList<>();

    /**
     * method used to generate random alphanumeric string
     *
     * @param count : Length required of the Random alpha-numeric
     * @return String of the Random alpha-numeric created
     */
    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * Constant.ALPHA_NUMERIC_STRING.length());
            builder.append(Constant.ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return width;
    }

    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        return height;
    }

    public static double getActionBarHeight(Context context) {
        TypedValue tv = new TypedValue();
        double actionBarHeight = 0;
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,context.getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    public static void playNotificationSound() {
        try {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(MyApplication.getInstance().getApplicationContext(), alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }


    public static Class<?> getCurrentActivityName(Context context) {
        Class<?> clss = null;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            clss = activeProcess.getClass();
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                clss = componentInfo.getClass();
            }
        }

        return clss;
    }



    public static void setListViewHeightBasedOnChildrenOld(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static boolean isValidEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;

        } else {
            //String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
            String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
            Pattern p = Pattern.compile(ePattern);
            Matcher m = p.matcher(email);
            return m.matches();
        }
    }

    public static List<DiscoveryComment> updateDiscoveryCommentTimeSort(List<DiscoveryComment> tempList) {
        Collections.sort(tempList, new UpdateDiscoveryCommentTimeSorter());
        return tempList;
    }

    public static List<ResponseComment> updateResponseCommentTimeSort(List<ResponseComment> tempList) {
        Collections.sort(tempList, new UpdateResponseCommentTimeSorter());
        return tempList;
    }

    public static List<QueryResponse> updateTimeSort(List<QueryResponse> tempList) {
        Collections.sort(tempList, new UpdateTimeSorter());
        return tempList;
    }

    public static List<Chat> updateTimeSort2(List<Chat> tempList) {
        Collections.sort(tempList, new UpdateTimeSorter2());
        return tempList;
    }

    static class UpdateTimeSorter2 implements Comparator<Chat> {
        @Override
        public int compare(Chat lhs, Chat rhs) {

            long lTime = getDate(lhs.getCreated_date(), Constant.DATE_FORMAT3).getTime();
            long rTime = getDate(rhs.getCreated_date(), Constant.DATE_FORMAT3).getTime();
            MyLog.e(TAG, "lTime : "+lTime+" , rTime : "+rTime +" diff : "+(lTime-rTime)+" diffint : "+((int)(lTime-rTime)));
            long diff = (lTime-rTime);

            return (diff<0) ? -1 : 1;

//            return (int) (getDate(lhs.getCreated_date(), Constant.DATE_FORMAT3).getTime() -
//                    getDate(rhs.getCreated_date(), Constant.DATE_FORMAT3).getTime());
        }
    }

    static class UpdateTimeSorter implements Comparator<QueryResponse> {
        @Override
        public int compare(QueryResponse lhs, QueryResponse rhs) {
            return (int) (getDate(rhs.getCreated_date(), Constant.DATE_FORMAT3).getTime() -
                    getDate(lhs.getCreated_date(), Constant.DATE_FORMAT3).getTime());
        }
    }

    static class UpdateResponseCommentTimeSorter implements Comparator<ResponseComment> {
        @Override
        public int compare(ResponseComment lhs, ResponseComment rhs) {
            return (int) (getDate(rhs.getCreated_date(), Constant.DATE_FORMAT3).getTime() -
                    getDate(lhs.getCreated_date(), Constant.DATE_FORMAT3).getTime());
        }
    }

    static class UpdateDiscoveryCommentTimeSorter implements Comparator<DiscoveryComment> {
        @Override
        public int compare(DiscoveryComment lhs, DiscoveryComment rhs) {
            return (int) (getDate(rhs.getCreated_date(), Constant.DATE_FORMAT3).getTime() -
                    getDate(lhs.getCreated_date(), Constant.DATE_FORMAT3).getTime());
        }
    }

    /**
     * This function is used to check if the time supplied has already occurred or not
     *
     * @param time Time to check against current time format yyyy-MM-dd HH:mm:ss
     * @return true is already passed false if not occurred
     */
    public static Date getDate(String time, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
        Date date = new Date();
        try {
            date = simpleDateFormat.parse(time);
        } catch (Exception e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }



    /**
     * Get App Package Name
     *
     */
    public static String getPackageName(Context context) {

        return context.getPackageName();

    }
    public static String removeLastComma(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length()-1)==',') {
            str = str.substring(0, str.length()-1);
        }
        return str;
    }
}