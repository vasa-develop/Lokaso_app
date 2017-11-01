package com.lokaso.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lokaso.R;
import com.lokaso.activity.ChatActivity;
import com.lokaso.activity.CreateSuggestionActivity;
import com.lokaso.activity.OthersProfileActivity;
import com.lokaso.activity.ProfileActivity;
import com.lokaso.activity.SavedDiscoveriesActivity;
import com.lokaso.activity.SplashActivity;
import com.lokaso.activity.SuggestionActivity;
import com.lokaso.activity.SuggestionCommentActivity;
import com.lokaso.activity.SuggestionLikeListActivity;
import com.lokaso.activity.VideoPlayActivity;
import com.lokaso.activity.ViewSuggestionActivity;
import com.lokaso.custom.ImageViewTint;
import com.lokaso.model.Action;
import com.lokaso.model.Ads;
import com.lokaso.model.Interest;
import com.lokaso.model.Profile;
import com.lokaso.model.Suggestion;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroAction;
import com.lokaso.retromodel.RetroResponse;
import com.lokaso.util.Constant;
import com.lokaso.util.MarshmallowPermission;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyLog;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;
import com.lokaso.util.Picaso;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Androcid on 19-Aug-16.
 */
public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.ViewHolder> {

    private String TAG = SuggestionAdapter.class.getSimpleName();
    private Activity context;

    private List<Suggestion> suggestionList;

    private int lastPosition = 0;

    public static final int TYPE_SUGGESTION         = 0;
    public static final int TYPE_AD                 = 1;
    public static final int TYPE_SUGGESTION_VIEW_ALL           = 2;

    public static final int SHARE_DELAY_TIME = 600;

    private MarshmallowPermission marshmallowPermission;

    public NetworkConnection networkConnection;

    private ClickInterface clickInterface;
    public interface ClickInterface {
        public void onItemClick(Suggestion suggestion);
        public void onLocationClick(double lat, double lng, String location);
        public void onSaveDiscoveryClick(int action, int discovery_id, int user_id, int position);
        public void onFavClick(int action, int discovery_id, int user_id, int position);
        public void onCommentClick(Suggestion suggestion);
        public void onSpamClick(int action, int suggestion_id, int position);
        public void onUserClick(Profile profile, boolean status, int leader, int follower, int position);
        public void onEditClick(Suggestion suggestion);
        void onShare(Suggestion suggestion, int position);
        void onShareComplete(Suggestion suggestion, int position);
        void onLikeCountClick(Suggestion suggestion, int position);
        void onCommentCountClick(Suggestion suggestion, int position);
    }

    public void setOnClickListener(ClickInterface clickInterface) {
        this.clickInterface = clickInterface;
    }

    public SuggestionAdapter(Activity context, List<Suggestion> suggestionList) {
        this.context = context;
        this.suggestionList = suggestionList;
        marshmallowPermission = new MarshmallowPermission(context);
        networkConnection = new NetworkConnection(context);
    }

    public void refresh(List<Suggestion> suggestionList) {
        this.suggestionList = suggestionList;
        lastPosition = 0;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        MyLog.e(TAG, "viewType : "+viewType);
        if(viewType==TYPE_AD) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad, parent, false);
        }
        else if(viewType==TYPE_SUGGESTION_VIEW_ALL) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggetion_all, parent, false);
        }
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggestion, parent, false);
        return new ViewHolder(view, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        final Suggestion suggestion = suggestionList.get(position);
        return suggestion.getType();
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    public void animate(RecyclerView.ViewHolder viewHolder, int position) {
        if (position > lastPosition) {
            viewHolder.itemView.clearAnimation();
            final Animation animation = AnimationUtils.loadAnimation(context, R.anim.left_from_right);
            viewHolder.itemView.setAnimation(animation);
            lastPosition = position;
        }
    }

    public void animateView(View view) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.rebound);
        view.startAnimation(animation);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
