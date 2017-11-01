package com.lokaso.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lokaso.R;
import com.lokaso.adapter.FolksAdapter;
import com.lokaso.model.Profile;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroProfile;
import com.lokaso.retromodel.RetroResponse;
import com.lokaso.service.GPSTracker;
import com.lokaso.util.Constant;
import com.lokaso.util.HideShowScrollListener;
import com.lokaso.util.MarshmallowPermission;
import com.lokaso.util.MyLog;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;
import com.lokaso.util.Picaso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FolksActivity extends BaseActivity2 {

    private String TAG = FolksActivity.class.getSimpleName();
    private Context context = FolksActivity.this;

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private Toolbar toolbar;
    private ImageView bSuggestion, bQuery, bSearch, bNotification;
    private FloatingActionButton bAddQuery;
    private RecyclerView recyclerView;
    private FolksAdapter folksAdapter;
    private GPSTracker gps;
    private MarshmallowPermission marshmallowPermission;
    private List<Profile> folksList = new ArrayList<>();
    private LinearLayoutManager llm;
    private boolean loading = false;
    private boolean loadFinish = false;
    private int scroll = 0;
    private NetworkConnection networkConnection;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<Profile> profileList = new ArrayList<>();
    private TextView tvError, tvName, tvProfession, tvDiscoveryCount, tvQueryCount, tvFollowersCount;
    private CircleImageView ivPic;
    private AlertDialog.Builder builder;
    private LinearLayout layoutBottom;

    private int checkFilter = Constant.FILTER_BY_LATEST;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folks);



        builder = new AlertDialog.Builder(context);



        llm = new LinearLayoutManager(context);
        networkConnection = new NetworkConnection(context);
        marshmallowPermission = new MarshmallowPermission(FolksActivity.this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mSwipeRefreshLayout);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        tvError = (TextView) findViewById(R.id.tvError);

        bNotification = (ImageView) findViewById(R.id.bNotification);
        bNotification.setImageResource(R.drawable.ic_notification_black);

        bSuggestion = (ImageView) findViewById(R.id.bDiscovery);
        bQuery = (ImageView) findViewById(R.id.bQuery);
        bSearch = (ImageView) findViewById(R.id.bSearch);

        bAddQuery = (FloatingActionButton) findViewById(R.id.bAddQuery);
        layoutBottom = (LinearLayout) findViewById(R.id.layoutBottom);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        nvDrawer.setItemIconTintList(null);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        recyclerView.setFadingEdgeLength(0);
        setAdapter();

        toolbar.setTitle(R.string.folks);
        setSupportActionBar(toolbar);
        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);
        setupDrawerContent(nvDrawer);

        bSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
            }
        });

        bQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picIntent = new Intent(context, QueriesActivity.class);
                picIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(picIntent);
                finish();
                overridePendingTransition(0, 0);
            }
        });

        bSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picIntent = new Intent(context, SearchActivity.class);
                picIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(picIntent);
                finish();
                overridePendingTransition(0, 0);
            }
        });

        bAddQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
