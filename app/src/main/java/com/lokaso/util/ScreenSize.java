package com.lokaso.util;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

public class ScreenSize {
	
	private Context context;
	
	private DisplayMetrics metrics;
	private WindowManager wm;
	
	private float scale, resize, adjust;
	private double defaultScreenSize = 7.507; // Size of 7" tablet
	
	public ScreenSize(Context context) {
		this.context = context;
		metrics = new DisplayMetrics();
		wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);			
	}
	
	public float getScreenDensity() {
		scale = context.getResources().getDisplayMetrics().density;
		return scale;
	}
	
	public double getScreenWidth() {
		wm.getDefaultDisplay().getMetrics(metrics);	
		double width = Math.pow(metrics.widthPixels/metrics.xdpi,2);
		return width;
	}
	
	public double getScreenWidthPixel() {
		wm.getDefaultDisplay().getMetrics(metrics);	
		double width = metrics.widthPixels;
		return width;
	}
	
	public double getScreenHeightPixel() {
		wm.getDefaultDisplay().getMetrics(metrics);	
		double height = metrics.heightPixels;
		return height;
	}
	
	public double getScreenHeight() {
		wm.getDefaultDisplay().getMetrics(metrics);	
		double height = Math.pow(metrics.heightPixels/metrics.ydpi,2);
		return height;
	}
	
	public double getScreenInches() {
		double x = getScreenWidth();
        double y = getScreenHeight();
        double screenInches = Math.sqrt(x + y);
        return screenInches;
	}
	
	public double getScaleValue()
	{	
		double screenInches = getScreenInches();         
        double adjustValue = (float) (screenInches / defaultScreenSize);
		return adjustValue;
	}
	
	public void setDefaultScreenSize(double defaultScreenSize) {
		this.defaultScreenSize = defaultScreenSize;
	}
	
	public int calculateSize(int value) {
		double scaleSize = getScaleValue() * getScreenDensity();
        return (int)(value * scaleSize);
	}
	
	public int getWidthRatio(int value) {
		return (int)(getScreenWidthPixel() * value)/100;
	}	
	
	public int getHeightRatio(int value) {
		return (int)(getScreenHeightPixel() * value)/100;
	}
	
	public int getTextScaleValue(int value)
	{
		double screenInches = getScreenInches();         
        double adjustValue = (float) (screenInches / defaultScreenSize);
		return (int) (value * adjustValue);
	}
	

	public double getScreenOrientation() {
		wm.getDefaultDisplay().getMetrics(metrics);	
		
		double width = metrics.widthPixels;
		return width;
	}
	
	public float getDp(int pixel) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixel, metrics);
		//return TypedValue.complexToDimensionPixelSize(pixel, context.getResources().getDisplayMetrics());
	}

	public boolean isLandscape() {
		return context.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE;
	}

	public boolean isXLarge() {
		return (context.getResources().getConfiguration().screenLayout & context.getResources().getConfiguration().SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	public boolean isLarge() {
		return (context.getResources().getConfiguration().screenLayout & context.getResources().getConfiguration().SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	public boolean isNormal() {
		return (context.getResources().getConfiguration().screenLayout & context.getResources().getConfiguration().SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL;
	}

	public boolean isSmall() {
		return (context.getResources().getConfiguration().screenLayout & context.getResources().getConfiguration().SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL;
	}



	/**
	 * Get the height of actionbar.
	 * This will work for apis that support ActionBar.
	 *
	 * @return actionBarHeight
	 */
	public int getActionBarHeight() {
		int actionBarHeight = 50;
		TypedValue tv = new TypedValue();
		if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
		{
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
		}
		return actionBarHeight;
	}
}