//        animate(holder, position);

        final Suggestion suggestion = suggestionList.get(position);

        if(suggestion!=null) {

        }

        MyLog.e(TAG, "position : "+position + " , "+suggestion.getType());

        if(getItemViewType(position)==TYPE_AD) {
            MyLog.e(TAG, "positionnnnn : "+position);
            final List<Ads> adsList = suggestion.getAdsList();
            if(holder.pageAdapter==null) {
                holder.pageAdapter = new AdsPagerAdapter(((SuggestionActivity) context).getSupportFragmentManager(), adsList);
                MyLog.e(TAG, "pager : " + holder.pager);
                holder.pager.setAdapter(holder.pageAdapter);

                holder.pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {

                        holder.setAdPlay(adsList.get(position), VideoPlayActivity.VIEW_SUCCESS, "");
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

                if(adsList.size()>0)
                    holder.setAdPlay(adsList.get(0), VideoPlayActivity.VIEW_SUCCESS, "");
            }
            else {
                holder.pageAdapter.refresh(adsList);

            }
        }
        else if(getItemViewType(position)==TYPE_SUGGESTION_VIEW_ALL) {

        }
        else {

            Profile profile = suggestion.getProfile();

            String imageUrl = suggestion.getImage();
            MyLog.e(TAG, "userImageUrl : " + imageUrl);
            if (!imageUrl.endsWith(".jpg")) {
                holder.tvCaption.setVisibility(View.GONE);
                holder.ivPic.setVisibility(View.GONE);
                holder.ivPic1.setVisibility(View.GONE);
            } else {
                holder.tvCaption.setVisibility(View.VISIBLE);
                holder.ivPic.setVisibility(View.VISIBLE);
                holder.ivPic1.setVisibility(View.VISIBLE);
            }

            Picaso.loadSuggestion(context, imageUrl, holder.ivPic);

            String suggestion_name = "\"" + suggestion.getSuggestion() + "\"";
            holder.tvSuggestion.setText(suggestion_name);

            String distance = suggestion.getDistance_display();
            holder.tvDistance.setText(distance);

            holder.tvFav.setText(suggestion.getFav_count() + "");

            String locationStr = "- " + suggestion.getLocation() + "";
            holder.tvLocation.setText(locationStr);

            String caption = suggestion.getCaption();
            holder.tvCaption.setText(caption);

            int likeCount = suggestion.getFav_count();
            int commentCount = suggestion.getComment_count();
            String likeTxt = " likes";
            if (likeCount == 1) {
                likeTxt = " like";
            }
            String commentTxt = " comments";
            if (commentCount == 1) {
                commentTxt = " comment";
            }
            holder.tvLikeCount.setText(likeCount + likeTxt);
            holder.tvCommentCount.setText(commentCount + commentTxt);

            holder.bShare.setSelected(false);
            holder.bLocation.setSelected(false);

            boolean isAddedToFav = suggestion.isUser_fav();
            holder.bFav.setSelected(isAddedToFav);

            boolean isAddedToWishlist = suggestion.isUser_discovery();
            holder.bBookmark.setSelected(isAddedToWishlist);

            if (isAddedToWishlist) {
                holder.tvWishlist.setText(R.string.remove_from_wishlist);
            } else {
                holder.tvWishlist.setText(R.string.add_to_wishlist);
            }

            if (profile != null) {
                holder.tvUser.setText(profile.getName());

                String credit = "(" + profile.getCredits_display() + " Credits)";
                holder.tvUserCredit.setText(credit);

                if (profile.getId() == MyPreferencesManager.getId(context)) {
                    holder.layoutSpam.setVisibility(View.GONE);

                    if (imageUrl.endsWith(".jpg")) {
                        holder.bEdit.setVisibility(View.VISIBLE);
                        holder.editLayout.setVisibility(View.GONE);
                    } else {
                        holder.bEdit.setVisibility(View.GONE);
                        holder.editLayout.setVisibility(View.VISIBLE);
                    }

                } else {
                    holder.bEdit.setVisibility(View.GONE);
                    holder.editLayout.setVisibility(View.GONE);
                    holder.layoutSpam.setVisibility(View.VISIBLE);

                    if (suggestion.isSuggestion_spam()) {
                        holder.bSpam.setImageResource(R.drawable.ic_spam_orange);
                    } else {
                        holder.bSpam.setImageResource(R.drawable.ic_spam);
                    }
                }


                holder.layoutUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean status = suggestion.getProfile().isUser_followed();
                        holder.onUserClick(suggestion.getProfile(), status, suggestion.getProfile().getId(), MyPreferencesManager.getId(context), position);

//                    if (clickInterface != null) {
//                        clickInterface.onUserClick(suggestion.getProfile(), status, suggestion.getProfile().getId(), MyPreferencesManager.getId(context), position);
//                    }
                    }
                });
            }

            if (suggestion.is_share_click()) {
                holder.layout2.setVisibility(View.GONE);
                holder.layout3.setVisibility(View.GONE);
                holder.bEdit.setVisibility(View.GONE);
                holder.editLayout.setVisibility(View.GONE);
                holder.userMore.setVisibility(View.GONE);
                holder.tvDistance.setVisibility(View.GONE);
            } else {
                holder.layout2.setVisibility(View.VISIBLE);
                holder.layout3.setVisibility(View.VISIBLE);
                holder.userMore.setVisibility(View.VISIBLE);
                holder.tvDistance.setVisibility(View.VISIBLE);

                holder.bEdit.setVisibility(View.GONE);
                holder.editLayout.setVisibility(View.GONE);
                if (profile != null) {
                    if (profile.getId() == MyPreferencesManager.getId(context)) {
                        if (imageUrl.endsWith(".jpg")) {
                            holder.bEdit.setVisibility(View.VISIBLE);
                        } else {
                            holder.editLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }


            // Remove Spam on Main Page
            holder.layoutSpam.setVisibility(View.GONE);


            String interestStr = "";
            try {
                Interest interest = suggestion.getInterest();
                if (interest != null)
                    interestStr = interest.getName();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (interestStr == null) interestStr = "";
            interestStr = interestStr.toUpperCase(Locale.getDefault());
            if (interestStr.length() == 0) {
                holder.tvInterest.setVisibility(View.GONE);
            } else {
                holder.tvInterest.setVisibility(View.VISIBLE);
            }
            holder.tvInterest.setText(interestStr);

            holder.bEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Suggestion suggestion = suggestionList.get(position);
                    onEditClick(suggestion);
                }
            });
            holder.editLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Suggestion suggestion = suggestionList.get(position);
                    onEditClick(suggestion);
                }
            });

            holder.favLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int suggestion_id = suggestion.getId();
                    int user_id = MyPreferencesManager.getId(context);
                    int action = Constant.FAV;

                    if (!suggestion.isUser_fav()) {
                        action = Constant.FAV;
                        holder.bFav.setSelected(true);
                    } else {
                        action = Constant.UNFAV;
                        holder.bFav.setSelected(false);
                    }
                    holder.discoveryFav(action, suggestion_id, user_id, position);

