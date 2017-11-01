package com.lokaso.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lokaso.R;
import com.lokaso.adapter.ChatRoomAdapter;
import com.lokaso.gcm.QuickstartPreferences;
import com.lokaso.model.ChatRoom;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroChatRoom;
import com.lokaso.util.Constant;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ChatRoomActivity extends AppCompatActivity {

    private String TAG = ChatRoomActivity.class.getSimpleName();
    private Context context = ChatRoomActivity.this;

    private RecyclerView recyclerView;
    private ChatRoomAdapter chatRoomAdapter;
    private List<ChatRoom> chatRoomList = new ArrayList<>();
    private NetworkConnection networkConnection;
    private AlertDialog.Builder builder;

    private TextView tvError;
    private ImageView tvErrorImage;
    private CardView tvErrorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        builder = new AlertDialog.Builder(context);
        networkConnection = new NetworkConnection(context);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        tvError = (TextView) findViewById(R.id.tvError);
        tvErrorImage = (ImageView) findViewById(R.id.tvErrorImage);
        tvErrorLayout = (CardView) findViewById(R.id.tvErrorLayout);

        if (toolbar != null) {
            toolbar.setTitle(R.string.chat_room);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.hasFixedSize();

        chatRoomAdapter = new ChatRoomAdapter(context, chatRoomList);
        recyclerView.setAdapter(chatRoomAdapter);

        chatRoomAdapter.setOnClickListener(new ChatRoomAdapter.ClickInterface() {
            @Override
            public void onItemClick(ChatRoom chatRoom) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra(Constant.PROFILE, chatRoom.getProfile());
                intent.putExtra(Constant.CHAT_ID, chatRoom.getId());
                startActivity(intent);
                //overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);
            }
        });

        new MyFont1(context).setAppFont((ViewGroup)findViewById(R.id.container));
    }


    BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(QuickstartPreferences.PUSH_NOTIFICATION)) {
                getUsersChatRoom();
            }
        }
    };

    /**
     * Method used to Register receiver for GCM push notifications
     */
    private void registerReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.PUSH_NOTIFICATION));
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
    @Override
    protected void onStart() {
        super.onStart();
        getUsersChatRoom();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //overridePendingTransition(R.anim.close_enter_animate, R.anim.close_exit_animate);
    }

    /**
     * Method used to get Chat room of the current user using API call
     */
    private void getUsersChatRoom() {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().getUsersChatRoom(MyPreferencesManager.getId(context), new Callback<RetroChatRoom>() {
                @Override
                public void success(RetroChatRoom retroChatRoom, Response response) {
                    chatRoomAdapter.refresh(retroChatRoom.getDetails());
                    if (retroChatRoom.getDetails().size() == 0){
                        tvErrorLayout.setVisibility(View.VISIBLE);
                        tvError.setText("No chat rooms found");
                        tvErrorImage.setImageResource(R.drawable.ic_holder_chat);
                    } else {
                        tvErrorLayout.setVisibility(View.GONE);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
//                    MyToast.tshort(context, "" + error);
                }
            });
        } else {
            MyToast.tshort(context, getString(R.string.check_network));
        }
    }
}
