package com.lokaso.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.lokaso.R;
import com.lokaso.adapter.SuggestionAdapter;
import com.lokaso.custom.EndlessRecyclerViewScrollListener;
import com.lokaso.gcm.MyFirebaseMessagingService;
import com.lokaso.gcm.QuickstartPreferences;
import com.lokaso.model.Ads;
import com.lokaso.model.Profile;
import com.lokaso.model.Suggestion;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.preference.SuggestionPreference;
import com.lokaso.preference.TourPreference;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroSuggestion;
import com.lokaso.service.GPSTracker;
import com.lokaso.util.Constant;
import com.lokaso.util.HideShowScrollListener;
import com.lokaso.util.MarshmallowPermission;
import com.lokaso.util.MyDialog;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyLog;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;
import com.lokaso.util.TourClass;
import com.transitionseverywhere.Rotate;
import com.transitionseverywhere.TransitionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SuggestionActivity extends BaseActivity2 {

    private String TAG = SuggestionActivity.class.getSimpleName();
    private Context context = SuggestionActivity.this;

    private ActionBarDrawerToggle drawerToggle;
    private NavigationView nvDrawer;
    private Toolbar toolbar;
    private ImageView bDiscovery, bNotification, bQuery, bSearch;
    private FloatingActionButton bAddQuery;

    private EndlessRecyclerViewScrollListener scrollListener;
    private RecyclerView recyclerView;
    private LinearLayoutManager llm;

    private List<Suggestion> suggestionList = new ArrayList<>();
    private SuggestionAdapter suggestionAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean loading = false;
    private boolean loadFinish = false;
    private int scroll = 0;

    private CardView errLayout;
    private TextView tvError;
    private Button postButton, viewAllButton;

    private CardView tvGps;
    private LinearLayout layoutBottom, layoutDiscovery, layoutQuery, layoutSearch, layoutNotification;
    private RelativeLayout progress_layout;

    private TextView toolbarTitle;
    private ImageView locationImage, filterImage;
    private ImageView drawerDummyImage;

    private TextView debugView;


    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;

    private boolean checkLocation = false;

    private boolean isPostDialogVisible = false;

    private boolean waitingForLatLng = true;

    private int checkFilter = Constant.FILTER_BY_LATEST;
    private String interest_ids = "";

    private int searchType = Constant.TYPE_SEARCH_NORMAL;

    private MyFont1 myFont;

    private TourClass tourAdd, tourFilter, tourLocation, tourDrawer;


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean sentToken = MyPreferencesManager.sentTokenToServer(context);
                if (sentToken) {
                    MyLog.e(TAG, getString(R.string.gcm_send_message));
                } else {
                    MyLog.e(TAG, getString(R.string.token_error_message));
                }
            }
        };

        registerReceiver();

        networkConnection = new NetworkConnection(context);


        debugView = (TextView) findViewById(R.id.debugView);
