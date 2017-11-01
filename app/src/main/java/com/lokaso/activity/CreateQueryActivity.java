package com.lokaso.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.lokaso.R;
import com.lokaso.adapter.InterestArrayAdapter;
import com.lokaso.dao.DaoFunctions;
import com.lokaso.model.Interest;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroInterest;
import com.lokaso.retromodel.RetroResponse;
import com.lokaso.util.Constant;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyInputMethodManager;
import com.lokaso.util.MyLog;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CreateQueryActivity extends AppCompatActivity {

    private String TAG = CreateQueryActivity.class.getSimpleName(), errorMsg = "";
    private Context context = CreateQueryActivity.this;

    private double lat = 0, lng = 0;
    private EditText tvQuery;
    private TextView tvLocation, tvInterest, tvTime, tvInterestHint, tvLocationHint, tvQueryHint, tvTimeHint;
    private int interest_id = 0;
    private List<Interest> interestList = new ArrayList<>();
    private Button bPost;
    private InterestArrayAdapter interestArrayAdapter;
    private String[] duration = new String[]{"30 mins", "2 hours", "4 hours", "12 hours", "1 day", "3 days", "7 days"};
    private NetworkConnection networkConnection;
    private AlertDialog.Builder builder;
    private boolean checkLocation = false;




    private RelativeLayout progress_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_query);

        builder = new AlertDialog.Builder(context);
        networkConnection = new NetworkConnection(context);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvQuery = (EditText) findViewById(R.id.tvQuery);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        tvInterest = (TextView) findViewById(R.id.tvInterest);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvInterestHint = (TextView) findViewById(R.id.tvInterestHint);
        tvLocationHint = (TextView) findViewById(R.id.tvLocationHint);
        tvQueryHint = (TextView) findViewById(R.id.tvQueryHint);
        tvTimeHint = (TextView) findViewById(R.id.tvTimeHint);
        bPost = (Button) findViewById(R.id.bPost);

        progress_layout = (RelativeLayout) findViewById(R.id.progress_layout);


        setFont();

        if (toolbar != null) {
            toolbar.setTitle(R.string.post_query);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkLocation) {

                    checkLocation = true;
                    int PLACE_PICKER_REQUEST = Constant.LOCATION_SELECTION;

                    //
                    PlaceAutocomplete.IntentBuilder builder = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN);
                    try {
                        startActivityForResult(builder.build(CreateQueryActivity.this), PLACE_PICKER_REQUEST);
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

        DaoFunctions daoFunctions = new DaoFunctions(context);
        interestList = daoFunctions.getAllInterest();
        daoFunctions.close();

        if (interestList != null) {
            if (interestList.size() == 0){
                getInterests();
            }
            interestArrayAdapter = new InterestArrayAdapter(context, interestList);
        }
        else {
            getInterests();
        }

        final ArrayAdapter<String> spdurationAdapter = new ArrayAdapter<>(this, R.layout.layout_profession, R.id.tv, duration);
        spdurationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        tvInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new android.support.v7.app.AlertDialog.Builder(context)
                        .setTitle(getString(R.string.select_interest))
                        .setAdapter(interestArrayAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tvInterest.setText(interestArrayAdapter.getItem(which).getName());
                                interest_id = (int)interestArrayAdapter.getItem(which).getId();
                            }
                        })
                        .show();
            }
        });

        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle(getString(R.string.select_validity))
                        .setAdapter(spdurationAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tvTime.setText(spdurationAdapter.getItem(which));
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        //overridePendingTransition(R.anim.close_enter_animate, R.anim.close_exit_animate);

        showConfirmationDialog();
    }

    private void showConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.dialog_title_on_cancel));
        builder.setMessage(getString(R.string.dialog_message_on_cancel));
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                exit();
            }
        });
        builder.setNegativeButton("No", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void exit() {
        super.onBackPressed();
    }

    /**
     * method used to get interests
     */
    private void getInterests() {
        if (networkConnection.isNetworkAvailable()) {

            DaoFunctions daoFunctions = new DaoFunctions(context);
            String updated_date = daoFunctions.getLatestUpdateInterestDate();
            daoFunctions.close();

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

                        interestArrayAdapter = new InterestArrayAdapter(context, interestList);
                    } else {
                        interestList = new ArrayList<>();
                        MyToast.tshort(context, retroInterest.getMessage() + "");
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    interestList = new ArrayList<>();
                    MyToast.tshort(context, "" + error);
                }
            });
        }
    }

    /**
     * method used to set fonts
     */
    private void setFont() {
        MyFont1 myFont = new MyFont1(context);
        myFont.setAppFont((ViewGroup) findViewById(R.id.container));
    }

    /**
     * method used on click od Post Query
     * @param view Button view
     */
    public void onPostQueryClick(View view) {
        String interest = tvInterest.getText().toString().trim();
        String query = tvQuery.getText().toString().trim();
        String location = tvLocation.getText().toString().trim();
        String time = tvTime.getText().toString().trim();

        Calendar date = Calendar.getInstance();
        long t = date.getTimeInMillis();
        Date dateUntil = null;

        String sendingDuration = "";
        try {
            String[] splitDuration = time.split(" ");
            int durationInt = Integer.parseInt(splitDuration[0]);

            if (splitDuration[1].equalsIgnoreCase(Constant.HOURS)) {
                sendingDuration = String.valueOf(60 * durationInt);
                dateUntil = new Date(t + (60 * durationInt * Constant.ONE_MINUTE_IN_MILLIS));

            } else if (splitDuration[1].contains(Constant.DAY)) {
                sendingDuration = String.valueOf(60 * 24 * durationInt);
                dateUntil = new Date(t + (60 * 24 * durationInt * Constant.ONE_MINUTE_IN_MILLIS));

            } else {
                sendingDuration = splitDuration[0];
                dateUntil = new Date(t + (durationInt * Constant.ONE_MINUTE_IN_MILLIS));
            }

            DateFormat dateFormat = new SimpleDateFormat(Constant.DATE_FORMAT, Locale.ENGLISH);
            sendingDuration = dateFormat.format(dateUntil);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (validateForm(interest, query, location, time)) {
            new MyInputMethodManager(context, view);
            MyLog.e(TAG, sendingDuration + "");
            if (networkConnection.isNetworkAvailable()) {

                progress_layout.setVisibility(View.VISIBLE);
                bPost.setEnabled(false);

                RestClient.getLokasoApi().postQuery(MyPreferencesManager.getId(context), query, sendingDuration, lat, lng,
                        location, interest_id, new Callback<RetroResponse>() {
                            @Override
                            public void success(RetroResponse retroResponse, Response response) {

                                progress_layout.setVisibility(View.GONE);
                                bPost.setEnabled(true);
                                if(retroResponse!=null) {

                                    String message = retroResponse.getMessage();
                                    if(retroResponse.getSuccess()) {

                                        MyToast.tshort(context, message + "");
                                        finish();
                                    }
                                    else {
                                        MyToast.tshort(context, "" + message);
                                    }
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                bPost.setEnabled(true);
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

    /**
     * method used to validate form
     * @param interest
     * @param query
     * @param location
     * @param time
     * @return
     */
    private boolean validateForm(String interest, String query, String location, String time) {
        if (interest.isEmpty()) {
            errorMsg = "Select interest field";
            return false;
        }

        if (query.isEmpty()) {
            errorMsg = "Query field is empty";
            return false;
        }

        if (query.length() > 140) {
            errorMsg = "Query field exceeds 140 character limit";
            return false;
        }

        if (location.isEmpty()) {
            errorMsg = "location field is empty";
            return false;
        }

        if (time.isEmpty()) {
            errorMsg = "Select time field";
            return false;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            // Original
            case Constant.LOCATION_SELECTION:
                checkLocation = false;
                if (resultCode == RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(this, data);
                    MyLog.e(TAG, "Place : " + place.getName());
                    if (place.getName() != null) {
                        tvLocation.setText(place.getName());
                        lat = place.getLatLng().latitude;
                        lng = place.getLatLng().longitude;

                        MyToast.tdebug(context, lat+" : "+lng);


                    } else {
                        MyToast.tshort(context, "Please select location");
                    }
                }
                break;
            default:
                break;
        }
    }
}
