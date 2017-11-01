package com.lokaso.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.lokaso.R;
import com.lokaso.activity.LoginActivity;
import com.lokaso.preference.WalkthroughPreference;
import com.lokaso.util.Constant;
import com.lokaso.util.Helper;
import com.lokaso.util.MyFont1;

public class ImageFragment extends Fragment {

    private static final String TAG = ImageFragment.class.getSimpleName();
    private Context context;
    ImageView myimage;
    Button button_login;
    int pos;
    TextView tour_text_body, tour_text_heading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Getting the arguments to the Bundle object */
        Bundle data = getArguments();
        pos = data.getInt(Constant.POSITION, 0);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        if (pos != 2) {
            button_login.setVisibility(View.INVISIBLE);
        }

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WalkthroughPreference.setWalkthroughSeen(getActivity(), true);

                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
                getActivity().finish();

            }
        });

        int path = getImage(pos);
        /*Picasso.with(getActivity())
                .load(path)
                .into(myimage);*/

        final String image = "android.resource://"+ Helper.getPackageName(context)+"/" + getImage(pos);
        //MyLg.e(TAG, "Image " + image);
        Ion.with(getActivity())
                .load(image)
                .intoImageView(myimage);

        tour_text_body.setText(getDesc(pos));
    }

    private int getImage(int pos) {

        switch (pos) {
            case 0:
                return R.mipmap.walkthrough1;

            case 1:
                return R.mipmap.walkthrough2;

            case 2:
                return R.mipmap.walkthrough3;
        }

        return 0;
    }

    private String getDesc(int pos) {

        String text = getString(R.string.walkthrough_screen1);
        switch (pos) {
            case 0:
                return getString(R.string.walkthrough_screen1);

            case 1:
                return getString(R.string.walkthrough_screen2);

            case 2:
                return getString(R.string.walkthrough_screen3);
        }

        return text;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View row = inflater.inflate(R.layout.fragment_image, container, false);
        myimage = (ImageView) row.findViewById(R.id.imageView1);
        button_login = (Button) row.findViewById(R.id.bStart);

        tour_text_body = (TextView) row.findViewById(R.id.tour_text_body);
        tour_text_heading = (TextView) row.findViewById(R.id.tour_text_heading);


        context = getActivity();

        new MyFont1(context).setAppFont((ViewGroup) row.findViewById(R.id.container));

        return row;
    }

}