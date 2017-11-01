package com.lokaso.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lokaso.R;
import com.lokaso.model.Profile;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.util.Constant;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyLog;
import com.lokaso.util.Picaso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Androcid on 17-Aug-16.
 */
public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.ViewHolder> {

    private String TAG = FollowersAdapter.class.getSimpleName();
    private Context context;
    private List<Profile> followersList;
    private ClickInterface clickInterface;

    public interface ClickInterface {
        public void onItemClick(Profile profile);

        public void onFollowClick(int action, int leader, int follower, int position);
    }
    public void setOnClickListener(ClickInterface clickInterface){
        this.clickInterface = clickInterface;
    }

    public FollowersAdapter(Context context, List<Profile> followersList) {
        this.context = context;
        this.followersList = followersList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_followers, parent, false);
        return new ViewHolder(view);
    }

    public void refresh(List<Profile> followersList){
        this.followersList = followersList;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Profile profile = followersList.get(position);

        String imageUrl = profile.getImage();
        MyLog.e(TAG, "userImageUrl : "+imageUrl);
        Picaso.loadUser(context, imageUrl, holder.ivPic);

        holder.tvName.setText(profile.getName());
        holder.tvProfession.setText(profile.getProfession());
        holder.tvCreditPoints.setText(profile.getCredits() + "");
        holder.tvDistance.setText(profile.getDistance() + "");
        holder.tvQueriesAttended.setText(profile.getNum_asks() + "");
        if (profile.isUser_followed()) {
            holder.bFollow.setImageResource(R.drawable.ic_follow_red);
            holder.tvFollowHint.setText(context.getString(R.string.following));

        } else {
            holder.bFollow.setImageResource(R.drawable.ic_follow_grey);
            holder.tvFollowHint.setText(context.getString(R.string.follow));
        }

        if (MyPreferencesManager.getId(context) == profile.getId()){
            holder.bFollow.setVisibility(View.GONE);
            holder.tvFollowHint.setVisibility(View.GONE);
        }

        holder.bFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickInterface != null) {
                    if (profile.isUser_followed()) {
                        holder.bFollow.setImageResource(R.drawable.ic_follow_grey);
                        holder.tvFollowHint.setText(context.getString(R.string.follow));
                        clickInterface.onFollowClick(Constant.UNFAV, profile.getId(), MyPreferencesManager.getId(context), position);

                    } else {
                        holder.bFollow.setImageResource(R.drawable.ic_follow_red);
                        holder.tvFollowHint.setText(context.getString(R.string.following));
                        clickInterface.onFollowClick(Constant.FAV, profile.getId(), MyPreferencesManager.getId(context), position);
                    }
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickInterface != null) {
                    clickInterface.onItemClick(profile);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return followersList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        protected CircleImageView ivPic;
        protected ImageView bFollow;
        protected TextView tvName, tvProfession, tvCreditPoints, tvDistance, tvQueriesAttended, tvCreditPointsHint, tvDistanceHint,
                tvQueriesAttendedHint, tvFollowHint;

        public ViewHolder(View itemView) {
            super(itemView);

            ivPic = (CircleImageView) itemView.findViewById(R.id.ivPic);
            bFollow = (ImageView) itemView.findViewById(R.id.ivFollow);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvFollowHint = (TextView) itemView.findViewById(R.id.tvFollowHint);
            tvProfession = (TextView) itemView.findViewById(R.id.tvProfession);
            tvCreditPoints = (TextView) itemView.findViewById(R.id.tvCreditPoints);
            tvDistance = (TextView) itemView.findViewById(R.id.tvDistance);
            tvQueriesAttended = (TextView) itemView.findViewById(R.id.tvQueriesAttended);
            tvCreditPointsHint = (TextView) itemView.findViewById(R.id.tvCreditPointsHint);
            tvDistanceHint = (TextView) itemView.findViewById(R.id.tvDistanceHint);
            tvQueriesAttendedHint = (TextView) itemView.findViewById(R.id.tvQueriesAttendedHint);


            MyFont1 myFont = new MyFont1(context);
            myFont.setAppFont((ViewGroup) itemView);
            myFont.setFont(tvName, MyFont1.CENTURY_GOTHIC_BOLD);
        }
    }

}
