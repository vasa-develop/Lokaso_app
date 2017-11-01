package com.lokaso.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lokaso.R;
import com.lokaso.adapter.ChatAdapter;
import com.lokaso.gcm.QuickstartPreferences;
import com.lokaso.model.Action;
import com.lokaso.model.Chat;
import com.lokaso.model.Profile;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroAction;
import com.lokaso.retromodel.RetroChat;
import com.lokaso.retromodel.RetroCreateChatroom;
import com.lokaso.retromodel.RetroResponse;
import com.lokaso.util.Constant;
import com.lokaso.util.Helper;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyLog;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;
import com.lokaso.util.Picaso;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ChatActivity extends AppCompatActivity {

    private String TAG = ChatActivity.class.getSimpleName(), chat_user = "";
    private Context context = ChatActivity.this;

    private ListView listView;
    private EditText tvMessage;
    private ImageButton bSend;
    private List<Chat> chatList = new ArrayList<>();
    private ChatAdapter testAdapter;
    private Profile profile;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private int chat_id = 0;
    private NetworkConnection networkConnection;
    private AlertDialog.Builder builder;
    private Button bReport, bProfile;
    private Dialog dialog;
    private boolean checkReport = false;
    private Toolbar toolbar;
    private ImageView ivProfile;
    private TextView tvProfileName, tvProfession;

    private RelativeLayout progress_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        builder = new AlertDialog.Builder(context);
        networkConnection = new NetworkConnection(context);
        /*profile = (Profile) getIntent().getSerializableExtra(Constant.PROFILE);
        chat_id = getIntent().getIntExtra(Constant.CHAT_ID, 0);
        chat_user = profile.getName();*/

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        ivProfile = (ImageView) findViewById(R.id.ivProfile);
        tvProfileName = (TextView) findViewById(R.id.tvProfileName);
        tvProfession = (TextView) findViewById(R.id.tvProfession);
        bSend = (ImageButton) findViewById(R.id.bSend);
        tvMessage = (EditText) findViewById(R.id.tvMessage);
        listView = (ListView) findViewById(R.id.listView);

        progress_layout = (RelativeLayout) findViewById(R.id.progress_layout);
        progress_layout.setVisibility(View.GONE);

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_query_action);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.RIGHT | Gravity.TOP;
        bReport = (Button) dialog.findViewById(R.id.bReport);
        bProfile = (Button) dialog.findViewById(R.id.bFlag);
        bProfile.setText(getResources().getString(R.string.view_profile));

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setSupportActionBar(toolbar);
        /*
        toolbar_layout.inflateMenu(R.menu.menu_option);
        toolbar_layout.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.id_options:
                        dialog.show();
                        return true;
                }
                return false;
            }
        });
        */
            /*toolbar_layout.inflateMenu(R.menu.menu_view_profile);
            toolbar_layout.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.idViewProfile:
                            Intent intent = new Intent(context, OthersProfileActivity.class);
                            intent.putExtra("profile", profile.getId());
                            startActivity(intent);
                            return true;
                    }
                    return false;
                }
            });*/

        setFonts();

        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chat_message = tvMessage.getText().toString().trim();
                sendMessage(chat_message);
            }
        });

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(QuickstartPreferences.PUSH_NOTIFICATION)) {
                    handlePushNotification(intent);
                }
            }
        };

        bReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkReport) {
                    bReport.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_report, 0, 0, 0);
                    userSpam(Constant.UNFAV, profile.getId(), MyPreferencesManager.getId(context));
                    checkReport = false;

                } else {
                    bReport.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_report_orange, 0, 0, 0);
                    userSpam(Constant.FAV, profile.getId(), MyPreferencesManager.getId(context));
                    checkReport = true;
                }
                dialog.dismiss();
            }
        });

        bProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OthersProfileActivity.class);
                intent.putExtra(Constant.PROFILE_ID, profile.getId());
                intent.putExtra(Constant.PROFILE, profile);
                startActivity(intent);
                //overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        profile = (Profile) getIntent().getSerializableExtra(Constant.PROFILE);
        chat_id = getIntent().getIntExtra(Constant.CHAT_ID, 0);
        chat_user = profile.getName();

        String imageUrl = profile.getImage();
        MyLog.e(TAG, "userImageUrl : "+imageUrl);
        Picaso.loadUser(context, imageUrl, ivProfile);

        tvProfileName.setText(chat_user);
        tvProfession.setText(profile.getProfession());

        if (!profile.isReport()) {
            bReport.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_report, 0, 0, 0);

        } else {
            bReport.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_report_orange, 0, 0, 0);
        }

        checkReport = profile.isReport();

        if (chat_id == 0) {
            createChatRoom(MyPreferencesManager.getId(context), profile.getId());

        } else {
            getChat(chat_id);
        }

        setChatAdapter();

