package com.lokaso.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;
import com.lokaso.R;
import com.lokaso.activity.ChatActivity;
import com.lokaso.activity.SplashActivity;
import com.lokaso.activity.ViewQueryActivity;
import com.lokaso.model.Profile;
import com.lokaso.model.Queries;
import com.lokaso.util.Constant;
import com.lokaso.util.Helper;
import com.lokaso.util.MyLog;
import com.lokaso.preference.MyPreferencesManager;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = MyGcmListenerService.class.getSimpleName();

    @Override
    public void onMessageReceived(String from, Bundle data) {
        MyLog.e(TAG, "GCM notification onMessageReceived : " + data.toString());
        String message = data.getString(Constant.MESSAGE);
        try {
            String profile = data.getString(Constant.PROFILE);
            int chat_id = Integer.parseInt(data.getString(Constant.CHAT_ID));
            int from_user_id = Integer.parseInt(data.getString(Constant.FROM_USER_ID));
            String timeStamp = data.getString(Constant.CREATED_DATE);

            MyLog.e(TAG, "GCM From: " + from);
            MyLog.e(TAG, "GCM Message: " + message);
            MyLog.e(TAG, "GCM chat_id: " + chat_id);
            MyLog.e(TAG, "GCM from_user_id: " + from_user_id);
            MyLog.e(TAG, "GCM timeStamp: " + timeStamp);

            if (profile != null) {
                if (profile.trim().length() != 0) {
                    Gson gson = new Gson();
                    Profile profile1 = gson.fromJson(profile, Profile.class);

                    if (profile1.getTo_user_id() == MyPreferencesManager.getId(this)) {
                        if (!Helper.isAppIsInBackground(getApplicationContext())) {
                            Intent pushNotification = new Intent(QuickstartPreferences.PUSH_NOTIFICATION);
                            pushNotification.putExtra(Constant.CHAT_ID, chat_id);
                            pushNotification.putExtra(Constant.FROM_USER_ID, from_user_id);
                            pushNotification.putExtra(Constant.MESSAGE, message);
                            pushNotification.putExtra(Constant.PROFILE, profile1);
                            pushNotification.putExtra(Constant.CREATED_DATE, timeStamp);
                            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                            sendNotification(chat_id, message, profile1);
                            //Helper.playNotificationSound();

                        } else {
                            sendNotification(chat_id, message, profile1);
                        }
                    }
                }
            }
        } catch (Exception e) {
            String profile = data.getString(Constant.PROFILE);
            if (profile != null) {
                if (profile.trim().length() != 0) {
                    Gson gson = new Gson();
                    Profile profile1 = gson.fromJson(profile, Profile.class);

                    if (profile1.getId() == MyPreferencesManager.getId(this)) {
                        sendNotification(message);
                    }
                }
            }
            e.printStackTrace();
        }

        /*if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }*/
    }

    private void sendNotification(int chat_id, String message, Profile profile) {
        /*
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constant.PROFILE, profile);
        intent.putExtra(Constant.CHAT_ID, chat_id);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_notification)
                .setContentTitle(Constant.APP_NAME)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Constant.NOTIFICATION_ID, notificationBuilder.build());
        */
    }

    private void sendNotification(String message, Queries queries) {
        /*
        Intent intent = new Intent(this, ViewQueryActivity.class);
        intent.putExtra(Constant.INTENT_QUERIES, queries);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_notification)
                .setContentTitle(Constant.APP_NAME)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Constant.NOTIFICATION_ID, notificationBuilder.build());
        */
    }

    private void sendNotification(String message) {
        /*
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_notification)
                .setContentTitle(Constant.APP_NAME)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Constant.NOTIFICATION_ID, notificationBuilder.build());
        */
    }
}
