package com.lokaso.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lokaso.R;
import com.lokaso.adapter.UserListAdapter;
import com.lokaso.model.Profile;
import com.lokaso.model.Suggestion;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroProfile;
import com.lokaso.util.Constant;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SuggestionLikeListActivity extends AppCompatActivity {

    private String TAG = SuggestionLikeListActivity.class.getSimpleName();
    private Context context = SuggestionLikeListActivity.this;

    private RecyclerView recyclerView;
    private UserListAdapter adapter;
    private List<Profile> list;
    private NetworkConnection networkConnection;
    private AlertDialog.Builder builder;

    private TextView tvError;
    private ImageView tvErrorImage;
    private CardView tvErrorLayout;

    private int page = 0;

    private Suggestion suggestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        builder = new AlertDialog.Builder(context);
        networkConnection = new NetworkConnection(context);

        try {
            suggestion = (Suggestion) getIntent().getSerializableExtra(Constant.SUGGESTION);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        tvError = (TextView) findViewById(R.id.tvError);
        tvErrorImage = (ImageView) findViewById(R.id.tvErrorImage);
        tvErrorLayout = (CardView) findViewById(R.id.tvErrorLayout);

        if (toolbar != null) {
            toolbar.setTitle(R.string.activity_tip_like_user);
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

        list = new ArrayList<>();
        adapter = new UserListAdapter(context, list);
        recyclerView.setAdapter(adapter);

        adapter.setOnClickListener(new UserListAdapter.ClickInterface() {
            @Override
            public void onItemClick(int folks_id) {

            }

            @Override
            public void onFollowClick(int action, int leader, int follower, int position) {

            }
        });

        new MyFont1(context).setAppFont((ViewGroup)findViewById(R.id.container));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSuggestionLikeList();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



    /**
     * method used to get users who have liked tip using API call
     *
     */
    private void getSuggestionLikeList() {
        if (page == 0) {
            //showProgress(false);

        } else {
            if (networkConnection.isNetworkAvailable()) {
                //showProgress(true);
            }
        }

        RestClient.getLokasoApi().getSuggestionLikeList(
                MyPreferencesManager.getId(context),
                suggestion.getId(),
                page,
                new Callback<RetroProfile>() {
                    @Override
                    public void success(RetroProfile retroResponse, Response response) {
                        if (retroResponse.getSuccess()) {
                            if (page == 0) {
                                list = new ArrayList<>();
                            }

                            tvError.setVisibility(View.GONE);
                            list.addAll(retroResponse.getDetails());
                            adapter.refresh(list);

                        } else {
                            if (list.size() == 0) {
                                tvError.setText(R.string.folks_not_found);
                                tvError.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        MyToast.tshort(context, "" + error);
                    }
                });
    }
}