//                builderSingle.setTitle(getString(R.string.select_option));
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.select_dialog_item);
                arrayAdapter.add(getString(R.string.post_query));
                arrayAdapter.add(getString(R.string.create_suggestion));
                builderSingle.setAdapter(
                        arrayAdapter,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String strName = arrayAdapter.getItem(which);
                                if (getString(R.string.post_query).equalsIgnoreCase(strName)) {
                                    Intent postIntent = new Intent(context, CreateQueryActivity.class);
                                    startActivity(postIntent);
                                    //overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);

                                } else if (getString(R.string.create_suggestion).equalsIgnoreCase(strName)) {
                                    Intent picIntent = new Intent(context, CreateSuggestionActivity.class);
                                    startActivity(picIntent);
                                    //overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);
                                }
                            }
                        });
                builderSingle.show();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = recyclerView.getChildCount();
                int totalItemCount = llm.getItemCount();
                int pastVisiblesItems = llm.findFirstVisibleItemPosition();

                if (dy >= 0 && !loading && totalItemCount != 0) {
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        loading = true;
                        MyLog.e(TAG, "Last Item Wow !");
                        if (!loadFinish) {
                            scroll++;
                            checkLocation();
                        }
                    }
                }
            }
        });

        recyclerView.addOnScrollListener(new HideShowScrollListener() {
            @Override
            public void onHide() {
                layoutBottom.animate()
                        .alpha(0.0f)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .translationY(layoutBottom.getHeight())
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                layoutBottom.setVisibility(View.GONE);
                                layoutBottom.clearAnimation();
                            }
                        })
                        .start();
            }

            @Override
            public void onShow() {
                layoutBottom.setVisibility(View.GONE);
                layoutBottom.setAlpha(0.0f);
                layoutBottom.animate()
                        .alpha(1.0f)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .translationY(0)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationStart(animation);
                                layoutBottom.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                layoutBottom.setVisibility(View.VISIBLE);
                                layoutBottom.clearAnimation();
                            }
                        })
                        .start();
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    scroll = 0;
                    checkLocation();

                    if (!networkConnection.isNetworkAvailable()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                scroll = 0;
//                checkLocation();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;

            case R.id.id_filter:
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.item_menu_folks);
                WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                wmlp.gravity = Gravity.TOP | Gravity.RIGHT;

                Button idName, idDistance, idCredit;
                idName = (Button) dialog.findViewById(R.id.idName);
                idDistance = (Button) dialog.findViewById(R.id.idDistance);
                idCredit = (Button) dialog.findViewById(R.id.idCredit);

                idName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(folksList, new Comparator<Profile>() {
                            @Override
                            public int compare(Profile lhs, Profile rhs) {
                                refreshList();
                                return lhs.getName().compareTo(rhs.getName());
                            }
                        });
                        dialog.dismiss();
                    }
                });

                idDistance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(folksList, new Comparator<Profile>() {
                            @Override
                            public int compare(Profile lhs, Profile rhs) {
                                if (lhs.getDistance() < rhs.getDistance()) {
                                    refreshList();
                                    return -1;

                                } else if (lhs.getDistance() > rhs.getDistance()) {
                                    refreshList();
                                    return 1;

                                } else {
                                    refreshList();
                                    return 0;
                                }
                            }
                        });
                        dialog.dismiss();
                    }
                });

                idCredit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Collections.sort(folksList, new Comparator<Profile>() {
                            @Override
                            public int compare(Profile lhs, Profile rhs) {
                                if (lhs.getCredits() < rhs.getCredits()) {
                                    refreshList();
                                    return 1;

                                } else if (lhs.getCredits() > rhs.getCredits()) {
                                    refreshList();
                                    return -1;

                                } else {
                                    refreshList();
                                    return 0;
                                }
                            }
                        });
                    }
                });

                dialog.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * method used to get all folks using API call
     *
     * @param latitude : Current latitude of the user
     * @param longitude : Current longitude of the user
     */
    private void getFolksData(double latitude, double longitude) {
        if (scroll == 0) {
            showProgress(false);

        } else {
            if (networkConnection.isNetworkAvailable()) {
                showProgress(true);
            }
        }
        int limit = scroll * Constant.REQUEST_LIMIT;

        RestClient.getLokasoApi().getFolks(latitude, longitude, Constant.RANGE, MyPreferencesManager.getId(context),
                limit, scroll,
                checkFilter,
                new Callback<RetroProfile>() {
                    @Override
                    public void success(RetroProfile retroFolks, Response response) {
                        if (retroFolks.getSuccess()) {
//                            folksList = retroFolks.getDetails();
//                            setAdapter();
                            if (scroll == 0) {
                                folksList = new ArrayList<>();
                            }

                            tvError.setVisibility(View.GONE);
                            folksList.addAll(retroFolks.getDetails());
                            refreshList();

                        } else {
//                            MyToast.tshort(context, retroFolks.getMessage() + "");
                            if (folksList.size() == 0) {
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

    private void showProgress(boolean b) {
        /*try {
            if (b) {
                NewsDisplay display = new NewsDisplay(NewsDisplayAdapter.TYPE_LOADER);
                newsDisplayList.add(display);
            } else {
                if (newsDisplayList.size() != 0) {
                    if (newsDisplayList.get(newsDisplayList.size() - 1).getType() == 3) {
                        newsDisplayList.remove(newsDisplayList.size() - 1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        refreshList();
    }

    /**
     * method used to set folks adapter
     */
    private void setAdapter() {
        folksAdapter = new FolksAdapter(context, folksList);
        recyclerView.setAdapter(folksAdapter);
        //refreshList();

        folksAdapter.setOnClickListener(new FolksAdapter.ClickInterface() {
            @Override
            public void onItemClick(int folks_id) {
                Intent intent = new Intent(context, OthersProfileActivity.class);
                intent.putExtra(Constant.PROFILE_ID, folks_id);
                startActivity(intent);
                //overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);
            }

            @Override
            public void onFollowClick(int action, int leader, int follower, int position) {
                setFollow(action, leader, follower, position);
            }
        });
    }

    /**
     * method used to refresh folks adapter
     */
    private void refreshList() {
        mSwipeRefreshLayout.setRefreshing(false);
        folksAdapter.refresh(folksList);
        loading = false;
    }

    /**
     * method used to follow a folk using API call
     *
     * @param action : action = 1 to follow and action = 0 to un-follow
     * @param leader : user id of the user who is being followed
     * @param follower : user id of the user who will be following
     */
    private void setFollow(final int action, int leader, int follower, final int position) {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().follow(action, leader, follower, new Callback<RetroResponse>() {
                @Override
                public void success(RetroResponse retroResponse, Response response) {
//                MyToast.tshort(context, retroResponse.getMessage() + "");
                    if (action == Constant.FAV) {
                        folksList.get(position).setUser_followed(true);
                    } else {
                        folksList.get(position).setUser_followed(false);
                    }
                    folksAdapter.refresh(folksList);
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
    protected void onStart() {
        super.onStart();
        if (!marshmallowPermission.checkPermissionForLocation()) {
            marshmallowPermission.requestPermissionForLocation();

        } else {
            gps = new GPSTracker(context, FolksActivity.this);
            scroll = 0;
            checkLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MarshmallowPermission.FINE_LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    gps = new GPSTracker(context, FolksActivity.this);
                    scroll = 0;
                    checkLocation();
                }
                return;
            }

            default:
                return;
        }
    }

    /**
     * method used to check location
     */
    private void checkLocation() {
        if (networkConnection.isNetworkAvailable()) {
            if (gps.canGetLocation()) {
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

                if (latitude > 0 && longitude > 0) {
                    getFolksData(latitude, longitude);
                } /*else {
                MyToast.tshort(context, "Unable to get your location");
                checkLocation();
            }*/
            } else {
                showSettingsAlert();
            }
        } else {
            MyToast.tshort(context, getString(R.string.check_network));
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * method used to set actionbar drawer toggle
     *
     * @return
     */
    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(FolksActivity.this, mDrawer, toolbar,
                R.string.app_name,
                R.string.app_name);
    }

    /**
     * method used to setup drawer content
     *
     * @param navigationView
     */

    /*
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });


        View hearderView = navigationView; // navigationView.getHeaderView(0);

        tvName = (TextView) hearderView.findViewById(R.id.tvName);
        tvProfession = (TextView) hearderView.findViewById(R.id.tvProfession);
        tvDiscoveryCount = (TextView) hearderView.findViewById(R.id.tvDiscoveryCount);
        tvQueryCount = (TextView) hearderView.findViewById(R.id.tvQueryCount);
        tvFollowersCount = (TextView) hearderView.findViewById(R.id.tvFollowersCount);
        TextView tvDiscovery = (TextView) hearderView.findViewById(R.id.tvDiscovery);
        TextView tvQuery = (TextView) hearderView.findViewById(R.id.tvQuery);
        TextView tvFollowers = (TextView) hearderView.findViewById(R.id.tvFollowers);
        ivPic = (CircleImageView) hearderView.findViewById(R.id.ivPic);
        MyFont myFont = new MyFont(context);
        myFont.setTypeface(tvName);
        myFont.setTypeface(tvProfession);
        myFont.setTypeface(tvDiscoveryCount);
        myFont.setTypeface(tvQueryCount);
        myFont.setTypeface(tvFollowersCount);
        myFont.setTypeface(tvDiscovery);
        myFont.setTypeface(tvQuery);
        myFont.setTypeface(tvFollowers);

        try {
            String imageUrl = MyPreferencesManager.getImage(context);
            MyLog.e(TAG, "userImageUrl : "+imageUrl);
            Picaso.loadUser(context, imageUrl, ivPic);

            tvName.setText(MyPreferencesManager.getName(context));
            tvProfession.setText(MyPreferencesManager.getProfession(context));
            tvDiscoveryCount.setText(MyPreferencesManager.getDiscovery_count(context) + "");
            tvQueryCount.setText(MyPreferencesManager.getQuery_count(context) + "");
            tvFollowersCount.setText(MyPreferencesManager.getFollowing_count(context) + "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        getProfile();

        hearderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                startActivity(intent);
                //overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);
                mDrawer.closeDrawers();
            }
        });
    }
*/
    /**
     * method used to select drawer item
     *
     * @param menuItem
     */
    public void selectDrawerItem(final MenuItem menuItem) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (menuItem.getItemId()) {
                    case R.id.nav_notification:
                        Intent intent2 = new Intent(context, NotificationActivity.class);
                        startActivity(intent2);
                        //overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);
                        break;

                    case R.id.nav_chats:
                        Intent intent3 = new Intent(context, ChatRoomActivity.class);
                        startActivity(intent3);
                        //overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);
                        break;

                    case R.id.nav_save_discovery:
                        Intent intent5 = new Intent(context, SavedDiscoveriesActivity.class);
                        startActivity(intent5);
                        //overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);
                        break;

                    case R.id.nav_logout:
                        MyPreferencesManager.logOutUser(context);
                        Intent intent4 = new Intent(context, LoginActivity.class);
                        startActivity(intent4);
                        //overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);
                        finish();
                        break;
                }
            }
        }, 200);

        mDrawer.closeDrawers();
    }

    /**
     * method used to fetch profile of current user using API call
     */
    private void getProfile() {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().getProfile(MyPreferencesManager.getId(context), MyPreferencesManager.getId(context), new Callback<RetroProfile>() {
                @Override
                public void success(RetroProfile retroProfile, Response response) {
                    if (retroProfile.getSuccess()) {
                        profileList = retroProfile.getDetails();
                        setProfileData();

                    } else {
                        MyToast.tshort(context, retroProfile.getMessage() + "");
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
     * method used to set profile data
     */
    private void setProfileData() {
        Profile profile = profileList.get(0);

        String imageUrl = profile.getImage();
        MyLog.e(TAG, "userImageUrl : "+imageUrl);
        Picaso.loadUser(context, imageUrl, ivPic);


        tvName.setText(profile.getName());
        tvProfession.setText(profile.getProfession());
        tvDiscoveryCount.setText(profile.getDiscovery_count() + "");
        tvQueryCount.setText(profile.getQuery_count() + "");
        tvFollowersCount.setText(profile.getFollowing_count() + "");

        MyPreferencesManager.saveEmail(context, profile.getEmail());
        MyPreferencesManager.saveImage(context, profile.getImage());
        MyPreferencesManager.saveName(context, profile.getName());
        MyPreferencesManager.setUserLocation(context, profile.getLocation());
        MyPreferencesManager.saveProfession(context, profile.getProfession());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}
