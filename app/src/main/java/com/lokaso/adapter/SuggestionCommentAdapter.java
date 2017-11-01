package com.lokaso.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.lokaso.R;
import com.lokaso.model.DiscoveryComment;
import com.lokaso.model.Profile;
import com.lokaso.util.Constant;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyLog;
import com.lokaso.util.Picaso;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Androcid on 12-Aug-16.
 */
public class SuggestionCommentAdapter extends RecyclerView.Adapter<SuggestionCommentAdapter.ViewHolder> {

    private String TAG = SuggestionCommentAdapter.class.getSimpleName();
    private Context context;
    private List<DiscoveryComment> discoveryCommentList;
    private Picasso picasso;
    private DateFormat sdf;
    private Date date;
    private ClickInterface clickInterface;

    public interface ClickInterface {
        public void onItemClick(DiscoveryComment discoveryComment, int position);
        public void onUserClick(Profile profile, int position);
    }

    public void setOnClickListener(ClickInterface clickInterface) {
        this.clickInterface = clickInterface;
    }

    public SuggestionCommentAdapter(Context context, List<DiscoveryComment> discoveryCommentList) {
        this.context = context;
        this.discoveryCommentList = discoveryCommentList;
        this.picasso = Picasso.with(context);
        this.sdf = new SimpleDateFormat(Constant.DATE_FORMAT, Locale.ENGLISH);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_discovery_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final DiscoveryComment discoveryComment = discoveryCommentList.get(position);
        try {
            date = sdf.parse(discoveryComment.getCreated_date());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        MyLog.e(TAG, "date : " + date);



        String imageUrl = discoveryComment.getProfile().getImage();
        MyLog.e(TAG, "userImageUrl : "+imageUrl);
        Picaso.loadUser(context, imageUrl, holder.ivPic);

        holder.tvName.setText(discoveryComment.getProfile().getName());
        holder.tvComment.setText(discoveryComment.getComment());
        holder.tvDate.setReferenceTime(date.getTime());

        holder.ivPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clickInterface!=null) {
                    Profile profile = discoveryCommentList.get(position).getProfile();
                    clickInterface.onUserClick(profile, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return discoveryCommentList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        protected CircleImageView ivPic;
        protected TextView tvName, tvComment;
        protected RelativeTimeTextView tvDate;

        public ViewHolder(View itemView) {
            super(itemView);

            ivPic = (CircleImageView) itemView.findViewById(R.id.ivPic);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvComment = (TextView) itemView.findViewById(R.id.tvComment);
            tvDate = (RelativeTimeTextView) itemView.findViewById(R.id.tvDate);

            MyFont1 myFont = new MyFont1(context);
            myFont.setAppFont((ViewGroup) itemView);
            myFont.setFont(tvName, MyFont1.CENTURY_GOTHIC_BOLD);
        }
    }

}
