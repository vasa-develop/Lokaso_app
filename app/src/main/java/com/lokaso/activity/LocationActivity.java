package com.lokaso.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lokaso.R;
import com.lokaso.adapter.LocationAdapter;
import com.lokaso.adapter.LocationAdapter1;
import com.lokaso.custom.CustomEditText;
import com.lokaso.dao.Dao;
import com.lokaso.dao.DaoController;
import com.lokaso.model.Place;
import com.lokaso.model.PlaceDetail;
import com.lokaso.model.PlaceResult;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroPlace;
import com.lokaso.retromodel.RetroPlaceDetail;
import com.lokaso.service.GPSTracker;
import com.lokaso.util.CommonFunctions;
import com.lokaso.util.Constant;
import com.lokaso.util.MyDialog;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyLog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LocationActivity extends GpsActivity {

	private static final String TAG = LocationActivity.class.getSimpleName();
	private Context context = LocationActivity.this;

	private Toolbar toolbar;

	private EditText searchEditText;
	private ImageButton clearButton;

	private TextView nameTextView;
	private ImageButton editButton;

	private LinearLayout searchLayout, nameLayout;

	private RecyclerView recyclerView;
	private LinearLayoutManager llm;

	private LocationAdapter adapter;
	//private List<PlaceResult.Place> list;
	private List<Place> list;

	//private String latitude = "15.4909", longitude = "73.8278";
	private double latitude = 15.4909, longitude = 73.8278;
	private Place gpsPlace = null;

	public static final int FROM_NONE = 0;
	public static final int FROM_SUGGESTION = 1;
	public static final int FROM_QUERY = 2;

	private int from = FROM_NONE;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		context = this;

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		try {
			from = getIntent().getIntExtra(Constant.FROM, FROM_NONE);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Getting a reference to the map

		// Getting reference to btn_find of the layout activity_main
		searchEditText = (EditText) findViewById(R.id.searchEditText);
		clearButton = (ImageButton) findViewById(R.id.clearButton);

		nameTextView = (TextView) findViewById(R.id.nameTextView);
		editButton = (ImageButton) findViewById(R.id.editButton);

		searchLayout = (LinearLayout) findViewById(R.id.searchLayout);
		nameLayout = (LinearLayout) findViewById(R.id.nameLayout);

		recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

		llm = new LinearLayoutManager(context);
		recyclerView.setLayoutManager(llm);
		recyclerView.hasFixedSize();

		list = new ArrayList<>();

		Place place = new Place(getString(R.string.location_text_gps), LocationAdapter.TYPE_GPS);
		list.add(place);

		List<Place> recentPlaces = DaoController.getRecentLocation(context);
		for (int i=0; i<recentPlaces.size(); i++) {
			recentPlaces.get(i).setType(LocationAdapter.TYPE_RECENT);
		}
		if(recentPlaces.size()>0) {
			//Place placeHeader = new Place(getString(R.string.location_text_header_recent), LocationAdapter.TYPE_HEADER);
			//list.add(placeHeader);

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

		nameTextView.setText(MyPreferencesManager.getLocationSelected(context));

		editButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				nameLayout.setVisibility(View.GONE);
				searchLayout.setVisibility(View.VISIBLE);

				searchEditText.requestFocus();

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


		new MyFont1(context).setAppFont((ViewGroup)findViewById(R.id.container));
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

	private void getPlaces(final double lat, final double lon) {
		latitude = lat;
		longitude = lon;

		RestClient.getPlacesApiLatLng().getPlaces(
				lat+","+lon,
				new Callback<RetroPlace>() {
					@Override
					public void success(RetroPlace retroResponse, Response response) {
						List<Place> placeList = retroResponse.getPlaces();

						if(placeList.size()>0) {
							gpsPlace = placeList.get(0);
							gpsPlace.setLatLng(""+lat, ""+lon);
							gpsPlace.setType(LocationAdapter.TYPE_GPS);
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

		// Save Filter Sort by to Latest
		if(from==FROM_SUGGESTION) {
			MyPreferencesManager.setSuggestionFilter(context, Constant.FILTER_BY_DISTANCE);
		}
		else if(from==FROM_QUERY) {
			MyPreferencesManager.setQueryFilter(context, Constant.FILTER_BY_DISTANCE);
		}

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
	public void onLocationChange(final double lat, final double lon) {
		super.onLocationChange(lat, lon);
		MyLog.e(TAG, "latlng22 : "+lat);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				getPlaces(lat, lon);
			}
		},5000);
		//getPlaces(lat, lon);
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

	public static void hideKeyboard(Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		//Find the currently focused view, so we can grab the correct window token from it.
		View view = activity.getCurrentFocus();
		//If no view currently has focus, create a new one, just so we can grab a window token from it
		if (view == null) {
			view = new View(activity);
		}
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
}
