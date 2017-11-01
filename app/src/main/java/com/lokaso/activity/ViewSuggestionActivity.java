package com.lokaso.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.lokaso.R;
import com.lokaso.adapter.AnswerAdapter;
import com.lokaso.adapter.SuggestionAdapter;
import com.lokaso.custom.ImageViewTint;
import com.lokaso.model.Answer;
import com.lokaso.model.Interest;
import com.lokaso.model.Profile;
import com.lokaso.model.Suggestion;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.preference.TourPreference;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroAction;
import com.lokaso.retromodel.RetroAnswer;
import com.lokaso.retromodel.RetroResponse;
import com.lokaso.util.Constant;
import com.lokaso.util.MarshmallowPermission;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyLog;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;
import com.lokaso.util.Picaso;
import com.lokaso.util.TourClass;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ViewSuggestionActivity extends AppCompatActivity {

    private String TAG = ViewSuggestionActivity.class.getSimpleName();
    private Context context = ViewSuggestionActivity.this;

    public static Suggestion suggestion;
    private TextView tvName, tvLocation, tvInterest, tvDistance, tvProfileName, tvProfession,
            tvFav, tvComment;
    private TextView tvNameHint, tvInterestHint, tvLocationHint;
    // private LinearLayout layoutLocation;
    private TextView tvCaption, tvUserCredit;

    private ImageView ivProfile;
    private ImageView ivPic;
    private RelativeLayout imageLayout;
    private View viewNoImage, viewWithImage;
    private List<Answer> answerList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AnswerAdapter answerAdapter;
    public static List<Interest> interestList = new ArrayList<>();

    private ImageViewTint bFav, bSpam;

    private ImageButton /*bFav, */bComment, bWishlist;

    private LinearLayout layoutUser;

    private LinearLayout likeLayout, commentLayout;
    private TextView tvLikeCount, tvCommentCount;
    private LinearLayout favLayout, wishlistLayout, locationLayout, shareLayout, layoutSpam;

    private ImageViewTint userMore;

    protected LinearLayout layout2, layout3;

    private RelativeLayout progress_layout;

    private NetworkConnection networkConnection;
    private AlertDialog.Builder builder;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    private MarshmallowPermission marshmallowPermission;

    TourClass tourWishlist, tourShare, tourLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_discovery);

        builder = new AlertDialog.Builder(context);
        networkConnection = new NetworkConnection(context);
        marshmallowPermission = new MarshmallowPermission(ViewSuggestionActivity.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.local_tip);
        setSupportActionBar(toolbar);


        ivProfile = (ImageView) findViewById(R.id.ivProfile);
        tvName = (TextView) findViewById(R.id.tvName);
        tvCaption = (TextView) findViewById(R.id.tvCaption);
        tvUserCredit = (TextView) findViewById(R.id.tvUserCredit);
        //layoutLocation = (LinearLayout) findViewById(R.id.layoutLocation);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        tvInterest = (TextView) findViewById(R.id.tvInterest);
        tvDistance = (TextView) findViewById(R.id.tvDistance);
        tvNameHint = (TextView) findViewById(R.id.tvNameHint);
        tvInterestHint = (TextView) findViewById(R.id.tvInterestHint);
        tvLocationHint = (TextView) findViewById(R.id.tvLocationHint);
        tvProfileName = (TextView) findViewById(R.id.tvProfileName);
        tvProfession = (TextView) findViewById(R.id.tvProfession);
        //bFav = (ImageButton) findViewById(R.id.bFav);
        bFav = (ImageViewTint) findViewById(R.id.bFav);
        bComment = (ImageButton) findViewById(R.id.bComment);
        bWishlist = (ImageButton) findViewById(R.id.bWishlist);
