package com.lokaso.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.lokaso.R;
import com.lokaso.util.Constant;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;

public class TermsConditionsActivity extends AppCompatActivity {

    private String TAG = TermsConditionsActivity.class.getSimpleName();
    private Context context = TermsConditionsActivity.this;

    private WebView termsWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions);

        getWindow().setBackgroundDrawableResource(R.mipmap.background_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        if (toolbar != null) {
            toolbar.setTitle(R.string.terms_conditions);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        termsWebView = (WebView) findViewById(R.id.termsWebView);


        if(!NetworkConnection.isNetworkAvailable(context)) {
            MyToast.showInternet(context);
            //showNoResult(true);
            return;
        }

        String url = Constant.TERMS_URL;

        load(url);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void load(String url) {

        termsWebView.loadUrl(url);
    }


}
