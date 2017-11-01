package com.lokaso.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.lokaso.R;
import com.lokaso.model.AddInterest;
import com.lokaso.model.Profile;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.preference.TourPreference;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroProfile;
import com.lokaso.util.Constant;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyLog;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;
import com.lokaso.util.Picaso;
import com.lokaso.util.TourClass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Androcid-6 on 22-10-2016.
 */

public class BaseActivity2 extends AppCompatActivity {

    private static final String TAG = BaseActivity2.class.getSimpleName();
    private Context context = BaseActivity2.this;

    public TextView locationTextView;
    public ImageView locationImageView;

    public TextView tvProfileName, tvProfession, tvDiscoveryCount, tvQueryCount, tvFollowersCount;
    private LinearLayout chat_count_layout;
    private TextView tvChatCount;

    public CircleImageView ivPic;

    public DrawerLayout mDrawer;

    public NetworkConnection networkConnection;

    public List<Profile> profileList = new ArrayList<>();

    private Profile profile = null;

    private boolean isInviteTourSeen = false;

    public void inviteMessage() {

        String playStore = "https://goo.gl/3dcjvb"; //https://play.google.com/store/apps/details?id="+context.getPackageName();

        String message = "Hey check out my app at: https://play.google.com/store/apps/details?id=com.google.android.apps.plus";

        String userName = ""+ MyPreferencesManager.getName(context);
        String referralCode = ""+MyPreferencesManager.getReferCode(context);
        MyLog.e(TAG, "refer_code : " + referralCode);

        message = "Hey, "+userName+" has sent you an invite to join Lokaso and earn points\nUse referral code "+referralCode+" and earn points. Download lokaso from "+playStore;

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }


