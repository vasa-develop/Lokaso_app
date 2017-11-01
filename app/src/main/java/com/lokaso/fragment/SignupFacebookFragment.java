package com.lokaso.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.lokaso.R;
import com.lokaso.activity.BaseActivity2;
import com.lokaso.activity.RegisterFacebookActivity;
import com.lokaso.activity.SelectInterestActivity;
import com.lokaso.activity.SplashActivity;
import com.lokaso.activity.SuggestionActivity;
import com.lokaso.activity.TermsConditionsActivity;
import com.lokaso.adapter.ProfessionAdapter;
import com.lokaso.dao.DaoController;
import com.lokaso.gcm.RegistrationIntentService;
import com.lokaso.model.AddInterest;
import com.lokaso.model.FbUser;
import com.lokaso.model.Interest;
import com.lokaso.model.LoginResponse;
import com.lokaso.model.Profession;
import com.lokaso.model.Profile;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroLogin;
import com.lokaso.retromodel.RetroProfession;
import com.lokaso.util.AlertDialogList;
import com.lokaso.util.AppUtil;
import com.lokaso.util.Constant;
import com.lokaso.util.DeviceUtil;
import com.lokaso.util.Helper;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyLog;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Androcid on 06-Aug-16.
 */
public class SignupFacebookFragment extends Fragment {

    private String TAG = SignupFacebookFragment.class.getSimpleName();
    private Context context;

    private String errorMsg = "";
    private double tvLatitude = 0, tvLongitude = 0;

    private LinearLayout container;
    private TextView tvLocation, tvProfession, bTnC;
    private EditText tvFName, tvLName, tvEmail, tvPassword, tvAboutMe;
    private EditText tvReferCode;
    private TextView tvReferCodeTitle;
    //    private Spinner spProfession;
    private Button bSignup;
    private List<Profession> professionList = new ArrayList<>();
    private ProfessionAdapter professionAdapter;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered, checkLocation = false;
    private NetworkConnection networkConnection;
    private AlertDialog.Builder builder;
    private RelativeLayout progress_layout;

    private AlertDialogList alertDialogList;

    private FbUser fbUser;
    private boolean is_update_user;
    private Profile profile;

    private int profession_id = 0;
    private String facebook_id = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        if(bundle!=null) {
            fbUser = (FbUser) bundle.getSerializable(Constant.USER);
            is_update_user = bundle.getBoolean(Constant.IS_UPDATE, false);
            profile = (Profile) bundle.getSerializable(Constant.PROFILE);
        }

        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        builder = new AlertDialog.Builder(context);
        networkConnection = new NetworkConnection(context);

        professionList = new ArrayList<>();
        professionAdapter = new ProfessionAdapter(context, professionList);

        professionList = DaoController.getAllProfession(context);

        if (professionList.size() > 0) {
            professionAdapter.refresh(professionList);

        }else {
            getProfessions();
        }

        container = (LinearLayout) view.findViewById(R.id.container);
        progress_layout = (RelativeLayout) view.findViewById(R.id.progress_layout);
        tvLocation = (TextView) view.findViewById(R.id.tvLocation);
        tvFName = (EditText) view.findViewById(R.id.tvFName);
        tvLName = (EditText) view.findViewById(R.id.tvLName);
        tvEmail = (EditText) view.findViewById(R.id.tvEmail);
        tvPassword = (EditText) view.findViewById(R.id.tvPassword);
        tvAboutMe = (EditText) view.findViewById(R.id.tvAboutMe);
        tvReferCodeTitle = (TextView) view.findViewById(R.id.tvReferCodeTitle);
        tvReferCode = (EditText) view.findViewById(R.id.tvReferCode);
        tvProfession = (TextView) view.findViewById(R.id.tvProfession);
        bTnC = (TextView) view.findViewById(R.id.bTnC);
        bSignup = (Button) view.findViewById(R.id.bSignup);

        if(profile!=null) {

            String fname = profile.getFname();
            String lname = profile.getLname();
            String email = profile.getEmail();
            String location = profile.getLocation();
            tvLatitude = profile.getCurrent_lat();
            tvLongitude = profile.getCurrent_lng();
            String about_me = profile.getAbout_me();
            String profession = profile.getProfession();
            profession_id = profile.getProfession_id();

            tvFName.setText(fname);
            tvLName.setText(lname);
            tvEmail.setText(email);
            tvLocation.setText(location);
            tvAboutMe.setText(about_me);
            tvProfession.setText(profession);
        }

