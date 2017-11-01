package com.lokaso.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.lokaso.R;
import com.lokaso.adapter.FilterInterestListAdapter;
import com.lokaso.dao.DaoFunctions;
import com.lokaso.model.AddInterest;
import com.lokaso.model.Interest;
import com.lokaso.model.Profile;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroInterest;
import com.lokaso.util.Constant;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyLog;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FilterActivity extends AppCompatActivity {

    private String TAG = FilterActivity.class.getSimpleName();
    private Context context = FilterActivity.this;

    private RecyclerView recyclerView;
    private FilterInterestListAdapter adapter;
    private List<Interest> interestList;
    private NetworkConnection networkConnection;
    private AlertDialog.Builder builder;

    private Profile profile = null;

    private LinearLayout sortLatestLayout, sortDistanceLatest, sortExpiringSoonLatest;
    private Button applyButton;

    public static final int TYPE_SUGGESTION = 1;
    public static final int TYPE_QUERY = 2;
    private int type = TYPE_SUGGESTION;

    private int checkFilter = Constant.FILTER_BY_LATEST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        builder = new AlertDialog.Builder(context);
        networkConnection = new NetworkConnection(context);

        if(getIntent().hasExtra(Constant.PROFILE)) {
            type = getIntent().getIntExtra(Constant.TYPE, TYPE_SUGGESTION);
            profile = (Profile) getIntent().getSerializableExtra(Constant.PROFILE);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        sortLatestLayout = (LinearLayout) findViewById(R.id.sortLatestLayout);
        sortDistanceLatest = (LinearLayout) findViewById(R.id.sortDistanceLatest);
        sortExpiringSoonLatest = (LinearLayout) findViewById(R.id.sortExpiringSoonLatest);

        applyButton = (Button) findViewById(R.id.applyButton);

        if (toolbar != null) {
            toolbar.setTitle(R.string.activity_filter);
            toolbar.setNavigationIcon(R.drawable.ic_cancel);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.hasFixedSize();

        interestList = new ArrayList<>();
        adapter = new FilterInterestListAdapter(context, interestList);
        recyclerView.setAdapter(adapter);

        adapter.setOnClickListener(new FilterInterestListAdapter.ClickInterface() {
            @Override
            public void onItemClick(int position, boolean isChecked) {
                interestList.get(position).setChecked(isChecked);
            }
        });
        sortLatestLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFilter = Constant.FILTER_BY_LATEST;
                setFilterBy(checkFilter);
            }
        });

        sortDistanceLatest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFilter = Constant.FILTER_BY_DISTANCE;
                setFilterBy(checkFilter);
            }
        });

        sortExpiringSoonLatest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFilter = Constant.FILTER_BY_EXPIRING;
                setFilterBy(checkFilter);
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String interest_ids = "";

                List<String> interestIdList = new ArrayList<>();

                if(interestList!=null && interestList.size()>0) {
                    for (Interest interest : interestList) {
                        if(interest.isChecked()) {
                            int interestId = (int)interest.getId();
                            interest_ids = interest_ids + interestId + ",";
                            interestIdList.add(""+interestId);
                        }
                    }
                }

                MyLog.e(TAG, "interest_ids:"+interest_ids+" ,checkFilter:"+checkFilter);

                MyPreferencesManager.saveUserFilterInterestList(context, interestIdList);

                // save filter sort by

                int filterBy = checkFilter;
                if(type==TYPE_QUERY) {
                    MyPreferencesManager.setQueryFilter(context, filterBy);
                }
                else {
                    MyPreferencesManager.setSuggestionFilter(context, filterBy);
                }

                Intent intent = new Intent();
                intent.putExtra(Constant.FILTER_SORT_BY, checkFilter);
                intent.putExtra(Constant.FILTER_INTEREST, interest_ids);
                setResult(Constant.SELECTION_FILTER, intent);
                finish();
            }
        });

        if(type==TYPE_QUERY) {
            sortExpiringSoonLatest.setVisibility(View.VISIBLE);

            if(MyPreferencesManager.getQueryFilter(context)==0) {
                MyPreferencesManager.setQueryFilter(context, Constant.FILTER_BY_LATEST);
            }
            checkFilter = MyPreferencesManager.getQueryFilter(context);
            setFilterBy(checkFilter);
        }
        else {
            sortExpiringSoonLatest.setVisibility(View.GONE);

            if(MyPreferencesManager.getSuggestionFilter(context)==0) {
                MyPreferencesManager.setSuggestionFilter(context, Constant.FILTER_BY_LATEST);
            }

            checkFilter = MyPreferencesManager.getSuggestionFilter(context);
            setFilterBy(checkFilter);
        }

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

        new MyFont1(context).setAppFont((ViewGroup)findViewById(R.id.container));
    }

    private void setFilterBy(int filterBy) {

        if(type==TYPE_QUERY) {
            sortDistanceLatest.setSelected(filterBy == Constant.FILTER_BY_DISTANCE);
            sortLatestLayout.setSelected(filterBy == Constant.FILTER_BY_LATEST);
            sortExpiringSoonLatest.setSelected(filterBy == Constant.FILTER_BY_EXPIRING);
        }
        else {
            sortDistanceLatest.setSelected(filterBy == Constant.FILTER_BY_DISTANCE);
            sortLatestLayout.setSelected(filterBy == Constant.FILTER_BY_LATEST);
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
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    interestList = new ArrayList<>();
                    MyToast.tshort(context, Constant.ERROR);
                }
            });

        } else {
            MyToast.tshort(context, "Please check your internet connection");
        }
    }

    private void setData() {

        List<String> interestIdList = MyPreferencesManager.getUserFilterInterestList(context);
        if(interestIdList.size()>0) {

            for (int i = 0; i < interestIdList.size(); i++) {
                int interestId = Integer.parseInt(interestIdList.get(i));
                List<Interest> allInterests = interestList;
                for (int x = 0; x < allInterests.size(); x++) {
                    if (allInterests.get(x).getId() == interestId) {
                        interestList.get(x).setChecked(true);
                        break;
                    }
                }
            }
        }
        else {

            if (profile != null) {
                MyLog.e(TAG, " , profile : " + profile.getInterestList());

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
        //interestList.get(1).setChecked(true);

        adapter.refresh(interestList);


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //overridePendingTransition(R.anim.close_enter_animate, R.anim.close_exit_animate);
    }

}
