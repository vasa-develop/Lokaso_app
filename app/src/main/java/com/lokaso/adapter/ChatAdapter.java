package com.lokaso.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.lokaso.R;
import com.lokaso.model.Chat;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.util.Constant;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyLog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Androcid on 31-Aug-16.
 */
public class ChatAdapter extends BaseAdapter {

    private String TAG = ChatAdapter.class.getSimpleName();
    private Context context;
    private List<Chat> chatList;
    private LayoutInflater inflater;
    private DateFormat sdf;
    private Date date;

    public ChatAdapter(Context context, List<Chat> chatList) {
        this.context = context;
        this.chatList = chatList;
        this.inflater = LayoutInflater.from(this.context);
        this.sdf = new SimpleDateFormat(Constant.DATE_FORMAT, Locale.ENGLISH);
    }

    @Override
    public int getCount() {
        return chatList.size();
    }

    @Override
    public Object getItem(int position) {
        return chatList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_chat, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);

        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        Chat chat = chatList.get(position);
        try {
            date = sdf.parse(chat.getCreated_date());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        MyLog.e(TAG, "date : " + date + " | position : " + position + " | msg : " + chat.getMessage());

        if (chat.getFrom_user_id() == MyPreferencesManager.getId(context)) {
            mViewHolder.layoutOther.setVisibility(View.GONE);
            mViewHolder.layoutCurrent.setVisibility(View.VISIBLE);

            try {
                mViewHolder.tvCurrentDate.setReferenceTime(date.getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
            mViewHolder.tvCurrentUserName.setText(chat.getProfile().getName());
            mViewHolder.tvCurrentMessage.setText(chat.getMessage());

        } else {
            mViewHolder.layoutOther.setVisibility(View.VISIBLE);
            mViewHolder.layoutCurrent.setVisibility(View.GONE);

            try {
                mViewHolder.tvOtherDate.setReferenceTime(date.getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
            mViewHolder.tvOtherUserName.setText(chat.getProfile().getName());
            mViewHolder.tvOtherMessage.setText(chat.getMessage());
        }
        return convertView;
    }

    public void refresh(List<Chat> chatList) {
        this.chatList = new ArrayList<>();
        this.chatList = chatList;
        /*Collections.sort(this.chatList, new Comparator<Chat>() {
            @Override
            public int compare(Chat lhs, Chat rhs) {
                return lhs.getCreated_date().compareTo(rhs.getCreated_date());
            }
        });*/
        notifyDataSetChanged();
    }

    private class MyViewHolder {

        protected TextView tvOtherUserName, tvCurrentUserName, tvOtherMessage, tvCurrentMessage;
        protected RelativeTimeTextView tvCurrentDate, tvOtherDate;
        protected LinearLayout layoutOther, layoutCurrent;

        public MyViewHolder(View itemView) {
            tvOtherMessage = (TextView) itemView.findViewById(R.id.tvOtherMessage);
            tvOtherUserName = (TextView) itemView.findViewById(R.id.tvOtherUserName);
            tvCurrentUserName = (TextView) itemView.findViewById(R.id.tvCurrentUserName);
            tvCurrentMessage = (TextView) itemView.findViewById(R.id.tvCurrentMessage);
            layoutOther = (LinearLayout) itemView.findViewById(R.id.layoutOther);
            layoutCurrent = (LinearLayout) itemView.findViewById(R.id.layoutCurrent);
            tvOtherDate = (RelativeTimeTextView) itemView.findViewById(R.id.tvOtherDate);
            tvCurrentDate = (RelativeTimeTextView) itemView.findViewById(R.id.tvCurrentDate);

            MyFont1 myFont = new MyFont1(context);
            myFont.setAppFont((ViewGroup) itemView);
        }
    }
}
