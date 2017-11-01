package com.lokaso.util;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.lokaso.R;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

/**
 * Created by Androcid-6 on 06-01-2016.
 */
public class TourClass {

    private static Activity context;

    private TourGuide mTourGuideHandler;

    private Pointer pointer;
    private Overlay overlay;
    private ToolTip toolTip;

    private String title = null;
    private String message = null;
    private View clickView = null;
    private View anchorView = null;

    private boolean isMenu = false;

    private OnClickListener onClickListener;

    public TourClass(Activity ctx) {
        context = ctx;
        init(context);
    }

    private void init(Activity context) {

        mTourGuideHandler = TourGuide.init(context).with(TourGuide.Technique.Click);

        pointer = new Pointer();
        //pointer.setColor(ContextCompat.getColor(context, R.color.tour_bg_full));
        pointer.setColor(ContextCompat.getColor(context, R.color.tra_white));


        overlay = new Overlay();
        overlay.setBackgroundColor(ContextCompat.getColor(context, R.color.tour_bg_full)); //Color.parseColor("#96fdc008"));
        //overlay.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent)); //Color.parseColor("#96fdc008"));
        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onClickListener.onClick(mInstance);
            }
        });

        toolTip = new ToolTip();
        toolTip.setTextColor(context.getResources().getColor(R.color.tour_text)); //Color.parseColor("#bdc3c7")
        toolTip.setBackgroundColor(context.getResources().getColor(R.color.tour_bg_box)); //Color.parseColor("#e74c3c")
        toolTip.setShadow(true);
        toolTip.setGravity(Gravity.TOP | Gravity.LEFT);
        toolTip.mExitAnimation = AnimationUtils.loadAnimation(context, R.anim.go);
        //toolTip.setEnterAnimation(AnimationUtils.loadAnimation(context, R.anim.fab_in));
        toolTip.setEnterAnimation(AnimationUtils.loadAnimation(context, R.anim.go));



    }

    public <T> TourClass isTopMost() {
        if(toolTip!=null)
            overlay.setBackgroundColor(ContextCompat.getColor(context, R.color.tour_bg_full));
        return this;
    }

    public <T> TourClass isMenu() {
        isMenu = true;
        return this;
    }

    public <T> TourClass topLeft() {
        if(toolTip!=null)
        toolTip.setGravity(Gravity.TOP | Gravity.LEFT);
        return this;
    }

    public <T> TourClass bottomLeft() {
        if(toolTip!=null)
        toolTip.setGravity(Gravity.BOTTOM | Gravity.LEFT);
        return this;
    }

    public <T> TourClass bottomRight() {
        if(toolTip!=null)
        toolTip.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
        return this;
    }

    public <T> TourClass topRight() {
        if(toolTip!=null)
        toolTip.setGravity(Gravity.TOP | Gravity.RIGHT);
        return this;
    }

    public <T> TourClass click() {
        if(mTourGuideHandler!=null)
        mTourGuideHandler.with(TourGuide.Technique.Click);
        return this;
    }

    public <T> TourClass leftSwipe() {
        if(mTourGuideHandler!=null)
        mTourGuideHandler.with(TourGuide.Technique.HorizontalLeft);
        return this;
    }
    public <T> TourClass rightSwipe() {
        if(mTourGuideHandler!=null)
            mTourGuideHandler.with(TourGuide.Technique.HorizontalRight);
        return this;
    }



    public interface OnClickListener {
        public void onClick(TourClass tour);
    }

    public <T> TourClass setOnClickListener(OnClickListener listener) {
        this.onClickListener = listener;

        if(toolTip!=null)
            toolTip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onClick(TourClass.this);
                }
            });

        return this;
    }


    public <T> TourClass title(String title) {
        if(toolTip!=null) {
            toolTip.setTitle(title);
        }
        return this;
    }
    public <T> TourClass message(String message) {
        if(toolTip!=null) {
            toolTip.setDescription(message);
        }
        return this;
    }

    /**
     * This is the click view upon which the user will do some action
     * This is different from the anchor view
     *
     * @param view
     * @param <T>
     * @return
     */
    public <T> TourClass clickView(View view) {
        clickView = view;
        return this;
    }
    public <T> TourClass anchor(View view) {
        anchorView = view;
        if(clickView==null) {
            clickView=view;
        }
/*
        ViewParent viewParent = clickView.getParent();

        ViewGroup viewGroup = (ViewGroup) viewParent;


        ViewGroup viewGroupParent = (ViewGroup) viewGroup.getParent();
        RelativeLayout outerView = null;
        if(viewGroupParent instanceof LinearLayout) {

            outerView = new RelativeLayout(context);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(clickView.getWidth(), clickView.getHeight());
            outerView.setLayoutParams(layoutParams);
            outerView.setBackgroundColor(Color.RED);

            viewGroupParent.addView(outerView);

            outerView.addView(viewGroup);

            viewGroup.setVisibility(View.VISIBLE);
        }
        else {

        }
*/


//        View dummyView = new View(context);
//
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(clickView.getWidth(), clickView.getHeight());
//        dummyView.setLayoutParams(layoutParams);
//        dummyView.setBackgroundColor(Color.RED);
//
//        viewGroup.addView(dummyView);

        View view1 = clickView;

        return this;
    }

    public <T> TourClass show() {

        if(mTourGuideHandler!=null) {

            if(!isMenu) {
                clickView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClickListener.onClick(TourClass.this);
                    }
                });
            }

            mTourGuideHandler.setPointer(pointer)
                    .setToolTip(toolTip)
                    .setOverlay(overlay)
                    .playOn(anchorView);
        }
        return this;
    }



    public <T> TourClass show(View view, String message) {
        return show(view, null, message);
    }

    public <T> TourClass show(View view, String title, String message) {


        ViewParent viewParent = view.getParent();

        ViewGroup viewGroup = (ViewGroup) viewParent;

        ViewGroup viewGroupParent = (ViewGroup) viewGroup.getParent();


        if(view instanceof ViewGroup) {

        }
        else {

        }



        View dummyView = new View(context);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(view.getWidth(), view.getHeight());
        dummyView.setLayoutParams(layoutParams);
        dummyView.setBackgroundColor(Color.RED);

        viewGroup.addView(dummyView);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onClickListener.onClick(TourClass.this);
            }
        });


        if(toolTip!=null) {
            toolTip.setTitle(title);
            toolTip.setDescription(message);
        }
        if(mTourGuideHandler!=null) {
            mTourGuideHandler.setPointer(pointer)
                    .setToolTip(toolTip)
                    .setOverlay(overlay)
                    .playOn(view);
        }
        return this;
    }

    public <T> void dismiss() {
        if(mTourGuideHandler!=null) {
            pointer = null;
            toolTip = null;

            mTourGuideHandler.cleanUp();
            mTourGuideHandler = null;
        }
    }
}
