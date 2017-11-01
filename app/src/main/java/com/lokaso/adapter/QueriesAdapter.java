package com.lokaso.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.lokaso.R;
import com.lokaso.model.Queries;
import com.lokaso.util.Constant;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyLog;
import com.lokaso.util.Picaso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Androcid on 17-Aug-16.
 */
public class QueriesAdapter extends RecyclerView.Adapter<QueriesAdapter.ViewHolder> {

    private String TAG = QueriesAdapter.class.getSimpleName();
    private Context context;
    private List<Queries> queriesList;
    private ClickInterface clickInterface;

    public void refresh(List<Queries> queriesList) {
        this.queriesList = queriesList;
        notifyDataSetChanged();
    }

    public interface ClickInterface {
        public void itemClick(Queries queries);

        public void followQuery(int action, int query_id);
    }

    public void setOnClickListener(ClickInterface clickInterface) {
        this.clickInterface = clickInterface;
    }

    public QueriesAdapter(Context context, List<Queries> queriesList) {
        this.context = context;
        this.queriesList = queriesList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_queries, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Queries queries = queriesList.get(position);
        if (queries != null) {
            try {
                MyLog.e(TAG, "queries name : " + queries.getProfile().getName());
                holder.tvName.setText(queries.getProfile().getName());
                holder.tvProfesion.setText(queries.getProfile().getProfession());

                String imageUrl = queries.getProfile().getImage();
                MyLog.e(TAG, "userImageUrl : "+imageUrl);
                Picaso.loadUser(context, imageUrl, holder.ivPic);

            } catch (Exception e) {
                MyLog.e(TAG, "queries id : " + queries.getId());
                e.printStackTrace();
            }

            holder.tvQuery.setText(queries.getDescription());
            holder.tvDistance.setText(queries.getDistance() + " km");
            holder.tvResponses.setText(queries.getResponse_count() + "");
            holder.bFollowQuery.setChecked(queries.isQuery_fav());
//
//            if (MyPreferencesManager.getId(context) == queries.getProfile().getId()){
//                holder.layoutQuery.setVisibility(View.GONE);
//
//            } else {
//                holder.layoutQuery.setVisibility(View.VISIBLE);
//            }
        }

        if (!queries.getValid_until().equals("0")) {
            holder.tvValidity.setText(queries.getValid_until());
            holder.tvValidityHint.setVisibility(View.VISIBLE);

        } else {
            holder.tvValidity.setText("Expired");
            holder.tvValidityHint.setVisibility(View.GONE);
        }

        holder.bFollowQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickInterface != null) {
                    if (holder.bFollowQuery.isChecked()) {
                        clickInterface.followQuery(Constant.FAV, queries.getId());

                    } else {
                        clickInterface.followQuery(Constant.UNFAV, queries.getId());
                    }
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickInterface != null) {
                    clickInterface.itemClick(queries);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return queriesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        protected CircleImageView ivPic;
        protected TextView tvName, tvProfesion, tvQuery, tvResponses, tvDistance, tvValidity, tvResponsesHint, tvValidityHint,
                tvDistanceHint;
        private LinearLayout layoutQuery;
        protected ToggleButton bFollowQuery;

        public ViewHolder(View itemView) {
            super(itemView);

            ivPic = (CircleImageView) itemView.findViewById(R.id.ivPic);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvProfesion = (TextView) itemView.findViewById(R.id.tvProfesion);
            tvQuery = (TextView) itemView.findViewById(R.id.tvQuery);
            tvResponses = (TextView) itemView.findViewById(R.id.tvResponses);
            tvDistance = (TextView) itemView.findViewById(R.id.tvDistance);
            tvValidity = (TextView) itemView.findViewById(R.id.tvValidity);
            bFollowQuery = (ToggleButton) itemView.findViewById(R.id.bFollowQuery);
            tvResponsesHint = (TextView) itemView.findViewById(R.id.tvResponsesHint);
            tvValidityHint = (TextView) itemView.findViewById(R.id.tvValidityHint);
            tvDistanceHint = (TextView) itemView.findViewById(R.id.tvDistanceHint);
            layoutQuery = (LinearLayout) itemView.findViewById(R.id.layoutQuery);

            MyFont1 myFont = new MyFont1(context);
            myFont.setAppFont((ViewGroup) itemView);
            myFont.setFont(tvName, MyFont1.CENTURY_GOTHIC_BOLD);
        }
    }
}
