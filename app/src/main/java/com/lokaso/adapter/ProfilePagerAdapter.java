package com.lokaso.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.lokaso.fragment.FollowersFragment;
import com.lokaso.fragment.QueryFragment;
import com.lokaso.fragment.SuggestionFragment;
import com.lokaso.util.Constant;

public class ProfilePagerAdapter extends FragmentStatePagerAdapter {

    private String TAG = ProfilePagerAdapter.class.getSimpleName();
    private int mNumOfTabs, profile_id;

    public ProfilePagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    public ProfilePagerAdapter(FragmentManager fm, int NumOfTabs, int profile_id) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.profile_id = profile_id;
    }

    public void refresh(int profile_id){
        this.profile_id = profile_id;
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Bundle bundle1 = new Bundle();
                bundle1.putInt(Constant.PROFILE, profile_id);
                Fragment fragment1 = new SuggestionFragment();
                fragment1.setArguments(bundle1);
                return fragment1;

            case 1:
                Bundle bundle2 = new Bundle();
                bundle2.putInt(Constant.PROFILE, profile_id);
                Fragment fragment2 = new QueryFragment();
                fragment2.setArguments(bundle2);
                return fragment2;

            case 2:
                Bundle bundle3 = new Bundle();
                bundle3.putInt(Constant.PROFILE, profile_id);
                Fragment fragment3 = new FollowersFragment();
                fragment3.setArguments(bundle3);
                return fragment3;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
