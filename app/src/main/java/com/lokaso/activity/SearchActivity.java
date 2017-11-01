package com.lokaso.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import com.lokaso.adapter.FolksAdapter;
import com.lokaso.adapter.QueriesAdapter;
import com.lokaso.adapter.SuggestionAdapter;
import com.lokaso.model.Action;
import com.lokaso.model.Profile;
import com.lokaso.model.Queries;
import com.lokaso.model.Suggestion;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.preference.TourPreference;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroAction;
import com.lokaso.retromodel.RetroProfile;
import com.lokaso.retromodel.RetroQueries;
import com.lokaso.retromodel.RetroResponse;
import com.lokaso.retromodel.RetroSuggestion;
import com.lokaso.service.GPSTracker;
import com.lokaso.util.Constant;
import com.lokaso.util.HideShowScrollListener;
import com.lokaso.util.MarshmallowPermission;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyLog;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;
import com.lokaso.util.Picaso;
import com.lokaso.util.TourClass;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

public class SearchActivity extends BaseActivity2 {

    private String TAG = SearchActivity.class.getSimpleName();
    private Context context = SearchActivity.this;

    private ActionBarDrawerToggle drawerToggle;
    private NavigationView nvDrawer;
    private Toolbar toolbar;
    private SearchView searchView;
    private ImageView filterImage;

    private GPSTracker gps;
    private MarshmallowPermission marshmallowPermission;
    private ImageView bDiscovery, bNotification, bQuery, bSearch;
    private FloatingActionButton bAddQuery;
    private RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private boolean loading = false;
    private boolean loadFinish = false;
    private int scroll = 0;
//    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout layoutBottom, layoutDiscovery, layoutQuery, layoutSearch, layoutNotification;

    private TextView tvError;
    private CardView tvGps;
    private AlertDialog.Builder builder;
    double latitude = 0;
    double longitude = 0;
    private SuggestionAdapter suggestionAdapter;
    private QueriesAdapter queriesAdapter;
    private FolksAdapter folksAdapter;
    private List<Suggestion> suggestionList = new ArrayList<>();
    private List<Queries> queriesList = new ArrayList<>();
    private List<Profile> folksList = new ArrayList<>();
    private int searchType = Constant.SUGGESTIONS;
    private Runnable runnable;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Dialog dialog;
    private Button bSuggestion, bQueryM, bFolks;
    private RelativeLayout progress_layout;

    private String myString = "";

    private String interest_ids = "";

    private TourClass tourFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        builder = new AlertDialog.Builder(context);

        llm = new LinearLayoutManager(context);
        networkConnection = new NetworkConnection(context);
        marshmallowPermission = new MarshmallowPermission(SearchActivity.this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        progress_layout = (RelativeLayout) findViewById(R.id.progress_layout);
        searchView = (SearchView) findViewById(R.id.searchView);
        filterImage = (ImageView) findViewById(R.id.filterImage);

//        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mSwipeRefreshLayout);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        tvError = (TextView) findViewById(R.id.tvError);
        bAddQuery = (FloatingActionButton) findViewById(R.id.bAddQuery);
        layoutBottom = (LinearLayout) findViewById(R.id.layoutBottom);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        tvGps = (CardView) findViewById(R.id.tvGps);

        bSearch = (ImageView) findViewById(R.id.bSearch);
        bSearch.setImageResource(R.drawable.ic_search_black);

        bDiscovery = (ImageView) findViewById(R.id.bDiscovery);
        bQuery = (ImageView) findViewById(R.id.bQuery);
        bNotification = (ImageView) findViewById(R.id.bNotification);

        nvDrawer.setItemIconTintList(null);

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_menu_search);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP | Gravity.RIGHT;

        bSuggestion = (Button) dialog.findViewById(R.id.bSuggestion);
        bQueryM = (Button) dialog.findViewById(R.id.bQuery);
        bFolks = (Button) dialog.findViewById(R.id.bFolks);

        setBackgroundColor(bSuggestion, bQueryM, bFolks);
        bSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackgroundColor(bSuggestion, bQueryM, bFolks);
//                getSuggestions();
                searchType = Constant.SUGGESTIONS;
                searchView.setQueryHint(getString(R.string.search_suggestions));
                searchView.setQuery("", false);
                suggestionList = new ArrayList<>();
                setSuggestionAdapter(suggestionList);
                dialog.cancel();
            }
        });

        bQueryM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackgroundColor(bQueryM, bSuggestion, bFolks);
