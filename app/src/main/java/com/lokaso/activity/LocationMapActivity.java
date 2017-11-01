package com.lokaso.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lokaso.R;
import com.lokaso.adapter.LocationAdapter;
import com.lokaso.custom.CustomEditText;
import com.lokaso.dao.DaoController;
import com.lokaso.model.Place;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroPlace;
import com.lokaso.retromodel.RetroPlaceDetail;
import com.lokaso.service.GPSTracker;
import com.lokaso.util.CommonFunctions;
import com.lokaso.util.Constant;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyLog;
import com.lokaso.util.MyToast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LocationMapActivity extends GpsActivity implements OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener,
		GoogleMap.OnCameraMoveListener,
		GoogleMap.OnCameraMoveCanceledListener,
		GoogleMap.OnCameraIdleListener {

	private static final String TAG = LocationMapActivity.class.getSimpleName();
	private Context context = LocationMapActivity.this;

	private Toolbar toolbar;

	private TextView locationTextView;
	private Button selectButton;

	//private String latitude = "15.4909", longitude = "73.8278";
	private double latitude = 15.4909, longitude = 73.8278;
	private float zoom = 14;

	private GoogleMap googleMap;

	private Place selectedPlace;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_map);

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		locationTextView 	= (TextView) findViewById(R.id.locationTextView);
		selectButton 		= (Button) findViewById(R.id.selectButton);

		initilizeMap();

		selectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				if(selectedPlace!=null) {
					String place_id = selectedPlace.getPlace_id();
					getPlacesDetail(place_id);
				}
			}
		});

		setFonts();
	}

	/**
	 * function to load map. If map is not created it will create it for you
	 * */
	private void initilizeMap() {
		if (googleMap == null) {
			SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
			mapFragment.getMapAsync(this);

		}
	}

	/**
	 * Method used to set fonts
	 */
	private void setFonts() {
		new MyFont1(context).setAppFont((ViewGroup)findViewById(R.id.container));
	}


	private void getPlaces(String name) {

		RestClient.getPlacesApiAutocomplete().getPlaces(
			name, latitude+","+longitude,
			new Callback<RetroPlace>() {
				@Override
				public void success(RetroPlace retroResponse, Response response) {
					List<Place> placeList = retroResponse.getPlaces();
					//setPlaceList(placeList);
					//adapter.refresh(list);
				}

				@Override
				public void failure(RetrofitError error) {

				}
			});
	}

	private void getPlacesDetail(String place_id) {

		RestClient.getPlacesApiDetail().getPlacesDetail(
				place_id,
				new Callback<RetroPlaceDetail>() {
					@Override
					public void success(RetroPlaceDetail retroResponse, Response response) {
                        Place place = retroResponse.getPlace();

						if(googleMap!=null) {
							CameraPosition cameraPosition = googleMap.getCameraPosition();
							LatLng latLon = cameraPosition.target;

							String latitude = ""+latLon.latitude;
							String longitude = ""+latLon.longitude;
							place.setLatLng(latitude, longitude);
						}

						sendResponse(place);
					}

					@Override
					public void failure(RetrofitError error) {

					}
				});
	}

	private void getPlaces(double lat, double lon) {
		latitude = lat;
		longitude = lon;

		RestClient.getPlacesApiLatLng().getPlaces(
				latitude+","+longitude,
				new Callback<RetroPlace>() {
					@Override
					public void success(RetroPlace retroResponse, Response response) {
						List<Place> placeList = retroResponse.getPlaces();

						Place thisPlace = null;
						if(placeList.size()>0) {
							thisPlace = placeList.get(0);
						}

						setPlace(thisPlace);
					}

					@Override
					public void failure(RetrofitError error) {

					}
				});
	}

	private void setPlace(Place thisPlace) {
		selectedPlace = thisPlace;
		if(thisPlace!=null)
			locationTextView.setText(""+thisPlace.getName());
	}

	private void sendResponse(final Place place) {

		String dateNow = CommonFunctions.formatDate(CommonFunctions.Date_yyyyMMddHHmmss, new Date());
		place.setUpdated_date(dateNow);
		place.setStatus(1);

		Intent intentFinish = new Intent(Constant.FINISH_ACTIVITY);
		sendBroadcast(intentFinish);

		Intent intentPlace = new Intent(Constant.PLACE_ACTIVITY);
		intentPlace.putExtra(Constant.PLACE, place);
		sendBroadcast(intentPlace);

		new Handler().post(new Runnable() {
			@Override
			public void run() {
				close();
			}
		});
	}

	private void close() {
		finish();
		overridePendingTransition(R.anim.stay, R.anim.top_to_bottom);
	}

	@Override
	public void onLocationListenerReady() {
		super.onLocationListenerReady();

		if(GPSTracker.isGpsEnabled(context)) {
			detectLocation();
		}
	}

	@Override
	public void onLocationChange(double lat, double lon) {
		super.onLocationChange(lat, lon);
		MyLog.e(TAG, "latlng22 : "+lat);

		getPlaces(lat, lon);
	}

	@Override
	public void onBackPressed() {
		close();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
            case android.R.id.home:
				close();
                return true;
		}
        return super.onOptionsItemSelected(item);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {

		this.googleMap = googleMap;
		MyLog.e(TAG, "onMapReady googleMap : " + googleMap);

		// check if map is created successfully or not
		if (googleMap == null) {
			MyToast.tshort(getApplicationContext(), "Sorry! unable to create maps");
		}

		if(googleMap!=null) {
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

			// Set initial map position
			LatLng latLng = new LatLng(latitude, longitude);
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
			googleMap.moveCamera(cameraUpdate);

			googleMap.setOnCameraMoveStartedListener(this);
			googleMap.setOnCameraMoveListener(this);
			googleMap.setOnCameraIdleListener(this);
			googleMap.setOnCameraMoveCanceledListener(this);
		}
	}

	@Override
	public void onCameraMoveStarted(int reason) {
		if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
			MyLog.e(TAG, "onCameraMoveStarted The user gestured on the map.");
		} else if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION) {
			MyLog.e(TAG, "onCameraMoveStarted The user tapped something on the map.");
		} else if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION) {
			MyLog.e(TAG, "onCameraMoveStarted The app moved the camera.");
		}
	}

	@Override
	public void onCameraMove() {
		if(googleMap!=null) {
			CameraPosition cameraPosition = googleMap.getCameraPosition();
			LatLng latLon = cameraPosition.target;
			MyLog.e(TAG, "onCameraMove lat : " + latLon.latitude+","+latLon.longitude);
		}
	}

	// This is the state
	@Override
	public void onCameraIdle() {

		if(googleMap!=null) {
			CameraPosition cameraPosition = googleMap.getCameraPosition();
			LatLng latLon = cameraPosition.target;

			double latitude = latLon.latitude;
			double longitude = latLon.longitude;

			MyLog.e(TAG, "onCameraIdle lat : " + latLon.latitude + "," + latLon.longitude);

			getPlaces(latitude, longitude);
		}
	}

	@Override
	public void onCameraMoveCanceled() {

	}
}
