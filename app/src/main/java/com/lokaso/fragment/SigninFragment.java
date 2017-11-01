package com.lokaso.fragment;

import android.content.Context;
import android.content.DialogInterface;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lokaso.R;
import com.lokaso.activity.BaseActivity2;
import com.lokaso.activity.ForgotPasswordActivity;
import com.lokaso.activity.RegisterFacebookActivity;
import com.lokaso.activity.SelectInterestActivity;
import com.lokaso.activity.SuggestionActivity;
import com.lokaso.activity.TermsConditionsActivity;
import com.lokaso.gcm.MyFirebaseMessagingService;
import com.lokaso.model.FbUser;
import com.lokaso.model.Interest;
import com.lokaso.model.LoginResponse;
import com.lokaso.model.Profile;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroLogin;
import com.lokaso.util.AppUtil;
import com.lokaso.util.Constant;
import com.lokaso.util.DeviceUtil;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyLog;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Androcid on 06-Aug-16.
 */
public class SigninFragment extends Fragment {

    private String TAG = SigninFragment.class.getSimpleName(), location = "", errorMsg = "";
    private Context context;

    private LoginButton facebookButton;
    public static CallbackManager callbackManager;
    private EditText tvEmail, tvPassword;
    private Button bSignin;
    private NetworkConnection networkConnection;
    private List<String> permissions;
    private FbUser fbUser;
    private TextView bForgot, bTnC, tvOr;
    private RelativeLayout bFacebook;
    private LinearLayout container;
    private boolean check = false;
    private AlertDialog.Builder builder;
    private RelativeLayout progress_layout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signin, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();
        builder = new AlertDialog.Builder(context);
        if (AccessToken.getCurrentAccessToken() != null) LoginManager.getInstance().logOut();
        networkConnection = new NetworkConnection(getActivity());
        callbackManager = CallbackManager.Factory.create();
        container = (LinearLayout) view.findViewById(R.id.container);
        facebookButton = (LoginButton) view.findViewById(R.id.login_button);
        tvEmail = (EditText) view.findViewById(R.id.tvEmail);
        tvPassword = (EditText) view.findViewById(R.id.tvPassword);
        bSignin = (Button) view.findViewById(R.id.bSignin);
        bFacebook = (RelativeLayout) view.findViewById(R.id.bFacebook);
        bForgot = (TextView) view.findViewById(R.id.bForgot);
        bTnC = (TextView) view.findViewById(R.id.bTnC);
        tvOr = (TextView) view.findViewById(R.id.tvOr);
        progress_layout = (RelativeLayout) view.findViewById(R.id.progress_layout);

        setFonts();

        bForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        bFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (networkConnection.isNetworkAvailable()) {
                    permissions = new ArrayList<>();
                    permissions.add(Constant.PARAM_USER_FRIENDS);
                    permissions.add(Constant.PARAM_EMAIL);
                    permissions.add(Constant.PARAM_PUBLIC_PROFILE);
                    permissions.add(Constant.PARAM_USER_BIRTHDAY);
                    permissions.add(Constant.PARAM_USER_LOCATION);
                    LoginManager.getInstance().logInWithReadPermissions(getActivity(), permissions);

                } else {
                    try {
                        new AlertDialog.Builder(context)
                                .setTitle(getString(R.string.network_error))
                                .setMessage(getString(R.string.no_internet_connection))
                                .setPositiveButton(getString(R.string.retry), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (networkConnection.isNetworkAvailable()) {
                                            permissions = new ArrayList<>();
                                            permissions.add(Constant.PARAM_USER_FRIENDS);
                                            permissions.add(Constant.PARAM_EMAIL);
                                            LoginManager.getInstance().logInWithReadPermissions(getActivity(), permissions);
                                        }
                                    }
                                })
                                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
//                                        getActivity().finish();
                                    }
                                })
                                .show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (networkConnection.isNetworkAvailable()) {
                    permissions = new ArrayList<>();
                    permissions.add(Constant.PARAM_USER_FRIENDS);
                    permissions.add(Constant.PARAM_EMAIL);
                    permissions.add(Constant.PARAM_PUBLIC_PROFILE);
                    permissions.add(Constant.PARAM_USER_BIRTHDAY);
                    permissions.add(Constant.PARAM_USER_LOCATION);
                    LoginManager.getInstance().logInWithReadPermissions(getActivity(), permissions);

                } else {
                    try {
                        new AlertDialog.Builder(context)
                                .setTitle(getString(R.string.network_error))
                                .setMessage(getString(R.string.no_internet_connection))
                                .setPositiveButton(getString(R.string.retry), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (networkConnection.isNetworkAvailable()) {
                                            permissions = new ArrayList<>();
                                            permissions.add(Constant.PARAM_USER_FRIENDS);
                                            permissions.add(Constant.PARAM_EMAIL);
                                            LoginManager.getInstance().logInWithReadPermissions(getActivity(), permissions);
                                        }
                                    }
                                })
                                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
