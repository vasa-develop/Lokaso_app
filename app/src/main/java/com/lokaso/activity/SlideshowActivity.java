package com.lokaso.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.lokaso.R;
import com.lokaso.dao.DaoController;
import com.lokaso.dao.DaoFunctions;
import com.lokaso.gcm.QuickstartPreferences;
import com.lokaso.gcm.RegistrationIntentService;
import com.lokaso.model.Interest;
import com.lokaso.model.Profession;
import com.lokaso.model.Setting;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.preference.SettingPreference;
import com.lokaso.preference.WalkthroughPreference;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroInterest;
import com.lokaso.retromodel.RetroProfession;
import com.lokaso.retromodel.RetroSetting;
import com.lokaso.util.AppUtil;
import com.lokaso.util.Constant;
import com.lokaso.util.DeviceUtil;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyLog;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;
import com.splunk.mint.Mint;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SlideshowActivity extends AppCompatActivity {

    private String TAG = SlideshowActivity.class.getSimpleName();
    private Context context = SlideshowActivity.this;

    private Handler handler;
    private Runnable runnable;
    private List<Interest> interestList = new ArrayList<>();
    public List<Profession> professionList = new ArrayList<>();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;
    private NetworkConnection networkConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Mint.initAndStartSession(this.getApplication(), "4b0fddb5");

        getWindow().setBackgroundDrawableResource(R.mipmap.background_login);




        networkConnection = new NetworkConnection(context);
        getProfessions();
        getInterests();
        getSetting();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean sentToken = MyPreferencesManager.sentTokenToServer(context);
                if (sentToken) {
                    MyLog.e(TAG, getString(R.string.gcm_send_message));
                } else {
                    MyLog.e(TAG, getString(R.string.token_error_message));
                }
            }
        };

        registerReceiver();

        if (checkPlayServices()) {
            Intent intent = new Intent(context, RegistrationIntentService.class);
            startService(intent);

            runnable = new Runnable() {
                @Override
                public void run() {

//                    Intent intent1 = new Intent(context, SelectInterestActivity.class);
//                    startActivity(intent1);
//                    finish();
//
//                    if(true) return;

                    // First Check if there is a force update or normal update is not seen
                    if (SettingPreference.isForceUpdateAvailable(context) || !SettingPreference.isNormalUpdateSeen(context)) {

                        Intent intent = new Intent(context, UpdateActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    // If walkthrough is not seen, take him to walkthrough screen
                    else if (!WalkthroughPreference.isWalkthroughSeen(context)) {

                        Intent intent = new Intent(context, WalkthroughActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        // If not logged in, take him to login screen
                        if (!MyPreferencesManager.isLoggedIn(context)) {
                            Intent intent = new Intent(context, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            boolean interestSelected = true;

                            Set<String> mySet = MyPreferencesManager.getUserInterestList(context);
                            if(mySet.size()==0) {
                                interestSelected = false;
                            }

                            // If user has not selected interest, take him to interest screen
                            if(!interestSelected) {
                                Intent intent = new Intent(context, SelectInterestActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(context, SuggestionActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                }
            };

            handler = new Handler();
            handler.postDelayed(runnable, Constant.SPLASH_SHOW_TIME);
        }


        new MyFont1(context).setAppFont((ViewGroup)findViewById(R.id.container));

    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    /**
     * method used to register receiver
     */
    private void registerReceiver() {
        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }

    /**
     * method used to check play services
     *
     * @return true if Play services are accessible and false for vice-versa
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, Constant.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                MyLog.e(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * method used to get professions
     */
    private void getProfessions() {
        if (networkConnection.isNetworkAvailable()) {

            String updated_date = DaoController.getLatestUpdateProfessionDate(context);
            RestClient.getLokasoApi().professions(
                    updated_date,
                    new Callback<RetroProfession>() {
                @Override
                public void success(RetroProfession retroProfession, Response response) {
                    if (retroProfession.getSuccess()) {
                        professionList = retroProfession.getDetails();
                        DaoController.updateProfessionList(context, professionList);

                    } else {
                        professionList = new ArrayList<>();
                        MyToast.tdebug(context, retroProfession.getMessage() + "");
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    professionList = new ArrayList<>();
                    MyToast.tdebug(context, "" + error);
                }
            });
        }
    }

    /**
     * method used to get interests
     */
    private void getInterests() {
        if (networkConnection.isNetworkAvailable()) {

            DaoFunctions daoFunctions = new DaoFunctions(context);
            String updated_date = daoFunctions.getLatestUpdateInterestDate();
            daoFunctions.close();

            if(updated_date==null) {
                updated_date = "";
            }

            RestClient.getLokasoApi().interests(
                    updated_date,
                    new Callback<RetroInterest>() {
                @Override
                public void success(RetroInterest retroInterest, Response response) {
                    if (retroInterest.getSuccess()) {
                        interestList = retroInterest.getDetails();

                        DaoFunctions daoFunctions = new DaoFunctions(context);
                        daoFunctions.updateInterestList(interestList);
                        daoFunctions.close();

                    } else {
                        interestList = new ArrayList<>();
                        MyToast.tdebug(context, retroInterest.getMessage() + "");
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    interestList = new ArrayList<>();
                    MyToast.tdebug(context, "" + error);
                }
            });
        }
    }


    /**
     * method used to get setting
     */
    private void getSetting() {
        if (networkConnection.isNetworkAvailable()) {

            int user_id = MyPreferencesManager.getId(context);
            String device_id = DeviceUtil.getDeviceId(context);
            String device_os = DeviceUtil.getDeviceOs(context);
            String device_name = DeviceUtil.getDeviceFullname(context);
            String device_os_version = DeviceUtil.getDeviceAndroidVersion(context);
            int app_version_code = AppUtil.getVersionCode(context);
            String app_version_name = AppUtil.getVersionName(context);


            RestClient.getLokasoApi().getSetting(
                    user_id,
                    device_id,
                    device_os,
                    device_name,
                    device_os_version,
                    app_version_code,
                    app_version_name,
                    new Callback<RetroSetting>() {
                @Override
                public void success(RetroSetting retroSetting, Response response) {
                    if (retroSetting!=null) {
                        if (retroSetting.getSuccess()) {

                            Setting setting = retroSetting.getDetails();

                            int version_code = setting.getVersion_code();
                            int version_update_type = setting.getVersion_update_type();

                            SettingPreference.setVersionCode(context, version_code);
                            SettingPreference.setVersionName(context, setting.getVersion_name());
                            SettingPreference.setVersionUpdateType(context, version_update_type);

                            SettingPreference.resetUpdateSeenIfNewerVersion(context, version_code);
                            SettingPreference.resetLastForceUpdateVersionIfSameChangedToNormalUpdate(context, version_code, version_update_type);
                            if (version_update_type == SettingPreference.UPDATE_TYPE_FORCE)
                                SettingPreference.setLastForceUpdateVersion(context, version_code);

                        } else {
                            MyToast.tdebug(context, "" + retroSetting.getMessage());
                        }
                    } else {
                        MyToast.tdebug(context, "Error fetching settings");
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    MyToast.tdebug(context, "" + error);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            handler.removeCallbacks(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}