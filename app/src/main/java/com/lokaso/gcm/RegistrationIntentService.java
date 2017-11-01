package com.lokaso.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.firebase.iid.FirebaseInstanceId;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroResponse;
import com.lokaso.util.MyLog;

import java.io.IOException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = RegistrationIntentService.class.getSimpleName();
    private static final String[] TOPICS = {"global"};

    public RegistrationIntentService() {
        super(TAG);
        MyLog.e(TAG, "RegistrationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        MyLog.e(TAG, "onHandleIntent");
        try {
            // This is using GCM which is deprecated
            //InstanceID instanceID = InstanceID.getInstance(this);
            //String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            // This is using FCM
            String token = FirebaseInstanceId.getInstance().getToken();

            MyLog.e(TAG, "GCM Registration Token : " + token);

            String device_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            MyLog.e(TAG, "GCM device_id : " + device_id);
            sendRegistrationToServer(device_id, token);
            subscribeTopics(token);
            MyPreferencesManager.saveSentTokenToServer(this, true);

        } catch (Exception e) {
            MyLog.e(TAG, "Failed to complete token refresh " + e);
            MyPreferencesManager.saveSentTokenToServer(this, false);
        }

        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(String device_id, String token) {
        if (MyPreferencesManager.isLoggedIn(this)) {
            RestClient.getLokasoApi().gcm_register(MyPreferencesManager.getId(this), device_id, token,
                    new Callback<RetroResponse>() {
                        @Override
                        public void success(RetroResponse retroResponse, Response response2) {
                            MyLog.e(TAG, "Response : " + retroResponse.getMessage());
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            MyLog.e(TAG, "RetrofitError : " + error);
                        }
                    });

        } else {
            MyLog.e("TAG", "The gcm is not registered please login");
        }
    }

    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
}