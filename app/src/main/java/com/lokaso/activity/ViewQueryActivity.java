package com.lokaso.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lokaso.R;
import com.lokaso.adapter.QueryResponseAdapter;
import com.lokaso.model.Action;
import com.lokaso.model.Profile;
import com.lokaso.model.Queries;
import com.lokaso.model.QueryResponse;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroAction;
import com.lokaso.retromodel.RetroQueryResponse;
import com.lokaso.retromodel.RetroResponse;
import com.lokaso.retromodel.RetroValidity;
import com.lokaso.util.Constant;
import com.lokaso.util.Helper;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyInputMethodManager;
import com.lokaso.util.MyLog;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;
import com.lokaso.util.Picaso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ViewQueryActivity extends AppCompatActivity {

    private String TAG = ViewQueryActivity.class.getSimpleName(), extended_time = "";
    private Context context = ViewQueryActivity.this;

    private CircleImageView ivPic;
    private TextView tvName, tvProfession, tvQuery/*, tvResponses, tvDistance*/, tvValidity, tvPointsHint, tvFollowHint,
            tvCreditPoints, tvFollowQueryHint, tvValidityHint, tvError;
    private Queries queries;
    private RecyclerView recyclerView;
    private QueryResponseAdapter queryResponseAdapter;
    private List<QueryResponse> queryResponseList = new ArrayList<>();

    private EditText tvResponse;
    private ImageButton bSend;
    private Button bRespond, bExtend;
    private LinearLayout layoutComment, layoutAction, layoutFollow, layoutFollowQuery;
    private ImageButton bFollow, bFollowQuery;
    private LinearLayout layoutHeader;

    private NetworkConnection networkConnection;
    private AlertDialog.Builder builder;
    private Button bReport, bFlag;
    private boolean checkFlag = false, checkReport = false, checkFollow = false, checkFollowQuery = false;
    private Dialog dialog;
    private String[] duration = new String[]{"10 mins", "1 hour", "1 day"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_query);

        builder = new AlertDialog.Builder(context);
        networkConnection = new NetworkConnection(context);
        queries = (Queries) getIntent().getSerializableExtra(Constant.INTENT_QUERIES);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ivPic = (CircleImageView) findViewById(R.id.ivPic);
        tvName = (TextView) findViewById(R.id.tvName);
        tvProfession = (TextView) findViewById(R.id.tvProfession);
        tvQuery = (TextView) findViewById(R.id.tvQuery);
        /*tvResponses = (TextView) findViewById(R.id.tvResponses);
        tvDistance = (TextView) findViewById(R.id.tvDistance);*/
        tvError = (TextView) findViewById(R.id.tvError);
        tvValidity = (TextView) findViewById(R.id.tvValidity);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        tvResponse = (EditText) findViewById(R.id.tvResponse);
        bSend = (ImageButton) findViewById(R.id.bSend);
        bRespond = (Button) findViewById(R.id.bRespond);
        bExtend = (Button) findViewById(R.id.bExtend);
        layoutComment = (LinearLayout) findViewById(R.id.layoutComment);
        layoutAction = (LinearLayout) findViewById(R.id.layoutAction);
        layoutFollow = (LinearLayout) findViewById(R.id.layoutFollow);
        layoutFollowQuery = (LinearLayout) findViewById(R.id.layoutFollowQuery);
        tvPointsHint = (TextView) findViewById(R.id.tvPointsHint);
        tvFollowHint = (TextView) findViewById(R.id.tvFollowHint);
        tvFollowQueryHint = (TextView) findViewById(R.id.tvFollowQueryHint);
        tvCreditPoints = (TextView) findViewById(R.id.tvCreditPoints);
        tvValidityHint = (TextView) findViewById(R.id.tvValidityHint);
        bFollow = (ImageButton) findViewById(R.id.bFollow);
        bFollowQuery = (ImageButton) findViewById(R.id.bFollowQuery);

        layoutHeader = (LinearLayout) findViewById(R.id.layoutHeader);




        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_query_action);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.RIGHT | Gravity.TOP;
        bReport = (Button) dialog.findViewById(R.id.bReport);
        bFlag = (Button) dialog.findViewById(R.id.bFlag);

        if (queries.getProfile().getId() == MyPreferencesManager.getId(context)) {
            bRespond.setVisibility(View.GONE);
            bExtend.setVisibility(View.VISIBLE);
            layoutAction.setVisibility(View.GONE);
        }

        if (toolbar != null) {
            toolbar.setTitle(R.string.view_query);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            if (MyPreferencesManager.getId(context) != queries.getProfile().getId()) {
                toolbar.inflateMenu(R.menu.menu_option);
                toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.id_options:
                                dialog.show();
                                return true;
                        }
                        return false;
                    }
                });
            }
        }

        if (queries.isUser_followed()) {
            tvFollowHint.setText(getString(R.string.following_nuser));
            bFollow.setImageResource(R.drawable.ic_follow);
            checkFollow = true;

        } else {
            tvFollowHint.setText(getString(R.string.follow_nuser));
            bFollow.setImageResource(R.drawable.ic_unfollow);
            checkFollow = false;
        }

        layoutFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkFollow) {
                    queries.setUser_followed(false);
                    setFollow(Constant.UNFAV, queries.getProfile().getId(), MyPreferencesManager.getId(context));
                    tvFollowHint.setText(getString(R.string.follow_nuser));
                    bFollow.setImageResource(R.drawable.ic_unfollow);
                    checkFollow = false;

                } else {
                    queries.setUser_followed(true);
                    setFollow(Constant.FAV, queries.getProfile().getId(), MyPreferencesManager.getId(context));
                    tvFollowHint.setText(getString(R.string.following_nuser));
                    bFollow.setImageResource(R.drawable.ic_follow);
                    checkFollow = true;
                }
            }
        });

        layoutHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Profile profile = queries.getProfile();
                int profile_id = profile.getId();
                int session_user_id = MyPreferencesManager.getId(context);

                Class<?> clss = OthersProfileActivity.class;
                if(profile_id==session_user_id)
                    clss = ProfileActivity.class;

                Intent intent = new Intent(context, clss);
                intent.putExtra(Constant.PROFILE_ID, profile_id);
                intent.putExtra(Constant.PROFILE, profile);
                startActivity(intent);
            }
        });

        if (queries.isQuery_fav()) {
            tvFollowQueryHint.setText(getString(R.string.following_nquery));
            bFollowQuery.setImageResource(R.drawable.ic_follow_query);
            checkFollowQuery = true;

        } else {
            tvFollowQueryHint.setText(getString(R.string.follow_nquery));
            bFollowQuery.setImageResource(R.drawable.ic_unfollow_query);
            checkFollowQuery = false;
        }

        layoutFollowQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setfollowQuery(queries.getId());
                /*
                if (checkFollowQuery) {
                    queries.setQuery_fav(false);
                    setfollowQuery(Constant.UNFAV, queries.getId());
                    tvFollowQueryHint.setText(getString(R.string.follow_nquery));
                    bFollowQuery.setImageResource(R.drawable.ic_unfollow_query);
                    checkFollowQuery = false;

                } else {
                    queries.setQuery_fav(true);
                    setfollowQuery(Constant.FAV, queries.getId());
                    tvFollowQueryHint.setText(getString(R.string.following_nquery));
                    bFollowQuery.setImageResource(R.drawable.ic_follow_query);
                    checkFollowQuery = true;
                }
                */
            }
        });

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.hasFixedSize();
        setAdapter();

        tvName.setText(queries.getProfile().getName());
        tvQuery.setText(queries.getDescription());
        tvProfession.setText(queries.getProfile().getProfession());
        /*tvDistance.setText(queries.getDistance() + " km\naway");
        tvResponses.setText(queries.getResponse_count() + "\nResponses");*/
        tvCreditPoints.setText(queries.getProfile().getCredits() + "");

        if (!queries.getValid_until().equals("0")) {
            tvValidity.setText(queries.getValid_until() + "");
            tvValidityHint.setVisibility(View.VISIBLE);
            bRespond.setVisibility(View.VISIBLE);

        } else {
            tvValidity.setText("Expired");
            tvValidityHint.setVisibility(View.GONE);
            bRespond.setVisibility(View.GONE);
        }

        String imageUrl = queries.getProfile().getImage();
        MyLog.e(TAG, "userImageUrl : "+imageUrl);
        Picaso.loadUser(context, imageUrl, ivPic);
        setTypeface();

        bRespond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyInputMethodManager(context, v, 1);
                layoutComment.setVisibility(View.VISIBLE);
                tvResponse.requestFocus();
                bRespond.setVisibility(View.GONE);
            }
        });

        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String response = tvResponse.getText().toString().trim();
                if (response.isEmpty()) {
                    MyToast.tshort(context, "Response cannot be empty");

                } else {
                    if (networkConnection.isNetworkAvailable()) {
                        queryResponse(response, queries.getId(), queries.getProfile().getId(), MyPreferencesManager.getId(context));
                        tvResponse.setText("");
                        new MyInputMethodManager(context, v);
                        layoutComment.setVisibility(View.GONE);
                        bRespond.setVisibility(View.VISIBLE);

                    } else {
                        MyToast.tshort(context, getString(R.string.check_network));
                    }
                }
            }
        });

        final ArrayAdapter<String> spdurationAdapter = new ArrayAdapter<>(this, R.layout.layout_profession, R.id.tv, duration);
        spdurationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        bExtend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle(getString(R.string.extend_validity))
                        .setAdapter(spdurationAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int query_id = queries.getId();
                                String current_validity;
                                String currentdate = "";

                                /*if (queries.getValid_until().equals("0")){
                                    Date date = new Date();
                                    DateFormat dateFormat = new SimpleDateFormat(Constant.DATE_FORMAT3, Locale.ENGLISH);
                                    currentdate = dateFormat.format(date);
                                    current_validity = currentdate;

                                } else {*/
                                current_validity = queries.getValid_date();
//                                }
                                MyLog.e(TAG, "current_validity : " + current_validity + " | currentdate : " + currentdate);

                                String extend_time = spdurationAdapter.getItem(which);
                                extended_time = extend_time;
                                /*SimpleDateFormat df = new SimpleDateFormat(Constant.DATE_FORMAT, Locale.ENGLISH);
                                try {
                                    Date d = df.parse(current_validity);

                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(d);

                                    if (extend_time.contains(Constant.DAY)) {
                                        cal.add(Calendar.DATE, 1);

                                        String newTime = df.format(cal.getTime());
                                        MyLog.e(TAG, "newTime : " + newTime);*/
                                extendValidity(query_id, current_validity, extend_time);

                                    /*} else if (extend_time.contains(Constant.HOUR)) {
                                        cal.add(Calendar.HOUR_OF_DAY, 1);

                                        String newTime = df.format(cal.getTime());
                                        MyLog.e(TAG, "newTime : " + newTime);
                                        extendValidity(query_id, newTime);

                                    } else if (extend_time.contains(Constant.MIN)) {
                                        cal.add(Calendar.MINUTE, 10);

                                        String newTime = df.format(cal.getTime());
                                        MyLog.e(TAG, "newTime : " + newTime);
                                        extendValidity(query_id, newTime);

                                    } else {
                                        MyLog.e(TAG, "else");
                                    }

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }*/
                            }
                        })
                        .show();
            }
        });

        MyLog.e(TAG, "report " + queries.getProfile().isReport());
        checkReport = queries.getProfile().isReport();
        if (queries.getProfile().isReport()) {
            bReport.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_report_orange, 0, 0, 0);

        } else {
            bReport.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_report, 0, 0, 0);
        }

        bReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkReport) {
                    bReport.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_report, 0, 0, 0);
                    userSpam(Constant.UNFAV, queries.getProfile().getId(), MyPreferencesManager.getId(context));
                    checkReport = false;

                } else {
                    bReport.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_report_orange, 0, 0, 0);
                    userSpam(Constant.FAV, queries.getProfile().getId(), MyPreferencesManager.getId(context));
                    checkReport = true;
                }
                dialog.dismiss();
            }
        });

        checkFlag = queries.isFlag();
        if (queries.isFlag()) {
            bFlag.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_flag_orange, 0, 0, 0);

        } else {
            bFlag.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_flag, 0, 0, 0);
        }

        bFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkFlag) {
                    bFlag.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_flag, 0, 0, 0);
                    querySpam(Constant.UNFAV, queries.getId(), MyPreferencesManager.getId(context));
                    checkFlag = false;

                } else {
                    bFlag.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_flag_orange, 0, 0, 0);
                    querySpam(Constant.FAV, queries.getId(), MyPreferencesManager.getId(context));
                    checkFlag = true;
                }
                dialog.dismiss();
            }
        });
    }

    /**
     * method used to follow query
     *
     * @param query_id Query id
     */
    private void setfollowQuery(int query_id) {
        if (networkConnection.isNetworkAvailable()) {

            layoutFollowQuery.setClickable(false);

            RestClient.getLokasoApi().followQuery(query_id, MyPreferencesManager.getId(context),
                    new Callback<RetroAction>() {
                        @Override
                        public void success(RetroAction retroAction, Response response) {
//                    MyToast.tshort(context, retroResponse.getMessage() + "");

                            layoutFollowQuery.setClickable(true);
                            if(retroAction!=null) {
                                if(retroAction.getSuccess()) {
                                    Action action = retroAction.getAction();
                                    if(action!=null) {

                                        boolean isFollowed = action.getAction_status()==1;

                                        queries.setQuery_fav(isFollowed);
                                        if(isFollowed) {
                                            tvFollowQueryHint.setText(getString(R.string.following_nquery));
                                            bFollowQuery.setImageResource(R.drawable.ic_follow_query);
                                        }
                                        else {
                                            tvFollowQueryHint.setText(getString(R.string.follow_nquery));
                                            bFollowQuery.setImageResource(R.drawable.ic_unfollow_query);

                                        }
                                    }
                                    else {
                                        MyToast.tshort(context, "Oops. Something went wrong.");
                                    }
                                }
                                else {
                                    MyToast.tshort(context, ""+retroAction.getMessage());
                                }
                            }
                            else {
                                MyToast.tshort(context, "Oops. Something went wrong.");
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            layoutFollowQuery.setClickable(true);
                            MyToast.tshort(context, "Oops. Something went wrong.");
                        }
                    });
        } else {
            MyToast.tshort(context, getString(R.string.check_network));
        }
    }

    /*
    private void setfollowQuery(int action, int query_id) {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().followQuery(action, query_id, MyPreferencesManager.getId(context),
                    new Callback<RetroResponse>() {
                        @Override
                        public void success(RetroResponse retroResponse, Response response) {
//                    MyToast.tshort(context, retroResponse.getMessage() + "");

                            if(retroResponse!=null) {
                                if(retroResponse.getSuccess()) {

                                }
                                else {
                                    MyToast.tshort(context, ""+retroResponse.getMessage());
                                }
                            }
                            else {
                                MyToast.tshort(context, "Oops. Something went wrong.");
                            }

                        }

                        @Override
                        public void failure(RetrofitError error) {
                            MyToast.tshort(context, "Oops. Something went wrong.");
                        }
                    });
        } else {
            MyToast.tshort(context, getString(R.string.check_network));
        }
    }
*/
    /**
     * method used to follow user
     *
     * @param action   action = 1 to follow User and action = 0 for vice-versa
     * @param leader   user id of the user being followed
     * @param follower user id of the user following
     */
    private void setFollow(int action, int leader, int follower) {
        if (networkConnection.isNetworkAvailable()) {
            final String failMessage = "Failed to follow user";
            RestClient.getLokasoApi().follow(action, leader, follower, new Callback<RetroResponse>() {
                @Override
                public void success(RetroResponse retroResponse, Response response) {
                    if(retroResponse!=null) {
                        if(retroResponse.getSuccess()) {
                            queries.setUser_followed(true);
                        }
                        else {
                            MyToast.tshort(context, "" + retroResponse.getMessage());
                        }
                    }
                    else {
                        MyToast.tshort(context, failMessage);
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    MyToast.tshort(context, failMessage);
                }
            });
        } else {
            MyToast.tshort(context, getString(R.string.check_network));
        }
    }

    /**
     * method used to extend validity
     *
     * @param query_id    Query id
     * @param extend_time Validity of the Query
     */
    private void extendValidity(final int query_id, String current_validity, String extend_time) {
        if (networkConnection.isNetworkAvailable()) {
            final String failMessage = "Failed to extend";
            RestClient.getLokasoApi().extendQueryValidity(MyPreferencesManager.getId(context), query_id, current_validity,
                    extend_time, new Callback<RetroValidity>() {
                        @Override
                        public void success(RetroValidity retroResponse, Response response) {
                            if(retroResponse!=null) {
                                if (retroResponse.getSuccess()) {
                                    MyToast.tshort(context, "Validity extended");
                                    tvValidity.setText(retroResponse.getDetails().get(0).getValid_until());
                                    queries.setValid_until(retroResponse.getDetails().get(0).getValid_until());
                                    queries.setValid_date(retroResponse.getDetails().get(0).getValid_date());
                                }
                                else {
                                    MyToast.tshort(context, retroResponse.getMessage());
                                }
                            }
                            else {
                                MyToast.tshort(context, failMessage);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            MyToast.tshort(context, failMessage);
                            extended_time = "";
                        }
                    });
        } else {
            MyToast.tshort(context, getString(R.string.check_network));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        queries = (Queries) getIntent().getSerializableExtra(Constant.INTENT_QUERIES);
        getResponses(queries.getId());

        if (queries.getProfile().getId() == MyPreferencesManager.getId(context)) {
            bRespond.setVisibility(View.GONE);
            bExtend.setVisibility(View.VISIBLE);
            layoutAction.setVisibility(View.GONE);
        }
    }

    /**
     * method used to set fonts
     */
    private void setTypeface() {

        MyFont1 myFont = new MyFont1(context);
        myFont.setAppFont((ViewGroup) findViewById(R.id.container));
        myFont.setFont(tvName, MyFont1.CENTURY_GOTHIC_BOLD);
        myFont.setFont(tvCreditPoints, MyFont1.CENTURY_GOTHIC_BOLD);
        myFont.setFont(tvValidity, MyFont1.CENTURY_GOTHIC_BOLD);
    }

    /**
     * method used to get responses
     *
     * @param query_id Query id
     */
    private void getResponses(int query_id) {
        if (networkConnection.isNetworkAvailable()) {
            final String failMessage = "Failed to get responses";
            RestClient.getLokasoApi().getQueryResponse(query_id, MyPreferencesManager.getId(context),
                    new Callback<RetroQueryResponse>() {
                        @Override
                        public void success(RetroQueryResponse retroQueryResponse, Response response) {
                            if(retroQueryResponse!=null) {
                                if (retroQueryResponse.getSuccess()) {
                                    queryResponseList = retroQueryResponse.getDetails();
                                    setAdapter();

                                }
                                else {

                                }
                            }
                            else {

                            }

                            if (queryResponseList.size() == 0) {
                                tvError.setText("No responses found");
                                tvError.setVisibility(View.VISIBLE);

                            } else {
                                tvError.setVisibility(View.GONE);
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
     * method used to post query response
     *
     * @param response      Response to the query
     * @param query_id      Query id
     * @param query_user_id user id of the query's owner
     * @param user_id       user id of the current user
     */
    private void queryResponse(final String response, int query_id, int query_user_id, int user_id) {
        if (networkConnection.isNetworkAvailable()) {
            final String failMessage = "Failed to get responses";
            RestClient.getLokasoApi().addQueryResponse(response, query_id, query_user_id, user_id, new Callback<RetroResponse>() {
                @Override
                public void success(RetroResponse retroResponse, Response responseB) {

                    if(retroResponse!=null) {
                        if (retroResponse.getSuccess()) {
                            QueryResponse queryResponse = new QueryResponse();
                            queryResponse.setResponse(response);
                            queryResponse.setId(Integer.parseInt(retroResponse.getMessage()));
                            Profile profile = new Profile();
                            profile.setId(MyPreferencesManager.getId(context));
                            profile.setName(MyPreferencesManager.getName(context));
                            profile.setImage(MyPreferencesManager.getImage(context));
                            profile.setProfession(MyPreferencesManager.getProfession(context));
                            queryResponse.setProfile(profile);
                            queryResponse.setUser_fav(-1);
                            queryResponseList.add(queryResponse);
                            setAdapter();
                            tvError.setVisibility(View.GONE);
                        }
                        else {
                            MyToast.tshort(context, retroResponse.getMessage() + "");
                        }
                    }
                    else {
                        MyToast.tshort(context, failMessage);
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    MyToast.tshort(context, failMessage);
                }
            });
        } else {
            MyToast.tshort(context, getString(R.string.check_network));
        }
    }

    /**
     * method used to set query response adapter
     */
    private void setAdapter() {
        if (queryResponseList.size() > 0) {
            List<QueryResponse> queryResponseListTemp;
            queryResponseListTemp = Helper.updateTimeSort(queryResponseList);
            queryResponseList = new ArrayList<>();
            queryResponseList = queryResponseListTemp;
        }

        queryResponseAdapter = new QueryResponseAdapter(context, queryResponseList);
        recyclerView.setAdapter(queryResponseAdapter);

        queryResponseAdapter.setOnClickListener(new QueryResponseAdapter.ClickInterface() {
            @Override
            public void onItemClick(QueryResponse queryResponse) {
                if (queryResponse.getProfile().getId() != MyPreferencesManager.getId(context)) {
                    Intent intent = new Intent(context, OthersProfileActivity.class);
                    intent.putExtra(Constant.PROFILE_ID, queryResponse.getProfile().getId());
                    startActivity(intent);
                }
            }

            @Override
            public void onCommentClick(QueryResponse queryResponse) {
                Intent intent = new Intent(context, QueryResponseCommentActivity.class);
                intent.putExtra(Constant.INTENT_QUERYRESPONSE, queryResponse);
                startActivity(intent);
                //overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);
            }

            @Override
            public void onVoteClick(int response_id, int action, int position) {
                responseVotes(response_id, action, position);
            }

            @Override
            public void onEditClick(final int response_id, final int position) {
                final View view = LayoutInflater.from(context).inflate(R.layout.alert_label_editor, null);
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setView(view);
                alertDialog.setTitle(getString(R.string.edit_response));

                final EditText tvComment = (EditText) view.findViewById(R.id.tvComment);
                tvComment.setText(queryResponseList.get(position).getResponse());

                alertDialog.setPositiveButton(getString(R.string.Edit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String response = tvComment.getText().toString().trim();
                        if (!response.isEmpty()) {
                            updateResponse(response_id, MyPreferencesManager.getId(context), response, position);
                        } else {
                            MyToast.tshort(context, "Response cannot be empty");
                        }
                        new MyInputMethodManager(context, view);
                    }
                });

                alertDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new MyInputMethodManager(context, view);
                    }
                });

                alertDialog.show();
            }

            @Override
            public void onSpamClick(final int action, final int response_id, final int position) {

                if (action == 1) {
                    new AlertDialog.Builder(context)
                            .setTitle(getString(R.string.dialog_title_spam_query_response))
                            .setMessage(getString(R.string.dialog_message_spam_query_response))
                            .setPositiveButton(getString(R.string.dialog_spam_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    responseSpam(action, response_id, MyPreferencesManager.getId(context), position);
                                }
                            })
                            .setNegativeButton(getString(R.string.dialog_spam_cancel), null)
                            .show();
                }
                else {
                    responseSpam(action, response_id, MyPreferencesManager.getId(context), position);
                }

            }
        });
    }

    /**
     * method used to update response
     *
     * @param response_id      Response id
     * @param user_id          user id
     * @param response_comment response comment
     * @param position         position of the list
     */
    private void updateResponse(int response_id, int user_id, final String response_comment, final int position) {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().updateResponse(response_id, user_id, response_comment, new Callback<RetroResponse>() {
                @Override
                public void success(RetroResponse retroResponse, Response response) {
                    queryResponseList.get(position).setResponse(response_comment);
                    queryResponseAdapter.refresh(queryResponseList);
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
     * method used to post votes on response
     *
     * @param response_id Response id
     * @param action      action = 1 to vote on response and action = 0 for vice-versa
     * @param position    position of the list
     */
    private void responseVotes(int response_id, final int action, final int position) {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().responseVotes(MyPreferencesManager.getId(context), response_id, action,
                    new Callback<RetroResponse>() {
                        @Override
                        public void success(RetroResponse retroResponse, Response response) {
//                            MyToast.tshort(context, retroResponse.getMessage() + "");
                            if (retroResponse.getSuccess()) {
                                int old_up = queryResponseList.get(position).getUpvotes();
                                int old_down = queryResponseList.get(position).getDownvotes();
                                if (action == 1) {
                                    queryResponseList.get(position).setUpvotes(old_up + 1);
                                    if (old_down > 0 && !retroResponse.getMessage().equals("1")) {
                                        queryResponseList.get(position).setDownvotes(old_down - 1);
                                    }

                                } else {
                                    queryResponseList.get(position).setDownvotes(old_down + 1);
                                    if (old_up > 0 && !retroResponse.getMessage().equals("1")) {
                                        queryResponseList.get(position).setUpvotes(old_up - 1);
                                    }
                                }
                                queryResponseAdapter.refresh(queryResponseList);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
//                            MyToast.tshort(context, "" + error);
                        }
                    });
        } else {
            MyToast.tshort(context, getString(R.string.check_network));
        }
    }

    /**
     * method used to Spam response
     *
     * @param action      action = 1 to spam response and action = 0 for vice-versa
     * @param response_id Response id
     * @param user_id     user id
     * @param position    position of the list
     */
    private void responseSpam(final int action, int response_id, int user_id, final int position) {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().responseSpam(action, response_id, user_id, new Callback<RetroResponse>() {
                @Override
                public void success(RetroResponse retroResponse, Response response) {
                    /*
                    if(retroResponse!=null) {
                        if (retroResponse.getSuccess()){
                            if (action == Constant.FAV){
                                queryResponseList.get(position).setSpam(true);

                            } else {
                                queryResponseList.get(position).setSpam(false);
                            }
                            queryResponseAdapter.refresh(queryResponseList);
                        }
                        else {

                            MyToast.tshort(context, "" + retroResponse.getMessage());
                        }
                    }
                    */
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
     * method used to spam Query
     *
     * @param action   action = 1 to spam query and action = 0 for vice-versa
     * @param query_id Query id
     * @param user_id  user id
     */
    private void querySpam(int action, int query_id, int user_id) {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().querySpam(action, query_id, user_id, new Callback<RetroResponse>() {
                @Override
                public void success(RetroResponse retroResponse, Response response) {

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
     * method used to spam user
     *
     * @param action       action = 1 to spam user and action = 0 for vice-versa
     * @param spam_user_id user id of the user being spammed
     * @param user_id      user id of the user spamming
     */
    private void userSpam(int action, int spam_user_id, int user_id) {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().userSpam(action, spam_user_id, user_id, new Callback<RetroAction>() {
                @Override
                public void success(RetroAction retroAction, Response response) {

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

    @Override
    public void onBackPressed() {
        if (layoutComment.getVisibility() == View.GONE) {
            super.onBackPressed();
            //overridePendingTransition(R.anim.close_enter_animate, R.anim.close_exit_animate);

        } else {
            layoutComment.setVisibility(View.GONE);
            bRespond.setVisibility(View.VISIBLE);
        }
    }
}