//                if (clickInterface != null) {
//                    clickInterface.onFavClick(action, suggestion_id, user_id, position);
//                }
                }
            });

            holder.wishlistLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    boolean isAddedToWishlist = suggestion.isUser_discovery();
                    int action = (!isAddedToWishlist) ? Constant.FAV : Constant.UNFAV;
                    int suggestion_id = suggestion.getId();
                    int user_id = MyPreferencesManager.getId(context);

                    if (context.getClass() != SavedDiscoveriesActivity.class) {
                        holder.saveUserSuggestion(action, suggestion_id, user_id, position);
                    } else {
                        if (clickInterface != null) {
                            if (!suggestion.isUser_discovery()) {
                                clickInterface.onSaveDiscoveryClick(Constant.FAV, suggestion.getId(), MyPreferencesManager.getId(context),
                                        position);
                                //holder.bBookmark.setImageResource(R.drawable.ic_wishlist_red);
                                holder.bBookmark.setSelected(false);
                                holder.tvWishlist.setText(R.string.remove_from_wishlist);

                            } else {
                                clickInterface.onSaveDiscoveryClick(Constant.UNFAV, suggestion.getId(), MyPreferencesManager.getId(context),
                                        position);
                                //holder.bBookmark.setImageResource(R.drawable.ic_wishlist);
                                holder.bBookmark.setSelected(true);
                                holder.tvWishlist.setText(R.string.add_to_wishlist);
                            }
                        }
                    }
                }
            });

            holder.shareLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    holder.layout2.setVisibility(View.GONE);
                    holder.layout3.setVisibility(View.GONE);
                    holder.bEdit.setVisibility(View.GONE);
                    holder.editLayout.setVisibility(View.GONE);
                    holder.userMore.setVisibility(View.GONE);
                    holder.tvDistance.setVisibility(View.GONE);

                    holder.progress_layout.setVisibility(View.VISIBLE);

