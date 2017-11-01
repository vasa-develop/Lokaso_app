package com.lokaso.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.lokaso.R;
import com.lokaso.adapter.InterestAdapter;
import com.lokaso.dao.DaoFunctions;
import com.lokaso.model.AddInterest;
import com.lokaso.model.Interest;
import com.lokaso.model.Profile;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroAddInterest;
import com.lokaso.retromodel.RetroInterest;
import com.lokaso.retromodel.RetroResponse;
import com.lokaso.util.Constant;
import com.lokaso.util.Helper;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyLog;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;
import com.lokaso.util.ScreenSize;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SelectInterestActivity extends AppCompatActivity {

    private String TAG = SelectInterestActivity.class.getSimpleName();
    private Context context = SelectInterestActivity.this;

    private RecyclerView recyclerView;
    private List<Interest> interestList = new ArrayList<>();
    private List<String> interestIdList = new ArrayList<>();
    private InterestAdapter interestAdapter;
    private NetworkConnection networkConnection;
    private AlertDialog.Builder builder;
    private RelativeLayout progress_layout;

    private ScreenSize screenSize;

    public static final int MODE_ADD = 0;
    public static final int MODE_EDIT = 1;

    private int mode = MODE_ADD;
    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interest_list_layout);

        builder = new AlertDialog.Builder(context);
        networkConnection = new NetworkConnection(context);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        progress_layout = (RelativeLayout) findViewById(R.id.progress_layout);

        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        recyclerView.hasFixedSize();

        if(getIntent().hasExtra(Constant.MODE)) {
            mode = getIntent().getIntExtra(Constant.MODE, MODE_ADD);

            if(mode == MODE_EDIT) {
                if(toolbar!=null) {
                    toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    });
                }
            }
        }

        if(getIntent().hasExtra(Constant.PROFILE)) {
            profile = (Profile) getIntent().getSerializableExtra(Constant.PROFILE);

        }

        new MyFont1(context).setAppFont((ViewGroup) findViewById(R.id.container));

        screenSize = new ScreenSize(context);
        int width = (int)screenSize.getScreenWidthPixel();

        interestAdapter = new InterestAdapter(context, interestList, width);
        recyclerView.setAdapter(interestAdapter);

        interestAdapter.setInterestListener(new InterestAdapter.InterestListener() {

            @Override
            public void onCheckChannge(int position, boolean isChecked) {

                if(interestList!=null && position<interestList.size()) {
                    interestList.get(position).setChecked(isChecked);
                    interestAdapter.refresh(interestList);
                }
            }

//
//            @Override
//            public void add(int id) {
//                interestIdList.add(id + "");
//            }
//
//            @Override
//            public void remove(int id) {
//                interestIdList.remove(id + "");
//            }
        });


        DaoFunctions daoFunctions = new DaoFunctions(context);
        interestList = daoFunctions.getAllInterest();
        daoFunctions.close();

        if (interestList != null) {

            if (interestList.size() == 0) {
                getInterests();
            }
            else {
                setData();
            }
        } else {
            getInterests();
        }
    }

    /**
     * method used to get interests
     */
    private void getInterests() {
        if (networkConnection.isNetworkAvailable()) {
            progress_layout.setVisibility(View.VISIBLE);

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

                        setData();

                    } else {
                        interestList = new ArrayList<>();
//                        MyToast.tshort(context, retroInterest.getMessage() + "");
                    }
                    progress_layout.setVisibility(View.GONE);
                }

                @Override
                public void failure(RetrofitError error) {
                    interestList = new ArrayList<>();
                    MyToast.tshort(context, Constant.ERROR);
                    progress_layout.setVisibility(View.GONE);
                }
            });

        } else {
            MyToast.tshort(context, "Please check your internet connection");
        }
    }

    private void setData() {

        MyLog.e(TAG, "mode : " + mode);

        if(mode==MODE_EDIT) {

            if(profile!=null) {
                MyLog.e(TAG, "mode : " + mode + " , profile : "+profile.getInterestList());

                List<AddInterest> myInterest = new ArrayList<>();

                if (profile.getInterestList() != null) {
                    MyLog.e(TAG, "interest : " + profile.getInterestList());

                    myInterest.addAll(profile.getInterestList());
                    MyLog.e(TAG, "interest size: " + myInterest.size());
                }

                for (int i = 0; i < myInterest.size(); i++) {
                    int interestId = myInterest.get(i).getInterest_id();
                    List<Interest> allInterests = interestList;
                    for (int x = 0; x < allInterests.size(); x++) {
                        if (allInterests.get(x).getId() == interestId) {
                            interestList.get(x).setChecked(true);
                            break;
                        }
                    }
                }
            }
        }
        interestAdapter.refresh(interestList);
    }

    /**
     * method used on click of Continue button
     *
     * @param view Button view
     */
    public void onContinueClick(View view) {

        if(interestList!=null && interestList.size()>0) {

            String interestid = "";
            boolean atleastOneChecked = false;
            for (int i = 0; i < interestList.size(); i++) {
                if(interestList.get(i).isChecked()) {
                    atleastOneChecked = true;
                    interestid += "" + interestList.get(i).getId() + ",";
                }
            }
            if(atleastOneChecked) {
                interestid = Helper.removeLastComma(interestid);

                if (networkConnection.isNetworkAvailable()) {
                    progress_layout.setVisibility(View.VISIBLE);
                    RestClient.getLokasoApi().addInterests(MyPreferencesManager.getId(context), interestid,
                            new Callback<RetroAddInterest>() {
                                @Override
                                public void success(RetroAddInterest retroAddInterest, Response response) {
                                    progress_layout.setVisibility(View.GONE);
                                    if (retroAddInterest.getSuccess()) {

                                        List<AddInterest> myInterest = retroAddInterest.getDetails();

                                        List<String> interestList = new ArrayList<>();
                                        for (int i = 0; i < myInterest.size(); i++) {
                                            interestList.add(""+myInterest.get(i).getInterest_id());
                                        }

                                        Set<String> mySet = new HashSet<>(interestList);
                                        MyPreferencesManager.saveUserInterestList(context, mySet);


                                        MyToast.tshort(context, "Interests saved");

                                        if(mode==MODE_EDIT) {
                                            if(EditProfileActivity.profile!=null)
                                                EditProfileActivity.profile.setInterestList(myInterest);
                                            finish();
                                        }
                                        else {
                                            getDialog();
                                        }

                                    } else {
                                        MyToast.tshort(context, retroAddInterest.getMessage());
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    MyToast.tshort(context, error);
                                    progress_layout.setVisibility(View.GONE);
                                }
                            });
                } else {
                    MyToast.tshort(context, getString(R.string.check_network));
                }

            } else {
                MyToast.tshort(context, "Please select at least one interest");
            }
        } else {
            MyToast.tshort(context, "There are no interests");
        }

/*
        if (interestIdList.size() > 0) {
            String interestid = "";
            for (int i = 0; i < interestIdList.size(); i++) {
                if (i == 0) {
                    interestid = interestIdList.get(i);
                } else {
                    interestid = interestid + "," + interestIdList.get(i);
                }
            }

            if (networkConnection.isNetworkAvailable()) {
                progress_layout.setVisibility(View.VISIBLE);
                RestClient.getLokasoApi().addInterests(MyPreferencesManager.getId(context), interestid,
                        new Callback<RetroAddInterest>() {
                            @Override
                            public void success(RetroAddInterest retroAddInterest, Response response) {
                                if (retroAddInterest.getSuccess()) {

                                    List<AddInterest> myInterest = retroAddInterest.getDetails();

                                    List<String> interestList = new ArrayList<>();
                                    for (int i = 0; i < myInterest.size(); i++) {
                                        interestList.add(""+myInterest.get(i).getInterest_id());
                                    }

                                    Set<String> mySet = new HashSet<>(interestList);
                                    MyPreferencesManager.saveUserInterestList(context, mySet);

                                    MyToast.tshort(context, "Interests saved");
                                } else {
                                    MyToast.tshort(context, retroAddInterest.getMessage());
                                }

                                progress_layout.setVisibility(View.GONE);
                                getDialog();
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                MyToast.tshort(context, Constant.ERROR);
                                progress_layout.setVisibility(View.GONE);
//                        getDialog();
                            }
                        });
            } else {
                MyToast.tshort(context, getString(R.string.check_network));
            }

        } else {
            MyToast.tshort(context, "Please select at least one interest");
        }
        */
    }

    /**
     * method used to get dialog
     */
    private void getDialog() {
        new AlertDialog.Builder(context)
                .setCancelable(true)
                .setTitle(getString(R.string.lokaso_notifications))
                .setMessage(getString(R.string.ask_for_notifications))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setNotificationFlag();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveNext();
                    }
                })
                .show();
    }

    /**
     * method used to set notification flag
     */
    private void setNotificationFlag() {
        if (networkConnection.isNetworkAvailable()) {
            progress_layout.setVisibility(View.VISIBLE);
            RestClient.getLokasoApi().setNotificationFlag(MyPreferencesManager.getId(context), 1, new Callback<RetroResponse>() {
                @Override
                public void success(RetroResponse retroResponse, Response response) {
                    if (retroResponse.getSuccess()) {
                        moveNext();
                    } else {
                        MyToast.tshort(context, retroResponse.getMessage() + "");
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    MyToast.tshort(context, Constant.ERROR);
                }
            });
        } else {
            MyToast.tshort(context, getString(R.string.check_network));
        }
    }

    /**
     * method used to move to next intent
     */
    private void moveNext() {
        progress_layout.setVisibility(View.GONE);
        Intent intent = new Intent(context, SuggestionActivity.class);
        startActivity(intent);
        finish();
    }
}
