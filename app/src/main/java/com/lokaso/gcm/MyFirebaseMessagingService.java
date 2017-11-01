package com.lokaso.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.lokaso.R;
import com.lokaso.activity.ChatActivity;
import com.lokaso.activity.ChatRoomActivity;
import com.lokaso.activity.OthersProfileActivity;
import com.lokaso.activity.QueriesActivity;
import com.lokaso.activity.QueryResponseCommentActivity;
import com.lokaso.activity.SplashActivity;
import com.lokaso.activity.SuggestionActivity;
import com.lokaso.activity.SuggestionCommentActivity;
import com.lokaso.activity.ViewQueryActivity;
import com.lokaso.activity.ViewSuggestionActivity;
import com.lokaso.model.Adhoc;
import com.lokaso.model.Gcm;
import com.lokaso.model.Profile;
import com.lokaso.model.Queries;
import com.lokaso.model.QueryResponse;
import com.lokaso.model.Suggestion;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.util.Constant;
import com.lokaso.util.Helper;
import com.lokaso.util.MyLog;

import org.json.JSONObject;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private static final int GCM_NONE = 0;
    private static final int GCM_FOLLOW_USER = 1;
    private static final int GCM_FOLLOW_QUERY = 2;
    private static final int GCM_FOLLOW_QUERY_RESPOND = 3;
    private static final int GCM_CHAT = 4;

    private static final int GCM_NOTIFICATION_MY_QUERY_RESPONSE			    = 1;
    private static final int GCM_NOTIFICATION_RESPONSE_ON_FOLLOWED_QUERY	= 2;
    private static final int GCM_NOTIFICATION_FOLLOW_QUERY				    = 4;
    private static final int GCM_NOTIFICATION_LIKE_QUERY				    = 5;
    private static final int GCM_NOTIFICATION_DISLIKE_QUERY				    = 6;
    private static final int GCM_NOTIFICATION_QUERY_RESPONSE_LIKE		    = 7;
    private static final int GCM_NOTIFICATION_QUERY_RESPONSE_DISLIKE	    = 8;
    private static final int GCM_NOTIFICATION_QUERY_RESPONSE_COMMENT	    = 3;
    private static final int GCM_NOTIFICATION_QUERY_NEARBY	                = 9;

    private static final int GCM_NOTIFICATION_LOCAL_SUGGESTERS			    = 11;
    private static final int GCM_NOTIFICATION_SUGGESTION_LIKE			    = 12;
    private static final int GCM_NOTIFICATION_SUGGESTION_COMMENT		    = 13;

    private static final int GCM_NOTIFICATION_USER_FOLLOW				    = 21;
    private static final int GCM_NOTIFICATION_CHAT						    = 22;
    private static final int GCM_NOTIFICATION_FRIEND_JOINS				    = 23;

    private static final int GCM_NOTIFICATION_ADHOC				            = 30;
    private static final int GCM_NOTIFICATION_LOGOUT_USER				    = 31;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ

        if(remoteMessage!=null) {
            Log.d(TAG, "GCM_RESPONSE remoteMessage : " + remoteMessage);

            long sentTime = remoteMessage.getSentTime();
            String from = remoteMessage.getFrom();
            Map<String, String> data = remoteMessage.getData();
            Log.d(TAG, "GCM_RESPONSE sentTime : " + sentTime);
            Log.d(TAG, "GCM_RESPONSE from : " + from);
            Log.d(TAG, "GCM_RESPONSE data : " + data);

            for (int i=0; i<data.size(); i++) {
                Log.d(TAG, "GCM_RESPONSE data ("+i+"): " + data.get(i));
            }

            RemoteMessage.Notification notification = remoteMessage.getNotification();
            MyLog.d(TAG, "GCM_RESPONSE notification : " + notification);
            if(notification!=null) {
                MyLog.d(TAG, "GCM_RESPONSE notification getBody : " + notification.getBody());
                MyLog.d(TAG, "GCM_RESPONSE notification getTitle : " + notification.getTitle());
            }
        }
        else {
            MyLog.d(TAG, "GCM_RESPONSE remoteMessage : " + remoteMessage);
        }

        // Check if message contains a data payload.
        Map<String, String> mapData = remoteMessage.getData();
        if (mapData!=null && mapData.size() > 0) {

            MyLog.e(TAG, "GCM_RESPONSE: " + mapData);

            JSONObject data = null;
            int type = GCM_NONE;

            Gcm gcm = null;
            try {
                String dataStr = mapData.get("message");
                MyLog.e(TAG, "GCM_OUTPUT: " + dataStr);
                Gson gson = new Gson();
                gcm = gson.fromJson(dataStr, Gcm.class);
                MyLog.e(TAG, "GCM_OUTPUT gcm: " + gcm);
                MyLog.e(TAG, "GCM_OUTPUT gcm message: " + gcm.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }

            boolean isAppInBackground = Helper.isAppIsInBackground(getApplicationContext());
            if(gcm!=null) {
                int to_user_id = gcm.getTo_user_id();
                if(to_user_id==MyPreferencesManager.getId(this) || to_user_id==0) {
                    sendNotification(type, gcm, isAppInBackground);
                }
                else {
                    MyLog.e(TAG, "You are not the right person to receive this notification: " + gcm.getTo_user_id());
                }
            }
        }
        else {
            Log.d(TAG, "GCM_RESPONSE: " + mapData);
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]


    private void sendNotification(int notification_type, Gcm gcm, boolean isAppInBackground) {

        String title = Constant.APP_NAME;
        String message = gcm.getMessage();

        notification_type = gcm.getNotificationType();

        int notification_id = notification_type;

        if(gcm.getTitle()!=null && gcm.getTitle().length()>0) {
            title = gcm.getTitle();
        }

        Intent[] activityIntents = null;

        Intent intent = new Intent(this, SplashActivity.class);

        switch (notification_type) {

            case GCM_NOTIFICATION_CHAT:
            {
                Gcm.GcmDetail details = gcm.getDetails();
                Profile profile = details.getProfile();
                int chat_id = details.getChat_id();
                String chat_message = details.getChat_message();

                title = profile.getName();

                notification_id = notification_type + chat_id;

                Intent backIntent = new Intent(this, SuggestionActivity.class);
                backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                Intent backIntent2 = new Intent(this, ChatRoomActivity.class);
                backIntent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                intent = new Intent(this, ChatActivity.class);
                intent.putExtra(Constant.PROFILE, profile);
                intent.putExtra(Constant.CHAT_ID, chat_id);

                if(isAppInBackground) {
                    activityIntents = new Intent[] {backIntent, backIntent2, intent};
                }
                else {

                    Intent pushNotification = new Intent(QuickstartPreferences.PUSH_NOTIFICATION);
                    pushNotification.putExtra(Constant.CHAT_ID, chat_id);
                    pushNotification.putExtra(Constant.FROM_USER_ID, profile.getId());
                    pushNotification.putExtra(Constant.MESSAGE, chat_message);
                    pushNotification.putExtra(Constant.PROFILE, profile);
                    pushNotification.putExtra(Constant.CREATED_DATE, gcm.getCreated_date());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                    Class<?> clss = Helper.getCurrentActivityName(getApplicationContext());
                    if(clss!=null) {
                        MyLog.e(TAG, "CurrentClass : "+clss.getSimpleName());
                        if(ChatActivity.class.getSimpleName().equals(clss.getSimpleName())) {
                            return;
                        }
                    }
                    else {
                        MyLog.e(TAG, "CurrentClass : "+clss);
                    }

                    activityIntents = new Intent[] {intent};
                }
            }
            break;

            case GCM_NOTIFICATION_USER_FOLLOW:
            case GCM_NOTIFICATION_FRIEND_JOINS:
            {
                Gcm.GcmDetail details = gcm.getDetails();
                Profile profile = details.getProfile();
                int chat_id = details.getChat_id();


                notification_id = notification_type + chat_id;

                if(profile!=null) {
                    //title = profile.getName();
                    int profile_id = profile.getId();

                    Intent backIntent = new Intent(this, SuggestionActivity.class);
                    backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    intent = new Intent(this, OthersProfileActivity.class);
                    intent.putExtra(Constant.PROFILE_ID, profile_id);
                    intent.putExtra(Constant.PROFILE, profile);

                    if (isAppInBackground) {
                        activityIntents = new Intent[]{backIntent, intent};
                    } else {
                        activityIntents = new Intent[]{intent};
                    }
                }
            }
            break;

            case GCM_NOTIFICATION_FOLLOW_QUERY:
            case GCM_NOTIFICATION_LIKE_QUERY:
            case GCM_NOTIFICATION_DISLIKE_QUERY:
            case GCM_NOTIFICATION_MY_QUERY_RESPONSE:
            case GCM_NOTIFICATION_RESPONSE_ON_FOLLOWED_QUERY:
            case GCM_NOTIFICATION_QUERY_RESPONSE_LIKE:
            case GCM_NOTIFICATION_QUERY_RESPONSE_DISLIKE:
            case GCM_NOTIFICATION_QUERY_NEARBY:
            {
                Gcm.GcmDetail details = gcm.getDetails();
                Profile profile = details.getProfile();
                Queries queries = details.getQuery();
                notification_id = notification_type;
                if(queries!=null) {
                    int query_id = queries.getId();
                    //title = queries.getDescription();

                    notification_id = notification_type + query_id;

                    Intent backIntent = new Intent(this, QueriesActivity.class);
                    backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    intent = new Intent(this, ViewQueryActivity.class);
                    intent.putExtra(Constant.INTENT_QUERIES, queries);

                    if(isAppInBackground) {
                        activityIntents = new Intent[] {backIntent, intent};
                    }
                    else {
                        activityIntents = new Intent[] {intent};
                    }
                }
            }
            break;

            case GCM_NOTIFICATION_QUERY_RESPONSE_COMMENT:
            {
                Gcm.GcmDetail details = gcm.getDetails();
                Profile profile = details.getProfile();
                Queries queries = details.getQuery();
                QueryResponse queryResponse = details.getQueryResponse();
                if(queries!=null) {
                    int query_id = queries.getId();
                    //title = queries.getDescription();

                    notification_id = notification_type + query_id;

                    Intent backIntent = new Intent(this, QueriesActivity.class);
                    backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    Intent backIntent2 = new Intent(this, ViewQueryActivity.class);
                    backIntent2.putExtra(Constant.INTENT_QUERIES, queries);
                    backIntent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    intent = new Intent(this, QueryResponseCommentActivity.class);
                    intent.putExtra(Constant.INTENT_QUERYRESPONSE, queryResponse);

                    if (isAppInBackground) {
                        activityIntents = new Intent[]{backIntent, backIntent2, intent};
                    } else {
                        activityIntents = new Intent[]{intent};
                    }
                }
            }
            break;

            case GCM_NOTIFICATION_LOCAL_SUGGESTERS:
            case GCM_NOTIFICATION_SUGGESTION_LIKE:
            {
                Gcm.GcmDetail details = gcm.getDetails();
                Profile profile = details.getProfile();
                Suggestion suggestion = details.getSuggestion();
                if(suggestion!=null) {
                    int suggestion_id = suggestion.getId();
                    //title = suggestion.getCaption();

                    notification_id = notification_type + suggestion_id;

                    Intent backIntent = new Intent(this, SuggestionActivity.class);
                    backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    intent = new Intent(this, ViewSuggestionActivity.class);
                    intent.putExtra(Constant.SUGGESTION, suggestion);

                    if (isAppInBackground) {
                        activityIntents = new Intent[]{backIntent, intent};
                    } else {
                        activityIntents = new Intent[]{intent};
                    }
                }
            }
            break;

            case GCM_NOTIFICATION_SUGGESTION_COMMENT:
            {
                Gcm.GcmDetail details = gcm.getDetails();
                Profile profile = details.getProfile();
                Suggestion suggestion = details.getSuggestion();
                if(suggestion!=null) {
                    int suggestion_id = suggestion.getId();
                    //title = suggestion.getCaption();

                    notification_id = notification_type + suggestion_id;

                    Intent backIntent = new Intent(this, SuggestionActivity.class);
                    backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    intent = new Intent(this, SuggestionCommentActivity.class);
                    intent.putExtra(Constant.SUGGESTION, suggestion);

                    if (isAppInBackground) {
                        activityIntents = new Intent[]{backIntent, intent};
                    } else {
                        activityIntents = new Intent[]{intent};
                    }
                }
            }
            break;

            case GCM_NOTIFICATION_ADHOC:
            {
                Gcm.GcmDetail details = gcm.getDetails();
                Profile profile = details.getProfile();
                Adhoc adhoc = details.getAdhoc();
                if(adhoc!=null) {
                    int adhoc_id = adhoc.getId();

                    notification_id = notification_type + adhoc_id;

                    if (isAppInBackground) {
                        activityIntents = new Intent[]{intent};
                    } else {
                        activityIntents = new Intent[]{intent};
                    }
                }
            }
            break;

            case GCM_NOTIFICATION_LOGOUT_USER:
            {
                Gcm.GcmDetail details = gcm.getDetails();
                Profile profile = details.getProfile();
                if(profile!=null) {
                    int profile_id = profile.getId();

                    notification_id = notification_type + profile_id;

                    if (isAppInBackground) {
                        activityIntents = new Intent[]{intent};
                    } else {
                        activityIntents = new Intent[]{intent};
                    }

                    MyPreferencesManager.logOutUser(getApplicationContext());
                }
            }
            break;

            default:
            {
                if(isAppInBackground) {
                    activityIntents = new Intent[] {intent};
                }
                else {
                    activityIntents = new Intent[] {intent};
                }
            }
            break;
        }

        if(activityIntents==null) {
            activityIntents = new Intent[] {intent};
        }


        // Remove slash n from text
        message = message.replace("\\n", " ");

        final PendingIntent pendingIntent = PendingIntent.getActivities(this, 0, activityIntents, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(getNotificationIcon())
                .setContentTitle(title)
                .setContentText(Html.fromHtml(message))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(message)))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);


        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notification_id, notificationBuilder.build());
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.ic_launcher_notification_white : R.mipmap.ic_launcher_notification;
    }



    public static void sendNotificationFirstChat(Context context) {

        String title = context.getString(R.string.first_chat_title);
        String message = context.getString(R.string.first_chat_message);

        Intent intent = new Intent(context, OthersProfileActivity.class);
        intent.putExtra(Constant.PROFILE_ID, 1);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);


        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Constant.NOTIFICATION_ID, notificationBuilder.build());

        MyPreferencesManager.setFirstChatNotificationSeen(context, true);
    }
}