package com.lokaso.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lokaso.R;
import com.lokaso.adapter.ProfilePagerAdapter;
import com.lokaso.model.Action;
import com.lokaso.model.Profile;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.preference.TourPreference;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroAction;
import com.lokaso.retromodel.RetroProfile;
import com.lokaso.retromodel.RetroResponse;
import com.lokaso.service.GPSTracker;
import com.lokaso.util.Constant;
import com.lokaso.util.MarshmallowPermission;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyLog;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;
import com.lokaso.util.Picaso;
import com.lokaso.util.PopupInfo;
import com.lokaso.util.TourClass;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class OthersProfileActivity extends AppCompatActivity {

    private String TAG = OthersProfileActivity.class.getSimpleName(), unread_count = "0";
    private Context context = OthersProfileActivity.this;

    private CircleImageView ivPic;
    private TextView tvName, tvProfession, tvCreditPoints, tvDiscoveryCount, tvQueryCount, tvFollowersCount, tvAboutMe,
            tvUnreadCount, tvPointsHint;
    private TextView tvFollow;

    private ImageView bFollow;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private LinearLayout followLayout, messageLayout;

    private GPSTracker gps;
    private MarshmallowPermission marshmallowPermission;
    private List<Profile> profileList = new ArrayList<>();

    private int profile_id = 0;
    private Profile profile;
    private NetworkConnection networkConnection;
    private AlertDialog.Builder builder;

    private String aboutMe = "";

    private boolean checkReport = false;

    private TourClass tourFollow, tourChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);

        builder = new AlertDialog.Builder(context);
        networkConnection = new NetworkConnection(context);


        try {
            profile_id = getIntent().getIntExtra(Constant.PROFILE_ID, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            profile = (Profile) getIntent().getSerializableExtra(Constant.PROFILE);

        } catch (Exception e) {
            e.printStackTrace();
        }


        marshmallowPermission = new MarshmallowPermission(OthersProfileActivity.this);
        gps = new GPSTracker(context, OthersProfileActivity.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        bFollow = (ImageView) findViewById(R.id.bFollow);
        ivPic = (CircleImageView) findViewById(R.id.ivPic);
        tvName = (TextView) findViewById(R.id.tvName);
        tvProfession = (TextView) findViewById(R.id.tvProfession);
        tvCreditPoints = (TextView) findViewById(R.id.tvCreditPoints);
        tvAboutMe = (TextView) findViewById(R.id.tvAboutMe);
        tvUnreadCount = (TextView) findViewById(R.id.tvUnreadCount);
        tvPointsHint = (TextView) findViewById(R.id.tvPointsHint);

        tvFollow = (TextView) findViewById(R.id.tvFollow);

        followLayout = (LinearLayout) findViewById(R.id.followLayout);
        messageLayout = (LinearLayout) findViewById(R.id.messageLayout);


        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

/*
        tvAboutMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupInfo popupInfo = new PopupInfo(context, tvAboutMe);
                popupInfo.show(""+aboutMe);
            }
        });
*/
        setTab();
        setupTabLayout();

        if (toolbar != null) {
            toolbar.setTitle(R.string.user_profile);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

/*
            toolbar_layout.inflateMenu(R.menu.menu_profile_other);
            toolbar_layout.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.idReport:
                            if (profileList.size() > 0) {
                                Profile profile = profileList.get(0);
                                if (checkReport) {
                                    //bReport.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_report, 0, 0, 0);
                                    userSpam(Constant.UNFAV, profile.getId(), MyPreferencesManager.getId(context));
                                    checkReport = false;

                                } else {
                                    //bReport.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_report_orange, 0, 0, 0);
                                    userSpam(Constant.FAV, profile.getId(), MyPreferencesManager.getId(context));
                                    checkReport = true;
                                }

                            } else {
                                MyToast.tshort(context, getString(R.string.failed_to_fetch_data));
                            }
                            return true;
                    }
                    return false;
                }
            });*/
        }


        setListener();

        if(profile!=null) {
            profile_id = profile.getId();
            setData(profile);
        }



        // Tour
        tourFollow = new TourClass(OthersProfileActivity.this).click().bottomRight().isTopMost()
                .message(getString(R.string.tour_profile_follow_desc))
                .anchor(followLayout)
                .setOnClickListener(new TourClass.OnClickListener() {
                    @Override
                    public void onClick(TourClass tour) {
                        tour.dismiss();

                        TourPreference.setProfileFollowSeen(context, true);

                        tourChat.show();
                    }
                });

        tourChat = new TourClass(OthersProfileActivity.this).click().bottomLeft().isTopMost()
                .message(getString(R.string.tour_profile_chat_desc))
                .anchor(messageLayout)
                .setOnClickListener(new TourClass.OnClickListener() {
                    @Override
                    public void onClick(TourClass tour) {
                        tour.dismiss();
                        TourPreference.setProfileChatSeen(context, true);
                        setListener();
                    }
                });

        if(!TourPreference.isProfileFollowSeen(context)) {
            tourFollow.show();
        }
        else if(!TourPreference.isProfileChatSeen(context)) {
            tourChat.show();
        }

        setFonts();
    }

    private void setListener() {

        followLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profile.isUser_followed()) {
                    profile.setUser_followed(false);
                    setFollowButton(false);

                    //bFollow.setImageResource(R.drawable.ic_follow_grey);
                    setFollow(Constant.UNFAV, profile_id, MyPreferencesManager.getId(context));

                } else {
                    profile.setUser_followed(true);
                    //followLayout.setSelected(true);
                    //tvFollow.setText(""+getString(R.string.following));
                    setFollowButton(true);
                    //bFollow.setImageResource(R.drawable.ic_follow_red);
                    setFollow(Constant.FAV, profile_id, MyPreferencesManager.getId(context));
                }
            }
        });

        messageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (profile != null) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra(Constant.PROFILE, profile);
                    intent.putExtra(Constant.CHAT_ID, 0);
                    startActivity(intent);
                    //overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);

                } else {
                    MyToast.tshort(context, "Failed to fetch profile details. Try again later!");
                }
            }
        });
    }


    /**
     * method used to spam user
     *
     * @param action       : action = 1 to spam user, action = 0 to un spam user
     * @param spam_user_id : User id of the user on whom action will be taken
     * @param user_id      : User id of the user who will take action on other user
     */
    private void userSpam(int action, int spam_user_id, int user_id) {
        if (networkConnection.isNetworkAvailable()) {
            final String failMessage = "Failed to spam user";
            RestClient.getLokasoApi().userSpam(action, spam_user_id, user_id, new Callback<RetroAction>() {
                @Override
                public void success(RetroAction retroAction, Response response) {

                    if(retroAction!=null) {
                        if (retroAction.getSuccess()) {
                            MyToast.tshort(context, retroAction.getMessage());

                            Action action = retroAction.getAction();
                            if(action!=null) {

                                boolean isValid = action.getAction_status()==1;
                                checkReport = isValid;
                                invalidateOptionsMenu();
                            }
                        }
                        else {
                            MyToast.tshort(context, retroAction.getMessage());
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile_other, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.idEditProfile:
                if (profileList.size() > 0) {
                    final Profile profile = profileList.get(0);
                    if (checkReport) {
                        //bReport.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_report, 0, 0, 0);
                        userSpam(Constant.UNFAV, profile.getId(), MyPreferencesManager.getId(context));

                    } else {
                        new AlertDialog.Builder(context)
                                .setTitle(getString(R.string.dialog_title_spam_user))
                                .setMessage(getString(R.string.dialog_message_spam_user))
                                .setPositiveButton(getString(R.string.dialog_spam_ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        userSpam(Constant.FAV, profile.getId(), MyPreferencesManager.getId(context));
                                    }
                                })
                                .setNegativeButton(getString(R.string.dialog_spam_cancel), null)
                                .show();

                        //bReport.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_report_orange, 0, 0, 0);
                    }
                    invalidateOptionsMenu();

                } else {
                    MyToast.tshort(context, getString(R.string.failed_to_fetch_data));
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem report = menu.findItem(R.id.idEditProfile);

        if(checkReport) {
            report.setIcon(R.drawable.ic_spam_true);
        }
        else {
            report.setIcon(R.drawable.ic_spam_false);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProfile();
        getChatUnreadCount(profile_id);
    }

    /*

    @Override
    protected void onStart() {
        super.onStart();
        profile_id = getIntent().getIntExtra(Constant.PROFILE_ID, 0);
        getProfile();
        getChatUnreadCount(profile_id);
    }
*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //overridePendingTransition(R.anim.close_enter_animate, R.anim.close_exit_animate);
    }

    /**
     * method used to fetch profile using API call
     */
    private void getProfile() {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().getProfile(profile_id, MyPreferencesManager.getId(context), new Callback<RetroProfile>() {
                @Override
                public void success(RetroProfile retroProfile, Response response) {
                    if (retroProfile.getSuccess()) {
                        profileList = retroProfile.getDetails();
                        profile = profileList.get(0);
                        setData(profile);

                    } else {
//                    MyToast.tshort(context, retroProfile.getMessage() + "");
                    }
                }

                @Override
                public void failure(RetrofitError error) {
//                    MyToast.tshort(context, "" + error);
                    profile = null;
                }
            });

        } else {
            MyToast.tshort(context, getString(R.string.check_network));
        }
    }

    /**
     * method used to set tab
     */
    private void setTab() {
        tabLayout.addTab(tabLayout.newTab().setText(R.string.discoveries));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.queries));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.followers));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        ProfilePagerAdapter adapter = new ProfilePagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), profile_id);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        MyFont1 myFont = new MyFont1(context);
        ViewGroup slidingTabStrip = (ViewGroup) tabLayout.getChildAt(0);
        for (int i = 0, count = adapter.getCount(); i < count; i++) {
            AppCompatTextView view = (AppCompatTextView) ((ViewGroup) slidingTabStrip.getChildAt(i)).getChildAt(1);
            myFont.setFont(view, MyFont1.CENTURY_GOTHIC_BOLD);
        }

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                tab.getCustomView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                TextView tvTagCount, tvTag;
                tvTagCount = (TextView) tab.getCustomView().findViewById(R.id.tvTagCount);
                tvTag = (TextView) tab.getCustomView().findViewById(R.id.tvTag);
                tvTagCount.setTextColor(getResources().getColor(R.color.white_text));
                tvTag.setTextColor(getResources().getColor(R.color.white_text));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getCustomView().setBackgroundColor(getResources().getColor(R.color.light_grey_background));
                TextView tvTagCount, tvTag;
                tvTagCount = (TextView) tab.getCustomView().findViewById(R.id.tvTagCount);
                tvTag = (TextView) tab.getCustomView().findViewById(R.id.tvTag);
                tvTagCount.setTextColor(getResources().getColor(R.color.black_text));
                tvTag.setTextColor(getResources().getColor(R.color.black_text));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * method used to setup tab layout
     */
    private void setupTabLayout() {
        MyFont1 myFont = new MyFont1(context);

        View view1 = LayoutInflater.from(this).inflate(R.layout.custom_profile_tab, null);
        TextView tvDiscovery = (TextView) view1.findViewById(R.id.tvTag);
        tvDiscoveryCount = (TextView) view1.findViewById(R.id.tvTagCount);

        myFont.setFont(tvDiscoveryCount, MyFont1.CENTURY_GOTHIC_BOLD);
        myFont.setFont(tvDiscovery);
        tvDiscovery.setTextColor(getResources().getColor(R.color.white_text));
        tvDiscoveryCount.setTextColor(getResources().getColor(R.color.white_text));

        tvDiscovery.setText(getString(R.string.tab_suggestion));
        tabLayout.getTabAt(0).setCustomView(view1);
        tabLayout.getTabAt(0).getCustomView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        View view2 = LayoutInflater.from(this).inflate(R.layout.custom_profile_tab, null);
        TextView tvQuery = (TextView) view2.findViewById(R.id.tvTag);
        tvQueryCount = (TextView) view2.findViewById(R.id.tvTagCount);
        myFont.setFont(tvQueryCount, MyFont1.CENTURY_GOTHIC_BOLD);
        myFont.setFont(tvQuery);
        tvQuery.setText(getString(R.string.tab_query));
        tabLayout.getTabAt(1).setCustomView(view2);

        View view3 = LayoutInflater.from(this).inflate(R.layout.custom_profile_tab, null);
        TextView tvFollowers = (TextView) view3.findViewById(R.id.tvTag);
        tvFollowersCount = (TextView) view3.findViewById(R.id.tvTagCount);

        myFont.setFont(tvFollowersCount, MyFont1.CENTURY_GOTHIC_BOLD);
        myFont.setFont(tvFollowers);

        tvFollowers.setText(getString(R.string.tab_follower));
        tabLayout.getTabAt(2).setCustomView(view3);
    }

    /**
     * method used to set data
     *
     * @param profile : Profile data
     */
    private void setData(Profile profile) {

        String imageUrl = profile.getImage();
        MyLog.e(TAG, "userImageUrl : "+imageUrl);
        Picaso.loadUser(context, imageUrl, ivPic);


        String user_name = profile.getName();
        tvName.setText(user_name);
        tvProfession.setText(profile.getProfession());
        tvCreditPoints.setText(profile.getCredits_display() + "");

        String aboutMeBlank = getString(R.string.message_aboutme, user_name);
        String about = profile.getAbout_me();
        if(about!=null && about.length()>0)
            aboutMe = about;
        else
            aboutMe = aboutMeBlank;

        tvAboutMe.setText(aboutMe);

        /*
        if (profile.isUser_followed()) {
            bFollow.setImageResource(R.drawable.ic_follow_red);

        } else {bFollow.setImageResource(R.drawable.ic_follow_grey);

        }
        */
        MyLog.e(TAG, "profile.isUser_followed() : "+profile.isUser_followed());
        setFollowButton(profile.isUser_followed());
        //followLayout.setSelected(profile.isUser_followed());


        tvDiscoveryCount.setText(profile.getDiscovery_count() + "");
        tvQueryCount.setText(profile.getQuery_count() + "");
        tvFollowersCount.setText(profile.getFollowing_count() + "");

        checkReport = profile.isReport();
        MyLog.e(TAG, "checkReport : "+checkReport);
        invalidateOptionsMenu();
    }

    /**
     * method used to get unread count of a chat
     *
     * @param to_user_id : User id of the other user
     */
    private void getChatUnreadCount(int to_user_id) {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().getChatUnreadCount(MyPreferencesManager.getId(context), to_user_id, new Callback<RetroResponse>() {
                @Override
                public void success(RetroResponse retroResponse, Response response) {
                    unread_count = retroResponse.getDetails();
                    tvUnreadCount.setText(unread_count);
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
     * method used on click of Message button
     *
     * @param view Button view
     */
    /*
    public void onMessageClick(View view) {
        if (profile != null) {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra(Constant.PROFILE, profile);
            intent.putExtra(Constant.CHAT_ID, 0);
            startActivity(intent);
            //overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);

        } else {
            MyToast.tshort(context, "Failed to fetch profile details. Try again later!");
        }
    }*/

    /**
     * method used to follow a user
     *
     * @param action action = 1 for user follow and action = 0 for user un-follow
     * @param leader : user id of the user being followed
     * @param follower : user id o the user who is following
     */
    private void setFollow(int action, int leader, int follower) {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().follow(action, leader, follower, new Callback<RetroResponse>() {
                @Override
                public void success(RetroResponse retroResponse, Response response) {
//                MyToast.tshort(context, retroResponse.getMessage() + "");
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

    private void setFollowButton(boolean isFollowing) {

        if(isFollowing) {
            followLayout.setSelected(true);
            tvFollow.setText(""+getString(R.string.following));
        }
        else {
            followLayout.setSelected(false);
            tvFollow.setText(""+getString(R.string.follow));
        }
    }
    /**
     * method used to set fonts
     */
    private void setFonts() {

        MyFont1 myFont = new MyFont1(context);
        myFont.setAppFont((ViewGroup) findViewById(R.id.container));

//        MyFont myFont = new MyFont(context);
//        myFont.setTypeface(tvProfession);
//        myFont.setTypeface(tvDiscoveryCount);
//        myFont.setTypeface(tvQueryCount);
//        myFont.setTypeface(tvFollowersCount);
//        myFont.setTypeface(tvAboutMe);
//        myFont.setTypeface(tvPointsHint);
//        myFont.setTypeface(tvUnreadCount);
//        myFont.setTypeface(tvPointsHint);
//
//        MyFont myFontBold = new MyFont(context, MyFont.LEELAWBOLD3);
//        myFontBold.setTypeface(tvName);
//        myFontBold.setTypeface(tvCreditPoints);
    }

}
