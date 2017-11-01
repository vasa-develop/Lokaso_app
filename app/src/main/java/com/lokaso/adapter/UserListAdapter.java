package com.lokaso.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lokaso.R;
import com.lokaso.activity.OthersProfileActivity;
import com.lokaso.activity.ProfileActivity;
import com.lokaso.model.Profile;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroResponse;
import com.lokaso.util.Constant;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyLog;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;
import com.lokaso.util.Picaso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Androcid on 17-Aug-16.
 */
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private String TAG = UserListAdapter.class.getSimpleName();
    private Context context;
    private List<Profile> folksList;
    private ClickInterface clickInterface;

    private NetworkConnection networkConnection;

    public interface ClickInterface {
        public void onItemClick(int folks_id);

        public void onFollowClick(int action, int leader, int follower, int position);
    }

    public void setOnClickListener(ClickInterface clickInterface) {
        this.clickInterface = clickInterface;
    }

    public UserListAdapter(Context context, List<Profile> folksList) {
        this.context = context;
        this.folksList = folksList;
        networkConnection = new NetworkConnection(context);
    }

    public void refresh(List<Profile> folksList) {
        this.folksList = folksList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Profile profile = folksList.get(position);

        String imageUrl = profile.getImage();
        MyLog.e(TAG, "userImageUrl : "+imageUrl);
        Picaso.loadUser(context, imageUrl, holder.ivPic);


        holder.tvName.setText(profile.getName());
        holder.tvProfession.setText(profile.getProfession());
        holder.tvCreditPoints.setText(profile.getCredits_display() + "");
        holder.tvDistance.setText(profile.getDistance_display() + "");
        holder.tvQueriesAttended.setText(profile.getNum_responses() + "");
        if (profile.isUser_followed()) {
            holder.bFollow.setImageResource(R.drawable.ic_follow_red);
            holder.tvFollowHint.setText(context.getString(R.string.following));
        } else {
            holder.bFollow.setImageResource(R.drawable.ic_follow_grey);
            holder.tvFollowHint.setText(context.getString(R.string.follow));
        }

        holder.bFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickInterface != null) {
                    if (profile.isUser_followed()) {
                        holder.bFollow.setImageResource(R.drawable.ic_follow_grey);
                        holder.tvFollowHint.setText(context.getString(R.string.follow));
                        holder.setFollow(Constant.UNFAV, profile.getId(), MyPreferencesManager.getId(context), position);

                        //clickInterface.onFollowClick(Constant.UNFAV, folks.getId(), MyPreferencesManager.getId(context), position);

                    } else {
                        holder.bFollow.setImageResource(R.drawable.ic_follow_red);
                        holder.tvFollowHint.setText(context.getString(R.string.following));
                        holder.setFollow(Constant.FAV, profile.getId(), MyPreferencesManager.getId(context), position);
                        //clickInterface.onFollowClick(Constant.FAV, folks.getId(), MyPreferencesManager.getId(context), position);
                    }
                }
            }
        });

        holder.ivPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    if (profile.getId() != MyPreferencesManager.getId(context)) {
                        Intent intent = new Intent(context, OthersProfileActivity.class);
                        intent.putExtra(Constant.PROFILE_ID, profile.getId());
                        intent.putExtra(Constant.PROFILE, profile);
                        context.startActivity(intent);
                    } else {
                        Intent intent = new Intent(context, ProfileActivity.class);
                        intent.putExtra(Constant.PROFILE_ID, profile.getId());
                        intent.putExtra(Constant.PROFILE, profile);
                        context.startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
//
//                if (clickInterface != null) {
//                    clickInterface.onItemClick(profile.getId());
//                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return folksList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        protected CircleImageView ivPic;
        protected ImageView bFollow;
        protected TextView tvName, tvProfession, tvCreditPoints, tvDistance, tvQueriesAttended, tvCreditPointsHint, tvDistanceHint,
                tvQueriesAttendedHint, tvFollowHint;

        public ViewHolder(View itemView) {
            super(itemView);

            ivPic = (CircleImageView) itemView.findViewById(R.id.ivPic);
            bFollow = (ImageView) itemView.findViewById(R.id.ivFollow);
            tvFollowHint = (TextView) itemView.findViewById(R.id.tvFollowHint);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
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


        /**
         * method used to follow a folk using API call
         *
         * @param action : action = 1 to follow and action = 0 to un-follow
         * @param leader : user id of the user who is being followed
         * @param follower : user id of the user who will be following
         */
        public void setFollow(final int action, int leader, int follower, final int position) {
            if (networkConnection.isNetworkAvailable()) {
                RestClient.getLokasoApi().follow(action, leader, follower, new Callback<RetroResponse>() {
                    @Override
                    public void success(RetroResponse retroResponse, Response response) {
                        if (action == Constant.FAV) {
                            folksList.get(position).setUser_followed(true);
                        } else {
                            folksList.get(position).setUser_followed(false);
                        }
                        refresh(folksList);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        MyToast.tshort(context, "" + error);
                    }
                });
            } else {
                MyToast.tshort(context, context.getString(R.string.check_network));
            }
        }
    }
}