//                getQueries();
                searchType = Constant.QUERIES;
                searchView.setQueryHint(getString(R.string.search_queries));
                searchView.setQuery("", false);
                queriesList = new ArrayList<>();
                setQueriesAdapter(queriesList);
                dialog.cancel();
            }
        });

        bFolks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackgroundColor(bFolks, bSuggestion, bQueryM);
//                getFolks();
                searchType = Constant.FOLKS;
                searchView.setQueryHint(getString(R.string.search_folks));
                searchView.setQuery("", false);
                folksList = new ArrayList<>();
                setFolksAdapter(folksList);
                dialog.cancel();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        recyclerView.setFadingEdgeLength(0);

        toolbar.setTitle(R.string.search);
        setSupportActionBar(toolbar);
        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);
        setupDrawerContent(nvDrawer);

        layoutNotification = (LinearLayout) findViewById(R.id.layoutNotification);
        layoutDiscovery = (LinearLayout) findViewById(R.id.layoutDiscovery);
        layoutQuery = (LinearLayout) findViewById(R.id.layoutQuery);

        layoutDiscovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
            }
        });

        layoutQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picIntent = new Intent(context, QueriesActivity.class);
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

        /*mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    scroll = 0;
                    checkLocation();

                    searchView.setQuery("", false);
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
        });*/

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                myString = query;

                searchData(query.trim().toLowerCase());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                myString = newText;
                handler.removeCallbacks(runnable);
                final String search = newText.trim().toLowerCase();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        searchData(search);
                    }
                };
                handler.postDelayed(runnable, 500);
                return true;
            }
        });

        setListener();


        // Tour
        tourFilter = new TourClass(SearchActivity.this).click().bottomLeft().isTopMost()
                .message(getString(R.string.tour_search_filter_desc))
                .anchor(filterImage)
                .setOnClickListener(new TourClass.OnClickListener() {
                    @Override
                    public void onClick(TourClass tour) {
                        tour.dismiss();

                        TourPreference.setSearchFilterSeen(context, true);

                        setListener();

                    }
                });

        if(!TourPreference.isSearchFilterSeen(context)) {
            tourFilter.show();
        }

    }


    private void setListener() {

        filterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!TourPreference.isSearchFilterSeen(context)) {
                    tourFilter.dismiss();
                    TourPreference.setSearchFilterSeen(context, true);
                    return;
                }

                dialog.show();
            }
        });
    }


    /**
     * set background color to filters
     *
     * @param button  selected filter
     * @param button2 unselected
     * @param button3 unselected
     */
    private void setBackgroundColor(Button button, Button button2, Button button3) {
        button.setBackgroundColor(getResources().getColor(R.color.translucent_colorAccent));
        button2.setBackgroundColor(getResources().getColor(R.color.white));
        button3.setBackgroundColor(getResources().getColor(R.color.white));
    }

    /**
     * Search Operations performed
     *
     * @param query : String which is typed in search input field
     */
    private void searchData(String query) {
        switch (searchType) {
            case Constant.SUGGESTIONS:
                if (query.length() > 0) {
                    if (!networkConnection.isNetworkAvailable()) {
                        List<Suggestion> tempSuggestionList = new ArrayList<>();
                        for (int i = 0; i < suggestionList.size(); i++) {
                            String suggestion = suggestionList.get(i).getSuggestion().trim().toLowerCase();
                            if (suggestion.startsWith(query)) {
                                tempSuggestionList.add(suggestionList.get(i));
                            }
                        }
                        setSuggestionAdapter(tempSuggestionList);

                    } else {
                        searchSuggestions(query);
                    }

                } else {
                    MyLog.e(TAG, "Query 2");
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            tvError.setVisibility(View.GONE);
                            setSuggestionAdapter(suggestionList);
                        }
                    };
                    handler.postDelayed(runnable, 700);
                }
                break;

            case Constant.QUERIES:
                if (query.length() > 0) {
                    if (!networkConnection.isNetworkAvailable()) {
                        List<Queries> tempQueriesList = new ArrayList<>();
                        for (int i = 0; i < queriesList.size(); i++) {
                            String query_desc = queriesList.get(i).getDescription().trim().toLowerCase();
                            if (query_desc.startsWith(query)) {
                                tempQueriesList.add(queriesList.get(i));
                            }
                        }
                        setQueriesAdapter(tempQueriesList);

                    } else {
                        searchQueries(query);
                    }

                } else {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            tvError.setVisibility(View.GONE);
                            setQueriesAdapter(queriesList);
                        }
                    };
                    handler.postDelayed(runnable, 700);
                }
                break;

            case Constant.FOLKS:
                if (query.length() > 0) {
                    if (!networkConnection.isNetworkAvailable()) {
                        List<Profile> tempFolProfileList = new ArrayList<>();
                        for (int i = 0; i < folksList.size(); i++) {
                            String folk = folksList.get(i).getName().trim().toLowerCase();
                            if (folk.startsWith(query)) {
                                tempFolProfileList.add(folksList.get(i));
                            }
                        }
                        setFolksAdapter(tempFolProfileList);

                    } else {
                        searchFolks(query);
                    }

                } else {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            tvError.setVisibility(View.GONE);
                            setFolksAdapter(folksList);
                        }
                    };
                    handler.postDelayed(runnable, 700);
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_discovery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;

            case R.id.id_filter:

                if(!TourPreference.isSearchFilterSeen(context)) {
                    tourFilter.dismiss();
                    TourPreference.setSearchFilterSeen(context, true);
                    return true;
                }

                dialog.show();
                return true;

            case R.id.id_location:
                MyPreferencesManager.saveLocationModeManual(context, !item.isChecked());
                item.setChecked(MyPreferencesManager.isLocationModeManual(context));

                if (MyPreferencesManager.isLocationModeManual(context)) {
                    item.setIcon(R.drawable.ic_location);

                    int PLACE_PICKER_REQUEST = Constant.LOCATION_SELECTION;
                    PlaceAutocomplete.IntentBuilder builder = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN);
                    try {
                        startActivityForResult(builder.build(SearchActivity.this), PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }

                } else {
                    item.setIcon(R.drawable.ic_location_circle);
                    checkLocation();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem checkable = menu.findItem(R.id.id_location);
        checkable.setChecked(MyPreferencesManager.isLocationModeManual(context));
        if (MyPreferencesManager.isLocationModeManual(context)) {
            checkable.setIcon(R.drawable.ic_location);

        } else {
            checkable.setIcon(R.drawable.ic_location_circle);
        }
        return true;
    }
