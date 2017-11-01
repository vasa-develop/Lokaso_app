package com.lokaso.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lokaso.R;

public class ShareActivity extends AppCompatActivity {

    private String TAG = ShareActivity.class.getSimpleName();
    private Context context = ShareActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}