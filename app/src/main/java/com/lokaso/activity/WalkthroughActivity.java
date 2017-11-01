package com.lokaso.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.lokaso.R;
import com.lokaso.adapter.ImagePagerAdapter;
import com.viewpagerindicator.CirclePageIndicator;

public class WalkthroughActivity extends AppCompatActivity {

    private String TAG = WalkthroughActivity.class.getSimpleName();
    private Context context = WalkthroughActivity.this;

    // private Button bStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_walkthrough);


        ImagePagerAdapter pageAdapter = new ImagePagerAdapter(getSupportFragmentManager(), 3);
        ViewPager pager = (ViewPager) findViewById(R.id.imagePager);
        pager.setAdapter(pageAdapter);

        CirclePageIndicator mIndicator = (CirclePageIndicator) findViewById(R.id.titles);
//        mIndicator.setRadius(8);
//        mIndicator.setStrokeWidth(2);
//        mIndicator.setFillColor(ContextCompat.getColor(context, R.color.colorPrimary));
//        mIndicator.setPageColor(ContextCompat.getColor(context, R.color.transparent));
//        mIndicator.setStrokeColor(ContextCompat.getColor(context, R.color.green));
//        mIndicator.setMinimumHeight(9);
//        mIndicator.setCentered(true);
//        mIndicator.setPadding(2, 2, 2, 2);

        mIndicator.setViewPager(pager);
    }
}
