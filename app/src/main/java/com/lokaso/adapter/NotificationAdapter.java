package com.lokaso.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.lokaso.R;
import com.lokaso.model.Notification;
import com.lokaso.util.Constant;
import com.lokaso.util.MyFont1;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Androcid on 12-Aug-16.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private String TAG = NotificationAdapter.class.getSimpleName();
    private Context context;
    private List<Notification> notificationList;
    private DateFormat sdf;
    private Date date;

    public NotificationAdapter(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
        this.sdf = new SimpleDateFormat(Constant.DATE_FORMAT, Locale.ENGLISH);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Notification notification = notificationList.get(position);
        holder.tvName.setText(notification.getProfile().getName());
        holder.tvMessage.setText(notification.getMessage());
        try {
            date = sdf.parse(notification.getCreated_date());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            holder.tvDate.setReferenceTime(date.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvName, tvMessage;
        protected RelativeTimeTextView tvDate;

        public ViewHolder(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvMessage = (TextView) itemView.findViewById(R.id.tvMessage);
            tvDate = (RelativeTimeTextView) itemView.findViewById(R.id.tvDate);

            new MyFont1(context).setAppFont((ViewGroup) itemView.findViewById(R.id.container));
        }
    }
}
