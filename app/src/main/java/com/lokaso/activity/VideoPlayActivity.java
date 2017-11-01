package com.lokaso.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.lokaso.R;
import com.lokaso.dao.DaoController;
import com.lokaso.model.Ads;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroProfession;
import com.lokaso.retromodel.RetroResponse;
import com.lokaso.util.Constant;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyToast;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class VideoPlayActivity extends YouTubeBaseActivity {

    private String TAG = VideoPlayActivity.class.getSimpleName();
    private Context context = VideoPlayActivity.this;

    private ImageButton backButton;
    private YouTubePlayerView youtube_player_view;
    private YouTubePlayer youtube_player;

    private LinearLayout textLayout;
    TextView tour_text_body, tour_text_heading;

    private boolean fullScreen = false;
    private Ads ads;

    public static final int WATCH_SUCCESS = 2;
    public static final int VIEW_SUCCESS = 1;
    public static final int FAIL = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_play);

        try {
            ads = (Ads)getIntent().getSerializableExtra(Constant.AD);
        } catch (Exception e) {
            e.printStackTrace();
        }

        youtube_player_view = (YouTubePlayerView) findViewById(R.id.youtube_player_view);
        backButton = (ImageButton) findViewById(R.id.backButton);

        textLayout = (LinearLayout) findViewById(R.id.textLayout);
        tour_text_body = (TextView) findViewById(R.id.tour_text_body);
        tour_text_heading = (TextView) findViewById(R.id.tour_text_heading);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //if(savedInstanceState==null)
        if(ads!=null) {
            String youtube_id = ads.getYoutube_id();
            String message = ads.getMessage();
            tour_text_body.setText(message);
            tour_text_body.setMovementMethod(new ScrollingMovementMethod());
            playVideo(youtube_id);
        }

        new MyFont1(context).setAppFont((ViewGroup)findViewById(R.id.container));
    }

    private void playVideo(final String youtube_id) {

        String youtube_key = getString(R.string.map_android_key);
        youtube_player_view.initialize(youtube_key, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

                if (!b) {

                    youtube_player = youTubePlayer;

                    /** add listeners to YouTubePlayer instance **/
                    //youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
                    //youTubePlayer.setPlaybackEventListener(playbackEventListener);

                    // loadVideo() will auto play video
                    // Use cueVideo() method, if you don't want to play it automatically
                    youTubePlayer.loadVideo(youtube_id);

                    // Hiding player controls
                    youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);

                    youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                        @Override
                        public void onFullscreen(boolean b) {
                            fullScreen = b;
                        }
                    });

                    String data = "";
                    int status = WATCH_SUCCESS;
                    setAdPlay(status, data);
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                String data = "";
                int status = FAIL;
                setAdPlay(status, data);

                youTubeInitializationResult.getErrorDialog(VideoPlayActivity.this, 100);
            }
        });
    }


    /**
     * method used set ad play view
     */
    private void setAdPlay(int status, String data) {

        RestClient.getLokasoApi().setAdPlay(
            MyPreferencesManager.getId(context),
            ads.getId(),
            ads.getType(),
            status,
            data,
            new Callback<RetroResponse>() {
                @Override
                public void success(RetroResponse retroResponse, Response response) {

                }

                @Override
                public void failure(RetrofitError error) {
                }
            });
    }

    @Override
    public void onBackPressed() {
        if (fullScreen){
            youtube_player.setFullscreen(false);
        } else{
            super.onBackPressed();
        }
    }
}