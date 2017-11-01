package com.lokaso.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lokaso.R;
import com.lokaso.adapter.CreditsAdapter;
import com.lokaso.model.Credits;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroCredits;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CreditsActivity extends AppCompatActivity {

    private String TAG = CreditsActivity.class.getSimpleName();
    private Context context = CreditsActivity.this;

    private RecyclerView recyclerView;
    private CreditsAdapter creditsAdapter;
    private List<Credits> creditsList = new ArrayList<>();
    private NetworkConnection networkConnection;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        builder = new AlertDialog.Builder(context);
        networkConnection = new NetworkConnection(context);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        if (toolbar != null) {
            toolbar.setTitle(R.string.view_credits);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.hasFixedSize();
        setAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getUserCredits();
    }

    /**
     * Method used to Fetch User Credits using API call
     */
    private void getUserCredits() {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().getUserCredits(MyPreferencesManager.getId(context), new Callback<RetroCredits>() {
                @Override
                public void success(RetroCredits retroCredits, Response response) {
                    if (retroCredits.getSuccess()) {
                        creditsList = retroCredits.getDetails();
                        setAdapter();

                    } else {
                        MyToast.tshort(context, retroCredits.getMessage() + "");
                    }
                }

                @Override
                public void failure(RetrofitError error) {
//                    MyToast.tshort(context, "" + error);
                }
            });
        } else {
            MyToast.tshort(context, getString(R.string.check_network));
        }
    }

    /**
     * set credit adapter
     */
    private void setAdapter() {
        creditsAdapter = new CreditsAdapter(context, creditsList);
        recyclerView.setAdapter(creditsAdapter);
    }
}
