package com.lokaso.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lokaso.R;
import com.lokaso.model.PlaceResult;
import com.lokaso.util.MyFont1;

import java.util.List;

/**
 * Created by Androcid on 23-Aug-16.
 */
public class LocationAdapter1 extends RecyclerView.Adapter<LocationAdapter1.ViewHolder> {

    private String TAG = LocationAdapter1.class.getSimpleName();
    private Context context;
    private List<PlaceResult.Place> placeList;
    private ClickInterface clickInterface;

    public interface ClickInterface {
        public void onItemClick(int position, PlaceResult.Place place);
    }
    public void setOnClickListener(ClickInterface clickInterface) {
        this.clickInterface = clickInterface;
    }

    public LocationAdapter1(Context context, List<PlaceResult.Place> placeList) {
        this.context = context;
        this.placeList = placeList;
    }

    public void refresh(List<PlaceResult.Place> placeList) {
        this.placeList = placeList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location_name, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final PlaceResult.Place place = placeList.get(position);

        holder.tvName.setText(place.getName());
        holder.tvLocation.setText(place.getDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickInterface != null){
                    clickInterface.onItemClick(position, place);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvName, tvLocation;

        public ViewHolder(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);

            MyFont1 myFont = new MyFont1(context);
            myFont.setAppFont((ViewGroup)itemView.findViewById(R.id.container));
            myFont.setFont(tvLocation, MyFont1.CENTURY_GOTHIC_BOLD);
        }
    }

}