//        toolbar_layout.setTitle(chat_user);
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
    public void onBackPressed() {
        super.onBackPressed();
        //overridePendingTransition(R.anim.close_enter_animate, R.anim.close_exit_animate);
    }

    /**
     * Method used to set fonts
     */
    private void setFonts() {

        new MyFont1(context).setAppFont((ViewGroup)findViewById(R.id.container));

    }

    /**
     * Method used to set Chat adapter
     */
    private void setChatAdapter() {
        List<Chat> chatListTemp;
        chatListTemp = Helper.updateTimeSort2(chatList);
        chatList = new ArrayList<>();
        chatList = chatListTemp;
        testAdapter = new ChatAdapter(context, chatList);
        listView.setAdapter(testAdapter);
        setList();
    }

    /**
     * refresh list and go to bottom
     */
    private void setList() {
        testAdapter.refresh(chatList);
        testAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(testAdapter.getCount() - 1);
            }
        });
    }

    /**
     * Method used to Register receiver for GCM push notifications
     */
    private void registerReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.PUSH_NOTIFICATION));
    }

    /**
     * Method handles the push notification
     *
     * @param intent : Get details from intent
     */
    private void handlePushNotification(Intent intent) {


        int chat_id = intent.getIntExtra(Constant.CHAT_ID, 0);
        int from_user_id = intent.getIntExtra(Constant.FROM_USER_ID, 0);
        String message = intent.getStringExtra(Constant.MESSAGE);
        Profile profileInstant = (Profile) intent.getSerializableExtra(Constant.PROFILE);
        String timeStamp = /*new SimpleDateFormat(Constant.DATE_FORMAT3, Locale.ENGLISH)
                .format(*/intent.getStringExtra(Constant.CREATED_DATE);
//        String timeStamp = intent.getStringExtra(Constant.CREATED_DATE);

        if(profile.getId()==profileInstant.getId()) {

            Chat chat = new Chat();
            chat.setChat_id(chat_id);
            chat.setMessage(message);
            chat.setFrom_user_id(from_user_id);
            chat.setProfile(profileInstant);
            chat.setCreated_date(timeStamp);
            chatList.add(chat);

            testAdapter.refresh(chatList);


            getChat(chat_id);
        }
    }

    /**
     * Method used tho send message
     *
     * @param message : Entered message
     */
    private void sendMessage(String message) {
        if (!message.isEmpty()) {
            if (networkConnection.isNetworkAvailable()) {
//                String timeStamp = new SimpleDateFormat(Constant.DATE_FORMAT, Locale.ENGLISH).format(new java.util.Date());
//                MyLog.e(TAG, "timeStamp : " + timeStamp);
                postChat(MyPreferencesManager.getId(context), profile.getId(), chat_id, message, "");
                tvMessage.setText("");

            } else {
                MyToast.tshort(context, getString(R.string.check_network));
            }
        }
    }

    /**
     * Method used to create chat room
     *
     * @param from_user_id : User id of the user who is creating chat room
     * @param to_user_id   : User id of the other user with whom the current user wants to chat
     */
    private void createChatRoom(int from_user_id, int to_user_id) {
        final String failMessage = "Failed to create chatroom";
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().createChatRoom(from_user_id, to_user_id, new Callback<RetroCreateChatroom>() {
                @Override
                public void success(RetroCreateChatroom retroCreateChatroom, Response response) {
                    //MyToast.tshort(context, retroCreateChatroom.getMessage() + "");
                    if(retroCreateChatroom!=null) {
                        if(retroCreateChatroom.getSuccess()) {
                            chat_id = retroCreateChatroom.getDetails();
                            getChat(chat_id);
                        }
                        else {
                            MyToast.tshort(context, retroCreateChatroom.getMessage());
                        }
                    }
                    else {
                        MyToast.tshort(context, failMessage);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    MyToast.tshort(context, failMessage);
                }
            });
        } else {
            MyToast.tshort(context, getString(R.string.check_network));
        }
    }

    /**
     * Method used to fetch all the chats
     *
     * @param chat_id : Chat id
     */
    private void getChat(int chat_id) {
        if (networkConnection.isNetworkAvailable()) {
            final String failMessage = "Failed to create chat";


            progress_layout.setVisibility(View.VISIBLE);

            RestClient.getLokasoApi().getChat(chat_id, MyPreferencesManager.getId(context), new Callback<RetroChat>() {
                @Override
                public void success(RetroChat retroChat, Response response) {
                    progress_layout.setVisibility(View.GONE);
                    if(retroChat!=null) {
                        if(retroChat.getSuccess()) {
                            chatList = retroChat.getDetails();
                            setChatAdapter();
                        }
                        else {
                            MyToast.tshort(context, retroChat.getMessage());
                        }
                    }
                    else {
                        MyToast.tshort(context, failMessage);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    progress_layout.setVisibility(View.GONE);
                    MyToast.tshort(context, failMessage);
                }
            });
        } else {
            MyToast.tshort(context, getString(R.string.check_network));
        }
    }

    /**
     * Method used to post chat using API call
     *
     * @param from_user_id : User id of the user who is creating chat
     * @param to_user_id   : User id of the other user with whom the current user wants to chat
     * @param chat_id      : Chat id
     * @param message      : Message
     * @param timeStamp    : Timestamp of the message sent
     */
    private void postChat(int from_user_id, int to_user_id, final int chat_id, final String message, final String timeStamp) {
        if (networkConnection.isNetworkAvailable()) {
            final String failMessage = "Failed to create chat";
            progress_layout.setVisibility(View.VISIBLE);
            RestClient.getLokasoApi().chat(from_user_id, to_user_id, chat_id, message, timeStamp, new Callback<RetroResponse>() {
                @Override
                public void success(RetroResponse retroResponse, Response response) {
                    progress_layout.setVisibility(View.GONE);
                    if(retroResponse!=null) {
                        if(retroResponse.getSuccess()) {
                            Chat chat = new Chat();
                            chat.setChat_id(chat_id);
                            chat.setMessage(message);
                            chat.setFrom_user_id(MyPreferencesManager.getId(context));
                            Profile profile = new Profile();
                            profile.setName(MyPreferencesManager.getName(context));
                            chat.setProfile(profile);
                            chat.setCreated_date(retroResponse.getMessage());
                            chatList.add(chat);
                            testAdapter.refresh(chatList);
                        }
                        else {
                            MyToast.tshort(context, retroResponse.getMessage());
                        }
                    }
                    else {
                        MyToast.tshort(context, failMessage);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    progress_layout.setVisibility(View.GONE);
                    MyToast.tshort(context, failMessage);
                }
            });
        } else {
            MyToast.tshort(context, getString(R.string.check_network));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile_other, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.idEditProfile:
                    if(profile!=null) {
                        if (checkReport) {
                            //bReport.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_report, 0, 0, 0);
                            userSpam(Constant.UNFAV, profile.getId(), MyPreferencesManager.getId(context));

                        } else {
                            new AlertDialog.Builder(context)
                                    .setTitle(getString(R.string.dialog_title_spam_user))
                                    .setMessage(getString(R.string.dialog_message_spam_user))
                                    .setPositiveButton(getString(R.string.dialog_spam_ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            userSpam(Constant.FAV, profile.getId(), MyPreferencesManager.getId(context));
                                        }
                                    })
                                    .setNegativeButton(getString(R.string.dialog_spam_cancel), null)
                                    .show();

                            //bReport.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_report_orange, 0, 0, 0);
                        }
                        invalidateOptionsMenu();
                    }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem report = menu.findItem(R.id.idEditProfile);

        if(checkReport) {
            report.setIcon(R.drawable.ic_spam_true);
        }
        else {
            report.setIcon(R.drawable.ic_spam_false);
        }
        return true;
    }


    /**
     * method used to spam user
     *
     * @param action       : action = 1 to spam user, action = 0 to un spam user
     * @param spam_user_id : User id of the user on whom action will be taken
     * @param user_id      : User id of the user who will take action on other user
     */
    private void userSpam(int action, int spam_user_id, int user_id) {
        if (networkConnection.isNetworkAvailable()) {
            final String failMessage = "Failed to spam user";
            RestClient.getLokasoApi().userSpam(action, spam_user_id, user_id, new Callback<RetroAction>() {
                @Override
                public void success(RetroAction retroAction, Response response) {

                    if(retroAction!=null) {
                        if (retroAction.getSuccess()) {
                            MyToast.tshort(context, retroAction.getMessage());

                            Action action = retroAction.getAction();
                            if(action!=null) {

                                boolean isValid = action.getAction_status()==1;
                                checkReport = isValid;
                                invalidateOptionsMenu();
                            }
                        }
                        else {
                            MyToast.tshort(context, retroAction.getMessage());
                        }
                    }
                    else {
                        MyToast.tshort(context, failMessage);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    MyToast.tshort(context, failMessage);
                }
            });

        } else {
            MyToast.tshort(context, getString(R.string.check_network));
        }
    }

    public void onUserClick(View view) {

        if(profile!=null) {
            Intent intent = new Intent(context, OthersProfileActivity.class);
            intent.putExtra(Constant.PROFILE_ID, profile.getId());
            intent.putExtra(Constant.PROFILE, profile);
            startActivity(intent);
        }
    }
}
