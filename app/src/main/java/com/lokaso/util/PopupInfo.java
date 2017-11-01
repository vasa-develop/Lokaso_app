package com.lokaso.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lokaso.R;
import com.lokaso.activity.OthersProfileActivity;
import com.lokaso.activity.ProfileActivity;


public class PopupInfo {

	private static final String TAG = PopupInfo.class.getSimpleName();
	private Context context;

	private PopupWindow popupWindow;

	private OnClickListener clickListener;

	private View anchor;

	private TextView messageTextView;

	private ScreenSize screenSize;

	public PopupInfo(Context context, View anchor) {
		this.context = context;
		makeWindow(context, anchor);

	}
	
	private void makeWindow(Context context, View anchor) {
		
		this.anchor = anchor;
		
		LayoutInflater inflater = LayoutInflater.from(context);
		View contentView = inflater.inflate(R.layout.popup_comingsoon, null);

		messageTextView = (TextView) contentView.findViewById(R.id.messageTextView);

		int popupWidth = getWidth(context);
		if(context.getClass()==ProfileActivity.class || context.getClass()==OthersProfileActivity.class) {

			popupWidth = getWidthAbout(context);
		}

		MyLog.d(TAG, "pop w : "+popupWidth);
		
		popupWindow = new PopupWindow(contentView, popupWidth, LayoutParams.WRAP_CONTENT);
		
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), android.R.color.transparent);
		//Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.shadow_image2);
		popupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), bitmap));
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		
		//popupWindow.setAnimationStyle(R.style.popupMenuAnimation);
		
		//popupWindow.showAtLocation(anchor, Gravity.BOTTOM, 0, bottomOffset);

		popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				if(clickListener!=null)
					clickListener.onDismiss();
			}
		});

	}


	public void show(String message) {

		if(messageTextView!=null)
			messageTextView.setText(message);

		if(popupWindow!=null)
			popupWindow.showAsDropDown(anchor);
	}
	
	public void hide() {
		if(popupWindow!=null)
			popupWindow.dismiss();
	}
	
	public void setOnClickListener(OnClickListener listener) {
		this.clickListener = listener;
	}
	
	public interface OnClickListener {
		public void onClick(String gender);
		public void onDismiss();

	}

	public int getWidth(Context context) {
		screenSize = new ScreenSize(context);
		int popupWidth = (int)(screenSize.getScreenWidthPixel()/2);
		return popupWidth;
	}

	public int getWidthAbout(Context context) {
		screenSize = new ScreenSize(context);
		int popupWidth = (int)(2 * screenSize.getScreenWidthPixel()/3);
		return popupWidth;
	}
}
