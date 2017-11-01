package com.lokaso.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lokaso.R;
import com.lokaso.model.Place;
import com.lokaso.util.MyFont1;

import java.util.List;

/**
 * Created by Androcid on 19-Aug-16.
 */
public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    private String TAG = PlaceAdapter.class.getSimpleName();
    private Context context;

    private List<Place> placeList;
    private ClickInterface clickInterface;
    private int lastPosition = 0;
    private Animation animation;

    public interface ClickInterface {
        public void onItemClick(Place place);

    }

    public void setOnClickListener(ClickInterface clickInterface) {
        this.clickInterface = clickInterface;
    }

    public PlaceAdapter(Context context, List<Place> placeList) {
        this.context = context;
        this.placeList = placeList;
    }

    public PlaceAdapter(Context context, List<Place> placeList, String tag) {
        this.context = context;
        this.placeList = placeList;
    }

    public void refresh(List<Place> placeList) {
        this.placeList = placeList;
        lastPosition = 0;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    public void animate(RecyclerView.ViewHolder viewHolder, int position) {
        if (position > lastPosition) {
            viewHolder.itemView.clearAnimation();
            final Animation animation = AnimationUtils.loadAnimation(context, R.anim.left_from_right);
            viewHolder.itemView.setAnimation(animation);
            lastPosition = position;
        }
    }

    public void animateView(View view) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.rebound);
        view.startAnimation(animation);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
//        animate(holder, position);
        final Place place = placeList.get(position);

        holder.tvName.setText(place.getName() + "");
//        holder.tvComment.setText(suggestion.getComment_count() + "");
        holder.tvLocation.setText(place.getLocation());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickInterface != null) {
                    clickInterface.onItemClick(place);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        protected RelativeLayout container;
        protected TextView tvName, tvLocation;

        public ViewHolder(View itemView) {
            super(itemView);
            container = (RelativeLayout) itemView.findViewById(R.id.container);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);

            setTypeface(container);
        }
    }

    private void hideVisibility(ViewHolder holder) {
        holder.container.setLayoutParams(new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 220));
    }

    private void setTypeface(ViewGroup container) {
        MyFont1 myFont = new MyFont1(context);
        myFont.setAppFont(container);

    }
}
