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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.lokaso.R;
import com.lokaso.adapter.SuggestionCommentAdapter;
import com.lokaso.model.DiscoveryComment;
import com.lokaso.model.Profile;
import com.lokaso.model.Suggestion;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroDiscoveryComment;
import com.lokaso.retromodel.RetroResponse;
import com.lokaso.util.Constant;
import com.lokaso.util.Helper;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyInputMethodManager;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;
import com.lokaso.util.Picaso;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SuggestionCommentActivity extends AppCompatActivity {

    private String TAG = SuggestionCommentActivity.class.getSimpleName();
    private Context context = SuggestionCommentActivity.this;

    private ImageView ivDiscovery;
    private Suggestion suggestion;
    private RecyclerView recyclerView;
    private SuggestionCommentAdapter discoveryCommentAdapter;
    private List<DiscoveryComment> discoveryCommentList = new ArrayList<>();
    private EditText tvComment;
    private ImageButton bSend;
    private NetworkConnection networkConnection;
    private AlertDialog.Builder builder;
    private TextView tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery_comment);

        builder = new AlertDialog.Builder(context);
        networkConnection = new NetworkConnection(context);

        try {
            suggestion = (Suggestion) getIntent().getSerializableExtra(Constant.SUGGESTION);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ivDiscovery = (ImageView) findViewById(R.id.ivDiscovery);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        tvComment = (EditText) findViewById(R.id.tvComment);
        bSend = (ImageButton) findViewById(R.id.bSend);
        tvError = (TextView) findViewById(R.id.tvError);


        if (toolbar != null) {
            toolbar.setTitle(R.string.discovery_comment);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        String imageUrl = suggestion.getImage();
        Picaso.loadSuggestion(context, imageUrl, ivDiscovery);


        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.hasFixedSize();
        setAdapter();

        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = tvComment.getText().toString().trim();
                if (comment.isEmpty()) {
                    MyToast.tshort(context, "Comment cannot be empty");

                } else {
                    if (networkConnection.isNetworkAvailable()) {
                        discoveryComment(comment, suggestion.getId(), MyPreferencesManager.getId(context));
                        tvComment.setText("");
                        new MyInputMethodManager(context, v);

                    } else {
                        MyToast.tshort(context, getString(R.string.check_network));
                    }
                }
            }
        });


        new MyFont1(context).setAppFont((ViewGroup) findViewById(R.id.root));
    }

    @Override
    protected void onStart() {
        super.onStart();
        suggestion = (Suggestion) getIntent().getSerializableExtra(Constant.SUGGESTION);
        getComments(suggestion.getId());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //ViewSuggestionActivity.suggestion.setComment_count(discoveryCommentList.size());
        //overridePendingTransition(R.anim.close_enter_animate, R.anim.close_exit_animate);
    }

    /**
     * method used to fetch all comments using API call
     *
     * @param discovery_id Suggestion id
     */
    private void getComments(int discovery_id) {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().getDiscoveryComments(discovery_id, new Callback<RetroDiscoveryComment>() {
                @Override
                public void success(RetroDiscoveryComment retroDiscoveryComment, Response response) {
                    if (retroDiscoveryComment.getSuccess()) {
                        discoveryCommentList = retroDiscoveryComment.getDetails();
                        setAdapter();
                        tvError.setVisibility(View.GONE);

                    } else {
                        tvError.setText("Comments not found");
                        tvError.setVisibility(View.VISIBLE);
//                        MyToast.tshort(context, retroDiscoveryComment.getMessage() + "");
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
     * method used to set suggestion comment adapter
     */
    private void setAdapter() {
        if (discoveryCommentList.size() > 0){
            List<DiscoveryComment> discoveryCommentListTemp;
            discoveryCommentListTemp = Helper.updateDiscoveryCommentTimeSort(discoveryCommentList);
            discoveryCommentList = new ArrayList<>();
            discoveryCommentList = discoveryCommentListTemp;
        }

        discoveryCommentAdapter = new SuggestionCommentAdapter(context, discoveryCommentList);
        recyclerView.setAdapter(discoveryCommentAdapter);

        discoveryCommentAdapter.setOnClickListener(new SuggestionCommentAdapter.ClickInterface() {
            @Override
            public void onItemClick(DiscoveryComment discoveryComment, int position) {

            }

            @Override
            public void onUserClick(Profile profile, int position) {

                if(profile!=null) {
                    int profile_id = profile.getId();
                    int session_user_id = MyPreferencesManager.getId(context);

                    Class<?> clss = OthersProfileActivity.class;
                    if (profile_id == session_user_id)
                        clss = ProfileActivity.class;

                    Intent intent = new Intent(context, clss);
                    intent.putExtra(Constant.PROFILE_ID, profile_id);
                    intent.putExtra(Constant.PROFILE, profile);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * method used to post suggestion comment using API call
     *
     * @param comment      Comment
     * @param discovery_id Suggestion id
     * @param user_id      User id
     */
    private void discoveryComment(final String comment, int discovery_id, int user_id) {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().discoveryComment(comment, discovery_id, user_id, new Callback<RetroResponse>() {
                @Override
                public void success(RetroResponse retroResponse, Response response) {
//                    MyToast.tshort(context, retroResponse.getMessage() + "");
                    if (retroResponse.getSuccess()) {
                        DiscoveryComment discoveryComment = new DiscoveryComment();
                        discoveryComment.setComment(comment);
                        discoveryComment.setCreated_date(retroResponse.getMessage());
                        Profile profile = new Profile();
                        profile.setName(MyPreferencesManager.getName(context));
                        profile.setImage(MyPreferencesManager.getImage(context));
                        discoveryComment.setProfile(profile);
                        discoveryCommentList.add(discoveryComment);

                        int commentCount = discoveryCommentList.size();
                        if(ViewSuggestionActivity.suggestion!=null)
                            ViewSuggestionActivity.suggestion.setComment_count(commentCount);

                        setAdapter();
                        tvError.setVisibility(View.GONE);
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
