package com.lokaso.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

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
import com.lokaso.util.MyLog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LocationGoogleActivity extends GpsActivity {

	private static final String TAG = LocationGoogleActivity.class.getSimpleName();
	private Context context = LocationGoogleActivity.this;

	private Toolbar toolbar;

	private EditText searchEditText;
	private ImageButton clearButton;

	private RecyclerView recyclerView;
	private LinearLayoutManager llm;

	private LocationAdapter adapter;
	//private List<PlaceResult.Place> list;
	private List<Place> list;

	//private String latitude = "15.4909", longitude = "73.8278";
	private double latitude = 15.4909, longitude = 73.8278;
	private Place gpsPlace = null;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		context = this;

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Getting a reference to the map

		// Getting reference to btn_find of the layout activity_main
		searchEditText = (EditText) findViewById(R.id.searchEditText);
		clearButton = (ImageButton) findViewById(R.id.clearButton);
		recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

		llm = new LinearLayoutManager(context);
		recyclerView.setLayoutManager(llm);
		recyclerView.hasFixedSize();

		list = new ArrayList<>();

		Place place = new Place(getString(R.string.location_text_gps), LocationAdapter.TYPE_GPS);
		list.add(place);

		List<Place> recentPlaces = DaoController.getAllLocation(context);
		for (int i=0; i<recentPlaces.size(); i++) {
			recentPlaces.get(i).setType(LocationAdapter.TYPE_RECENT);
		}
		if(recentPlaces.size()>0) {
			Place placeHeader = new Place(getString(R.string.location_text_header_recent), LocationAdapter.TYPE_HEADER);
			list.add(placeHeader);

			list.addAll(recentPlaces);
		}

		adapter = new LocationAdapter(context, list);
		recyclerView.setAdapter(adapter);

		adapter.setOnClickListener(new LocationAdapter.ClickInterface() {
			@Override
			public void onItemClick(int position, Place place) {

				if(place!=null) {
					if(place.getType()==LocationAdapter.TYPE_GPS) {

						if(place.getName().equals(getString(R.string.location_text_gps))) {
							detectLocation();
						}
						else {
							sendResponse(place);
						}
					}
					else if(place.getType()==LocationAdapter.TYPE_LOCATION) {
						getPlacesDetail(place.getPlace_id());
					}
					else if(place.getType()==LocationAdapter.TYPE_RECENT) {

						// Here it would be direct
						sendResponse(place);

						// Temporary
						//getPlacesDetail(place.getPlace_id());
					}
				}
			}
		});

		clearButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchEditText.setText("");
			}
		});

		CustomEditText.with(context)
				.into(searchEditText)
				.start()
				.watcher(new CustomEditText.TextWatcherListener() {
					@Override
					public void onTextChangeComplete(String text) {

						MyLog.e(TAG, "texttext:"+text);

						getPlaces(text);
					}

					@Override
					public void onTextChange(String text) {
//
					}
				})
				.threshold(new CustomEditText.ThresholdListener() {
					@Override
					public void lessThanThreshold(String text) {
						clearButton.setVisibility(View.GONE);
					}

					@Override
					public void equalToThreshold(String text) {
						clearButton.setVisibility(View.VISIBLE);
					}

					@Override
					public void moreThanThreshold(String text) {
						clearButton.setVisibility(View.VISIBLE);
					}
				});
	}

	private void getPlaces(String name) {

		RestClient.getPlacesApiAutocomplete().getPlaces(
			name, latitude+","+longitude,
			new Callback<RetroPlace>() {
				@Override
				public void success(RetroPlace retroResponse, Response response) {
					List<Place> placeList = retroResponse.getPlaces();
					setPlaceList(placeList);
					adapter.refresh(list);
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


						if(placeList.size()>0) {
							gpsPlace = placeList.get(0);
						}

						setPlaceGps(gpsPlace);
					}

					@Override
					public void failure(RetrofitError error) {

					}
				});
	}

	private void setPlaceList(List<Place> placeList) {
		for (int i=0;i<placeList.size(); i++) {
			placeList.get(i).setType(LocationAdapter.TYPE_LOCATION);
		}

		if(gpsPlace==null)
			gpsPlace = new Place(getString(R.string.location_text_gps), LocationAdapter.TYPE_GPS);

		list = new ArrayList<>();
		list.add(gpsPlace);

		list.addAll(placeList);
	}

	private void setPlaceGps(Place thisPlace) {

		thisPlace.setType(LocationAdapter.TYPE_GPS);
		if(list.size()>0) {
			list.set(0, thisPlace);
		}
		adapter.refresh(list);
	}

	private void sendResponse(Place place) {

		String dateNow = CommonFunctions.formatDate(CommonFunctions.Date_yyyyMMddHHmmss, new Date());
		place.setUpdated_date(dateNow);
		place.setStatus(1);

		Place placeLocal = DaoController.getLocation(context, place.getPlace_id());
		if(placeLocal!=null) {
			place.setCreated_date(placeLocal.getCreated_date());
		}
		else {
			place.setCreated_date(dateNow);
		}

		DaoController.updateLocation(context, place);

		Intent intent = new Intent();
		intent.putExtra(Constant.PLACE, place);
		setResult(Constant.SELECTION_LOCATION, intent);

		finish();



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
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
		}
        return super.onOptionsItemSelected(item);
	}
}