*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constant.LOCATION_SELECTION:
                if (resultCode == RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(this, data);
                    if (place.getName() != null) {
                        double lat = place.getLatLng().latitude;
                        double lng = place.getLatLng().longitude;
                        String location = "";

                        try {
                            location = ""+place.getAddress();
                            MyLog.e(TAG, "Place name : " + place.getName());
                            MyLog.e(TAG, "Place address : " + place.getAddress());
                            MyLog.e(TAG, "Place locale lang : " + place.getLocale().getLanguage());
                            MyLog.e(TAG, "latitude : " + lat);
                            MyLog.e(TAG, "longitude : " + lng);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        MyPreferencesManager.saveLocationSelected(context, location);
                        MyPreferencesManager.saveLat(context, lat);
                        MyPreferencesManager.saveLng(context, lng);


                        setLocation(location, false);

                        suggestionList = new ArrayList<>();
                        queriesList = new ArrayList<>();
                        folksList = new ArrayList<>();
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
    protected void onStart() {
        super.onStart();
        if (!marshmallowPermission.checkPermissionForLocation()) {
            if (!MyPreferencesManager.isGPS(context)) {
                marshmallowPermission.requestPermissionForLocation();
                MyPreferencesManager.saveGPS(context, true);
            } else {
                if(suggestionList.size()>0 || queriesList.size()>0 || folksList.size()>0) {
                    tvGps.setVisibility(View.GONE);
                }
                else {
                    //tvGps.setVisibility(View.VISIBLE);
                }
                tvGps.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        marshmallowPermission.requestPermissionForLocation();
                    }
                });
            }

        } else {
            tvGps.setVisibility(View.GONE);
            gps = new GPSTracker(context, SearchActivity.this);
            scroll = 0;
            tvError.setVisibility(View.VISIBLE);
            tvError.setText(R.string.suggestions_not_found);
            setSuggestionAdapter(suggestionList);
//            checkLocation();
            setupDrawerContent(nvDrawer);
            setBackgroundColor(bSuggestion, bQueryM, bFolks);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MarshmallowPermission.FINE_LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tvGps.setVisibility(View.GONE);
                    gps = new GPSTracker(context, SearchActivity.this);
                    scroll = 0;
                    checkLocation();
                }
                return;
            }

            default:
                return;
        }
    }

    private void checkLocationMode() {
        MyPreferencesManager.saveLocationModeSelected(context, true);
        new AlertDialog.Builder(context)
                .setTitle("Select location mode")
                .setCancelable(false)
                .setPositiveButton("Auto detect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyPreferencesManager.saveLocationModeManual(context, false);
                        checkLocation();
                    }
                })
                .setNegativeButton("Custom select", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyPreferencesManager.saveLocationModeManual(context, true);
                        checkLocation();
                    }
                })
                .show();
    }

    /**
     * method used to check location
     */
    private void checkLocation() {
        if (networkConnection.isNetworkAvailable()) {
            if (MyPreferencesManager.isLocationModeSelected(context)) {

                if(myString.length()==0) {
                    return;
                }


                if (!MyPreferencesManager.isLocationModeManual(context)) {
                    if (gps.canGetLocation()) {
                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();

                        setLocation(latitude, longitude);

                        if (latitude > 0 && longitude > 0) {
                            this.latitude = latitude;
                            this.longitude = longitude;
                            progress_layout.setVisibility(View.VISIBLE);

                            switch (searchType) {
                                case Constant.SUGGESTIONS:
                                    getSuggestions();
                                    break;

                                case Constant.QUERIES:
                                    getQueries();
                                    break;

                                case Constant.FOLKS:
                                    getFolks();
                                    break;
                            }
                        }
                    } else {
                        showSettingsAlert();
                    }

                } else {
                    double latitude = MyPreferencesManager.getLat(context);
                    double longitude = MyPreferencesManager.getLng(context);

                    setLocation(MyPreferencesManager.getLocationSelected(context), false);

                    if (latitude > 0 && longitude > 0) {
                        this.latitude = latitude;
                        this.longitude = longitude;
                        progress_layout.setVisibility(View.VISIBLE);

                        switch (searchType) {
                            case Constant.SUGGESTIONS:
                                getSuggestions();
                                break;

                            case Constant.QUERIES:
                                getQueries();
                                break;

                            case Constant.FOLKS:
                                getFolks();
                                break;
                        }

                    } else {
                        int PLACE_PICKER_REQUEST = Constant.LOCATION_SELECTION;
                        PlaceAutocomplete.IntentBuilder builder = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN);
                        try {
                            startActivityForResult(builder.build(SearchActivity.this), PLACE_PICKER_REQUEST);
                        } catch (GooglePlayServicesRepairableException e) {
                            e.printStackTrace();
                        } catch (GooglePlayServicesNotAvailableException e) {
                            e.printStackTrace();
                        }
                    }
                }

            } else {
                checkLocationMode();
            }

        } else {
            MyToast.tshort(context, getString(R.string.check_network));
        }
    }

    /**
     * method used to fetch all Suggestions using API call
     */
    private void getSuggestions() {
        setSuggestionAdapter(suggestionList);
        if (scroll == 0) {
            refreshList();

        } else {
            if (networkConnection.isNetworkAvailable()) {
                refreshList();
            }
        }
        int limit = scroll * Constant.REQUEST_LIMIT;

        int filter_by = Constant.FILTER_BY_LATEST;

        RestClient.getLokasoApi().getSuggestion(
                latitude,
                longitude,
                Constant.RANGE,
                MyPreferencesManager.getId(context),
                limit,
                scroll,
                filter_by,
                interest_ids,
                new Callback<RetroSuggestion>() {
                    @Override
                    public void success(RetroSuggestion retroSuggestion, Response response) {
                        if (retroSuggestion.getSuccess()) {
                            //suggestionList = retroSuggestion.getDetails();
                            //setAdapter();
                            if (scroll == 0) {
                                suggestionList = new ArrayList<>();
                            }

                            tvError.setVisibility(View.GONE);
                            suggestionList.addAll(retroSuggestion.getDetails());
                            setSuggestionAdapter(suggestionList);
                            refreshList();

                        } else {
//                            MyToast.tshort(context, retroSuggestion.getMessage() + "");
                            if (suggestionList.size() == 0) {
                                tvError.setText(R.string.suggestions_not_found);
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

    /**
     * Method used to search all Suggestions using API call
     *
     * @param search Search string
     */
    private void searchSuggestions(String search) {
        RestClient.getLokasoApi().searchSuggestion(latitude, longitude, MyPreferencesManager.getId(context), search,
                new Callback<RetroSuggestion>() {
                    @Override
                    public void success(RetroSuggestion retroSuggestion, Response response) {
                        if (retroSuggestion.getSuccess()) {
                            List<Suggestion> suggestionList = new ArrayList<>();
                            tvError.setVisibility(View.GONE);
                            suggestionList.addAll(retroSuggestion.getDetails());
                            setSuggestionAdapter(suggestionList);
                            refreshList();

                        } else {
//                            MyToast.tshort(context, retroSuggestion.getMessage() + "");
                            List<Suggestion> suggestionList = new ArrayList<>();
                            setSuggestionAdapter(suggestionList);
                            refreshList();
                            tvError.setText(R.string.suggestions_not_found);
                            tvError.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        MyToast.tshort(context, "" + error);
                    }
                });
    }

    /**
     * Suggestion adapter
     *
     * @param suggestionList Suggestion list
     */
    private void setSuggestionAdapter(List<Suggestion> suggestionList) {
        if (suggestionList.size() == 0) {
            tvError.setVisibility(View.VISIBLE);
            tvError.setText(R.string.suggestions_not_found);
        }
        if(suggestionList!=null && suggestionList.size()>0) {
            tvGps.setVisibility(View.GONE);
        }
        scroll = 0;
        suggestionAdapter = new SuggestionAdapter(SearchActivity.this, suggestionList);
        recyclerView.setAdapter(suggestionAdapter);
    }


    /**
     * method used to refresh the discovery adapter
     */
    private void refreshList() {
//        mSwipeRefreshLayout.setRefreshing(false);

        loading = false;
    }

    /**
     * On Message click
     *
     * @param profile Profile of the clicked user
     */
    private void onMessageClick(Profile profile) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Constant.PROFILE, profile);
        intent.putExtra(Constant.CHAT_ID, 0);
        startActivity(intent);
        //overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);
    }

    /**
     * method used to fetch queries
     */
    private void getQueries() {
        setQueriesAdapter(queriesList);
        if (scroll == 0) {
            refreshList();

        } else {
            if (networkConnection.isNetworkAvailable()) {
                refreshList();
            }
        }
        int limit = scroll * Constant.REQUEST_LIMIT;

        int filter_by = Constant.FILTER_BY_LATEST;

        RestClient.getLokasoApi().getQueries(latitude, longitude,
                Constant.RANGE,
                MyPreferencesManager.getId(context),
                limit, scroll,
                filter_by,
                interest_ids,
                new Callback<RetroQueries>() {
                    @Override
                    public void success(RetroQueries retroQueries, Response response) {
                        if (retroQueries.getSuccess()) {
//                            queriesList = retroQueries.getDetails();
//                            setAdapter();

                            if (scroll == 0) {
                                queriesList = new ArrayList<>();
                            }

                            tvError.setVisibility(View.GONE);
                            queriesList.addAll(retroQueries.getDetails());
                            setQueriesAdapter(queriesList);
                            refreshList();

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

    /**
     * Search all Queries using API call
     *
     * @param search Search String
     */
    private void searchQueries(String search) {
        RestClient.getLokasoApi().searchQuery(latitude, longitude, MyPreferencesManager.getId(context), search,
                new Callback<RetroQueries>() {
                    @Override
                    public void success(RetroQueries retroQueries, Response response) {
                        if (retroQueries.getSuccess()) {
                            List<Queries> queriesList = new ArrayList<>();
                            tvError.setVisibility(View.GONE);
                            queriesList.addAll(retroQueries.getDetails());
                            setQueriesAdapter(queriesList);
                            refreshList();

                        } else {
//                            MyToast.tshort(context, retroQueries.getMessage() + "");
                            List<Queries> queriesList = new ArrayList<>();
                            setQueriesAdapter(queriesList);
                            refreshList();
                            tvError.setText(R.string.queries_not_found);
                            tvError.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        MyToast.tshort(context, "" + error);
                    }
                });
    }

    /**
     * Queries adapter
     *
     * @param queriesList Queries list
     */
    private void setQueriesAdapter(List<Queries> queriesList) {
        if (queriesList.size() == 0) {
            tvError.setVisibility(View.VISIBLE);
            tvError.setText(R.string.queries_not_found);
        }

        if(queriesList!=null && queriesList.size()>0) {
            tvGps.setVisibility(View.GONE);
        }

        scroll = 0;
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
     * method used to get all folks using API call
     */
    private void getFolks() {
        setFolksAdapter(folksList);
        if (scroll == 0) {
            refreshList();

        } else {
            if (networkConnection.isNetworkAvailable()) {
                refreshList();
            }
        }
        int limit = scroll * Constant.REQUEST_LIMIT;

        int filter_by = Constant.FILTER_BY_LATEST;

        RestClient.getLokasoApi().getFolks(latitude, longitude,
                Constant.RANGE, MyPreferencesManager.getId(context),
                limit, scroll,
                filter_by,
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
                            setFolksAdapter(folksList);
                            refreshList();

                        } else {
//                            MyToast.tshort(context, retroFolks.getMessage() + "");
                            if (folksList.size() == 0) {
                                tvError.setText(R.string.folks_not_found);
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

    /**
     * Search all Folks using API call
     *
     * @param search
     */
    private void searchFolks(String search) {
        RestClient.getLokasoApi().searchFolks(latitude, longitude, MyPreferencesManager.getId(context), search,
                new Callback<RetroProfile>() {
                    @Override
                    public void success(RetroProfile retroProfile, Response response) {
                        if (retroProfile.getSuccess()) {
                            List<Profile> folksList = new ArrayList<>();
                            tvError.setVisibility(View.GONE);
                            folksList.addAll(retroProfile.getDetails());
                            setFolksAdapter(folksList);
                            refreshList();

                        } else {
//                            MyToast.tshort(context, retroFolks.getMessage() + "");
                            List<Profile> folksList = new ArrayList<>();
                            setFolksAdapter(folksList);
                            refreshList();
                            tvError.setText(R.string.folks_not_found);
                            tvError.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        MyToast.tshort(context, "" + error);
                    }
                });
    }

    /**
     * Folks adapter
     *
     * @param folksList Folks List
     */
    private void setFolksAdapter(List<Profile> folksList) {
        if (folksList.size() == 0) {
            tvError.setVisibility(View.VISIBLE);
            tvError.setText(R.string.folks_not_found);
        }
        if(folksList!=null && folksList.size()>0) {
            tvGps.setVisibility(View.GONE);
        }
        scroll = 0;
        folksAdapter = new FolksAdapter(context, folksList);
        recyclerView.setAdapter(folksAdapter);

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
                setFolksFollow(action, leader, follower, position);
            }
        });
    }


    /**
     * method used to follow query
     *
     * @param action   action = 1 to follow query and action = 0 vice-versa
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

    /**
     * method used to follow a folk using API call
     *
     * @param action   action = 1 to follow folk and action = 0 vice-versa
     * @param leader   user id of the user being followed
     * @param follower user id of the user following
     */
    private void setFolksFollow(final int action, int leader, int follower, final int position) {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().follow(action, leader, follower, new Callback<RetroResponse>() {
                @Override
                public void success(RetroResponse retroResponse, Response response) {
//                MyToast.tshort(context, retroResponse.getMessage() + "");
                    if (retroResponse.getSuccess()) {

                        /*if (action == 1) {
                            folksList.get(position).setUser_followed(true);

                        } else {
                            folksList.get(position).setUser_followed(false);
                        }
                        folksAdapter.refresh(folksList);*/
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
        return new ActionBarDrawerToggle(SearchActivity.this, mDrawer, toolbar,
                R.string.app_name,
                R.string.app_name);
    }

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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}
