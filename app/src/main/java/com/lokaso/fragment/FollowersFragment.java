package com.lokaso.fragment;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.lokaso.R;
import com.lokaso.activity.OthersProfileActivity;
import com.lokaso.activity.ProfileActivity;
import com.lokaso.adapter.FollowersAdapter;
import com.lokaso.model.Profile;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroProfile;
import com.lokaso.retromodel.RetroResponse;
import com.lokaso.util.Constant;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Androcid on 06-Aug-16.
 */
public class FollowersFragment extends Fragment {

    private String TAG = FollowersFragment.class.getSimpleName();
    private Context context;

    private RecyclerView recyclerView;
    private FollowersAdapter followersAdapter;
    private List<Profile> followersList = new ArrayList<>();
    private int profile_id;
    private NetworkConnection networkConnection;
    private AlertDialog.Builder builder;
    private TextView tvError;
    private ImageView tvErrorImage;
    private CardView tvErrorLayout;

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

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.hasFixedSize();

        new MyFont1(context).setAppFont((ViewGroup) view.findViewById(R.id.container));

        getFolksData(MyPreferencesManager.getLat(context), MyPreferencesManager.getLng(context));
    }

    /**
     * method used to fetch folks data
     */
    private void getFolksData(double lat, double lng) {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().getUserFollowers(lat, lng, profile_id,
                    new Callback<RetroProfile>() {
                        @Override
                        public void success(RetroProfile retroFolks, Response response) {
                            if (retroFolks.getSuccess()) {
                                followersList = retroFolks.getDetails();
                                setFollowersAdapter();
                                tvErrorLayout.setVisibility(View.GONE);

                            } else {
//                                MyToast.tshort(context, retroFolks.getMessage() + "");
                                tvErrorLayout.setVisibility(View.VISIBLE);
                                tvError.setText(R.string.followers_not_found);
                                tvErrorImage.setImageResource(R.drawable.ic_holder_follow);
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
     * method used to set followers adapter
     */
    private void setFollowersAdapter() {
        followersAdapter = new FollowersAdapter(context, followersList);
        recyclerView.setAdapter(followersAdapter);

        followersAdapter.setOnClickListener(new FollowersAdapter.ClickInterface() {
            @Override
            public void onItemClick(Profile profile) {
                if (profile.getId() == MyPreferencesManager.getId(context)) {
                    if (getActivity().getIntent().getStringExtra(Constant.SOURCE) == null) {
                        Intent intent = new Intent(context, ProfileActivity.class);
                        intent.putExtra(Constant.PROFILE, profile);
                        startActivity(intent);
//                        getActivity().overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);

                    } else {
//                        getActivity().overridePendingTransition(R.anim.close_enter_animate, R.anim.close_exit_animate);
                        getActivity().finish();
                    }

                } else {
                    Intent intent = new Intent(context, OthersProfileActivity.class);
                    intent.putExtra(Constant.PROFILE_ID, profile.getId());
                    intent.putExtra(Constant.PROFILE, profile);
                    startActivity(intent);
//                    getActivity().overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);
                }
            }

            @Override
            public void onFollowClick(int action, int leader, int follower, int position) {
                setFollow(action, leader, follower, position);
            }
        });
    }

    /**
     * method used to follow user
     * @param action
     * @param leader
     * @param follower
     */
    private void setFollow(final int action, int leader, int follower, final int position) {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().follow(action, leader, follower, new Callback<RetroResponse>() {
                @Override
                public void success(RetroResponse retroResponse, Response response) {
//                    MyToast.tshort(context, retroResponse.getMessage() + "");
                    if (action == 1) {
                        followersList.get(position).setUser_followed(true);

                    } else {
                        followersList.get(position).setUser_followed(false);
                    }
                    followersAdapter.refresh(followersList);
                }

                @Override
                public void failure(RetrofitError error) {
                    //MyToast.tshort(context, "" + error);
                }
            });
        } else {
            MyToast.tshort(context, getString(R.string.check_network));
        }
    }
}