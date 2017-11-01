package com.lokaso.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lokaso.R;
import com.lokaso.activity.ViewQueryActivity;
import com.lokaso.adapter.QueriesAdapter;
import com.lokaso.model.Queries;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroQueries;
import com.lokaso.retromodel.RetroResponse;
import com.lokaso.util.Constant;
import com.lokaso.util.MyFont1;
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
public class QueryFragment extends Fragment {

    private String TAG = QueryFragment.class.getSimpleName();
    private Context context;

    private RecyclerView recyclerView;
    private QueriesAdapter queriesAdapter;
    private List<Queries> queriesList = new ArrayList<>();
    private int profile_id;
    private NetworkConnection networkConnection;
    private AlertDialog.Builder builder;
    private TextView tvError;
    private ImageView tvErrorImage;
    private CardView tvErrorLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        profile_id = getArguments().getInt(Constant.PROFILE);
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();
        builder = new AlertDialog.Builder(context);
        networkConnection = new NetworkConnection(context);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        tvError = (TextView) view.findViewById(R.id.tvError);
        tvErrorImage = (ImageView) view.findViewById(R.id.tvErrorImage);
        tvErrorLayout = (CardView) view.findViewById(R.id.tvErrorLayout);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.hasFixedSize();


        new MyFont1(context).setAppFont((ViewGroup) view.findViewById(R.id.container));

        getQueriesData(MyPreferencesManager.getLat(context), MyPreferencesManager.getLng(context));
    }

    /**
     * method used to fetch queries
     */
    private void getQueriesData(double lat, double lng) {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().getUserQueries(lat, lng, profile_id,
                    new Callback<RetroQueries>() {
                        @Override
                        public void success(RetroQueries retroQueries, Response response) {
                            if (retroQueries.getSuccess()) {
                                queriesList = retroQueries.getDetails();
                                setQueriesAdapter();
                                tvErrorLayout.setVisibility(View.GONE);

                            } else {
//                                MyToast.tshort(context, retroQueries.getMessage() + "");
                                tvErrorLayout.setVisibility(View.VISIBLE);
                                tvError.setText(R.string.queries_not_found);
                                tvErrorImage.setImageResource(R.drawable.ic_holder_query);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
//                            MyToast.tshort(context, "" + error);
                        }
                    });

            //Fetching the location of the users
        } else {
            MyToast.tshort(context, getString(R.string.check_network));
        }
    }

    /**
     * method used to set queries adapter
     */
    private void setQueriesAdapter() {
        queriesAdapter = new QueriesAdapter(context, queriesList);
        recyclerView.setAdapter(queriesAdapter);

        queriesAdapter.setOnClickListener(new QueriesAdapter.ClickInterface() {
            @Override
            public void itemClick(Queries queries) {
                Intent intent = new Intent(context, ViewQueryActivity.class);
                intent.putExtra(Constant.INTENT_QUERIES, queries);
                startActivity(intent);
//                getActivity().overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);
            }

            @Override
            public void followQuery(int action, int query_id) {
                setfollowQuery(action, query_id);
            }
        });
    }

    /**
     * method used to follow query
     *
     * @param action
     * @param query_id
     */
    private void setfollowQuery(int action, int query_id) {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().followQuery(action, query_id, MyPreferencesManager.getId(context), new Callback<RetroResponse>() {
                @Override
                public void success(RetroResponse retroResponse, Response response) {
                    MyToast.tshort(context, retroResponse.getMessage() + "");
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
}