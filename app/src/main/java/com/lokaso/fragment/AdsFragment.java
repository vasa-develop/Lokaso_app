package com.lokaso.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.lokaso.R;
import com.lokaso.activity.LoginActivity;
import com.lokaso.activity.VideoPlayActivity;
import com.lokaso.model.Ads;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.preference.WalkthroughPreference;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroResponse;
import com.lokaso.util.Constant;
import com.lokaso.util.Helper;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyLog;
import com.lokaso.util.MyToast;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AdsFragment extends Fragment {

    private static final String TAG = AdsFragment.class.getSimpleName();
    private Context context;
    private int pos, length;

    private ImageView playImageView;
    private ImageView myimage;
    private LinearLayout textLayout;
    TextView tour_text_body, tour_text_heading;
    private TextView countTextView;
    private RelativeLayout containerLayout;

    private Ads ads;

    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_VIDEO = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            /** Getting the arguments to the Bundle object */
            Bundle data = getArguments();
            pos = data.getInt(Constant.POSITION, 0);
            length = data.getInt(Constant.LENGTH, 0);
            ads = (Ads) data.getSerializable(Constant.AD);
            MyLog.e(TAG, "0length : "+length + " , "+pos);
        } finally {

        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            /** Getting the arguments to the Bundle object */
            Bundle data = getArguments();
            pos = data.getInt(Constant.POSITION, 0);
            length = data.getInt(Constant.LENGTH, 0);
            ads = (Ads) data.getSerializable(Constant.AD);
            MyLog.e(TAG, "1length : "+length + " , "+pos);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String path = ads.getUrl();
        Picasso.with(getActivity())
                .load(path)
                .into(myimage);

        if(length>1) {
            String message = (pos%length+1)+"/"+length;
            countTextView.setVisibility(View.VISIBLE);
            countTextView.setText(message);
        }
        else {
            countTextView.setVisibility(View.GONE);
        }

        // Show of hide video play image
        playImageView.setVisibility(ads.getType()==TYPE_VIDEO ? View.VISIBLE : View.GONE);

        String message = ads.getMessage();
        if(message!=null && message.length()>0) {
            textLayout.setVisibility(View.VISIBLE);
            tour_text_body.setText(message);
        }
        else {
            textLayout.setVisibility(View.GONE);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View row = inflater.inflate(R.layout.fragment_ad, container, false);

        containerLayout = (RelativeLayout) row.findViewById(R.id.container);
        myimage = (ImageView) row.findViewById(R.id.imageView);
        playImageView = (ImageView) row.findViewById(R.id.playImageView);

        countTextView = (TextView) row.findViewById(R.id.countTextView);
        textLayout = (LinearLayout) row.findViewById(R.id.textLayout);
        tour_text_body = (TextView) row.findViewById(R.id.tour_text_body);
        tour_text_heading = (TextView) row.findViewById(R.id.tour_text_heading);

        context = getActivity();

        containerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ads.getType()==TYPE_VIDEO) {
                    Intent intent = new Intent(context, VideoPlayActivity.class);
                    intent.putExtra(Constant.AD, ads);
                    startActivity(intent);
                }
                else {
//                    Intent intent = new Intent(context, VideoPlayActivity.class);
//                    intent.putExtra(Constant.YOUTUBE_ID, ads.getYoutube_id());
//                    startActivity(intent);
                    //MyToast.tdebug(context, "TYPE_IMAGE");
                }
            }
        });

        new MyFont1(context).setAppFont((ViewGroup) row.findViewById(R.id.container));

        return row;
    }
}