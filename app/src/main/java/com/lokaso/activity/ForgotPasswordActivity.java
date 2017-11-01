package com.lokaso.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.lokaso.R;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroProfile;
import com.lokaso.retromodel.RetroResponse;
import com.lokaso.util.Constant;
import com.lokaso.util.Helper;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyInputMethodManager;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private String TAG = ForgotPasswordActivity.class.getSimpleName(), errorMsg = "", email = "";
    private Context context = ForgotPasswordActivity.this;

    private EditText tvEmail, tvOtp, tvPassword, tvConfirmPassword;
    private LinearLayout layoutForgot, layoutChange;
    private Button bChange, bSignin;
    private NetworkConnection networkConnection;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        getWindow().setBackgroundDrawableResource(R.mipmap.background_login);
        builder = new AlertDialog.Builder(context);
        networkConnection = new NetworkConnection(context);
        tvEmail = (EditText) findViewById(R.id.tvEmail);
        layoutForgot = (LinearLayout) findViewById(R.id.layoutForgot);
        tvOtp = (EditText) findViewById(R.id.tvOtp);
        tvPassword = (EditText) findViewById(R.id.tvPassword);
        tvConfirmPassword = (EditText) findViewById(R.id.tvConfirmPassword);
        layoutChange = (LinearLayout) findViewById(R.id.layoutChange);
        bChange = (Button) findViewById(R.id.bChange);
        bSignin = (Button) findViewById(R.id.bSignin);

        setTypeface();
    }

    /**
     * method used to set fonts
     */
    private void setTypeface() {

        new MyFont1(context).setAppFont((ViewGroup) findViewById(R.id.container));

    }

    /**
     * method used on click of Forgot button
     *
     * @param view : Button view
     */
    public void onForgotClick(View view) {
        email = tvEmail.getText().toString().trim();

        if (verifyLoginForm(email)) {
            String otp = Helper.randomAlphaNumeric(Constant.OTP_COUNT);
            String from_name = Constant.APP_NAME;
            String from_email = Constant.APP_EMAIL;
            String to_name = email;
            String to_email = email;
            String subject = Constant.APP_EMAIL_FORGOT_PASSWORD_SUBJECT;

            String body = "Dear " + to_name + ",\n" +
                    "You have requested to change your password.\n" +
                    "Please use this OTP code \"" + otp + "\" to change your account password.\n" +
                    "The Lokaso Team.";

            checkUser(from_name, from_email, to_name, to_email, subject, body);

            MyPreferencesManager.saveOTP(context, otp);

        } else {
            MyToast.tshort(context, errorMsg + "");
        }
    }

    /**
     * Checking if user exists
     *
     * @param from_name  : Name of the user who is sending the email
     * @param from_email : Email id of the user who is sending the email
     * @param to_name    : Name of the user to whom email is being sent
     * @param to_email   : Email id of the user to whom email is being sent
     * @param subject    : Subject of the email being sent
     * @param body       : OTP will be sent in the body of the email
     */
    private void checkUser(final String from_name, final String from_email, final String to_name, final String to_email, final String subject, final String body) {
        RestClient.getLokasoApi().checkUser(email, new Callback<RetroResponse>() {
            @Override
            public void success(RetroResponse retroResponse, Response response) {
//                MyToast.tshort(context, retroResponse.getMessage());
                if (retroResponse.getSuccess()) {
                    sendEmail(from_name, from_email, to_name, to_email, subject, body);
                    layoutForgot.setVisibility(View.GONE);
                    layoutChange.setVisibility(View.VISIBLE);

                } else {
                    MyToast.tshort(context, retroResponse.getMessage());
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    /**
     * method used to send email using API call
     *
     * @param from_name  : Name of the user who is sending the email
     * @param from_email : Email id of the user who is sending the email
     * @param to_name    : Name of the user to whom email is being sent
     * @param to_email   : Email id of the user to whom email is being sent
     * @param subject    : Subject of the email being sent
     * @param body       : OTP will be sent in the body of the email
     */
    private void sendEmail(String from_name, String from_email, String to_name, String to_email, String subject, String body) {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoEmailApi().sendEmail(from_name, from_email, to_name, to_email, subject, body, new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    MyToast.tshort(context, "OTP has been sent to your Email id");
                }

                @Override
                public void failure(RetrofitError error) {
//                    MyToast.tshort(context, errorMsg + "");
                }
            });
        } else {
            MyToast.tshort(context, getString(R.string.check_network));
        }
    }

    /**
     * method used to validate form
     *
     * @param email
     * @return
     */
    private boolean verifyLoginForm(String email) {
        if (email.isEmpty()) {
            errorMsg = "Email field is empty";
            return false;
        }

        if (!Helper.isValidEmail(email)) {
            errorMsg = "Email id is invalid";
            return false;
        }

        return true;
    }


    /**
     * method used on click of Change password button
     *
     * @param view : Button view
     */
    public void onChangeClick(final View view) {
        String otp = tvOtp.getText().toString().trim();
        String password = tvPassword.getText().toString().trim();
        String confirm_password = tvConfirmPassword.getText().toString().trim();

        if (verifyChangeForm(otp, password, confirm_password)) {
            if (networkConnection.isNetworkAvailable()) {
                RestClient.getLokasoApi().forgotPassword(email, password, new Callback<RetroProfile>() {
                    @Override
                    public void success(RetroProfile retroProfile, Response response) {
                        MyToast.tshort(context, retroProfile.getMessage() + "");
                        if (retroProfile.getSuccess()) {
                            new MyInputMethodManager(context, view);
                            finish();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        MyToast.tshort(context, "" + error);
                    }
                });
            } else {
                MyToast.tshort(context, getString(R.string.check_network));
            }

        } else {
            MyToast.tshort(context, errorMsg + "");
        }
    }

    /**
     * method used to validate form
     *
     * @param otp
     * @param password
     * @param confirm_password
     * @return
     */
    private boolean verifyChangeForm(String otp, String password, String confirm_password) {
        if (otp.isEmpty()) {
            errorMsg = "OTP field is empty";
            return false;
        }

        if (otp.length() < 5) {
            errorMsg = "Minimum 5 characters required for OTP field";
            return false;
        }

        if (password.isEmpty()) {
            errorMsg = "Password field is empty";
            return false;
        }

        if (password.length() < 6) {
            errorMsg = "Minimum 6 characters required for Password field";
            return false;
        }

        if (confirm_password.isEmpty()) {
            errorMsg = "Confirm password field is empty";
            return false;
        }

        if (confirm_password.length() < 6) {
            errorMsg = "Minimum 6 characters required for Confirm password field";
            return false;
        }

        if (!password.equals(confirm_password)) {
            errorMsg = "Passwords do not match";
            return false;
        }

        if (!otp.equals(MyPreferencesManager.getOTP(context))) {
            errorMsg = "Entered OTP is invalid";
            return false;
        }

        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyPreferencesManager.saveOTP(context, "");
    }

    @Override
    public void onBackPressed() {
        if (layoutForgot.getVisibility() == View.VISIBLE) {
            MyPreferencesManager.saveOTP(context, "");
            super.onBackPressed();

        } else {
            layoutForgot.setVisibility(View.VISIBLE);
            layoutChange.setVisibility(View.GONE);
            tvOtp.setText("");
            tvPassword.setText("");
            tvConfirmPassword.setText("");
            MyPreferencesManager.saveOTP(context, "");
        }
    }
}