//                if (clickInterface != null) {
//                    clickInterface.onShare(suggestion, position);
//                }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if (marshmallowPermission.checkPermissionForExternalStorage()) {

                                String shareText = "" + context.getString(R.string.share_text); //suggestion.getSuggestion();

                                Bitmap b = getBitmapFromView((ViewGroup) holder.container);
                                String filepath = saveBitmap(b);
                                filepath = "file://" + filepath;
                                Uri uri = Uri.parse(filepath);
                                MyLog.e(TAG, "uri : " + uri);

                                suggestionList.get(position).setIs_share_click(false);
                                refresh(suggestionList);

                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_SEND);
                                intent.putExtra(Intent.EXTRA_STREAM, uri);
                                intent.putExtra(Intent.EXTRA_TEXT, shareText);
                                intent.setType("image/jpeg");
                                context.startActivity(intent);

                                holder.progress_layout.setVisibility(View.GONE);

                                int suggestion_id = suggestion.getId();
                                int user_id = MyPreferencesManager.getId(context);

                                holder.suggestionShare(suggestion_id, user_id, position);

                            } else {
                                marshmallowPermission.requestPermissionForExternalStorage();
                            }
                        }
                    }, SHARE_DELAY_TIME);
                }
            });

            holder.tvDistance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    double lat = suggestion.getLat();
                    double lng = suggestion.getLng();
                    String location = suggestion.getLocation();

                    Uri gmmIntentUri = Uri.parse(Constant.MAP + lat + "," + lng + " (" + location + ")");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage(Constant.MAP_PACKAGE);
                    context.startActivity(mapIntent);

//                if (clickInterface != null) {
//                    clickInterface.onLocationClick(lat, lng, suggestion.getLocation());
//                }
                }
            });


            holder.tvLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    double lat = suggestion.getLat();
                    double lng = suggestion.getLng();
                    String location = suggestion.getLocation();

                    Uri gmmIntentUri = Uri.parse(Constant.MAP + lat + "," + lng + " (" + location + ")");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage(Constant.MAP_PACKAGE);
                    context.startActivity(mapIntent);

//                if (clickInterface != null) {
//                    clickInterface.onLocationClick(lat, lng, suggestion.getLocation());
//                }
                }
            });

            holder.locationLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    double lat = suggestion.getLat();
                    double lng = suggestion.getLng();
                    String location = suggestion.getLocation();

                    Uri gmmIntentUri = Uri.parse(Constant.MAP + lat + "," + lng + " (" + location + ")");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage(Constant.MAP_PACKAGE);
                    context.startActivity(mapIntent);

//                if (clickInterface != null) {
//                    clickInterface.onLocationClick(lat, lng, );
//                }
                }
            });

            holder.bSpam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final int suggestion_id = suggestion.getId();
                    int actionType = Constant.UNFAV;
                    if (!suggestion.isSuggestion_spam()) {
                        actionType = Constant.FAV;
                    }
                    final int action = actionType;
//
//                if (clickInterface != null) {
//                    if (!suggestion.isSuggestion_spam()) {
//                        //holder.bSpam.setImageResource(R.drawable.ic_spam_orange);
//                        clickInterface.onSpamClick(Constant.FAV, suggestion_id, position);
//
//                    } else {
//                        //holder.bSpam.setImageResource(R.drawable.ic_spam);
//                        clickInterface.onSpamClick(Constant.UNFAV, suggestion_id, position);
//                    }
//                }


                    if (action == 1) {

                        new AlertDialog.Builder(context)
                                .setTitle(context.getString(R.string.dialog_title_spam_tip))
                                .setMessage(context.getString(R.string.dialog_message_spam_tip))
                                .setPositiveButton(context.getString(R.string.dialog_spam_ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        holder.spamSuggestion(action, suggestion_id, position);
                                    }
                                })
                                .setNegativeButton(context.getString(R.string.dialog_spam_cancel), null)
                                .show();


                    } else {
                        holder.spamSuggestion(action, suggestion_id, position);
                    }

                }
            });

            holder.tvSpam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickInterface != null) {
                        if (!suggestion.isSuggestion_spam()) {
                            holder.bSpam.setImageResource(R.drawable.ic_spam_orange);
                            clickInterface.onSpamClick(Constant.FAV, suggestion.getId(), position);

                        } else {
                            holder.bSpam.setImageResource(R.drawable.ic_spam);
                            clickInterface.onSpamClick(Constant.UNFAV, suggestion.getId(), position);
                        }
                    }
                }
            });


            holder.likeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, SuggestionLikeListActivity.class);
                    intent.putExtra(Constant.SUGGESTION, suggestion);
                    context.startActivity(intent);

