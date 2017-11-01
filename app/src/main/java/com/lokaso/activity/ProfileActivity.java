package com.lokaso.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lokaso.R;
import com.lokaso.adapter.ProfilePagerAdapter;
import com.lokaso.model.Profile;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroAction;
import com.lokaso.retromodel.RetroProfile;
import com.lokaso.retromodel.RetroResponse;
import com.lokaso.service.GPSTracker;
import com.lokaso.util.Constant;
import com.lokaso.util.Helper;
import com.lokaso.util.MyFont;
import com.lokaso.util.MyFont1;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.util.MyLog;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;
import com.lokaso.util.Picaso;
import com.lokaso.util.PopupInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProfileActivity extends AppCompatActivity {

    private String TAG = ProfileActivity.class.getSimpleName();
    private Context context = ProfileActivity.this;

    private CircleImageView ivPic;
    private TextView tvName, tvProfession, tvCreditPoints, tvDiscoveryCount, tvQueryCount, tvFollowersCount, tvAboutMe,
            tvPointsHint;
    private List<Profile> profileList = new ArrayList<>();
    private GPSTracker gps;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Profile profile;
    private NetworkConnection networkConnection;
    private AlertDialog.Builder builder;
    private ProfilePagerAdapter adapter;

    public static final int TAB_LOCAL_TIP = 0;
    public static final int TAB_QUERY = 1;
    public static final int TAB_FOLLOWER = 2;

    private int currentTab = TAB_LOCAL_TIP;

    private MyFont1 myFont;

    private String aboutMe = "";

    private Dialog dialog;
    private Button bReport, bProfile;
    private boolean checkReport = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        builder = new AlertDialog.Builder(context);
        networkConnection = new NetworkConnection(context);
        gps = new GPSTracker(context, ProfileActivity.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        ivPic = (CircleImageView) findViewById(R.id.ivPic);
        tvName = (TextView) findViewById(R.id.tvName);
        tvProfession = (TextView) findViewById(R.id.tvProfession);
        tvCreditPoints = (TextView) findViewById(R.id.tvCreditPoints);
        tvAboutMe = (TextView) findViewById(R.id.tvAboutMe);
        tvPointsHint = (TextView) findViewById(R.id.tvPointsHint);



        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        try {
            currentTab = getIntent().getIntExtra(Constant.TAB_TYPE, TAB_LOCAL_TIP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            profile = (Profile) getIntent().getSerializableExtra(Constant.PROFILE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_query_action);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.RIGHT | Gravity.TOP;
        bReport = (Button) dialog.findViewById(R.id.bReport);
        bProfile = (Button) dialog.findViewById(R.id.bFlag);
        bProfile.setText(getResources().getString(R.string.edit_profile));



        bProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(profileList!=null && profileList.size()>0) {
                    Profile profile = profileList.get(0);
                    Intent intent = new Intent(context, EditProfileActivity.class);
                    intent.putExtra(Constant.PROFILE, profile);
                    startActivity(intent);
                }
                dialog.dismiss();
            }
        });


        setTab();
        setupTabLayout();


/*
        tvAboutMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupInfo popupInfo = new PopupInfo(context, tvAboutMe);
                popupInfo.show(""+aboutMe);
            }
        });
*/


        if (toolbar != null) {

            int actionbarHeight = (int) Helper.getActionBarHeight(context);
            //toolbar_layout.setLayoutParams(new CollapsingToolbarLayout.LayoutParams(CollapsingToolbarLayout.LayoutParams.MATCH_PARENT, actionbarHeight * 2));

            toolbar.setTitle(R.string.my_profile);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            /*
            toolbar_layout.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar_layout.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            toolbar_layout.inflateMenu(R.menu.menu_option);
            toolbar_layout.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
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
            */

            toolbar.inflateMenu(R.menu.menu_profile);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.idEditProfile:
                            if (profileList.size() > 0) {
                                Profile profile = profileList.get(0);
                                Intent intent = new Intent(context, EditProfileActivity.class);
                                intent.putExtra(Constant.PROFILE, profile);
                                startActivity(intent);
                                //overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);

                            } else {
                                MyToast.tshort(context, getString(R.string.failed_to_fetch_data));
                            }
                            return true;
                    }
                    return false;
                }
            });
        }

        setData();

        setFonts();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getProfile();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //overridePendingTransition(R.anim.close_enter_animate, R.anim.close_exit_animate);
    }

    /**
     * method used to set tab
     */
    private void setTab() {
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_suggestion));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_query));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_follower));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        adapter = new ProfilePagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        viewPager.setCurrentItem(currentTab);


        ViewGroup slidingTabStrip = (ViewGroup) tabLayout.getChildAt(0);
        for (int i = 0, count = adapter.getCount(); i < count; i++) {
            AppCompatTextView view = (AppCompatTextView) ((ViewGroup) slidingTabStrip.getChildAt(i)).getChildAt(1);
//            MyFont myFont = new MyFont(context, MyFont.CENTURY_GOTHIC_BOLD1);
//            myFont.setTypeface(view);
        }

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                setCurrentTab(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getCustomView().setBackgroundColor(getResources().getColor(R.color.light_grey_background));
                TextView tvTagCount, tvTag;
                tvTagCount = (TextView) tab.getCustomView().findViewById(R.id.tvTagCount);
                tvTag = (TextView) tab.getCustomView().findViewById(R.id.tvTag);
                tvTagCount.setTextColor(getResources().getColor(R.color.black_text));
                tvTag.setTextColor(getResources().getColor(R.color.gray_7));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * method used to set tab layout
     */
    private void setupTabLayout() {
        MyFont1 myFont = new MyFont1(context);

        View view1 = LayoutInflater.from(this).inflate(R.layout.custom_profile_tab, null);
        TextView tvDiscovery = (TextView) view1.findViewById(R.id.tvTag);
        tvDiscoveryCount = (TextView) view1.findViewById(R.id.tvTagCount);

        myFont.setFont(tvDiscoveryCount, MyFont1.CENTURY_GOTHIC_BOLD);
        myFont.setFont(tvDiscovery);

        tvDiscovery.setText(getString(R.string.tab_suggestion));
        tabLayout.setPadding(0,0,0,0);
        tabLayout.getTabAt(0).setCustomView(view1);

        View view2 = LayoutInflater.from(this).inflate(R.layout.custom_profile_tab, null);
        TextView tvQuery = (TextView) view2.findViewById(R.id.tvTag);
        tvQueryCount = (TextView) view2.findViewById(R.id.tvTagCount);

        myFont.setFont(tvQueryCount, MyFont1.CENTURY_GOTHIC_BOLD);
        myFont.setFont(tvQuery);
//        myFont1.setTypeface(tvQueryCount);
//        myFont.setTypeface(tvQuery);
        tvQuery.setText(getString(R.string.tab_query));
        tabLayout.getTabAt(1).setCustomView(view2);

        View view3 = LayoutInflater.from(this).inflate(R.layout.custom_profile_tab, null);
        TextView tvFollowers = (TextView) view3.findViewById(R.id.tvTag);
        tvFollowersCount = (TextView) view3.findViewById(R.id.tvTagCount);

        myFont.setFont(tvFollowersCount, MyFont1.CENTURY_GOTHIC_BOLD);
        myFont.setFont(tvFollowers);
//        myFont1.setTypeface(tvFollowersCount);
//        myFont.setTypeface(tvFollowers);

        tvFollowers.setText(getString(R.string.tab_follower));
        tabLayout.getTabAt(2).setCustomView(view3);

        final ViewGroup test = (ViewGroup)(tabLayout.getChildAt(0));//tabs is your Tablayout
        int tabLen = test.getChildCount();

        for (int i = 0; i < tabLen; i++) {
            View v = test.getChildAt(i);
            v.setPadding(0, 0, 0, 0);
        }


//        tvDiscovery.setTextColor(getResources().getColor(R.color.white_text));
//        tvDiscoveryCount.setTextColor(getResources().getColor(R.color.white_text));
//        tabLayout.getTabAt(0).getCustomView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        TabLayout.Tab tab = tabLayout.getTabAt(currentTab);
        setCurrentTab(tab);
    }


    private void setCurrentTab(TabLayout.Tab tab) {

        tab.getCustomView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        TextView tvTagCount, tvTag;
        tvTagCount = (TextView) tab.getCustomView().findViewById(R.id.tvTagCount);
        tvTag = (TextView) tab.getCustomView().findViewById(R.id.tvTag);
        tvTagCount.setTextColor(getResources().getColor(R.color.white_text));
        tvTag.setTextColor(getResources().getColor(R.color.gray_f));
    }

    /**
     * method used to set Profile pager adapter
     */
    private void setAdapter() {
        adapter.refresh(profile.getId());
    }


    /**
     * method used to fetch profile
     */
    private void getProfile() {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().getProfile(MyPreferencesManager.getId(context), MyPreferencesManager.getId(context), new Callback<RetroProfile>() {
                @Override
                public void success(RetroProfile retroProfile, Response response) {
                    if (retroProfile.getSuccess()) {
                        profileList = retroProfile.getDetails();
                        if(profileList!=null && profileList.size()>0) {
                            profile = profileList.get(0);
                        }
                        setData();

                    } else {
                        MyToast.tshort(context, retroProfile.getMessage() + "");
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
     * method used to set data
     */
    private void setData() {

        if(profile==null)
            return;

        setAdapter();

        String imageUrl = profile.getImage();
        MyLog.e(TAG, "userImageUrl : "+imageUrl);
        Picaso.loadUser(context, imageUrl, ivPic);

        aboutMe = profile.getAbout_me();

        tvName.setText(profile.getName());
        tvProfession.setText(profile.getProfession());
        tvCreditPoints.setText(profile.getCredits_display() + "");
        tvDiscoveryCount.setText(profile.getDiscovery_count() + "");
        tvQueryCount.setText(profile.getQuery_count() + "");
        tvFollowersCount.setText(profile.getFollowing_count() + "");
        tvAboutMe.setText(profile.getAbout_me());

        if(bReport!=null) {
            if (!profile.isReport()) {
                bReport.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_report, 0, 0, 0);

            } else {
                bReport.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_report_orange, 0, 0, 0);
            }
        }

        checkReport = profile.isReport();

    }

    /**
     * method used to set fonts
     */
    private void setFonts() {
        MyFont1 myFont = new MyFont1(context);
        myFont.setAppFont((ViewGroup) findViewById(R.id.container));
    }
}
