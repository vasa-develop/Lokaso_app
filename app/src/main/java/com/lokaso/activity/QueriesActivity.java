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
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.lokaso.R;
import com.lokaso.adapter.QueriesAdapter;
import com.lokaso.custom.EndlessRecyclerViewScrollListener;
import com.lokaso.model.Profile;
import com.lokaso.model.Queries;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.preference.SuggestionPreference;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroQueries;
import com.lokaso.retromodel.RetroResponse;
import com.lokaso.service.GPSTracker;
import com.lokaso.util.Constant;
import com.lokaso.util.HideShowScrollListener;
import com.lokaso.util.MarshmallowPermission;
import com.lokaso.util.MyLog;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class QueriesActivity extends BaseActivity2 {

    private String TAG = QueriesActivity.class.getSimpleName();
    private Context context = QueriesActivity.this;

    private ActionBarDrawerToggle drawerToggle;
    private NavigationView nvDrawer;
    private Toolbar toolbar;
    private ImageView bDiscovery, bNotification, bQuery, bSearch;
    private FloatingActionButton bAddQuery;

    private EndlessRecyclerViewScrollListener scrollListener;
    private LinearLayoutManager llm;
    private RecyclerView recyclerView;

    private List<Queries> queriesList = new ArrayList<>();
    private QueriesAdapter queriesAdapter;

    private boolean loading = false;
    private boolean loadFinish = false;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView tvError;
    private CardView tvGps;
    private LinearLayout layoutBottom, layoutDiscovery, layoutQuery, layoutSearch, layoutNotification;

    private RelativeLayout progress_layout;

    private int scroll = 0;

    private int checkFilter = Constant.FILTER_BY_LATEST;
    private String interest_ids = "";

    private TextView toolbarTitle;
    private ImageView locationImage, filterImage;
    private ImageView drawerDummyImage;


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queries);

        networkConnection = new NetworkConnection(context);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        locationImage = (ImageView) findViewById(R.id.locationImage);
        filterImage = (ImageView) findViewById(R.id.filterImage);
        drawerDummyImage = (ImageView) findViewById(R.id.drawerDummyImage);

        progress_layout = (RelativeLayout) findViewById(R.id.progress_layout);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mSwipeRefreshLayout);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        tvError = (TextView) findViewById(R.id.tvError);
        tvGps = (CardView) findViewById(R.id.tvGps);

        bQuery = (ImageView) findViewById(R.id.bQuery);
        bQuery.setImageResource(R.drawable.ic_query_black);

        bDiscovery = (ImageView) findViewById(R.id.bDiscovery);
        bNotification = (ImageView) findViewById(R.id.bNotification);
        bSearch = (ImageView) findViewById(R.id.bSearch);

        bAddQuery = (FloatingActionButton) findViewById(R.id.bAddQuery);
        layoutBottom = (LinearLayout) findViewById(R.id.layoutBottom);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        nvDrawer.setItemIconTintList(null);

        if(MyPreferencesManager.getQueryFilter(context)==0) {
            MyPreferencesManager.setQueryFilter(context, Constant.FILTER_BY_LATEST);
        }

        checkFilter = MyPreferencesManager.getQueryFilter(context);
        setQueryFilterBy(checkFilter);

        llm = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(llm);
        recyclerView.hasFixedSize();
        recyclerView.setFadingEdgeLength(0);
        setAdapter();

        toolbar.setTitle(R.string.queries_around);
        setSupportActionBar(toolbar);
        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);
        setupDrawerContent(nvDrawer);

        layoutNotification = (LinearLayout) findViewById(R.id.layoutNotification);
        layoutDiscovery = (LinearLayout) findViewById(R.id.layoutDiscovery);
        layoutSearch = (LinearLayout) findViewById(R.id.layoutSearch);

        layoutNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picIntent = new Intent(context, NotificationActivity.class);
                picIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(picIntent);
                finish();
                overridePendingTransition(0, 0);
            }
        });

        layoutDiscovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
            }
        });

        layoutSearch.setOnClickListener(new View.OnClickListener() {
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

                scroll = page; //scroll++;
                checkLocation();
            }
        };
        // Adds the scroll listener to RecyclerView
        recyclerView.addOnScrollListener(scrollListener);


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
            }
        });

        setListener();

    }


    private void setListener() {

        locationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LocationActivity.class);
                intent.putExtra(Constant.FROM, LocationActivity.FROM_QUERY);
                startActivityForResult(intent, Constant.SELECTION_LOCATION);
            }
        });

        filterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Profile profile = null;
                if (profileList != null && profileList.size() > 0) {
                    profile = profileList.get(0);
                }

                Intent intent = new Intent(context, FilterActivity.class);
                intent.putExtra(Constant.PROFILE, profile);
                intent.putExtra(Constant.TYPE, FilterActivity.TYPE_QUERY);
                startActivityForResult(intent, Constant.SELECTION_FILTER);
                //overridePendingTransition();

            }
        });
    }

    private void setQueryFilterBy(int filterBy) {

        scroll = 0;
        String filterByStr = "";
        if(filterBy == Constant.FILTER_BY_DISTANCE) {
            filterByStr = "by distance";
        }
        else if(filterBy == Constant.FILTER_BY_EXPIRING) {
            filterByStr = "by Expiring soon";
        }
        else {
            filterByStr = "by latest";
        }
        //toolbar.setTitle("Queries Around ("+filterByStr+")");
        toolbarTitle.setText("Queries Around ("+filterByStr+")");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case Constant.SELECTION_FILTER:
            {
                if(data!=null) {
                    interest_ids = data.getStringExtra(Constant.FILTER_INTEREST);
                    checkFilter = data.getIntExtra(Constant.FILTER_SORT_BY, checkFilter);
                    MyLog.e(TAG, "onActivityResult : interest_ids:"+interest_ids+" ,checkFilter:"+checkFilter);

                    scroll = 0;
                    checkLocation();
                }
            }
            break;

            case Constant.SELECTION_LOCATION:
                if (data!=null && data.hasExtra(Constant.PLACE))
                {
                    com.lokaso.model.Place place = (com.lokaso.model.Place) data.getSerializableExtra(Constant.PLACE);
                    if (place.getName() != null) {
                        String lat = place.getLatitude();
                        String lng = place.getLongitude();
                        String location = "";
                        try {
                            location = ""+place.getLocation();
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

                        queriesList = new ArrayList<>();
                        scroll = 0;
                        refreshList();
                        checkLocation();

                    } else {
                        MyToast.tshort(context, "Please select location");
                    }
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        tvGps.setVisibility(View.GONE);
        scroll = 0;
        checkLocation();
        setupDrawerContent(nvDrawer);
        setQueryFilterBy(checkFilter);
    }

    /**
     * method used to check location
     */
    private void checkLocation() {

        double lat = MyPreferencesManager.getLat(context);
        double lon = MyPreferencesManager.getLng(context);

        MyLog.e(TAG, "checkLocation : "
                +" , "+lat
                +" , "+lon);

        setLocation(MyPreferencesManager.getLocationSelected(context), false);

        getQueriesData(lat, lon);

        if(true)
            return;


        tvGps.setVisibility(View.GONE);

    }

    /**
     * method used to fetch queries
     *
     * @param latitude  : Current latitude
     * @param longitude : Current longitude
     */
    private void getQueriesData(double latitude, double longitude) {
        if (scroll == 0) {
            showProgress(false);

        } else {
            if (networkConnection.isNetworkAvailable()) {
                showProgress(true);
            }
        }
        int limit = scroll * Constant.REQUEST_LIMIT;

        RestClient.getLokasoApi().getQueries(
                latitude, longitude,
                Constant.RANGE,
                MyPreferencesManager.getId(context),
                limit,scroll,
                checkFilter,
                interest_ids,
                new Callback<RetroQueries>() {
                    @Override
                    public void success(RetroQueries retroQueries, Response response) {
                        if (retroQueries.getSuccess()) {

                            if (scroll == 0) {
                                queriesList = new ArrayList<>();
                            }

                            tvError.setVisibility(View.GONE);
                            queriesList.addAll(retroQueries.getDetails());
                            refreshList();
                            //setFilter();

                        } else {
//                            MyToast.tshort(context, retroQueries.getMessage() + "");
                            if (queriesList.size() == 0) {
                                tvError.setText(R.string.queries_not_found);
                                tvError.setVisibility(View.VISIBLE);
                            }
                        }
                        progress_layout.setVisibility(View.GONE);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        MyToast.tshort(context, "" + error);
                        progress_layout.setVisibility(View.GONE);
                    }
                });
    }

    private void showProgress(boolean b) {
        refreshList();
    }

    /**
     * method used to set queries adapter
     */
    private void setAdapter() {
        queriesAdapter = new QueriesAdapter(context, queriesList);
        recyclerView.setAdapter(queriesAdapter);

        queriesAdapter.setOnClickListener(new QueriesAdapter.ClickInterface() {
            @Override
            public void itemClick(Queries queries) {
                Intent intent = new Intent(context, ViewQueryActivity.class);
                intent.putExtra(Constant.INTENT_QUERIES, queries);
                startActivity(intent);
                //overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);
            }

            @Override
            public void followQuery(int action, int query_id) {
                setfollowQuery(action, query_id);
            }
        });
    }

    /**
     * method used to refresh list
     */
    private void refreshList() {
        if(queriesList.size()>0) {
            tvGps.setVisibility(View.GONE);
        }

        mSwipeRefreshLayout.setRefreshing(false);
        queriesAdapter.refresh(queriesList);
        loading = false;
    }

    /**
     * method used to follow query
     *
     * @param action   action = 1 to follow query and action = 0 to un-follow query
     * @param query_id Query id
     */
    private void setfollowQuery(int action, int query_id) {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().followQuery(action, query_id, MyPreferencesManager.getId(context),
                    new Callback<RetroResponse>() {
                        @Override
                        public void success(RetroResponse retroResponse, Response response) {
                            MyToast.tshort(context, retroResponse.getMessage() + "");
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
     * @return method used to set actionbar drawer toggle
     */
    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(QueriesActivity.this, mDrawer, toolbar,
                R.string.app_name,
                R.string.app_name);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}
