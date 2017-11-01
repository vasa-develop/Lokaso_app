package com.lokaso.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lokaso.R;
import com.lokaso.model.ChatRoom;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyLog;
import com.lokaso.util.Picaso;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Androcid on 23-Aug-16.
 */
public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder> {

    private String TAG = ChatRoomAdapter.class.getSimpleName();
    private Context context;
    private List<ChatRoom> chatRoomList;
    private ClickInterface clickInterface;
    private Picasso picasso;

    public interface ClickInterface {
        public void onItemClick(ChatRoom chatRoom);
    }
    public void setOnClickListener(ClickInterface clickInterface) {
        this.clickInterface = clickInterface;
    }

    public ChatRoomAdapter(Context context, List<ChatRoom> chatRoomList) {
        this.context = context;
        this.chatRoomList = chatRoomList;
        this.picasso = Picasso.with(context);
    }

    public void refresh(List<ChatRoom> chatRoomList) {
        this.chatRoomList = chatRoomList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ChatRoom chatRoom = chatRoomList.get(position);

        String imageUrl = chatRoom.getProfile().getImage();
        MyLog.e(TAG, "userImageUrl : "+imageUrl);
        Picaso.loadUser(context, imageUrl, holder.ivPic);

        holder.tvProfession.setText(chatRoom.getProfile().getProfession());
        holder.tvName.setText(chatRoom.getProfile().getName());
        holder.tvUnreadCount.setText(chatRoom.getUnread_count() + "");

        if (chatRoom.getUnread_count() > 0){
            holder.ivChatRead.setImageResource(R.drawable.ic_chat_red);
            holder.ivChatRead.setSelected(true);
            holder.ivChatRead.setColorFilter(ContextCompat.getColor(context,R.color.red));

            holder.tvUnreadCount.setVisibility(View.VISIBLE);

        } else {
            holder.ivChatRead.setImageResource(R.drawable.ic_comment_black);
            holder.ivChatRead.setSelected(false);
            holder.tvUnreadCount.setVisibility(View.GONE);
            holder.ivChatRead.setColorFilter(ContextCompat.getColor(context,R.color.gray_text));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickInterface != null){
                    clickInterface.onItemClick(chatRoom);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatRoomList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        protected ImageView ivPic, ivChatRead;
        protected TextView tvProfession, tvName, tvUnreadCount;

        public ViewHolder(View itemView) {
            super(itemView);

            ivPic = (ImageView) itemView.findViewById(R.id.ivPic);
            ivChatRead = (ImageView) itemView.findViewById(R.id.ivChatRead);
            tvProfession = (TextView) itemView.findViewById(R.id.tvProfession);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvUnreadCount = (TextView) itemView.findViewById(R.id.tvUnreadCount);

            MyFont1 myFont = new MyFont1(context);
            myFont.setAppFont((ViewGroup)itemView.findViewById(R.id.container));
            myFont.setFont(tvUnreadCount, MyFont1.CENTURY_GOTHIC_BOLD);
        }
    }

}