        if(fbUser!=null) {
            String fname = fbUser.getFirst_name();
            String lname = fbUser.getLast_name();
            String name = fbUser.getFirst_name() + " " + fbUser.getLast_name();
            String email = fbUser.getEmail();
            facebook_id = fbUser.getId();
            String image = Constant.FACEBOOK_URL + facebook_id + Constant.FACEBOOK_IMAGE;

            if(!is_update_user) {
                tvFName.setText(fname);
                tvLName.setText(lname);
                tvEmail.setText(email);
            }
            else {
                if(tvEmail.getText().toString().length()==0) {
                    tvEmail.setText(email);
                }
            }
        }

        if(is_update_user) {
            tvReferCodeTitle.setVisibility(View.GONE);
            tvReferCode.setVisibility(View.GONE);
            tvPassword.setVisibility(View.GONE);
        }
        else {
            tvReferCodeTitle.setVisibility(View.VISIBLE);
            tvReferCode.setVisibility(View.VISIBLE);
            tvPassword.setVisibility(View.VISIBLE);
        }

        setFonts();


        alertDialogList = new AlertDialogList(context);
        alertDialogList.setOnClickListener(new AlertDialogList.OnItemClickListener() {
            @Override
            public void onItemClick(Object object) {

                Profession profession = (Profession) object;

                tvProfession.setText(profession.getName());
                profession_id = (int) profession.getId();
            }
        });

