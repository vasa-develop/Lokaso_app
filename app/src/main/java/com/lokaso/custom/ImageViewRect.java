package com.lokaso.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ImageViewRect extends ImageView {
    public ImageViewRect(Context context) {
        super(context);
    }

    public ImageViewRect(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewRect(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int w = getMeasuredWidth();
        int h = getMeasuredHeight();



        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}