//        bSpam = (ImageButton) findViewById(R.id.bSpam);
        bSpam = (ImageViewTint) findViewById(R.id.bSpam);

        progress_layout = (RelativeLayout) findViewById(R.id.progress_layout);

        layoutUser = (LinearLayout) findViewById(R.id.layoutUser);

        favLayout = (LinearLayout) findViewById(R.id.favLayout);
        wishlistLayout = (LinearLayout) findViewById(R.id.wishlistLayout);
        layoutSpam = (LinearLayout) findViewById(R.id.layoutSpam);
        shareLayout = (LinearLayout) findViewById(R.id.shareLayout);
        locationLayout = (LinearLayout) findViewById(R.id.locationLayout);

        likeLayout = (LinearLayout) findViewById(R.id.likeLayout);
        commentLayout = (LinearLayout) findViewById(R.id.commentLayout);
        tvLikeCount = (TextView) findViewById(R.id.tvLikeCount);
        tvCommentCount = (TextView) findViewById(R.id.tvCommentCount);

        layout2 = (LinearLayout) findViewById(R.id.layout2);
        layout3 = (LinearLayout) findViewById(R.id.layout3);

        userMore = (ImageViewTint) findViewById(R.id.userMore);



        viewNoImage = (View) findViewById(R.id.viewNoImage);
        viewWithImage = (View) findViewById(R.id.viewWithImage);
        imageLayout = (RelativeLayout) findViewById(R.id.imageLayout);
        ivPic = (ImageView) findViewById(R.id.ivPic);
        tvFav = (TextView) findViewById(R.id.tvFav);
        tvComment = (TextView) findViewById(R.id.tvComment);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.hasFixedSize();

        try {
            suggestion = (Suggestion) getIntent().getSerializableExtra(Constant.SUGGESTION);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        getAnswers(suggestion.getId());

        if (toolbar != null) {
            toolbar.setTitle(R.string.local_tip);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            /*
            toolbar_layout.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            if (suggestion.getProfile().getId() == MyPreferencesManager.getId(context)) {
                toolbar_layout.inflateMenu(R.menu.menu_profile);
                toolbar_layout.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.idEditProfile:

                                Intent intent = new Intent(context, CreateSuggestionActivity.class);
                                intent.putExtra(Constant.SUGGESTION, suggestion);
                                startActivity(intent);

                                return true;
                        }
                        return false;
                    }
                });
            }*/
        }


        setTypeface();


        setListener();


        // Tour
        tourWishlist = new TourClass(ViewSuggestionActivity.this).click().topRight().isTopMost()
                .message(getString(R.string.tour_suggestion_detail_wishlist_desc))
                .anchor(wishlistLayout)
                .setOnClickListener(new TourClass.OnClickListener() {
                    @Override
                    public void onClick(TourClass tour) {
                        tour.dismiss();

                        TourPreference.setSuggestionWishListSeen(context, true);

                        tourShare.show();
                    }
                });

        tourShare = new TourClass(ViewSuggestionActivity.this).click().bottomLeft().isTopMost()
                .message(getString(R.string.tour_suggestion_detail_share_desc))
                .anchor(shareLayout)
                .setOnClickListener(new TourClass.OnClickListener() {
                    @Override
                    public void onClick(TourClass tour) {
                        tour.dismiss();
                        TourPreference.setSuggestionShareSeen(context, true);
                        tourLocation.show();
                    }
                });

        tourLocation = new TourClass(ViewSuggestionActivity.this).click().bottomLeft().isTopMost()
                .message(getString(R.string.tour_suggestion_detail_location_desc))
                .anchor(locationLayout)
                .setOnClickListener(new TourClass.OnClickListener() {
                    @Override
                    public void onClick(TourClass tour) {
                        tour.dismiss();
                        TourPreference.setSuggestionLocationSeen(context, true);
                        setListener();
                    }
                });

        if(!TourPreference.isSuggestionWishListSeen(context)) {
            tourWishlist.show();
        }
        else if(!TourPreference.isSuggestionShareSeen(context)) {
            tourShare.show();
        }
        else if(!TourPreference.isSuggestionLocationSeen(context)) {
            tourLocation.show();
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    private void setListener() {

        //////
        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                layout2.setVisibility(View.GONE);
                layout3.setVisibility(View.GONE);
                //holder.bEdit.setVisibility(View.GONE);
                //userMore.setVisibility(View.GONE);

                progress_layout.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (marshmallowPermission.checkPermissionForExternalStorage()) {

                            String shareText = ""+getString(R.string.share_text);//suggestion.getSuggestion();

                            Bitmap b = getBitmapFromView((ViewGroup) findViewById(R.id.cardViewLayout));
                            String filepath = saveBitmap(b);
                            filepath = "file://" + filepath;
                            Uri uri = Uri.parse(filepath);
                            MyLog.e(TAG, "uri : " + uri);

                            suggestion.setIs_share_click(false);
                            //refresh(suggestionList);
                            layout2.setVisibility(View.VISIBLE);
                            layout3.setVisibility(View.VISIBLE);

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_STREAM, uri);
                            intent.putExtra(Intent.EXTRA_TEXT, shareText);
                            intent.setType("image/jpeg");
                            context.startActivity(intent);

                            progress_layout.setVisibility(View.GONE);

                            int suggestion_id = suggestion.getId();
                            int user_id = MyPreferencesManager.getId(context);

                            suggestionShare(suggestion_id, user_id, 0);

                        } else {
                            marshmallowPermission.requestPermissionForExternalStorage();
                        }
                    }
                }, SuggestionAdapter.SHARE_DELAY_TIME);
            }
        });

        likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, SuggestionLikeListActivity.class);
                intent.putExtra(Constant.SUGGESTION, suggestion);
                context.startActivity(intent);

            }
        });

        commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, SuggestionCommentActivity.class);
                intent.putExtra(Constant.SUGGESTION, suggestion);
                context.startActivity(intent);
            }
        });

        layoutUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUserClick();
            }
        });


        ///////

        favLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!suggestion.isUser_fav()) {
                    //bFav.setImageResource(R.drawable.ic_upvote_orange);
                    bFav.setSelected(true);
                    suggestion.setUser_fav(true);
                    int count = suggestion.getFav_count() + 1;
                    suggestion.setFav_count(count);
                    discoveryFav(Constant.FAV, suggestion.getId(), MyPreferencesManager.getId(context));

                } else {
                    //bFav.setImageResource(R.drawable.ic_upvote);
                    bFav.setSelected(false);
                    suggestion.setUser_fav(false);
                    int count = suggestion.getFav_count() - 1;
                    suggestion.setFav_count(count);
                    discoveryFav(Constant.UNFAV, suggestion.getId(), MyPreferencesManager.getId(context));
                }

                setData();
            }
        });


        wishlistLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!suggestion.isUser_discovery()) {
                    saveUserSuggestion(Constant.FAV, suggestion.getId(), MyPreferencesManager.getId(context));
                    wishlistLayout.setSelected(false);
                } else {
                    saveUserSuggestion(Constant.UNFAV, suggestion.getId(), MyPreferencesManager.getId(context));
                    wishlistLayout.setSelected(true);
                }

                setData();
            }
        });


        bSpam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!suggestion.isSuggestion_spam()) {

                    new AlertDialog.Builder(context)
                            .setTitle(getString(R.string.dialog_title_spam_tip))
                            .setMessage(getString(R.string.dialog_message_spam_tip))
                            .setPositiveButton(getString(R.string.dialog_spam_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    bSpam.setSelected(false);
                                    onSpamClick(Constant.FAV, suggestion.getId());
                                }
                            })
                            .setNegativeButton(getString(R.string.dialog_spam_cancel), null)
                            .show();


                } else {
                    bSpam.setImageResource(R.drawable.ic_spam_orange);
                    bSpam.setSelected(true);
                    onSpamClick(Constant.UNFAV, suggestion.getId());
                }

                setData();
            }
        });

        bComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SuggestionCommentActivity.class);
                intent.putExtra(Constant.SUGGESTION, suggestion);
                startActivity(intent);
                //overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);
            }
        });

        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse(Constant.MAP + suggestion.getLat() + "," + suggestion.getLng()
                        + " (" + suggestion.getLocation() + ")");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage(Constant.MAP_PACKAGE);
                startActivity(mapIntent);
                //overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);
            }
        });
        locationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse(Constant.MAP + suggestion.getLat() + "," + suggestion.getLng()
                        + " (" + suggestion.getLocation() + ")");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage(Constant.MAP_PACKAGE);
                startActivity(mapIntent);
                //overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);

        MenuItem menuItem = menu.findItem(R.id.idEditProfile);

        try {

            if (suggestion!=null && suggestion.getProfile().getId() == MyPreferencesManager.getId(context)) {
                menuItem.setIcon(R.drawable.ic_edit);
            }
            else {
                menuItem.setIcon(R.drawable.ic_spam_false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.idEditProfile:
                if (suggestion!=null && suggestion.getProfile()!=null) {

                    if (suggestion.getProfile().getId() == MyPreferencesManager.getId(context)) {

                        Intent intent = new Intent(context, CreateSuggestionActivity.class);
                        intent.putExtra(Constant.SUGGESTION, suggestion);
                        startActivity(intent);
                    }
                    else {
                        if (suggestion.isSuggestion_spam()) {
                            onSpamClick(Constant.UNFAV, suggestion.getId());
                        } else {
                            new AlertDialog.Builder(context)
                                    .setTitle(getString(R.string.dialog_title_spam_tip))
                                    .setMessage(getString(R.string.dialog_message_spam_tip))
                                    .setPositiveButton(getString(R.string.dialog_spam_ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            onSpamClick(Constant.FAV, suggestion.getId());
                                        }
                                    })
                                    .setNegativeButton(getString(R.string.dialog_spam_cancel), null)
                                    .show();
                        }
                        invalidateOptionsMenu();
                    }

                } else {
                    MyToast.tshort(context, getString(R.string.failed_to_fetch_data));
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem report = menu.findItem(R.id.idEditProfile);

        if (suggestion!=null && suggestion.getProfile().getId() == MyPreferencesManager.getId(context)) {
            report.setIcon(R.drawable.ic_edit);
        }
        else {
            if (suggestion!=null && suggestion.isSuggestion_spam()) {
                report.setIcon(R.drawable.ic_spam_true);
            }
            else {
                report.setIcon(R.drawable.ic_spam_false);
            }
        }


        return true;
    }


    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view

        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }


    private String saveBitmap(Bitmap b) {
        MyLog.e(TAG, "saveBitmap : " + b);
        String destinationPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+Constant.APP_NAME;
        String fileName = new SimpleDateFormat("yyMMddHHmmss").format(Calendar.getInstance().getTime()) + ".jpeg";
        File folder = new File(destinationPath);
        File myPath = new File(destinationPath, fileName);

        /* if file not exist then create one */
        try {
            if (!folder.exists()) {
                folder.mkdirs();
                System.out.println("Making dirs");
            }

            try {
                myPath.createNewFile();
                System.out.println("Destination file doesn't exist. Creating one!");
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOutputStream fos = null;
//        try {
            fos = new FileOutputStream(myPath);
            b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            MediaStore.Images.Media.insertImage(context.getContentResolver(), b, "Screen", "screen");
            String filepath = destinationPath + "/" + fileName;
            MyLog.e(TAG, "filepath : " + filepath);
            return filepath;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }



    /**
     * method used to spam a Suggestion using API call
     *
     * @param action        action = 1 to spam Suggestion and action = 0 for vice-versa
     * @param suggestion_id Suggestion id
     */
    private void onSpamClick(final int action, int suggestion_id) {
//        if (action == 1) {
//            suggestion.setSuggestion_spam(true);
//
//        } else {
//            suggestion.setSuggestion_spam(false);
//        }

        if (networkConnection.isNetworkAvailable()) {
            final String failMessage = "Failed to spam";
            RestClient.getLokasoApi().suggestionSpam(action, suggestion_id, MyPreferencesManager.getId(context),
                    new Callback<RetroAction>() {
                        @Override
                        public void success(RetroAction retroAction, Response response) {


                            if(retroAction!=null) {
                                if (retroAction.getSuccess()) {
                                    //MyToast.tshort(context, retroAction.getMessage());

                                    com.lokaso.model.Action action = retroAction.getAction();
                                    if(action!=null) {

                                        boolean isValid = action.getAction_status()==1;
                                        //checkReport = isValid;
                                        //invalidateOptionsMenu();

                                        if (isValid) {
                                            suggestion.setSuggestion_spam(true);
                                        } else {
                                            suggestion.setSuggestion_spam(false);
                                        }
                                        invalidateOptionsMenu();
                                    }
                                }
                                else {
                                    MyToast.tshort(context, retroAction.getMessage());
                                }
                            }
                            else {
                                MyToast.tshort(context, failMessage);
                            }
                            setData();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            MyToast.tshort(context, "" + error);
                        }
                    });

        } else {
            MyToast.tshort(context, getString(R.string.check_network));
        }
    }


    /**
     * method used to save user Suggestion using API call
     *
     * @param action       action = 1 to save user Suggestion and action = 0 for vice-versa
     * @param discovery_id Suggestion id
     * @param user_id      User id
     */
    private void saveUserSuggestion(final int action, int discovery_id, int user_id) {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().userDiscovery(action, discovery_id, user_id, new Callback<RetroResponse>() {
                @Override
                public void success(RetroResponse retroResponse, Response response) {
//                    MyToast.tshort(context, retroResponse.getMessage() + "");
                    if (action == 1) {
                        suggestion.setUser_discovery(true);

                    } else {
                        suggestion.setUser_discovery(false);
                    }

                    setData();

                }

                @Override
                public void failure(RetrofitError error) {
                    MyToast.tshort(context, "" + error);
                }
            });

        } else {
            MyToast.tshort(context, getString(R.string.check_network));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        setData();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //overridePendingTransition(R.anim.close_enter_animate, R.anim.close_exit_animate);
    }

    /**
     * method used to set data
     */
    private void setData() {
        if (suggestion.getImage().endsWith(".jpg")) {

            String imageUrl = suggestion.getImage();
            MyLog.e(TAG, "userImageUrl : " + imageUrl);
            Picaso.loadSuggestion(context, imageUrl, ivPic);

            viewNoImage.setVisibility(View.GONE);
            viewWithImage.setVisibility(View.VISIBLE);
            imageLayout.setVisibility(View.VISIBLE);

        } else {
            viewNoImage.setVisibility(View.VISIBLE);
            viewWithImage.setVisibility(View.GONE);
            imageLayout.setVisibility(View.GONE);
            //ivPic.setVisibility(View.GONE);
        }

        tvCaption.setText("" + suggestion.getCaption());


        tvName.setText(suggestion.getSuggestion());

        String distance = suggestion.getDistance_display();
        tvDistance.setText(distance);

        String locationStr = "- "+suggestion.getLocation()+"";
        tvLocation.setText(locationStr);
        tvFav.setText(suggestion.getFav_count() + "");
        MyLog.e(TAG, "Comment count : " + suggestion.getComment_count());
        tvComment.setText(suggestion.getComment_count() + "");


        int likeCount = suggestion.getFav_count();
        int commentCount = suggestion.getComment_count();
        String likeTxt = " likes";
        if(likeCount==1) {
            likeTxt = " like";
        }
        String commentTxt = " comments";
        if(commentCount==1) {
            commentTxt = " comment";
        }
        tvLikeCount.setText(likeCount + likeTxt);
        tvCommentCount.setText(commentCount + commentTxt);

        try {

            Profile profile = suggestion.getProfile();

            MyLog.e(TAG, "suggestion.getProfile().getImage() : " + profile.getImage());
            Picaso.loadUser(context, profile.getImage(), ivProfile);

            String credit = "(" + profile.getCredits_display() + " Credits)";
            tvProfileName.setText(profile.getName());

            String profession = profile.getProfession();
            if(profession==null) profession="";
            if(profession.equals("null")) profession = "";
            if(profession.length()>0) profession = profession.concat(" ");
            tvProfession.setText(profession + ""+credit);
            tvUserCredit.setText(credit);
        } catch (Exception e) {
            e.printStackTrace();
        }

        favLayout.setSelected(suggestion.isUser_fav());
        wishlistLayout.setSelected(suggestion.isUser_discovery());

        //bFav.setSelected(suggestion.isUser_fav());

        /*
        if (suggestion.isUser_fav()) {
            bFav.setImageResource(R.drawable.ic_upvote_orange);
            bFav.setColorFilter(ContextCompat.getColor(context,R.color.colorPrimary));
        } else {
            bFav.setImageResource(R.drawable.ic_upvote);
            bFav.setColorFilter(ContextCompat.getColor(context,R.color.gray_bottom));
        }
        */

        /*
        bWishlist.setSelected(suggestion.isUser_discovery());

        if (suggestion.isUser_discovery()) {
            bWishlist.setSelected(false);
            //bWishlist.setImageResource(R.drawable.ic_wishlist_red);
            bWishlist.setColorFilter(ContextCompat.getColor(context,R.color.colorPrimary));
        } else {
            //bWishlist.setImageResource(R.drawable.ic_wishlist);
            bWishlist.setColorFilter(ContextCompat.getColor(context,R.color.gray_bottom));
            bWishlist.setSelected(true);
        }
*/

        MyLog.e(TAG, "spam : " + suggestion.isSuggestion_spam());

        bSpam.setSelected(suggestion.isSuggestion_spam());
        invalidateOptionsMenu();

        if (suggestion.isSuggestion_spam()) {
            bSpam.setImageResource(R.drawable.ic_spam_orange);
            bSpam.setColorFilter(ContextCompat.getColor(context,R.color.colorPrimary));
        } else {
            bSpam.setImageResource(R.drawable.ic_spam);
            bSpam.setColorFilter(ContextCompat.getColor(context,R.color.gray_bottom));
        }

        Interest interest = suggestion.getInterest();
        if (interest!=null) {
            String interestStr = interest.getName();
            tvInterest.setText(interestStr.toUpperCase(Locale.getDefault()));
        }
    }

    /**
     * method used to favourite suggestion
     *
     * @param action       action = 1 to favourite suggestion and action = 0 for vice-versa
     * @param discovery_id Suggestion id
     * @param user_id      user id
     */
    private void discoveryFav(final int action, int discovery_id, int user_id) {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().discoveryFav(action, discovery_id, user_id, new Callback<RetroResponse>() {
                @Override
                public void success(RetroResponse retroResponse, Response response) {
//                    MyToast.tshort(context, retroResponse.getMessage() + "");
                    /*int old_fav_count = suggestion.getFav_count();
                    MyLog.e(TAG, "action : " + action);
                    MyLog.e(TAG, "Fav count before : " + old_fav_count);

                    if (action == 1) {
                        old_fav_count++;
                        suggestion.setUser_fav(true);

                    } else {
                        old_fav_count--;
                        suggestion.setUser_fav(false);
                    }
                    MyLog.e(TAG, "Fav count after : " + old_fav_count);

                    suggestion.setFav_count(old_fav_count);*/
                }

                @Override
                public void failure(RetrofitError error) {
                    MyToast.tshort(context, "" + error);
                }
            });
        } else {
            MyToast.tshort(context, getString(R.string.check_network));
        }
    }


    /**
     * method used to share Suggestion using API call
     *
     * @param suggestion_id Suggestion id
     * @param user_id      user id
     * @param position     position of the list
     */
    public void suggestionShare(int suggestion_id, int user_id, final int position) {

        RestClient.getLokasoApi().suggestionShare(suggestion_id, user_id, new Callback<RetroAction>() {
            @Override
            public void success(RetroAction retroResponse, Response response) {
                if (retroResponse.getSuccess()) {

                }
            }

            @Override
            public void failure(RetrofitError error) {
                MyToast.tshort(context, "" + error);
            }
        });
    }


    /**
     * method used to set fonts
     */
    private void setTypeface() {
        new MyFont1(context).setAppFont((ViewGroup) findViewById(R.id.container));
    }

    /**
     * method used to get answers
     *
     * @param discovery_id Suggestion id
     */
    private void getAnswers(int discovery_id) {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().getAnswers(discovery_id, new Callback<RetroAnswer>() {
                @Override
                public void success(RetroAnswer retroAnswer, Response response) {
                    if (retroAnswer.getSuccess()) {
                        answerList = retroAnswer.getDetails();
                        setAnswers();

                    } else {
                        MyToast.tshort(context, retroAnswer.getMessage() + "");
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    MyToast.tshort(context, "" + error);
                }
            });
        } else {
            MyToast.tshort(context, getString(R.string.check_network));
        }
    }

    /**
     * method used to set answers
     */
    private void setAnswers() {
        answerAdapter = new AnswerAdapter(context, answerList);
        recyclerView.setAdapter(answerAdapter);
    }

    public void onUserClick(View view) {

        onUserClick();
    }

    private void onUserClick() {

        try {
            Profile profile = suggestion.getProfile();

            if (profile.getId() != MyPreferencesManager.getId(context)) {
                Intent intent = new Intent(context, OthersProfileActivity.class);
                intent.putExtra(Constant.PROFILE_ID, profile.getId());
                intent.putExtra(Constant.PROFILE, profile);
                startActivity(intent);
            } else {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra(Constant.PROFILE_ID, profile.getId());
                intent.putExtra(Constant.PROFILE, profile);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("ViewSuggestion Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