//                if (clickInterface != null) {
//                    clickInterface.onLikeCountClick(suggestion, position);
//                }
                }
            });

            holder.commentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                if (clickInterface != null) {
//                    clickInterface.onCommentCountClick(suggestion, position);
//                }

                    Intent intent = new Intent(context, SuggestionCommentActivity.class);
                    intent.putExtra(Constant.SUGGESTION, suggestion);
                    context.startActivity(intent);
                }
            });


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                if (clickInterface != null) {
//                    clickInterface.onItemClick(suggestion);
//                }

                    Intent intent = new Intent(context, ViewSuggestionActivity.class);
                    intent.putExtra(Constant.SUGGESTION, suggestion);
                    context.startActivity(intent);
                    //overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);

                }
            });
        }
    }

    private void onEditClick(Suggestion suggestion) {

        if(suggestion.getProfile().getId()==MyPreferencesManager.getId(context)) {
            Intent intent = new Intent(context, CreateSuggestionActivity.class);
            intent.putExtra(Constant.SUGGESTION, suggestion);
            context.startActivity(intent);
            //overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);
        }
//                if (clickInterface != null) {
//                    clickInterface.onEditClick(suggestion);
//                }

    }

    @Override
    public int getItemCount() {
        return suggestionList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ViewGroup container;

        protected LinearLayout layoutUser;
        private ImageViewTint userMore;

        protected LinearLayout layout2, layout3;
        protected ImageView ivPic, ivPic1;
        protected ImageView bEdit;
        private LinearLayout editLayout;
        protected TextView tvSuggestion, tvUserBy, tvUser, tvUserCredit, tvDistance, tvFav
                , tvInterest, tvCaption, tvWishlist, tvSpam;

        private LinearLayout layoutLocation;
        private TextView tvLocation;

        private LinearLayout likeLayout, commentLayout;
        private TextView tvLikeCount, tvCommentCount;

        private LinearLayout favLayout, wishlistLayout, locationLayout, shareLayout, layoutSpam;
        private ImageViewTint /*bLocation, bComment, */
                bFav, bBookmark, bSpam, bShare, bLocation;

        private RelativeLayout progress_layout;


        // Ads View
        private ViewPager pager;
        private AdsPagerAdapter pageAdapter;

        // Dialog
        private ImageView bFollow;
        private TextView tvFollow;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            MyFont1 myFont = new MyFont1(context);
            myFont.setAppFont((ViewGroup) itemView);

            if(viewType==TYPE_AD) {
                pager = (ViewPager) itemView.findViewById(R.id.imagePager);
                MyLog.e(TAG, "pager:"+pager);
            }
            else {


                container = (ViewGroup) itemView.findViewById(R.id.container);
                ivPic = (ImageView) itemView.findViewById(R.id.ivPic);
                ivPic1 = (ImageView) itemView.findViewById(R.id.ivPic1);
                bEdit = (ImageView) itemView.findViewById(R.id.bEdit);
                editLayout = (LinearLayout) itemView.findViewById(R.id.editLayout);
//            bLocation = (ImageButton) itemView.findViewById(R.id.bLocation);
//            bComment = (ImageButton) itemView.findViewById(R.id.bComment);
                layout2 = (LinearLayout) itemView.findViewById(R.id.layout2);
                layout3 = (LinearLayout) itemView.findViewById(R.id.layout3);

                layoutUser = (LinearLayout) itemView.findViewById(R.id.layoutUser);
                userMore = (ImageViewTint) itemView.findViewById(R.id.userMore);

                tvCaption = (TextView) itemView.findViewById(R.id.tvCaption);
                tvSuggestion = (TextView) itemView.findViewById(R.id.tvSuggestion);
                tvUserBy = (TextView) itemView.findViewById(R.id.tvUserBy);
                tvUser = (TextView) itemView.findViewById(R.id.tvUser);
                tvUserCredit = (TextView) itemView.findViewById(R.id.tvUserCredit);
                tvDistance = (TextView) itemView.findViewById(R.id.tvDistance);
                tvInterest = (TextView) itemView.findViewById(R.id.tvInterest);
                tvSpam = (TextView) itemView.findViewById(R.id.tvSpam);

                tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);

                bBookmark = (ImageViewTint) itemView.findViewById(R.id.bBookmark);
                bFav = (ImageViewTint) itemView.findViewById(R.id.bFav);
                bSpam = (ImageViewTint) itemView.findViewById(R.id.bSpam);
                bShare = (ImageViewTint) itemView.findViewById(R.id.bShare);
                bLocation = (ImageViewTint) itemView.findViewById(R.id.bLocation);

                favLayout = (LinearLayout) itemView.findViewById(R.id.favLayout);
                wishlistLayout = (LinearLayout) itemView.findViewById(R.id.wishlistLayout);
                layoutSpam = (LinearLayout) itemView.findViewById(R.id.layoutSpam);
                shareLayout = (LinearLayout) itemView.findViewById(R.id.shareLayout);
                locationLayout = (LinearLayout) itemView.findViewById(R.id.locationLayout);

                tvFav = (TextView) itemView.findViewById(R.id.tvFav);
                tvWishlist = (TextView) itemView.findViewById(R.id.tvWishlist);


                likeLayout = (LinearLayout) itemView.findViewById(R.id.likeLayout);
                commentLayout = (LinearLayout) itemView.findViewById(R.id.commentLayout);
                tvLikeCount = (TextView) itemView.findViewById(R.id.tvLikeCount);
                tvCommentCount = (TextView) itemView.findViewById(R.id.tvCommentCount);

                progress_layout = (RelativeLayout) itemView.findViewById(R.id.progress_layout);


                //myFont.setFont(tvDistance, MyFont1.CENTURY_GOTHIC_BOLD);
            }

        }

        /**
         * method used set ad play view
         */
        private void setAdPlay(Ads ads, int status, String data) {

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


        /**
         * method used to favorite Suggestion using API call
         *
         * @param action       action = 1 to favorite Suggestion and action = 0 for vice-versa
         * @param discovery_id Suggestion id
         * @param user_id      user id
         * @param position     position of the list
         */
        public void discoveryFav(final int action, int discovery_id, int user_id, final int position) {
            if (networkConnection.isNetworkAvailable())
            {
                //animateView(bFav);
                final long startTime = new Date().getTime();
                RestClient.getLokasoApi().discoveryFav(action, discovery_id, user_id, new Callback<RetroResponse>() {
                    @Override
                    public void success(RetroResponse retroResponse, Response response) {
//                    MyToast.tshort(context, retroResponse.getMessage() + "");

                        long endTime = new Date().getTime();

                        if (retroResponse.getSuccess()) {
                            int old_fav_count = suggestionList.get(position).getFav_count();

                            if (action == 1) {
                                old_fav_count++;
                                suggestionList.get(position).setUser_fav(true);

                            } else {
                                old_fav_count--;
                                suggestionList.get(position).setUser_fav(false);
                            }

                            final int newCount = old_fav_count;

                            long diff = endTime - startTime;

                            long waitTime = 1000;

                            if(diff<waitTime) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        suggestionList.get(position).setFav_count(newCount);
                                        refresh(suggestionList);
                                    }
                                }, waitTime-diff);
                            }
                            else {

                                suggestionList.get(position).setFav_count(newCount);
                                refresh(suggestionList);
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        MyToast.tshort(context, "" + error);
                    }
                });
            } else {
                MyToast.tshort(context, context.getString(R.string.check_network));
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

        public void onUserClick(final Profile profile, final boolean status, final int leader, final int follower, final int position)  {

            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.dialog_user);

            dialog.getWindow().getDecorView().setBackgroundResource(R.drawable.background_radius_orange);
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(final DialogInterface dialog1) {
                    Window window = dialog.getWindow();
                    window.getDecorView().setBackgroundResource(R.drawable.background_radius_orange);
                    window.getDecorView().setPadding(0, 0, 0, 0);
                }
            });

            RelativeLayout dialogLayout = (RelativeLayout) dialog.findViewById(R.id.dialogLayout);
            ImageView ivPic = (ImageView) dialog.findViewById(R.id.ivPic);
            TextView tvName = (TextView) dialog.findViewById(R.id.tvName);
            TextView tvProfession = (TextView) dialog.findViewById(R.id.tvProfession);
            LinearLayout layoutAction = (LinearLayout) dialog.findViewById(R.id.layoutAction);
            LinearLayout layoutFollow = (LinearLayout) dialog.findViewById(R.id.layoutFollow);
            LinearLayout layoutMessage = (LinearLayout) dialog.findViewById(R.id.layoutMessage);
            tvFollow = (TextView) dialog.findViewById(R.id.tvFollow);
            bFollow = (ImageView) dialog.findViewById(R.id.bFollow);
            TextView tvMessage = (TextView) dialog.findViewById(R.id.tvMessage);
            ImageView bMessage = (ImageView) dialog.findViewById(R.id.bMessage);

            MyFont1 myFont = new MyFont1(context);
            myFont.setAppFont(dialogLayout);

            String imageUrl = profile.getImage();
            MyLog.e(TAG, "userImageUrl : "+imageUrl);
            Picaso.loadUser(context, imageUrl, ivPic);

            tvName.setText(profile.getName());

            String profession = profile.getProfession();
            if(profession!=null && profession.length()>0) {
                tvProfession.setVisibility(View.VISIBLE);
            }
            else {
                tvProfession.setVisibility(View.GONE);
            }
            tvProfession.setText(profession);

            if (profile.getId() == MyPreferencesManager.getId(context)) {
                layoutAction.setVisibility(View.GONE);
            } else {
                layoutAction.setVisibility(View.VISIBLE);
                if (status) {
                    bFollow.setImageResource(R.drawable.ic_follow);
                    tvFollow.setText(context.getString(R.string.following));
                } else {
                    bFollow.setImageResource(R.drawable.ic_follow_white);
                    tvFollow.setText(context.getString(R.string.follow));
                }
            }

            layoutFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        Profile leaderProfile = suggestionList.get(position).getProfile();
                        // Action should be opposite of what it is currently
                        int action = leaderProfile.isUser_followed() ? 0 : 1;

                        setFollow(action, leaderProfile.getId(), MyPreferencesManager.getId(context), position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            layoutMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        Profile profile = suggestionList.get(position).getProfile();

                        Intent backIntent = new Intent(context, ProfileActivity.class);
                        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        if (profile.getId() != MyPreferencesManager.getId(context)) {
                            backIntent = new Intent(context, OthersProfileActivity.class);
                            backIntent.putExtra(Constant.PROFILE_ID, profile.getId());
                            backIntent.putExtra(Constant.PROFILE, profile);
                        }

                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra(Constant.PROFILE, profile);
                        intent.putExtra(Constant.CHAT_ID, 0);

                        Intent[] activityIntents = new Intent[]{backIntent, intent};
                        context.startActivities(activityIntents);

                        dialog.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            dialogLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (profile.getId() != MyPreferencesManager.getId(context)) {
                        Intent intent = new Intent(context, OthersProfileActivity.class);
                        intent.putExtra(Constant.PROFILE_ID, profile.getId());
                        intent.putExtra(Constant.PROFILE, profile);
                        context.startActivity(intent);
//                            getActivity().overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);

                    } else {
                        Intent intent = new Intent(context, ProfileActivity.class);
                        intent.putExtra(Constant.PROFILE_ID, profile.getId());
                        intent.putExtra(Constant.PROFILE, profile);
                        context.startActivity(intent);
//                            getActivity().overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);
                    }
                    dialog.dismiss();
                }
            });

            dialog.show();

        }


        private void saveUserSuggestion(final int action, int discovery_id, int user_id, final int position) {
            if (networkConnection.isNetworkAvailable()) {
                RestClient.getLokasoApi().userDiscovery(action, discovery_id, user_id, new Callback<RetroResponse>() {
                    @Override
                    public void success(RetroResponse retroResponse, Response response) {
//                    MyToast.tshort(context, retroResponse.getMessage() + "");
                        if (action == 1) {
                            suggestionList.get(position).setUser_discovery(true);
                        } else {
                            suggestionList.get(position).setUser_discovery(false);
                        }
                        refresh(suggestionList);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        MyToast.tshort(context, error);
                    }
                });

            } else {
                MyToast.tshort(context, context.getString(R.string.check_network));
            }
        }


        /**
         * method used to follow a folk using API call
         *
         * @param action   action = 1 to follow folk and action = 0 for vice-versa
         * @param leader   user id of the user being followed
         * @param follower user id of the user following
         */
        private void setFollow(int action, int leader, int follower, int position) {
            boolean status;
            if (action == 1) {
                status = true;
                //suggestionList.get(position).getProfile().setUser_followed(true);
            } else {
                status = false;
            }
            suggestionList.get(position).getProfile().setUser_followed(status);
            if (status) {
                bFollow.setImageResource(R.drawable.ic_follow);
                tvFollow.setText(context.getString(R.string.following));
            } else {
                bFollow.setImageResource(R.drawable.ic_follow_white);
                tvFollow.setText(context.getString(R.string.follow));
            }
            refresh(suggestionList);

            if (networkConnection.isNetworkAvailable()) {
                RestClient.getLokasoApi().follow(action, leader, follower, new Callback<RetroResponse>() {
                    @Override
                    public void success(RetroResponse retroResponse, Response response) {
//                MyToast.tshort(context, retroResponse.getMessage() + "");
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        MyToast.tshort(context, "" + error);
                    }
                });
            } else {
                MyToast.tshort(context, context.getString(R.string.check_network));
            }
        }



        /**
         * method used to spam a Suggestion using API call
         *
         * @param action        action = 1 to spam Suggestion and action = 0 for vice-versa
         * @param suggestion_id Suggestion id
         */
        private void spamSuggestion(final int action, int suggestion_id, final int position) {
//        if (action == 1) {
//            suggestionList.get(position).setSuggestion_spam(true);
//        } else {
//            suggestionList.get(position).setSuggestion_spam(false);
//        }
//        suggestionAdapter.refresh(suggestionList);

            if (networkConnection.isNetworkAvailable()) {
                final String failMessage = "Failed to spam";
                RestClient.getLokasoApi().suggestionSpam(action, suggestion_id, MyPreferencesManager.getId(context),
                        new Callback<RetroAction>() {
                            @Override
                            public void success(RetroAction retroAction, Response response) {

                                if(retroAction!=null) {
                                    if (retroAction.getSuccess()) {
                                        Action action = retroAction.getAction();
                                        if(action!=null) {
                                            boolean isValid = action.getAction_status()==1;
                                            //checkReport = isValid;
                                            //invalidateOptionsMenu();
                                            if (isValid) {
                                                suggestionList.get(position).setSuggestion_spam(true);
                                            } else {
                                                suggestionList.get(position).setSuggestion_spam(false);
                                            }
                                            refresh(suggestionList);
                                        }
                                    }
                                    else {
                                        MyToast.tshort(context, retroAction.getMessage());
                                    }
                                }
                                else {
                                    MyToast.tshort(context, failMessage);
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                MyToast.tshort(context, error);
                            }
                        });
            } else {
                MyToast.tshort(context, context.getString(R.string.check_network));
            }
        }

    }
/*
    private void hideVisibility(ViewHolder holder) {
        holder.container.setLayoutParams(new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 220));
        holder.layout2.setVisibility(View.GONE);
        holder.layoutUser.setVisibility(View.GONE);
        holder.tvCaption.setVisibility(View.GONE);
        holder.tvSuggestion.setVisibility(View.GONE);
        holder.bEdit.setVisibility(View.GONE);
    }
*/


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



}
