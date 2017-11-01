package com.lokaso.service;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.lokaso.util.MyLog;

/**
 * Created by Androcid on 19-Aug-16.
 */
public class GPSTracker extends Service {

    private Context mContext;
    // Flag for GPS status
    boolean isGPSEnabled = false;
    // Flag for network status
    boolean isNetworkEnabled = false;
    // Flag for GPS status
    boolean canGetLocation = false;
    Location location;
    double latitude;
    double longitude;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1000; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    Activity activity;

    public GPSTracker() {
    }

    public GPSTracker(Context context, Activity activity) {
        this.mContext = context;
        this.activity = activity;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // Getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // No network provider is enabled
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    int requestPermissionsCode = 50;

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, mLocationListener);
                    MyLog.e("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            }
            // If GPS enabled, get latitude/longitude using GPS Services
            if (isGPSEnabled) {
                if (location == null) {
                    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 50);

                    } else {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, mLocationListener);
                        MyLog.e("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {

                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }


    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app.
     */
    public void stopUsingGPS() {

    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                if(onLocationChangeListener!=null) {
                    onLocationChangeListener.onLocationChange(latitude, longitude);
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    /**
     * Function to get latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/Wi-Fi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to down_from_top settings alert dialog.
     * On pressing the Settings button it will launch Settings Options.
     */
    /*public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle(R.string.gps_is_settings);
        alertDialog.setMessage(R.string.gps_not_enabled);

        // On pressing the Settings button.
        alertDialog.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // On pressing the cancel button
        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

                double latitude = MyPreferencesManager.getLat(context);
                double longitude = MyPreferencesManager.getLng(context);

                MyLog.e(TAG, "checkLocation : "
                        +" , "+latitude
                        +" , "+longitude);

                setLocation(MyPreferencesManager.getLocationSelected(context), false);

                if (latitude > 0 && longitude > 0) {
                    progress_layout.setVisibility(View.VISIBLE);
                    getSuggestions(latitude, longitude);

                } else {
                    int PLACE_PICKER_REQUEST = Constant.LOCATION_SELECTION;
                    PlaceAutocomplete.IntentBuilder builder = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN);
                    try {
                        startActivityForResult(builder.build(SuggestionActivity.this), PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
*/

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private OnLocationChangeListener onLocationChangeListener;
    public void setOnLocationChangeListener(OnLocationChangeListener listener) {
        onLocationChangeListener = listener;
    }

    public interface OnLocationChangeListener {
        void onLocationChange(double latitude, double longitude);
    }


    // New Static methods


    public static boolean isGpsEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        // Getting GPS status
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}