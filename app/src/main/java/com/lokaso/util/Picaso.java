package com.lokaso.util;

import android.content.Context;
import android.widget.ImageView;

import com.lokaso.R;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class Picaso {

    private static final String TAG = Picaso.class.getSimpleName();

    public static void loadUser(final Context context, final String url, final ImageView imageView) {
        load(context, url, imageView, R.drawable.user_placeholder);
    }

    public static void loadSuggestion(final Context context, final String url, final ImageView imageView) {
        MyLog.e(TAG, imageView + " sugges url : "+url);
        load(context, url, imageView, R.drawable.ic_placeholder);
    }


    private static void load(final Context context, final String url, final ImageView imageView, final int placeholder) {

        MyLog.e(TAG, imageView + " common url : "+url);




        Picasso.with(context).load(url)
                .placeholder(placeholder)
                .error(placeholder)
                .into(imageView);

//        Ion.with(imageView)
//                .placeholder(placeholder)
//                .error(placeholder)
//                .load(url);

        if(true) return;

        if(url!=null && url.length()>0) {

            Picasso.with(context)
                    .load(url)
                    .placeholder(placeholder)
                    .error(placeholder)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(imageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            MyLog.e("Picasso", "Image Cache " + url);
                        }

                        @Override
                        public void onError() {
                            MyLog.e("Picasso", "onError " + url);
                            Picasso.with(context)
                                    .load(url)
                                    .placeholder(placeholder)
                                    .error(placeholder)
                                    .into(imageView, new com.squareup.picasso.Callback() {
                                        @Override
                                        public void onSuccess() {
                                            MyLog.e("Picasso", "Image Network " + url);
                                        }

                                        @Override
                                        public void onError() {
                                            MyLog.e("Picasso", "Could not fetch image");
                                        }
                                    });
                        }
                    });
        }
        else {
            imageView.setImageResource(placeholder);
        }

    }

    private static void load(final Context context, final String url, final ImageView imageView, final int placeholder, final int w, final int h) {

        Picasso.with(context)
                .load(url)
                .resize(w, h)
                .placeholder(placeholder)
                .error(placeholder)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(imageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        MyLog.e("Picasso", "Image Cache " + url);
                    }

                    @Override
                    public void onError() {
                        Picasso.with(context)
                                .load(url)
                                //.resize(w, h)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(placeholder)
                                .error(placeholder)
                                .into(imageView, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        MyLog.e("Picasso", "Image Network " + url);
                                    }

                                    @Override
                                    public void onError() {
                                        MyLog.e("Picasso", "Could not fetch image");

                                        Picasso.with(context)
                                                .load(url)
                                                .resize(w, h)
                                                .placeholder(placeholder)
                                                .error(placeholder)
                                                .into(imageView, new com.squareup.picasso.Callback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        MyLog.e("Picasso", "Image Network " + url);
                                                    }

                                                    @Override
                                                    public void onError() {
                                                        MyLog.e("Picasso", "Could not fetch image");

                                                        Picasso.with(context)
                                                                .load(url)
                                                                //.resize(w, h)
                                                                .placeholder(placeholder)
                                                                .error(placeholder)
                                                                .into(imageView, new com.squareup.picasso.Callback() {
                                                                    @Override
                                                                    public void onSuccess() {
                                                                        MyLog.e("Picasso", "Image Network " + url);
                                                                    }

                                                                    @Override
                                                                    public void onError() {
                                                                        MyLog.e("Picasso", "Could not fetch image");


                                                                    }
                                                                });

                                                    }
                                                });


                                    }
                                });
                    }
                });
    }


}