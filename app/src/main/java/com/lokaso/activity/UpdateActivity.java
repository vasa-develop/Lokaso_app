package com.lokaso.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lokaso.R;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.preference.SettingPreference;
import com.lokaso.preference.WalkthroughPreference;
import com.lokaso.util.MyFont1;

public class UpdateActivity extends AppCompatActivity {

    private String TAG = UpdateActivity.class.getSimpleName();
    private Context context = UpdateActivity.this;

    private TextView skipButton;
    private TextView updateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        skipButton = (TextView) findViewById(R.id.skipButton);
        updateTextView = (TextView) findViewById(R.id.updateTextView);

        if(SettingPreference.isForceUpdateAvailable(context)) {
            skipButton.setVisibility(View.GONE);
            updateTextView.setText(getString(R.string.app_update_message_force));
        }
        else {
            skipButton.setVisibility(View.VISIBLE);
            updateTextView.setText(getString(R.string.app_update_message_normal));
        }

        SettingPreference.setUpdateSeen(context, true);

        new MyFont1(context).setAppFont((ViewGroup)findViewById(R.id.container));
    }

    public void onUpdateClick(View view) {


        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + context.getPackageName()
                    )));

        } catch (android.content.ActivityNotFoundException e) {
            e.printStackTrace();
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName()
                    )));
        }
        catch (Exception e) {
            e.printStackTrace();
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName()
                    )));
        }

    }

    public void onSkipClick(View view) {

        // If walkthrough is not seen, take him to walkthrough screen
        if (!WalkthroughPreference.isWalkthroughSeen(context)) {

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
}