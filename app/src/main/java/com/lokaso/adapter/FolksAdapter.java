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
public class FolksAdapter extends RecyclerView.Adapter<FolksAdapter.ViewHolder> {

    private String TAG = FolksAdapter.class.getSimpleName();
    private Context context;
    private List<Profile> folksList;
    private ClickInterface clickInterface;

    public interface ClickInterface {
        public void onItemClick(int folks_id);

        public void onFollowClick(int action, int leader, int follower, int position);
    }

    public void setOnClickListener(ClickInterface clickInterface) {
        this.clickInterface = clickInterface;
    }

    public FolksAdapter(Context context, List<Profile> folksList) {
        this.context = context;
        this.folksList = folksList;
    }

    public void refresh(List<Profile> folksList) {
        this.folksList = folksList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folks, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Profile folks = folksList.get(position);

        String imageUrl = folks.getImage();
        MyLog.e(TAG, "userImageUrl : "+imageUrl);
        Picaso.loadUser(context, imageUrl, holder.ivPic);


        holder.tvName.setText(folks.getName());
        holder.tvProfession.setText(folks.getProfession());
        holder.tvCreditPoints.setText(folks.getCredits() + "");
        holder.tvDistance.setText(folks.getDistance() + "");
        holder.tvQueriesAttended.setText(folks.getNum_responses() + "");
        if (folks.isUser_followed()) {
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
                    if (folks.isUser_followed()) {
                        holder.bFollow.setImageResource(R.drawable.ic_follow_grey);
                        holder.tvFollowHint.setText(context.getString(R.string.follow));
                        clickInterface.onFollowClick(Constant.UNFAV, folks.getId(), MyPreferencesManager.getId(context), position);

                    } else {
                        holder.bFollow.setImageResource(R.drawable.ic_follow_red);
                        holder.tvFollowHint.setText(context.getString(R.string.following));
                        clickInterface.onFollowClick(Constant.FAV, folks.getId(), MyPreferencesManager.getId(context), position);
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
                if (clickInterface != null) {
                    clickInterface.onItemClick(folks.getId());
                }
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
    }

}