//                                        getActivity().finish();
                                    }
                                })
                                .show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AccessToken.getCurrentAccessToken().getPermissions();
                        MyLog.e(TAG, "The access token is as follow as in the logging " +
                                AccessToken.getCurrentAccessToken().getPermissions());
                        MyLog.e(TAG, "LOGGED IN to facebook successfully... the access token as " + loginResult.getAccessToken());
                        getFacebookLoginDetails(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {

                        progress_layout.setVisibility(View.GONE);
                        // App code
                        new AlertDialog.Builder(context)
                                .setTitle(R.string.cancelled)
                                .setMessage(R.string.permission_not_granted)
                                .setPositiveButton(R.string.ok, null)
                                .show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        progress_layout.setVisibility(View.GONE);
                        // App code
                        if (exception instanceof FacebookOperationCanceledException ||
                                exception instanceof FacebookAuthorizationException) {

                            new AlertDialog.Builder(context)
                                    .setTitle(R.string.fb_error_title)
                                    .setMessage(exception.getMessage())
                                    .setPositiveButton(R.string.ok, null)
                                    .show();

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

        bSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = tvEmail.getText().toString().trim();
                String pass = tvPassword.getText().toString().trim();

                if (verifyLoginForm()) {
                    if (networkConnection.isNetworkAvailable()) {
                        progress_layout.setVisibility(View.VISIBLE);

                        int user_id = MyPreferencesManager.getId(context);
                        String device_id = DeviceUtil.getDeviceId(context);
                        String device_os = DeviceUtil.getDeviceOs(context);
                        String device_name = DeviceUtil.getDeviceFullname(context);
                        String device_os_version = DeviceUtil.getDeviceAndroidVersion(context);
                        int app_version_code = AppUtil.getVersionCode(context);
                        String app_version_name = AppUtil.getVersionName(context);

                        RestClient.getLokasoApi().login(email, pass,
                                device_id, device_os, device_name, device_os_version, app_version_code, app_version_name,
                                new Callback<RetroLogin>() {
                            @Override
                            public void success(RetroLogin retroLogin, Response response) {
                                if (retroLogin.getSuccess()) {
                                    moveNext(retroLogin, context.getString(R.string.normal));

                                } else {
                                    MyToast.tshort(context, "Sign in error : " + retroLogin.getMessage());
                                }
                                progress_layout.setVisibility(View.GONE);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                MyToast.tshort(context, "" + error);
                                progress_layout.setVisibility(View.GONE);
                            }
                        });

                    } else {
                        MyToast.tshort(context, getString(R.string.check_network));
                    }

                } else {
                    MyToast.tshort(context, errorMsg + "");
                }
            }
        });
    }

    /**
     * method used to set fonts
     */
    private void setFonts() {
        MyFont1 myFont = new MyFont1(context);
        myFont.setAppFont(container);
    }

    /**
     * method used to validate form
     *
     * @return
     */
    private boolean verifyLoginForm() {
        String email = tvEmail.getText().toString().trim();
        String pass = tvPassword.getText().toString().trim();

        if (email.length() == 0) {
            errorMsg = "Email field is empty";
            return false;
        }
//
//        if (!Helper.isValidEmail(email)) {
//            errorMsg = "Email id is invalid";
//            return false;
//        }

        if (pass.length() == 0) {
            errorMsg = "Password field is empty";
            return false;
        }

//        if (pass.length() < 6) {
//            errorMsg = "Minimum 6 characters required for password";
//            return false;
//        }

        return true;
    }

    /**
     * method used to get facebook login details
     *
     * @param accessToken
     */
    private void getFacebookLoginDetails(final AccessToken accessToken) {
        progress_layout.setVisibility(View.VISIBLE);

        GraphRequest request = GraphRequest.newMeRequest(accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        try {
                            MyLog.e(TAG, "fb response : " + object.toString());
                            Gson gson = new Gson();
                            String st_location = null;
                            try {
                                st_location = object.get(Constant.PARAM_LOCATION).toString();
                                JSONObject jsonObject = new JSONObject(st_location);
                                location = jsonObject.get(Constant.PARAM_NAME).toString();
                                MyLog.e(TAG, "fb location : " + location);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            fbUser = gson.fromJson(String.valueOf(object), FbUser.class);

                            getFacebookProfile(accessToken.getToken());

                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                            progress_layout.setVisibility(View.GONE);
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString(Constant.PARAM_FIELDS, "id, email, first_name, last_name, gender, birthday, location");
        request.setParameters(parameters);
        request.executeAsync();
    }

    /**
     * method used to get facebook profile
     *
     * @param accessToken
     */
    String name, email, image, facebook_id;
    double latitude = 0, longitude = 0;
    private void getFacebookProfile(String accessToken) {


        name = fbUser.getFirst_name() + " " + fbUser.getLast_name();
        email = fbUser.getEmail();
        facebook_id = fbUser.getId();
        image = Constant.FACEBOOK_URL + facebook_id + Constant.FACEBOOK_IMAGE;

        try {
            LoginManager.getInstance().logOut();
        } catch (Exception e) {
            e.printStackTrace();
        }

        signupFbCheck(name, email, image, facebook_id, latitude, longitude);

        if(true)
            return;
        /*
        if(email==null) email = "";

        if(email.length()==0) {
            final View view = LayoutInflater.from(context).inflate(R.layout.alert_label_editor, null);
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setView(view);
            alertDialog.setTitle(getString(R.string.dialog_email_title));

            final EditText tvComment = (EditText) view.findViewById(R.id.tvComment);
            tvComment.setHint(""+getString(R.string.dialog_email_hint));

            alertDialog.setPositiveButton(getString(R.string.Edit), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String response = tvComment.getText().toString().trim();
                    if (!response.isEmpty()) {
                        email = response;
                        signupFb(name, email, image, facebook_id, latitude, longitude);
                    } else {
                        MyToast.tshort(context, "Please enter you email address");
                    }
                    new MyInputMethodManager(context, view);
                }
            });

            alertDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new MyInputMethodManager(context, view);
                }
            });

            alertDialog.show();
        }
        else {
            signupFb(name, email, image, facebook_id, latitude, longitude);
        }
*/
    }

    private void signupFbCheck(String name, String email, String image, String facebook_id, double latitude, double longitude) {

        if (networkConnection.isNetworkAvailable()) {

            int user_id = MyPreferencesManager.getId(context);
            String device_id = DeviceUtil.getDeviceId(context);
            String device_os = DeviceUtil.getDeviceOs(context);
            String device_name = DeviceUtil.getDeviceFullname(context);
            String device_os_version = DeviceUtil.getDeviceAndroidVersion(context);
            int app_version_code = AppUtil.getVersionCode(context);
            String app_version_name = AppUtil.getVersionName(context);

            RestClient.getLokasoApi().signupFbCheck(name, email, "", image, 0, 0, "", 0, location, latitude, longitude, Constant.FACEBOOK,
                    facebook_id, 0, 0,
                    device_id, device_os, device_name, device_os_version, app_version_code, app_version_name,
                    new Callback<RetroLogin>() {
                        @Override
                        public void success(RetroLogin retroLogin, Response response) {
                            progress_layout.setVisibility(View.GONE);
                            if (retroLogin.getSuccess()) {
                                moveNextCheck(retroLogin, context.getString(R.string.facebook));
                            } else {
                                MyToast.tshort(context, retroLogin.getMessage() + "");
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            progress_layout.setVisibility(View.GONE);
                            MyToast.tshort(context, "" + error);
                        }
                    });
        } else {
            progress_layout.setVisibility(View.GONE);
            MyToast.tshort(context, getString(R.string.check_network));
        }
    }

    private void signupFb(String name, String email, String image, String facebook_id, double latitude, double longitude) {

        if (networkConnection.isNetworkAvailable()) {

            int user_id = MyPreferencesManager.getId(context);
            String device_id = DeviceUtil.getDeviceId(context);
            String device_os = DeviceUtil.getDeviceOs(context);
            String device_name = DeviceUtil.getDeviceFullname(context);
            String device_os_version = DeviceUtil.getDeviceAndroidVersion(context);
            int app_version_code = AppUtil.getVersionCode(context);
            String app_version_name = AppUtil.getVersionName(context);

            RestClient.getLokasoApi().signupFb(name, email, "", image, 0, 0, "", 0, location, latitude, longitude, Constant.FACEBOOK,
                    facebook_id, 0, 0,
                    device_id, device_os, device_name, device_os_version, app_version_code, app_version_name,
                    new Callback<RetroLogin>() {
                        @Override
                        public void success(RetroLogin retroLogin, Response response) {
                            progress_layout.setVisibility(View.GONE);
                            if (retroLogin.getSuccess()) {
                                moveNext(retroLogin, context.getString(R.string.facebook));
                            } else {
                                MyToast.tshort(context, retroLogin.getMessage() + "");
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            progress_layout.setVisibility(View.GONE);
                            MyToast.tshort(context, "" + error);
                        }
                    });
        } else {
            progress_layout.setVisibility(View.GONE);
            MyToast.tshort(context, getString(R.string.check_network));
        }
    }

    private void emailIdEnter() {

    }

    /**
     * method used to move to next intent
     *
     * @param retroLogin
     * @param type
     */
    private void moveNextCheck(RetroLogin retroLogin, String type) {

        if (type.equals(context.getString(R.string.facebook))) {
            if(retroLogin.is_new_user()) {

                boolean is_user_update = retroLogin.is_new_user_update();
                if(is_user_update) {

                    List<LoginResponse> loginResponses = retroLogin.getDetails();

                    if(loginResponses!=null && loginResponses.size()>0) {
                        LoginResponse loginResponse = loginResponses.get(0);

                        Profile profile = loginResponse.getProfile();

                        Intent intent = new Intent(context, RegisterFacebookActivity.class);
                        intent.putExtra(Constant.USER, fbUser);
                        intent.putExtra(Constant.IS_UPDATE, is_user_update);
                        intent.putExtra(Constant.PROFILE, profile);
                        startActivity(intent);
                    }
                    else {
                        MyToast.dshort(context, "Failed");
                    }
                }
                else {

                    Profile profile = null;

                    Intent intent = new Intent(context, RegisterFacebookActivity.class);
                    intent.putExtra(Constant.USER, fbUser);
                    intent.putExtra(Constant.IS_UPDATE, is_user_update);
                    intent.putExtra(Constant.PROFILE, profile);
                    startActivity(intent);

                }

            }
            // Login
            else {
                if (!check) {
                    check = true;
                    savePreference(retroLogin);

                    Intent intent = new Intent(context, SuggestionActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        }
    }


    /**
     * method used to move to next intent
     *
     * @param retroLogin
     * @param type
     */
    private void moveNext(RetroLogin retroLogin, String type) {
        savePreference(retroLogin);

        if (type.equals(context.getString(R.string.facebook)) && retroLogin.getMessage().equals(context.getString(R.string.facebook_new_user_saved))) {

            // Show First chat notification
            MyFirebaseMessagingService.sendNotificationFirstChat(context);

            Intent intent = new Intent(context, SelectInterestActivity.class);
            startActivity(intent);
            getActivity().finish();

        } else {
            if (!check) {
                check = true;
                Intent intent = new Intent(context, SuggestionActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        }
    }

    /**
     * method used to save Preferences
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

            /*
            MyPreferencesManager.saveId(context, profile.getId());
            MyPreferencesManager.saveName(context, profile.getName());
            MyPreferencesManager.saveImage(context, profile.getImage());
            MyPreferencesManager.saveReferCode(context, profile.getRefer_code());

            MyPreferencesManager.setUserLatitude(context, profile.getCurrent_lat());
            MyPreferencesManager.saveReferCode(context, profile.getRefer_code());

            MyLog.e(TAG, "id : " + profile.getId());
            MyLog.e(TAG, "name : " + profile.getName());
            MyLog.e(TAG, "image : " + profile.getImage());
            MyLog.e(TAG, "refer_code : " + profile.getRefer_code());
            MyPreferencesManager.saveLoginState(context, true);
            */
        }
    }
}