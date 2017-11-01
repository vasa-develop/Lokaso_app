package com.lokaso.gcm;

import android.content.Intent;
import android.provider.Settings;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.lokaso.util.MyLog;

/**
 * Created by Androcid-6 on 20-10-2016.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        MyLog.d(TAG, "Refreshed token: " + refreshedToken);


        String device_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        MyLog.e(TAG, "GCM device_id : " + device_id);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(device_id, refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String device_id, String token) {
        // TODO: Implement this method to send token to your app server.

        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
//
//        Intent intent = new Intent(this, RegistrationIntentService.class);
//        startService(intent);
//
//        if (MyPreferencesManager.isLoggedIn(this)) {
//            RestClient.getLokasoApi().gcm_register(MyPreferencesManager.getId(this), device_id, token,
//                    new Callback<RetroResponse>() {
//                        @Override
//                        public void success(RetroResponse retroResponse, Response response2) {
//                            MyLog.e(TAG, "Response : " + retroResponse.getMessage());
//                        }
//
//                        @Override
//                        public void failure(RetrofitError error) {
//                            MyLog.e(TAG, "RetrofitError : " + error);
//                        }
//                    });
//
//        } else {
//            MyLog.e("TAG", "The gcm is not registered please login");
//        }

    }
}
