package com.lokaso.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.lokaso.R;
import com.lokaso.model.ResponseComment;
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
public class ResponseCommentAdapter extends RecyclerView.Adapter<ResponseCommentAdapter.ViewHolder> {

    private String TAG = ResponseCommentAdapter.class.getSimpleName();
    private Context context;
    private List<ResponseComment> responseCommentList;
    private Picasso picasso;
    private DateFormat sdf;
    private Date date;

    public ResponseCommentAdapter(Context context, List<ResponseComment> responseCommentList) {
        this.context = context;
        this.responseCommentList = responseCommentList;
        this.picasso = Picasso.with(context);
        this.sdf = new SimpleDateFormat(Constant.DATE_FORMAT, Locale.ENGLISH);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_discovery_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ResponseComment responseComment = responseCommentList.get(position);
        try {
            date = sdf.parse(responseComment.getCreated_date());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String imageUrl = responseComment.getProfile().getImage();
        MyLog.e(TAG, "userImageUrl : "+imageUrl);
        Picaso.loadUser(context, imageUrl, holder.ivPic);

        holder.tvName.setText(responseComment.getProfile().getName());
        holder.tvComment.setText(responseComment.getComment());
        holder.tvDate.setReferenceTime(date.getTime());
    }

    @Override
    public int getItemCount() {
        return responseCommentList.size();
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
