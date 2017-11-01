package com.lokaso.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.lokaso.R;
import com.lokaso.model.Place;
import com.lokaso.model.PlaceResult;
import com.lokaso.util.MyFont1;

import java.util.List;

/**
 * Created by Androcid on 23-Aug-16.
 */
public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private String TAG = LocationAdapter.class.getSimpleName();
    private Context context;
    private List<Place> placeList;

    public static final int TYPE_LOCATION       = 0;
    public static final int TYPE_RECENT         = 1;
    public static final int TYPE_GPS            = 2;
    public static final int TYPE_HEADER         = 3;

    public LocationAdapter(Context context, List<Place> placeList) {
        this.context = context;
        this.placeList = placeList;
    }

    public void refresh(List<Place> placeList) {
        this.placeList = placeList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = placeList.get(position).getType();
        return viewType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType==TYPE_GPS)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location_gps, parent, false);
        else if(viewType==TYPE_HEADER)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location_header, parent, false);
        else if(viewType==TYPE_RECENT)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location_recent, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location_name, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Place place = placeList.get(position);

        holder.tvName.setText(place.getName());

        if(place.getType()==TYPE_LOCATION)
            holder.tvLocation.setText(place.getLocation());

        if(place.getType()==TYPE_HEADER) {
            // No click
        }
        else {

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(place.getType()==TYPE_GPS) {
                        holder.animateView(holder.gpsImageView);
                    }

                    if (clickInterface != null) {
                        clickInterface.onItemClick(position, place);
                    }
                }
            });
        }

        if(place.isSearchingGps()) {

        }
        else {

        }
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvName, tvLocation;
        private ImageView gpsImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);

            gpsImageView = (ImageView) itemView.findViewById(R.id.gpsImageView);

            MyFont1 myFont = new MyFont1(context);
            myFont.setAppFont((ViewGroup)itemView.findViewById(R.id.container));
            //myFont.setFont(tvLocation, MyFont1.CENTURY_GOTHIC_BOLD);
        }


        public void animateView(View view) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.rebound);
            view.startAnimation(animation);
        }
    }

    private ClickInterface clickInterface;
    public interface ClickInterface {
        void onItemClick(int position, Place place);
    }
    public void setOnClickListener(ClickInterface clickInterface) {
        this.clickInterface = clickInterface;
    }
}