        tvProfession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogList.show(professionList);

//
//                new AlertDialog.Builder(context)
//                        .setTitle(getString(R.string.select_profession))
//                        .setAdapter(professionAdapter, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                tvProfession.setText(professionAdapter.getItem(which).getName());
//                                profession_id = professionAdapter.getItem(which).getId();
//                            }
//                        })
//                        .show();
            }
        });

        RegisterFacebookActivity mainActivity = (RegisterFacebookActivity) getActivity();
        mainActivity.setOnLocationChangeListener(new RegisterFacebookActivity.LocationListener() {
            @Override
            public void onChange(String place, double latitude, double longitude) {
                if (!place.isEmpty()) {
                    tvLocation.setText(place);
                    tvLongitude = longitude;
                    tvLatitude = latitude;
                }
                checkLocation = false;
            }
        });

        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkLocation) {
                    checkLocation = true;
                    int PLACE_PICKER_REQUEST = Constant.LOCATION_SELECTION;
                    PlaceAutocomplete.IntentBuilder builder = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN);
                    try {
                        getActivity().startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                        checkLocation = false;
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                        checkLocation = false;
                    }
                }
            }
        });

        bTnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TermsConditionsActivity.class);
                startActivity(intent);
            }
        });

        bSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fname        = tvFName.getText().toString().trim();
                String lname        = tvLName.getText().toString().trim();
                String email        = tvEmail.getText().toString().trim();
                String pass         = tvPassword.getText().toString().trim();
                String image        = RegisterFacebookActivity.imagePic;
                String about        = tvAboutMe.getText().toString().trim();
                String location     = tvLocation.getText().toString().trim();
                String profession   = tvProfession.getText().toString().trim();
                String referCode    = tvReferCode.getText().toString().trim();

                if (verifyRegForm()) {
                    progress_layout.setVisibility(View.VISIBLE);

                    int user_id = 0;
                    if(is_update_user) {
                        if(profile!=null) {
                            user_id = profile.getId();
                        }
                    }

                    String device_id = DeviceUtil.getDeviceId(context);
                    String device_os = DeviceUtil.getDeviceOs(context);
                    String device_name = DeviceUtil.getDeviceFullname(context);
                    String device_os_version = DeviceUtil.getDeviceAndroidVersion(context);
                    int app_version_code = AppUtil.getVersionCode(context);
                    String app_version_name = AppUtil.getVersionName(context);

                    String name = fname + " " + lname;
                    RestClient.getLokasoApi().signup(user_id, name, fname, lname, email, pass, image
                            , 0, 0, about, profession_id, location, tvLatitude, tvLongitude,
                            getActivity().getString(R.string.provider_facebook), facebook_id, 0, 0, referCode,
                            device_id, device_os, device_name, device_os_version, app_version_code, app_version_name,
                            new Callback<RetroLogin>() {
                                @Override
                                public void success(RetroLogin retroLogin, Response response) {
                                    progress_layout.setVisibility(View.GONE);
                                    if (retroLogin.getSuccess()) {

                                        savePreference(retroLogin);

                                        List<LoginResponse> loginResponses = retroLogin.getDetails();

                                        List<AddInterest> userInterestList = null;

                                        if(loginResponses!=null && loginResponses.size()>0) {
                                            LoginResponse loginResponse = loginResponses.get(0);

                                            Profile profile = loginResponse.getProfile();
                                            userInterestList = profile.getInterestList();
                                        }

                                        if(userInterestList!=null && userInterestList.size()>0) {

                                            Intent intent = new Intent(context, SuggestionActivity.class);
                                            startActivity(intent);
                                            getActivity().finish();
                                        }
                                        else {
                                            Intent intent = new Intent(context, SelectInterestActivity.class);
                                            startActivity(intent);
                                            getActivity().finish();
                                        }
                                    } else {
                                        MyToast.tshort(context, "Sign up Failed : " + retroLogin.getMessage());
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
//                                    MyToast.tshort(context, "" + error);
                                    progress_layout.setVisibility(View.GONE);
                                }
                            });
                } else {
                    MyToast.tshort(context, errorMsg + "");
                }
            }
        });
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
                        professionAdapter.refresh(professionList);

                    } else {
                        professionList = new ArrayList<>();
                        MyToast.tshort(context, retroProfession.getMessage() + "");
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    professionList = new ArrayList<>();
//                    MyToast.tshort(context, "" + error);
                }
            });

        } else {
            MyToast.tshort(context, "Please check your Internet connection");
        }
    }

    /**
     * method used to set fonts
     */
    private void setFonts() {

        MyFont1 myFont = new MyFont1(context);
        myFont.setAppFont(container);

    }

    /**
     * method used to verify form
     *
     * @return
     */
    private boolean verifyRegForm() {
        String fname = tvFName.getText().toString().trim();
        String lname = tvLName.getText().toString().trim();
        String email = tvEmail.getText().toString().trim();
        String pass = tvPassword.getText().toString().trim();
        String image = RegisterFacebookActivity.imagePic;
        String location = tvLocation.getText().toString().trim();
        String profession = tvProfession.getText().toString().trim();


        if (!networkConnection.isNetworkAvailable()) {
            errorMsg = getString(R.string.check_network);
            return false;
        }

        if (fname.isEmpty()) {
            errorMsg = "First name field is empty";
            return false;
        }

        if (fname.length() < 3) {
            errorMsg = "First name field should be greater than 2 characters";
            return false;
        }

        if (lname.isEmpty()) {
            errorMsg = "Last name field is empty";
            return false;
        }
//
//        if (lname.length() < 3) {
//            errorMsg = "Last name field should be greater than 2 characters";
//            return false;
//        }

        if (email.length() == 0) {
            errorMsg = "Email field is empty";
            return false;
        }

        if (!Helper.isValidEmail(email)) {
            errorMsg = "Email id is invalid";
            return false;
        }
/*
        if (pass.length() == 0) {
            errorMsg = "Password field is empty";
            return false;
        }

        if (pass.length() < 6) {
            errorMsg = "Minimum 6 characters required for password";
            return false;
        }

        if (profession.isEmpty()) {
            errorMsg = "Select a Profession";
            return false;
        }
*/
        if (location.isEmpty()) {
            errorMsg = "Location field is empty";
            return false;
        }

        if (image.isEmpty()) {
            errorMsg = "Choose profile picture";
            return false;
        }

        return true;
    }

    /**
     * method used to check Play services
     *
     * @return
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(getActivity(), resultCode, Constant.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                MyLog.e(TAG, "This device is not supported.");
                getActivity().finish();
            }
            return false;
        }
        return true;
    }

    /**
     * method used to save preferences
     *
     * @param retroLogin
     */
    private void savePreference(RetroLogin retroLogin) {

        List<LoginResponse> loginResponses = retroLogin.getDetails();

        if(loginResponses!=null && loginResponses.size()>0) {
            LoginResponse loginResponse = loginResponses.get(0);

            Profile profile = loginResponse.getProfile();

            BaseActivity2.setUserPreference(context, profile);
            MyPreferencesManager.saveLoginState(context, true);

            MyPreferencesManager.saveLocationModeSelected(context, true);
            MyPreferencesManager.saveLocationModeManual(context, true);
            MyPreferencesManager.saveLat(context, profile.getCurrent_lat());
            MyPreferencesManager.saveLng(context, profile.getCurrent_lng());
            MyPreferencesManager.saveLocationSelected(context, profile.getLocation());

        }

        registerBroadcastReceiver();

        if (checkPlayServices()) {
            Intent intent = new Intent(context, RegistrationIntentService.class);
            getActivity().startService(intent);
        }
    }

    /**
     * method used to register broadcast receiver
     */
    private void registerBroadcastReceiver() {
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
    }
}