    public void rateUs() {

        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + context.getPackageName()
                    )));

        } catch (android.content.ActivityNotFoundException e) {
            e.printStackTrace();
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName()
                    )));
        }
        catch (Exception e) {
            e.printStackTrace();
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName()
                    )));
        }
    }


    public void sendFeedback() {
        String url = "http://lokaso.in/contact/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    /**
     * method used to fetch profile of current user using API call
     */
    private void getProfile() {

        if(networkConnection==null) {
            networkConnection = new NetworkConnection(context);
        }

        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().getProfile(MyPreferencesManager.getId(context), MyPreferencesManager.getId(context),
                    new Callback<RetroProfile>() {
                        @Override
                        public void success(RetroProfile retroProfile, Response response) {
                            if (retroProfile.getSuccess()) {
                                profileList = retroProfile.getDetails();
                                setProfileData();

                            } else {
                                //MyToast.tshort(context, retroProfile.getMessage() + "");
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            //MyToast.tshort(context, "" + error);
                        }
                    });
        } else {
            //MyToast.tshort(context, getString(R.string.check_network));
        }
    }


    public static void setUserPreference(Context context, Profile profile) {

        MyPreferencesManager.saveId(context, profile.getId());
        MyPreferencesManager.saveName(context, profile.getName());
        MyPreferencesManager.saveEmail(context, profile.getEmail());
        MyPreferencesManager.saveImage(context, profile.getImage());
        MyPreferencesManager.setAboutMe(context, profile.getAbout_me());
        MyPreferencesManager.setUserLocation(context, profile.getLocation());
        MyPreferencesManager.setUserLatitude(context, profile.getCurrent_lat());
        MyPreferencesManager.setUserLongitude(context, profile.getCurrent_lng());
        MyPreferencesManager.saveProfession(context, profile.getProfession());
        MyPreferencesManager.saveDiscovery_count(context, profile.getDiscovery_count());
        MyPreferencesManager.saveQuery_count(context, profile.getQuery_count());
        MyPreferencesManager.saveFollowing_count(context, profile.getFollowing_count());
        MyPreferencesManager.saveChatUnreadCount(context, profile.getChat_unread_count());
        MyPreferencesManager.saveReferCode(context, profile.getRefer_code());

        List<AddInterest> myInterest = new ArrayList<>();

        if(profile.getInterestList()!=null) {
            MyLog.e(TAG, "interest : "+profile.getInterestList());

            myInterest.addAll(profile.getInterestList());
            MyLog.e(TAG, "interest size: "+myInterest.size());

            List<String> interestList = new ArrayList<>();
            for (int i = 0; i < myInterest.size(); i++) {
                interestList.add(""+myInterest.get(i).getInterest_id());
            }

            Set<String> mySet = new HashSet<>(interestList);
            MyPreferencesManager.saveUserInterestList(context, mySet);
        }

    }

    /**
     * method used to set profile data
     */
    private void setProfileData() {
        profile = profileList.get(0);

        setUserPreference(BaseActivity2.this, profile);
/*
        MyPreferencesManager.saveEmail(context, profile.getEmail());
        MyPreferencesManager.saveImage(context, profile.getImage());
        MyPreferencesManager.saveName(context, profile.getName());
        MyPreferencesManager.setUserLocation(context, profile.getLocation());
        MyPreferencesManager.setUserLatitude(context, profile.getCurrent_lat());
        MyPreferencesManager.setUserLongitude(context, profile.getCurrent_lng());
        MyPreferencesManager.saveProfession(context, profile.getProfession());
        MyPreferencesManager.saveDiscovery_count(context, profile.getDiscovery_count());
        MyPreferencesManager.saveQuery_count(context, profile.getQuery_count());
        MyPreferencesManager.saveFollowing_count(context, profile.getFollowing_count());
        MyPreferencesManager.saveChatUnreadCount(context, profile.getChat_unread_count());

        List<AddInterest> myInterest = new ArrayList<>();

        if(profile.getInterestList()!=null) {
            MyLog.e(TAG, "interest : "+profile.getInterestList());

            myInterest.addAll(profile.getInterestList());
            MyLog.e(TAG, "interest size: "+myInterest.size());

            List<String> interestList = new ArrayList<>();
            for (int i = 0; i < myInterest.size(); i++) {
                interestList.add(""+myInterest.get(i).getInterest_id());
            }

            Set<String> mySet = new HashSet<>(interestList);
            MyPreferencesManager.saveUserInterestList(context, mySet);
        }
*/

        setDrawerData();
/*
        String imageUrl = profile.getImage();
        //Picasso.with(context).load(imageUrl).into(ivPic);
        Picaso.loadUser(context, imageUrl, ivPic);
        tvProfileName.setText(profile.getName());
        tvProfession.setText(profile.getProfession());
        tvDiscoveryCount.setText(profile.getDiscovery_count() + "");
        tvQueryCount.setText(profile.getQuery_count() + "");
        tvFollowersCount.setText(profile.getFollowing_count() + "");

        if(MyPreferencesManager.getChatUnreadCount(context)>0) {
            chat_count_layout.setVisibility(View.VISIBLE);
            tvChatCount.setText(""+MyPreferencesManager.getChatUnreadCount(context));
        }
        else {
            chat_count_layout.setVisibility(View.GONE);
        }*/
    }


    private void setDrawerData() {

        try {

            String imageUrl = MyPreferencesManager.getImage(context);
            MyLog.e(TAG, "userImageUrl : "+imageUrl);
            Picaso.loadUser(context, imageUrl, ivPic);

            tvProfileName.setText(MyPreferencesManager.getName(context));
            tvProfession.setText(MyPreferencesManager.getProfession(context));
            tvDiscoveryCount.setText(MyPreferencesManager.getDiscovery_count(context) + "");
            tvQueryCount.setText(MyPreferencesManager.getQuery_count(context) + "");
            tvFollowersCount.setText(MyPreferencesManager.getFollowing_count(context) + "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(MyPreferencesManager.getChatUnreadCount(context)>0) {
            chat_count_layout.setVisibility(View.VISIBLE);
            tvChatCount.setText(""+MyPreferencesManager.getChatUnreadCount(context));
        }
        else {
            chat_count_layout.setVisibility(View.GONE);
        }

    }

    /**
     * method used to setup drawer content
     *
     * @param navigationView
     */

    LinearLayout nav_invite;
    TextView tvInvite;
    public void setupDrawerContent(NavigationView navigationView) {
        LinearLayout nav_notification = (LinearLayout) findViewById(R.id.nav_notification);
        LinearLayout nav_chats = (LinearLayout) findViewById(R.id.nav_chats);
        LinearLayout nav_save_discovery = (LinearLayout) findViewById(R.id.nav_save_discovery);
        LinearLayout nav_logout = (LinearLayout) findViewById(R.id.nav_logout);
        nav_invite = (LinearLayout) findViewById(R.id.nav_invite);
        tvInvite = (TextView) findViewById(R.id.tvInvite);

        LinearLayout nav_feedback = (LinearLayout) findViewById(R.id.nav_feedback);
        LinearLayout nav_rateus = (LinearLayout) findViewById(R.id.nav_rateus);
        TextView tvNotification = (TextView) findViewById(R.id.tvNotification);
        TextView tvChats = (TextView) findViewById(R.id.tvChats);
        TextView tvSaveDiscovery = (TextView) findViewById(R.id.tvSaveDiscovery);
        TextView tvLogout = (TextView) findViewById(R.id.tvLogout);

        chat_count_layout = (LinearLayout) findViewById(R.id.chat_count_layout);
        tvChatCount = (TextView) findViewById(R.id.tvChatCount);




        //RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        //LinearLayout container = (LinearLayout) findViewById(R.id.container);
        RelativeLayout headerLayout = (RelativeLayout) findViewById(R.id.headerLayout);
        LinearLayout layoutNavDiscovery = (LinearLayout) findViewById(R.id.layoutNavDiscovery);
        LinearLayout layoutNavQuery = (LinearLayout) findViewById(R.id.layoutNavQuery);
        LinearLayout layoutNavFollow = (LinearLayout) findViewById(R.id.layoutNavFollow);
        tvProfileName = (TextView) findViewById(R.id.tvProfileName);
        tvProfession = (TextView) findViewById(R.id.tvProfession);
        tvDiscoveryCount = (TextView) findViewById(R.id.tvDiscoveryCount);
        tvQueryCount = (TextView) findViewById(R.id.tvQueryCount);
        tvFollowersCount = (TextView) findViewById(R.id.tvFollowersCount);
        TextView tvDiscovery = (TextView) findViewById(R.id.tvDiscovery);
        TextView tvQuery = (TextView) findViewById(R.id.tvQuery);
        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        ivPic = (CircleImageView) findViewById(R.id.ivProfile);

        MyFont1 myFont = new MyFont1(context);
        myFont.setAppFont((ViewGroup) findViewById(R.id.container));

        setDrawerData();
/*
        try {

            String imageUrl = MyPreferencesManager.getImage(context);
            MyLog.e(TAG, "userImageUrl : "+imageUrl);

            Picaso.loadUser(context, imageUrl, ivPic);
            tvProfileName.setText(MyPreferencesManager.getName(context));
            tvProfession.setText(MyPreferencesManager.getProfession(context));
            tvDiscoveryCount.setText(MyPreferencesManager.getDiscovery_count(context) + "");
            tvQueryCount.setText(MyPreferencesManager.getQuery_count(context) + "");
            tvFollowersCount.setText(MyPreferencesManager.getFollowing_count(context) + "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(MyPreferencesManager.getChatUnreadCount(context)>0) {
            chat_count_layout.setVisibility(View.VISIBLE);
            tvChatCount.setText(""+MyPreferencesManager.getChatUnreadCount(context));
        }
        else {
            chat_count_layout.setVisibility(View.GONE);
        }
        */


        mDrawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

                if(!isInviteTourSeen) {
                    isInviteTourSeen = true;

                    if(!TourPreference.isDrawerInviteSeen(context)) {
                        new TourClass(BaseActivity2.this).click().bottomRight().isTopMost()
                                .message(getString(R.string.tour_drawer_invite_desc))
                                .anchor(tvInvite)
                                .setOnClickListener(new TourClass.OnClickListener() {
                                    @Override
                                    public void onClick(TourClass tour) {
                                        tour.dismiss();
                                        TourPreference.setDrawerInvite(context, true);

                                        setListener();
                                    }
                                }).show();
                    }
/*
                    Tour.getInstance(BaseActivity2.this).topRight().setOnClickListener(new Tour.OnClickListener() {
                        @Override
                        public void onClick(Tour tour) {
                            tour.dismiss();
                        }
                    }).show(tvInvite, getString(R.string.tour_drawer_invite_desc));*/
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });


        getProfile();

        headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra(Constant.PROFILE, profile);
                startActivity(intent);
                //overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);
                mDrawer.closeDrawers();
            }
        });


        layoutNavDiscovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra(Constant.TAB_TYPE, ProfileActivity.TAB_LOCAL_TIP);
                intent.putExtra(Constant.PROFILE, profile);
                startActivity(intent);
                //overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);
                mDrawer.closeDrawers();
            }
        });

        layoutNavQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra(Constant.TAB_TYPE, ProfileActivity.TAB_QUERY);
                intent.putExtra(Constant.PROFILE, profile);
                startActivity(intent);
                //overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);
                mDrawer.closeDrawers();
            }
        });

        layoutNavFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra(Constant.TAB_TYPE, ProfileActivity.TAB_FOLLOWER);
                intent.putExtra(Constant.PROFILE, profile);
                startActivity(intent);
                mDrawer.closeDrawers();
            }
        });

        nav_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(context, NotificationActivity.class);
                startActivity(intent2);
            }
        });

        nav_chats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(context, ChatRoomActivity.class);
                startActivity(intent3);
            }
        });

        nav_save_discovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent5 = new Intent(context, SavedDiscoveriesActivity.class);
                startActivity(intent5);
            }
        });

        nav_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteMessage();
            }
        });


        nav_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFeedback();
            }
        });

        nav_rateus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateUs();
            }
        });


        nav_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPreferencesManager.logOutUser(context);
                Intent intent4 = new Intent(context, LoginActivity.class);
                startActivity(intent4);
                //overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);
                finish();
            }
        });
    }

    private void setListener() {

        tvInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inviteMessage();
            }
        });
        nav_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteMessage();
            }
        });
    }

