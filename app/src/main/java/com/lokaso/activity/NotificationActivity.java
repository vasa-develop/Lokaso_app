package com.lokaso.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lokaso.R;
import com.lokaso.adapter.NotificationAdapter;
import com.lokaso.model.Notification;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroNotification;
import com.lokaso.util.HideShowScrollListener;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NotificationActivity extends BaseActivity2 {

    private String TAG = NotificationActivity.class.getSimpleName();
    private Context context = NotificationActivity.this;

    private ActionBarDrawerToggle drawerToggle;
    private NavigationView nvDrawer;
    private Toolbar toolbar;
    private ImageView bDiscovery, bNotification, bQuery, bSearch;
    private FloatingActionButton bAddQuery;
    private LinearLayoutManager llm;
    private LinearLayout layoutBottom, layoutDiscovery, layoutQuery, layoutSearch, layoutNotification;
    private TextView tvError;
    private RecyclerView recyclerView;
    private List<Notification> notificationList = new ArrayList<>();
    private NotificationAdapter notificationAdapter;
    private NetworkConnection networkConnection;
    private AlertDialog.Builder builder;
    private RelativeLayout progress_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        builder = new AlertDialog.Builder(context);

        llm = new LinearLayoutManager(context);
        networkConnection = new NetworkConnection(context);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        progress_layout = (RelativeLayout) findViewById(R.id.progress_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        tvError = (TextView) findViewById(R.id.tvError);
        bAddQuery = (FloatingActionButton) findViewById(R.id.bAddQuery);
        layoutBottom = (LinearLayout) findViewById(R.id.layoutBottom);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        bNotification = (ImageView) findViewById(R.id.bNotification);
        bNotification.setImageResource(R.drawable.ic_notification_black);

        bDiscovery = (ImageView) findViewById(R.id.bDiscovery);
        bQuery = (ImageView) findViewById(R.id.bQuery);
        bSearch = (ImageView) findViewById(R.id.bSearch);

        toolbar.setTitle(R.string.notifications);
            /*toolbar_layout.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar_layout.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });*/
        setSupportActionBar(toolbar);
        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);
        setupDrawerContent(nvDrawer);

        nvDrawer.setItemIconTintList(null);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        setAdapter();

        layoutSearch = (LinearLayout) findViewById(R.id.layoutSearch);
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        getNotifications();
        setupDrawerContent(nvDrawer);
    }

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.close_enter_animate, R.anim.close_exit_animate);
    }*/

    /**
     * get notification detalis
     */
    private void getNotifications() {
        if (networkConnection.isNetworkAvailable()) {
            progress_layout.setVisibility(View.VISIBLE);
            RestClient.getLokasoApi().getNotifications(MyPreferencesManager.getId(context), new Callback<RetroNotification>() {
                @Override
                public void success(RetroNotification retroNotification, Response response) {
                    if (retroNotification.getSuccess()) {
                        notificationList = retroNotification.getDetails();
                        tvError.setVisibility(View.GONE);
                        setAdapter();

                    } else {
//                        MyToast.tshort(context, retroNotification.getMessage() + "");
                        tvError.setText(R.string.notifications_not_found);
                        tvError.setVisibility(View.VISIBLE);
                    }
                    progress_layout.setVisibility(View.GONE);
                }

                @Override
                public void failure(RetrofitError error) {
                    MyToast.tshort(context, "" + error);
                    progress_layout.setVisibility(View.GONE);
                }
            });
        } else {
            MyToast.tshort(context, getString(R.string.check_network));
        }
    }

    /**
     * set notification adapter
     */
    private void setAdapter() {
        notificationAdapter = new NotificationAdapter(context, notificationList);
        recyclerView.setAdapter(notificationAdapter);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
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

    /**
     * method used to set actionbar drawer toggle
     *
     * @return
     */
    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(NotificationActivity.this, mDrawer, toolbar,
                R.string.app_name,
                R.string.app_name);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}
