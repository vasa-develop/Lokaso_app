package com.lokaso.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lokaso.R;
import com.lokaso.activity.ChatActivity;
import com.lokaso.activity.CreateSuggestionActivity;
import com.lokaso.activity.OthersProfileActivity;
import com.lokaso.activity.ProfileActivity;
import com.lokaso.activity.SuggestionCommentActivity;
import com.lokaso.activity.ViewSuggestionActivity;
import com.lokaso.adapter.SuggestionAdapter;
import com.lokaso.model.Action;
import com.lokaso.model.Profile;
import com.lokaso.model.Suggestion;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroAction;
import com.lokaso.retromodel.RetroResponse;
import com.lokaso.retromodel.RetroSuggestion;
import com.lokaso.util.Constant;
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

/**
 * Created by Androcid on 06-Aug-16.
 */
public class SuggestionFragment extends Fragment {

    private String TAG = SuggestionFragment.class.getSimpleName();
    private Context context;

    private RecyclerView recyclerView;
    private SuggestionAdapter suggestionAdapter;
    private List<Suggestion> suggestionList = new ArrayList<>();
    private int profile_id;
    private NetworkConnection networkConnection;
    private AlertDialog.Builder builder;
    private TextView tvError;
    private ImageView tvErrorImage;
    private CardView tvErrorLayout;

    private RelativeLayout progress_layout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        profile_id = getArguments().getInt(Constant.PROFILE);
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();
        builder = new AlertDialog.Builder(context);
        networkConnection = new NetworkConnection(context);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        tvError = (TextView) view.findViewById(R.id.tvError);
        tvErrorImage = (ImageView) view.findViewById(R.id.tvErrorImage);
        tvErrorLayout = (CardView) view.findViewById(R.id.tvErrorLayout);

        progress_layout = (RelativeLayout) view.findViewById(R.id.progress_layout);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.hasFixedSize();

        new MyFont1(context).setAppFont((ViewGroup) view.findViewById(R.id.container));

        getDiscovery();
    }

    /**
     * method used to fetch discovery
     */
    private void getDiscovery() {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().getMyDiscovery(
                    profile_id, // This is user id of the user whose profile is being viewed.
                    MyPreferencesManager.getId(context), // This is user id of the logged in user
                    new Callback<RetroSuggestion>() {
                        @Override
                        public void success(RetroSuggestion retroSuggestion, Response response) {
                            if (retroSuggestion.getSuccess()) {
                                suggestionList = retroSuggestion.getDetails();
                                setDiscoveryAdapter();
                                tvErrorLayout.setVisibility(View.GONE);

                            } else {
//                                MyToast.tshort(context, retroSuggestion.getMessage() + "");
                                tvErrorLayout.setVisibility(View.VISIBLE);
                                tvError.setText(R.string.suggestions_not_found);
                                tvErrorImage.setImageResource(R.drawable.ic_holder_suggestion);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
//                            MyToast.tshort(context, "" + error);
                        }
                    });
        } else {
            MyToast.tshort(context, getString(R.string.check_network));
        }
    }

    /**
     * method used to set discovery adapter
     */
    private void setDiscoveryAdapter() {
//        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        suggestionAdapter = new SuggestionAdapter(getActivity(), suggestionList/*, "Profile"*/);
        recyclerView.setAdapter(suggestionAdapter);
    }

    /**
     * On Message click
     * @param profile Profile of the clicked user
     */
    private void onMessageClick(Profile profile){
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Constant.PROFILE, profile);
        intent.putExtra(Constant.CHAT_ID, 0);
        startActivity(intent);
        //getActivity().overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);
    }


}