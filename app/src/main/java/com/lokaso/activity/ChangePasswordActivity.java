package com.lokaso.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.lokaso.R;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroProfile;
import com.lokaso.util.Helper;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private String TAG = ChangePasswordActivity.class.getSimpleName(), errorMsg = "";
    private Context context = ChangePasswordActivity.this;

    private EditText tvEmail, tvCurrentPassword, tvNewPassword;
    private NetworkConnection networkConnection;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        builder = new AlertDialog.Builder(context);
        networkConnection = new NetworkConnection(context);
        tvEmail = (EditText) findViewById(R.id.tvEmail);
        tvCurrentPassword = (EditText) findViewById(R.id.tvCurrentPassword);
        tvNewPassword = (EditText) findViewById(R.id.tvNewPassword);
    }

    /**
     * Click Listener for Change Password Button
     * @param view : Button view
     */
    public void onChangePasswordClick(View view) {
        String email = tvEmail.getText().toString().trim();
        String current_password = tvCurrentPassword.getText().toString().trim();
        String new_password = tvNewPassword.getText().toString().trim();

        if (verifyLoginForm(email, current_password, new_password)) {
            changePassword(email, current_password, new_password);
        } else {
            MyToast.tshort(context, errorMsg + "");
        }
    }

    /**
     * Method contains API call to changePassword
     * @param email : Email id
     * @param current_password : Current Password
     * @param new_password : New Password
     */
    private void changePassword(String email, String current_password, String new_password) {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().changePassword(email, current_password, new_password, new Callback<RetroProfile>() {
                @Override
                public void success(RetroProfile retroProfile, Response response) {
                    MyToast.tshort(context, retroProfile.getMessage() + "");
                }

                @Override
                public void failure(RetrofitError error) {
                    MyToast.tshort(context, "" + error);
                }
            });
        } else {
            MyToast.tshort(context, getString(R.string.check_network));
        }
    }

    /**
     * Method validates the form
     * @param email : Email ID
     * @param current_password : Current Password
     * @param new_password : New Password
     * @return : Return true if Form Validation is correct else return false
     */
    private boolean verifyLoginForm(String email, String current_password, String new_password) {
        if (email.isEmpty()) {
            errorMsg = "Email field is empty";
            return false;
        }

        if (!Helper.isValidEmail(email)) {
            errorMsg = "Email id is invalid";
            return false;
        }

        if (current_password.isEmpty()) {
            errorMsg = "Current password field is empty";
            return false;
        }

        if (current_password.length() < 6) {
            errorMsg = "Minimum 6 characters required for Confirm password field";
            return false;
        }

        if (new_password.isEmpty()) {
            errorMsg = "New password field is empty";
            return false;
        }

        if (new_password.length() < 6) {
            errorMsg = "Minimum 6 characters required for New password field";
            return false;
        }

        return true;
    }
}