//    public void setLocation(String location) {
//        if(locationTextView!=null) {
//            locationTextView.setText(location.toUpperCase(Locale.getDefault()));
//        }
//    }

    public void setLocation(String location, boolean fromGps) {
        MyLog.e(TAG, " setLocation location : "+location+" , fromGps : "+fromGps + " , locationTextView : "+locationTextView);

        if(locationTextView==null)
            locationTextView = (TextView) findViewById(R.id.locationTextView);
        if(locationImageView==null)
            locationImageView = (ImageView) findViewById(R.id.locationImageView);

        if(locationImageView!=null) {
            if (fromGps) {
                locationImageView.setImageResource(R.drawable.ic_location_circle);
            } else {
                locationImageView.setImageResource(R.drawable.ic_location);
            }
        }

        if(locationTextView!=null) {

            if(location==null) location = "";
            if(location.equalsIgnoreCase("null")) location = "";
            if(location.length()==0) {
                if(locationTextView.getText().toString().length()>0) {
                    location = locationTextView.getText().toString();
                }
                else {
                    location = getString(R.string.txt_current_location_select);
                }
            }

            locationTextView.setText(location.toUpperCase(Locale.getDefault()));
        }
    }

    private Double[] latlng = new Double[2];
    private int attempt = 0;
    public void setLocation(double latitude, double longitude) {

        MyLog.e(TAG, " setLocation latitude : "+latitude+" , longitude : "+longitude);
        latlng[0] = latitude;
        latlng[1] = longitude;
        attempt = 0;
        LocationFromLatLng locationFromLatLng = new LocationFromLatLng();
//        try {
//            locationFromLatLng.get(5000, TimeUnit.MILLISECONDS);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (TimeoutException e) {
//            e.printStackTrace();
//        }
        locationFromLatLng.execute(latlng);
    }



    private class LocationFromLatLng extends AsyncTask<Double, Void, String> {

        @Override
        protected String doInBackground(Double... place) {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> adr = null;
            String location = "";
            try {
                //adr = geocoder.getFromLocationName(place[0], 20);
                adr = geocoder.getFromLocation(place[0], place[1], 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            MyLog.e(TAG, " adr : "+adr);

            if(adr!=null && adr.size()>0) {

                Address address = adr.get(0);
				/*
				 * Format the first line of address (if available), city, and
				 * country name.
				 */

                String addr1 = address.getMaxAddressLineIndex() > 0 ? address
                        .getAddressLine(0) : "";

                String locality = address.getLocality();
                String country = address.getCountryName();

                String addressText = String.format(
                        "%s, %s, %s",
                        // If there's a street address, add it
                        address.getMaxAddressLineIndex() > 0 ? address
                                .getAddressLine(0) : "",
                        // Locality is usually a city
                        address.getLocality(),
                        // The country of the address
                        address.getCountryName());
                Log.e("adr", addressText + "");


                if(addr1!=null && addr1.length()>0 && !addr1.equalsIgnoreCase("null")
                        && locality!=null && locality.length()>0 && !locality.equalsIgnoreCase("null")
                        && country!=null && country.length()>0 && !country.equalsIgnoreCase("null"))
                {
                    addressText = addr1 +", "+locality+", "+country;
                }
                else if (addr1!=null && addr1.length()>0 && !addr1.equalsIgnoreCase("null")
                        && country!=null && country.length()>0 && !country.equalsIgnoreCase("null"))
                {
                    addressText = addr1 +", "+country;
                }
                else if(locality!=null && locality.length()>0 && !locality.equalsIgnoreCase("null")
                        && country!=null && country.length()>0 && !country.equalsIgnoreCase("null"))
                {
                    addressText = locality+", "+country;
                }
                else {
                    addressText = country;
                }

                location = addressText;

/*

                for (int i = 0; i < adr.size(); i++) {
                    Address address = adr.get(0);
                    String addressText = String.format(
                            "%s, %s, %s",
                            // If there's a street address, add it
                            address.getMaxAddressLineIndex() > 0 ? address
                                    .getAddressLine(0) : "",
                            // Locality is usually a city
                            address.getLocality(),
                            // The country of the address
                            address.getCountryName());
                    Log.e("adr", addressText + "");
                }
                */

            }

            return location;
        }

        @Override
        protected void onPostExecute(String location) {
            super.onPostExecute(location);

            if(location!=null && location.length()>0) {
                MyPreferencesManager.saveLocationSelected(context, location);
                setLocation(location, true);
                attempt = 0;
            }
            else {
                MyLog.e(TAG, "attempt = " +attempt + " latlng : " +latlng+" , latlng.length : "+latlng.length);
                if(latlng!=null && latlng.length>0 && attempt<=2) {
                    MyLog.e(TAG, "attempt = " +attempt + " second attm");

                    LocationFromLatLng locationFromLatLng = new LocationFromLatLng();
                    locationFromLatLng.execute(latlng);
                }
                ++attempt;
            }

        }
    }

    private class GeoTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... place) {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> adr = null;
            try {
                adr = geocoder.getFromLocationName(place[0], 20);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            for (int i = 0; i < adr.size(); i++) {
                Address address = adr.get(0);
				/*
				 * Format the first line of address (if available), city, and
				 * country name.
				 */

                String addr1 = address.getMaxAddressLineIndex() > 0 ? address
                        .getAddressLine(0) : "";

                String locality = address.getLocality();
                String country = address.getCountryName();



                String addressText = String.format(
                        "%s, %s, %s",
                        // If there's a street address, add it
                        addr1,
                        // Locality is usually a city
                        locality,
                        // The country of the address
                        country
                        );

                if(addr1!=null && addr1.length()>0 && !addr1.equalsIgnoreCase("null")
                        && locality!=null && locality.length()>0 && !locality.equalsIgnoreCase("null")
                        && country!=null && country.length()>0 && !country.equalsIgnoreCase("null"))
                {
                    addressText = addr1 +", "+locality+", "+country;
                }
                else if (addr1!=null && addr1.length()>0 && !addr1.equalsIgnoreCase("null")
                        && country!=null && country.length()>0 && !country.equalsIgnoreCase("null"))
                {
                    addressText = addr1 +", "+country;
                }
                else if(locality!=null && locality.length()>0 && !locality.equalsIgnoreCase("null")
                        && country!=null && country.length()>0 && !country.equalsIgnoreCase("null"))
                {
                    addressText = locality+", "+country;
                }
                else {
                    addressText = country;
                }

                Log.e("adr", addressText + "");

            }
            return null;
        }
    }

    android.app.AlertDialog.Builder alertDialogGpsSetting;
    android.app.AlertDialog alertDialog = null;
    public void showSettingsAlert() {
        if(alertDialogGpsSetting==null) {
            alertDialogGpsSetting = new android.app.AlertDialog.Builder(context);

            alertDialogGpsSetting.setTitle(R.string.dialog_gpssetting_title);
            alertDialogGpsSetting.setMessage(R.string.dialog_gpssetting_message);

            // On pressing the Settings button.
            alertDialogGpsSetting.setPositiveButton(R.string.dialog_gpssetting_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });

            // On pressing the cancel button
            alertDialogGpsSetting.setNegativeButton(R.string.dialog_gpssetting_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();

                    MyPreferencesManager.saveLocationModeManual(context, true);
                    invalidateOptionsMenu();

                    int PLACE_PICKER_REQUEST = Constant.LOCATION_SELECTION;
                    PlaceAutocomplete.IntentBuilder builder = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN);
                    try {
                        startActivityForResult(builder.build(BaseActivity2.this), PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                }
            });

            // Showing Alert Message
            alertDialog = alertDialogGpsSetting.create();
        }


        if(alertDialog!=null && !alertDialog.isShowing())
            alertDialog.show();
    }
    public void hideAlert() {
        if(alertDialog!=null)
            alertDialog.hide();
    }
}
