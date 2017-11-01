package com.lokaso.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lokaso.R;
import com.lokaso.adapter.SuggestionAdapter;
import com.lokaso.model.Profile;
import com.lokaso.model.Suggestion;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroResponse;
import com.lokaso.retromodel.RetroSuggestion;
import com.lokaso.util.Constant;
import com.lokaso.util.MyFont;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyLog;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;
import com.lokaso.util.Picaso;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SavedDiscoveriesActivity extends AppCompatActivity {

    private String TAG = SavedDiscoveriesActivity.class.getSimpleName();
    private Context context = SavedDiscoveriesActivity.this;

    private NetworkConnection networkConnection;
    private TextView tvError;
    private RecyclerView recyclerView;
    private List<Suggestion> suggestionList = new ArrayList<>();
    private SuggestionAdapter suggestionAdapter;

    private RelativeLayout progress_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_discoveries);

        networkConnection = new NetworkConnection(context);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvError = (TextView) findViewById(R.id.tvError);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        progress_layout = (RelativeLayout) findViewById(R.id.progress_layout);

        if (toolbar != null) {
            toolbar.setTitle(getString(R.string.saved_discoveries));
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        setAdapter();

        new MyFont1(context).setAppFont((ViewGroup)findViewById(R.id.container));
    }

    @Override
    protected void onStart() {
        super.onStart();
        getUserDiscovery();
    }

    /**
     * set users suggestion adapter
     */
    private void setAdapter() {
        suggestionAdapter = new SuggestionAdapter(SavedDiscoveriesActivity.this, suggestionList);
        recyclerView.setAdapter(suggestionAdapter);

        suggestionAdapter.setOnClickListener(new SuggestionAdapter.ClickInterface() {
            @Override
            public void onItemClick(Suggestion suggestion) {
            }

            @Override
            public void onLocationClick(double lat, double lng, String location) {
            }

            @Override
            public void onSaveDiscoveryClick(int action, int discovery_id, int user_id, int position) {
                saveUserDiscovery(action, discovery_id, user_id, position);
            }

            @Override
            public void onFavClick(int action, int discovery_id, int user_id, int position) {

            }

            @Override
            public void onCommentClick(Suggestion suggestion) {
            }

            @Override
            public void onSpamClick(int action, int suggestion_id, int position) {

            }

            @Override
            public void onUserClick(final Profile profile, final boolean status, final int leader, final int follower, final int position) {
            }

            @Override
            public void onEditClick(Suggestion suggestion) {
            }

            @Override
            public void onShare(final Suggestion suggestion, final int position) {
            }

            @Override
            public void onShareComplete(Suggestion suggestion, int position) {
            }

            @Override
            public void onLikeCountClick(Suggestion suggestion, int position) {
            }

            @Override
            public void onCommentCountClick(Suggestion suggestion, int position) {
            }
        });
    }

    /**
     * method used to save user discovery
     *
     * @param action
     * @param discovery_id
     * @param user_id
     * @param position
     */
    private void saveUserDiscovery(final int action, int discovery_id, int user_id, final int position) {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().userDiscovery(action, discovery_id, user_id, new Callback<RetroResponse>() {
                @Override
                public void success(RetroResponse retroResponse, Response response) {
                    MyToast.tshort(context, retroResponse.getMessage() + "");

                    if(retroResponse.getSuccess()) {
                        if (action == 1) {
//                        suggestionList.get(position).setUser_discovery(true);

                        } else {
//                        suggestionList.get(position).setUser_discovery(false);
                            suggestionList.remove(position);

                            if (suggestionList.size() == 0) {
                                tvError.setText(R.string.suggestions_not_found);
                                tvError.setVisibility(View.VISIBLE);
                            } else {
                                tvError.setVisibility(View.GONE);
                            }
                        }
                        suggestionAdapter.refresh(suggestionList);
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
    }

    /**
     * method used to fetch user discovery
     */
    private void getUserDiscovery() {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().getUserDiscovery(MyPreferencesManager.getId(context), new Callback<RetroSuggestion>() {
                @Override
                public void success(RetroSuggestion retroSuggestion, Response response) {
                    if (retroSuggestion.getSuccess()) {
                        suggestionList = retroSuggestion.getDetails();
                        setAdapter();
                        tvError.setVisibility(View.GONE);

                    } else {
                        tvError.setText(R.string.suggestions_not_found);
                        tvError.setVisibility(View.VISIBLE);
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
    }
}
