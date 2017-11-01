package com.lokaso.activity;

import android.content.Context;
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
import android.widget.TextView;
import android.widget.ToggleButton;

import com.lokaso.R;
import com.lokaso.adapter.ResponseCommentAdapter;
import com.lokaso.model.Profile;
import com.lokaso.model.QueryResponse;
import com.lokaso.model.ResponseComment;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroResponse;
import com.lokaso.retromodel.RetroResponseComment;
import com.lokaso.util.Constant;
import com.lokaso.util.Helper;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyInputMethodManager;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class QueryResponseCommentActivity extends AppCompatActivity {

    private String TAG = QueryResponseCommentActivity.class.getSimpleName();
    private Context context = QueryResponseCommentActivity.this;

    private QueryResponse queryResponse;
    private RecyclerView recyclerView;
    private EditText tvComment;
    private ImageButton bSend;
    private TextView tvResponse, tvError;
    private List<ResponseComment> responseCommentList = new ArrayList<>();
    private ResponseCommentAdapter responseCommentAdapter;
    private ToggleButton bFav;
    private NetworkConnection networkConnection;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response);

        builder = new AlertDialog.Builder(context);
        networkConnection = new NetworkConnection(context);
        queryResponse = (QueryResponse) getIntent().getSerializableExtra(Constant.INTENT_QUERYRESPONSE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvResponse = (TextView) findViewById(R.id.tvResponse);
        tvError = (TextView) findViewById(R.id.tvError);
        bFav = (ToggleButton) findViewById(R.id.bFav);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        tvComment = (EditText) findViewById(R.id.tvComment);
        bSend = (ImageButton) findViewById(R.id.bSend);

        tvResponse.setText(queryResponse.getResponse());

        if (toolbar != null) {
            toolbar.setTitle(queryResponse.getResponse());
//            toolbar_layout.setTitle("Response comment");
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

        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = tvComment.getText().toString().trim();

                if (!comment.isEmpty()) {
                    if (networkConnection.isNetworkAvailable()) {
                        sendComment(comment, queryResponse.getId());
                        tvComment.setText("");
                        new MyInputMethodManager(context, v);

                    } else {
                        MyToast.tshort(context, getString(R.string.check_network));
                    }

                } else {
                    MyToast.tshort(context, "Comment field is empty");
                }
            }
        });

        bFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bFav.isChecked()) {
                    setFav(Constant.FAV, queryResponse.getId(), MyPreferencesManager.getId(context));

                } else {
                    setFav(Constant.UNFAV, queryResponse.getId(), MyPreferencesManager.getId(context));
                }
            }
        });


        MyFont1 myFont = new MyFont1(context);
        myFont.setAppFont((ViewGroup) findViewById(R.id.container));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //overridePendingTransition(R.anim.close_enter_animate, R.anim.close_exit_animate);
    }

    /**
     * method used to set response comment adapter
     */
    private void setAdapter() {
        if (responseCommentList.size() > 0){
            List<ResponseComment> responseCommentListTemp;
            responseCommentListTemp = Helper.updateResponseCommentTimeSort(responseCommentList);
            responseCommentList = new ArrayList<>();
            responseCommentList = responseCommentListTemp;
        }

        responseCommentAdapter = new ResponseCommentAdapter(context, responseCommentList);
        recyclerView.setAdapter(responseCommentAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        queryResponse = (QueryResponse) getIntent().getSerializableExtra(Constant.INTENT_QUERYRESPONSE);
        getComments(queryResponse.getId());
//        bFav.setChecked(queryResponse.isUser_fav());
    }

    private void setFav(int action, int response_id, int user_id) {
        /*
        if (networkConnection.isNetworkAvailable()) {
        RestClient.getLokasoApi().responseFav(action, response_id, user_id, new Callback<RetroResponse>() {
            @Override
            public void success(RetroResponse retroResponse, Response response) {
                MyToast.tshort(context, retroResponse.getMessage());
            }

            @Override
            public void failure(RetrofitError error) {
                MyToast.tshort(context, "" + error);
            }
        });

        } else {
            MyToast.dshort(context, getString(R.string.check_network));
        }*/
    }

    /**
     * method used to post comment
     *
     * @param comment     Comment
     * @param response_id Response id
     */
    private void sendComment(final String comment, int response_id) {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().addResponseComment(comment, response_id, MyPreferencesManager.getId(context),
                    new Callback<RetroResponse>() {
                        @Override
                        public void success(RetroResponse retroResponse, Response response) {
//                            MyToast.tshort(context, retroResponse.getMessage());
                            if (retroResponse.getSuccess()) {
                                ResponseComment responseComment = new ResponseComment();
                                responseComment.setComment(comment);
                                responseComment.setCreated_date(retroResponse.getMessage());
                                Profile profile = new Profile();
                                profile.setName(MyPreferencesManager.getName(context));
                                profile.setImage(MyPreferencesManager.getImage(context));
                                responseComment.setProfile(profile);
                                responseCommentList.add(responseComment);
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

    /**
     * method used to fetch comments
     *
     * @param response_id Response id
     */
    private void getComments(int response_id) {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().getResponseComment(
                    MyPreferencesManager.getId(context),
                    response_id,
                    new Callback<RetroResponseComment>() {
                @Override
                public void success(RetroResponseComment retroResponseComment, Response response) {
                    responseCommentList = retroResponseComment.getDetails();
                    if (responseCommentList.size() == 0) {
                        tvError.setText("No comments present");
                        tvError.setVisibility(View.VISIBLE);

                    } else {
                        tvError.setVisibility(View.GONE);
                    }
                    setAdapter();
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