/*
        if(Constant.DEBUG || Constant.DEV_MODE) {
            debugView.setVisibility(View.VISIBLE);
            String server = (Constant.LOCAL_MODE) ? "LOCALHOST" : "TARGETPROGRESS";
            if(Constant.DEBUG && Constant.DEV_MODE) {
                debugView.setText("FULL DEBUG DEV MODE\n"+server);
            }
            else if(Constant.DEBUG) {
                debugView.setText("DEBUG LOGS ENABLED\n" + "LIVE");
            }
            else {
                debugView.setText("DEV MODE\n" + server);
            }
        }
        else {
            debugView.setVisibility(View.GONE);
        }
*/

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        locationImage = (ImageView) findViewById(R.id.locationImage);
        filterImage = (ImageView) findViewById(R.id.filterImage);
        drawerDummyImage = (ImageView) findViewById(R.id.drawerDummyImage);

        progress_layout = (RelativeLayout) findViewById(R.id.progress_layout);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mSwipeRefreshLayout);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);

        errLayout = (CardView) findViewById(R.id.errLayout);
        tvError = (TextView) findViewById(R.id.tvError);
        postButton = (Button) findViewById(R.id.postButton);
        viewAllButton = (Button) findViewById(R.id.viewAllButton);
        tvGps = (CardView) findViewById(R.id.tvGps);

        bDiscovery = (ImageView) findViewById(R.id.bDiscovery);
        bDiscovery.setImageResource(R.drawable.ic_discover_black);

        bNotification = (ImageView) findViewById(R.id.bNotification);
        bQuery = (ImageView) findViewById(R.id.bQuery);
        bSearch = (ImageView) findViewById(R.id.bSearch);

        bAddQuery = (FloatingActionButton) findViewById(R.id.bAddQuery);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutBottom = (LinearLayout) findViewById(R.id.layoutBottom);

        nvDrawer.setItemIconTintList(null);

        if(MyPreferencesManager.getSuggestionFilter(context)==0) {
            MyPreferencesManager.setSuggestionFilter(context, Constant.FILTER_BY_LATEST);
        }

        checkFilter = MyPreferencesManager.getSuggestionFilter(context);
        setSuggestionFilterBy(checkFilter);

        llm = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(llm);
        recyclerView.hasFixedSize();
        recyclerView.setFadingEdgeLength(0);
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        animator.setAddDuration(700);
        animator.setRemoveDuration(700);
        recyclerView.setItemAnimator(animator);
        setAdapter();

        layoutDiscovery = (LinearLayout) findViewById(R.id.layoutDiscovery);
        layoutNotification = (LinearLayout) findViewById(R.id.layoutNotification);
        layoutQuery = (LinearLayout) findViewById(R.id.layoutQuery);
        layoutSearch = (LinearLayout) findViewById(R.id.layoutSearch);

        layoutNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picIntent = new Intent(context, NotificationActivity.class);
                picIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(picIntent);
            }
        });

        layoutQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picIntent = new Intent(context, QueriesActivity.class);
                picIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(picIntent);
            }
        });

        layoutSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picIntent = new Intent(context, SearchActivity.class);
                picIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(picIntent);
            }
        });

        toolbar.setTitle(R.string.local_tips);
        setSupportActionBar(toolbar);

        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);
        setupDrawerContent(nvDrawer);

        // Apply fonts
        myFont = new MyFont1(context);
        myFont.setAppFont(mDrawer);

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(llm) {
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

            @Override
            public void onLoadMore(int page, int totalItemsCount, int visiblePosition, RecyclerView view) {
                MyLog.e(TAG, "page : "+page+ " , totalItemCount : "+totalItemsCount);

                if(visiblePosition>10) {
                    if(!SuggestionPreference.isSuggestionSeen(context)) {
                        showSuggestionDialog();
                    }
                }

                scroll = page; //scroll++;
                checkLocation();
            }
        };
        // Adds the scroll listener to RecyclerView
        recyclerView.addOnScrollListener(scrollListener);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent picIntent = new Intent(context, CreateSuggestionActivity.class);
                startActivity(picIntent);
            }
        });
        viewAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchType = Constant.TYPE_SEARCH_ALL;
                checkLocation();
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    scroll = 0;
                    searchType = Constant.TYPE_SEARCH_NORMAL;
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
                searchType = Constant.TYPE_SEARCH_NORMAL;
            }
        });

        // Show First chat notification
        if(!MyPreferencesManager.isFirstChatNotificationSeen(context)) {
            MyFirebaseMessagingService.sendNotificationFirstChat(context);
        }

        setListener();


        // Tour
        tourAdd = new TourClass(SuggestionActivity.this).click().topRight().isTopMost()
                .message(getString(R.string.tour_home_add_tip_desc))
                .anchor(bAddQuery)
                .setOnClickListener(new TourClass.OnClickListener() {
                    @Override
                    public void onClick(TourClass tour) {
                        tour.dismiss();

                        TourPreference.setHomeAddTipSeen(context, true);

                        tourDrawer.show();
                        //tourLocation.show();
                    }
                });

        tourDrawer = new TourClass(SuggestionActivity.this).click().bottomRight().isTopMost()
                .message(getString(R.string.tour_home_drawer_desc))
                .anchor(drawerDummyImage)
                .setOnClickListener(new TourClass.OnClickListener() {
                    @Override
                    public void onClick(TourClass tour) {
                        tour.dismiss();
                        TourPreference.setHomeDrawerSeen(context, true);
                        drawerDummyImage.setVisibility(View.GONE);
                        tourLocation.show();
                    }
                });

        tourLocation = new TourClass(SuggestionActivity.this).click().bottomLeft().isTopMost()
                .message(getString(R.string.tour_home_location))
                .anchor(locationImage)
                .setOnClickListener(new TourClass.OnClickListener() {
                    @Override
                    public void onClick(TourClass tour) {
                        tour.dismiss();
                        TourPreference.setHomeLocationSeen(context, true);
                        tourFilter.show();
                    }
                });

        tourFilter = new TourClass(SuggestionActivity.this).click().bottomLeft().isTopMost()
                .message(getString(R.string.tour_home_filter))
                .anchor(filterImage)
                .setOnClickListener(new TourClass.OnClickListener() {
                    @Override
                    public void onClick(TourClass tour) {
                        tour.dismiss();
                        TourPreference.setHomeFilterSeen(context, true);
                        setListener();
                    }
                });

        if(!TourPreference.isHomeAddTipSeen(context)) {
            drawerDummyImage.setVisibility(View.VISIBLE);
            tourAdd.show();
        }
        else if(!TourPreference.isHomeDrawerSeen(context)) {
            drawerDummyImage.setVisibility(View.VISIBLE);
            tourDrawer.show();
        }
        else if(!TourPreference.isHomeLocationSeen(context)) {
            tourLocation.show();
        }
        else if(!TourPreference.isHomeFilterSeen(context)) {
            tourFilter.show();
        }
    }


    private void setListener() {

        locationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LocationActivity.class);
                intent.putExtra(Constant.FROM, LocationActivity.FROM_SUGGESTION);
                startActivityForResult(intent, Constant.SELECTION_LOCATION);
            }
        });

        filterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Profile profile = null;
                if(profileList!=null && profileList.size()>0) {
                    profile = profileList.get(0);
                }

                Intent intent = new Intent(context, FilterActivity.class);
                intent.putExtra(Constant.PROFILE, profile);
                intent.putExtra(Constant.TYPE, FilterActivity.TYPE_SUGGESTION);
                startActivityForResult(intent, Constant.SELECTION_FILTER);
                //overridePendingTransition();
            }
        });

        bAddQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SuggestionPreference.setSuggestionSeen(context, false);

                isPostDialogVisible = true;
                togglePostDialog();

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
                builderSingle.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        isPostDialogVisible = false;
                        togglePostDialog();
                    }
                });
                builderSingle.show();

            }
        });
    }

    private void showSuggestionDialog() {

        MyLog.e(TAG, "showSuggestionDialog");

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setCancelable(false);
        builderSingle.setTitle(getString(R.string.dialog_suggestion_seen_title));
        builderSingle.setMessage(getString(R.string.dialog_suggestion_seen_message));
        builderSingle.setPositiveButton("" + getString(R.string.dialog_suggestion_seen_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(context, CreateQueryActivity.class);
                startActivity(intent);
            }
        });
        builderSingle.setNegativeButton(getString(R.string.dialog_suggestion_seen_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builderSingle.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });
        builderSingle.show();

        SuggestionPreference.setSuggestionSeen(context, true);
    }

    private void togglePostDialog() {

        TransitionManager.beginDelayedTransition(layoutBottom, new Rotate());
        bAddQuery.setRotation(isPostDialogVisible ? 45 : 0);

    }

    /**
     * set background color of filter
     */
    private void setSuggestionFilterBy(int filterBy) {

        String by = "";
        if(filterBy == Constant.FILTER_BY_DISTANCE) {
            by = "by distance";
        }
        else {
            by = "by latest";
        }
        //toolbar_layout.setTitle("Local tips ("+by+")");
        toolbarTitle.setText("Local tips ("+by+")");
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();

        tvGps.setVisibility(View.GONE);
        //scroll = 0;
        MyLog.e(TAG, " onStart permision given ");
        checkLocation();
        setupDrawerContent(nvDrawer);
        setSuggestionFilterBy(checkFilter);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    /**
     * method used to register receiver
     */
    private void registerReceiver() {
        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }

    /**
     * method used to set discovery adapter
     */
    private void setAdapter() {
        suggestionAdapter = new SuggestionAdapter(SuggestionActivity.this, suggestionList);
        recyclerView.setAdapter(suggestionAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                if(!TourPreference.isHomeDrawerSeen(context)) {
                    tourDrawer.dismiss();
                    TourPreference.setHomeDrawerSeen(context, true);
                    tourLocation.show();
                    return true;
                }
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case Constant.SELECTION_FILTER: {
                if (data != null) {
                    interest_ids = data.getStringExtra(Constant.FILTER_INTEREST);
                    checkFilter = data.getIntExtra(Constant.FILTER_SORT_BY, checkFilter);
                    MyLog.e(TAG, "onActivityResult : interest_ids:" + interest_ids + " ,checkFilter:" + checkFilter);

                    searchType = Constant.TYPE_SEARCH_NORMAL;
                    scroll = 0;
                    checkLocation();
                }
            }
            break;

            case Constant.SELECTION_LOCATION: {
                if (data != null && data.hasExtra(Constant.PLACE)) {
                    com.lokaso.model.Place place = (com.lokaso.model.Place) data.getSerializableExtra(Constant.PLACE);
                    if (place.getName() != null) {
                        String lat = place.getLatitude();
                        String lng = place.getLongitude();
                        String location = "";
                        try {
                            location = "" + place.getLocation();
                            MyLog.e(TAG, "Place name : " + place.getName());
                            MyLog.e(TAG, "latitude : " + lat);
                            MyLog.e(TAG, "longitude : " + lng);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // Set the location to preferences
                        MyPreferencesManager.saveLocationSelected(context, location);
                        MyPreferencesManager.saveLat(context, lat);
                        MyPreferencesManager.saveLng(context, lng);

                        setLocation(location, false);

                        // When distance is selected sort by distance
                        checkFilter = Constant.FILTER_BY_DISTANCE;

                        suggestionList = new ArrayList<>();
                        scroll = 0;
                        searchType = Constant.TYPE_SEARCH_NORMAL;
                        refreshList();
                        checkLocation();

                    } else {
                        MyToast.tshort(context, "Please select location");
                    }
                }
            }
            break;

            default:
                break;
        }
    }

    /**
     * method used to refresh the discovery adapter
     */
    private void refreshList() {
        if(suggestionList.size()>0) {
            tvGps.setVisibility(View.GONE);
        }

        mSwipeRefreshLayout.setRefreshing(false);
        suggestionAdapter.refresh(suggestionList);
        loading = false;
    }

    /**
     * method used to check current location
     */
    private void checkLocation() {

        double lat = MyPreferencesManager.getLat(context);
        double lon = MyPreferencesManager.getLng(context);

        MyLog.e(TAG, "checkLocation : "
                +" , "+lat
                +" , "+lon);

        setLocation(MyPreferencesManager.getLocationSelected(context), false);

        getSuggestions(lat, lon);
    }

    /**
     * method used to fetch all discoveries using API call
     *
     * @param latitude  Current latitude
     * @param longitude Current longitude
     */
    private void getSuggestions(double latitude, double longitude) {
        if (scroll == 0) {
            showProgress(false);
        } else {
            if (networkConnection.isNetworkAvailable()) {
                showProgress(true);
            }
        }

        int position = scroll;

        int limit = scroll * Constant.REQUEST_LIMIT;

        MyLog.e(TAG, "getSuggestion : list("+suggestionList.size()+") lat:("+latitude+","+longitude+") ,p"+position+", f:"+checkFilter+" ,intrst:["+interest_ids+"]");

        RestClient.getLokasoApi().getSuggestion(
                latitude, longitude,
                Constant.RANGE,
                MyPreferencesManager.getId(context),
                limit,position,
                checkFilter,
                interest_ids,
                searchType,
                new Callback<RetroSuggestion>() {
                    @Override
                    public void success(RetroSuggestion retroSuggestion, Response response) {
                        if (retroSuggestion.getSuccess()) {
                            if (scroll == 0) {
                                suggestionList = new ArrayList<>();
                            }

                            List<Suggestion> list = retroSuggestion.getDetails();
                            if(list.size()==0) {
                                loadFinish = true;
                            }
                            else {
                                loadFinish = false;
                            }

                            errLayout.setVisibility(View.GONE);
                            suggestionList.addAll(list);

                            MyLog.e(TAG, "suggestionListSIZEE : "+suggestionList.size());

                            // Ads list
                            List<Ads> adslist = retroSuggestion.getAds_list();
                            int ad_position = retroSuggestion.getAd_position();
                            if (scroll == 0) {

                                if(adslist==null)
                                    adslist = new ArrayList<>();

                                if(suggestionList.size()<ad_position) {
                                    ad_position=suggestionList.size();
                                }

                                if(adslist.size()>0) {
                                    Suggestion suggestion = new Suggestion(adslist, SuggestionAdapter.TYPE_AD);
                                    suggestionList.add(ad_position, suggestion);
                                }
                            }
/*
                            int listSize = suggestionList.size();
                            if(searchType==Constant.TYPE_SEARCH_NORMAL && listSize>1 && loadFinish) {
                                int position = listSize;
                                searchType = Constant.TYPE_SEARCH_ALL;
                                Suggestion suggestion = new Suggestion(SuggestionAdapter.TYPE_SUGGESTION_VIEW_ALL);
                                suggestionList.add(position, suggestion);
                            }
*/
                            for (int i=0; i<suggestionList.size(); i++) {
                                MyLog.e(TAG, "tyyype : "+suggestionList.get(i).getType());
                            }

                            refreshList();

                        } else {
                            if (suggestionList.size() == 0) {
                                tvError.setText(R.string.suggestion_empty);
                                errLayout.setVisibility(View.VISIBLE);
                                loadFinish = true;
                            }
                        }
                        progress_layout.setVisibility(View.GONE);
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        //MyToast.tshort(context, error);
                        progress_layout.setVisibility(View.GONE);
                    }
                });
    }

    private void showProgress(boolean b) {
        refreshList();
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

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(SuggestionActivity.this, mDrawer, toolbar,
                R.string.app_name,
                R.string.app_name)
        ;
        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TourPreference.isHomeDrawerSeen(context)) {
                    tourDrawer.dismiss();
                    TourPreference.setHomeDrawerSeen(context, true);
                    tourLocation.show();
                }
            }
        });

        return actionBarDrawerToggle;
    }
}
