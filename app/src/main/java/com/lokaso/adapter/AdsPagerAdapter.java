package com.lokaso.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.lokaso.fragment.AdsFragment;
import com.lokaso.fragment.ImageFragment;
import com.lokaso.model.Ads;
import com.lokaso.util.Constant;
import com.lokaso.util.MyLog;

import java.util.List;

public class AdsPagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = AdsPagerAdapter.class.getSimpleName();
    private int PAGE_COUNT;
    private int infinity = Integer.MAX_VALUE;
    private int repeat_count = 1; // Integer.MAX_VALUE

    private List<Ads> adsList;

    public AdsPagerAdapter(FragmentManager fm, List<Ads> adsList) {
        super(fm);
        init(adsList);
    }

    private void init(List<Ads> adsList) {

        this.adsList = adsList;
        PAGE_COUNT=adsList.size();
        infinity = PAGE_COUNT * 1;
    }

    public void refresh(List<Ads> adsList) {
        init(adsList);
        notifyDataSetChanged();
    }

    /** This method will be invoked when a page is requested to create */
    @Override
    public Fragment getItem(int pos) {

        pos = pos%PAGE_COUNT;

        Ads ads = adsList.get(pos);
        MyLog.e(TAG, "positionnnnn : "+adsList.size());

        AdsFragment myFragment = new AdsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.POSITION, pos);
        bundle.putInt(Constant.LENGTH, adsList.size());
        bundle.putSerializable(Constant.AD, ads);
        myFragment.setArguments(bundle);
        return myFragment;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
    /** Returns the number of pages */
    @Override
    public int getCount() {
        return infinity;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Page #" + ( position + 1 );
    }

}