package com.lokaso.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lokaso.fragment.ImageFragment;
import com.lokaso.util.Constant;

public class ImagePagerAdapter extends FragmentPagerAdapter {

    private int PAGE_COUNT;

    public ImagePagerAdapter(FragmentManager fm, int length) {
        super(fm);
        PAGE_COUNT=length;
    }

    /** This method will be invoked when a page is requested to create */
    @Override
    public Fragment getItem(int pos) {

        ImageFragment myFragment = new ImageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.POSITION, pos);
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
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Page #" + ( position + 1 );
    }

}