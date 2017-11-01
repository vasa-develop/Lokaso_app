package com.lokaso.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.lokaso.R;
import com.lokaso.model.QueryResponse;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.util.Constant;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyLog;
import com.lokaso.util.Picaso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Androcid on 12-Aug-16.
 */
public class QueryResponseAdapter extends RecyclerView.Adapter<QueryResponseAdapter.ViewHolder> {

    private String TAG = QueryResponseAdapter.class.getSimpleName();
    private Context context;
    private List<QueryResponse> queryResponseList;

    private ClickInterface clickInterface;

    public void refresh(List<QueryResponse> queryResponseList) {
        this.queryResponseList = queryResponseList;
        notifyDataSetChanged();
    }

    public interface ClickInterface {
        public void onItemClick(QueryResponse queryResponse);

        public void onCommentClick(QueryResponse queryResponse);

        public void onVoteClick(int response_id, int action, int position);

        public void onEditClick(int response_id, int position);

        public void onSpamClick(int action, int response_id, int position);
    }

    public void setOnClickListener(ClickInterface clickInterface) {
        this.clickInterface = clickInterface;
    }

    public QueryResponseAdapter(Context context, List<QueryResponse> queryResponseList) {
        this.context = context;
        this.queryResponseList = queryResponseList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_query_response, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final QueryResponse queryResponse = queryResponseList.get(position);

        String imageUrl = queryResponse.getProfile().getImage();
        MyLog.e(TAG, "userImageUrl : "+imageUrl);
        Picaso.loadUser(context, imageUrl, holder.ivPic);


        holder.tvName.setText(queryResponse.getProfile().getName());
        holder.tvComment.setText(queryResponse.getResponse());
        holder.tvUpvote.setText(queryResponse.getUpvotes() + "");
        holder.tvDownvote.setText(queryResponse.getDownvotes() + "");
        holder.tvCommentCount.setText(queryResponse.getComment_count() + "");
        holder.tvProfession.setText(queryResponse.getProfile().getProfession());

        if (queryResponse.getComment_count() > 0) {
            holder.bComment.setBackgroundResource(R.drawable.ic_comment_orange);
        }

        if (queryResponse.getUser_fav() == 1) {
            holder.bUpvote.setChecked(true);

        } else if (queryResponse.getUser_fav() == 0) {
            holder.bDownvote.setChecked(true);
        }

        if (queryResponse.getProfile().getId() == MyPreferencesManager.getId(context)) {
            holder.layoutReport.setVisibility(View.GONE);
            holder.layoutComment.setVisibility(View.VISIBLE);

        } else {
            holder.layoutComment.setVisibility(View.GONE);
            holder.layoutReport.setVisibility(View.VISIBLE);
        }

        if (queryResponse.isSpam()) {
            holder.bReport.setChecked(true);

        } else {
            holder.bReport.setChecked(false);
        }

        holder.bUpvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickInterface != null) {
                    if (holder.bUpvote.isChecked()) {
                        clickInterface.onVoteClick(queryResponse.getId(), Constant.FAV, position);
                        queryResponse.setUser_fav(1);
                        holder.bDownvote.setChecked(false);

                    } else {
                        holder.bUpvote.setChecked(true);
                    }
                }
            }
        });

        holder.bDownvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickInterface != null) {
                    if (holder.bDownvote.isChecked()) {
                        clickInterface.onVoteClick(queryResponse.getId(), Constant.UNFAV, position);
                        queryResponse.setUser_fav(0);
                        holder.bUpvote.setChecked(false);

                    } else {
                        holder.bDownvote.setChecked(true);
                    }
                }
            }
        });

        holder.bComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickInterface != null) {
                    clickInterface.onCommentClick(queryResponse);
                }
            }
        });

        holder.bEditComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickInterface != null) {
                    clickInterface.onEditClick(queryResponse.getId(), position);
                }
            }
        });

        holder.bReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickInterface != null) {
                    if (holder.bReport.isChecked()) {
                        clickInterface.onSpamClick(Constant.FAV, queryResponse.getId(), position);

                    } else {
                        clickInterface.onSpamClick(Constant.UNFAV, queryResponse.getId(), position);
                    }
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickInterface != null) {
                    clickInterface.onItemClick(queryResponse);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return queryResponseList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        protected CircleImageView ivPic;
        protected TextView tvName, tvProfession, tvComment, tvUpvote, tvDownvote, tvCommentCount;
        protected ToggleButton bReport, bUpvote, bDownvote;
        protected ImageButton bComment, bEditComment;
        protected LinearLayout layoutComment, layoutReport;

        public ViewHolder(View itemView) {
            super(itemView);

            ivPic = (CircleImageView) itemView.findViewById(R.id.ivPic);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvProfession = (TextView) itemView.findViewById(R.id.tvProfession);
            tvComment = (TextView) itemView.findViewById(R.id.tvComment);
            bReport = (ToggleButton) itemView.findViewById(R.id.bReport);
            bUpvote = (ToggleButton) itemView.findViewById(R.id.bUpvote);
            bDownvote = (ToggleButton) itemView.findViewById(R.id.bDownvote);
            bComment = (ImageButton) itemView.findViewById(R.id.bComment);
            bEditComment = (ImageButton) itemView.findViewById(R.id.bEditComment);
            tvUpvote = (TextView) itemView.findViewById(R.id.tvUpvote);
            tvDownvote = (TextView) itemView.findViewById(R.id.tvDownvote);
            tvCommentCount = (TextView) itemView.findViewById(R.id.tvCommentCount);
            layoutComment = (LinearLayout) itemView.findViewById(R.id.layoutComment);
            layoutReport = (LinearLayout) itemView.findViewById(R.id.layoutReport);

            MyFont1 myFont = new MyFont1(context);
            myFont.setAppFont((ViewGroup) itemView);
            myFont.setFont(tvName, MyFont1.CENTURY_GOTHIC_BOLD);
        }
    }

}
