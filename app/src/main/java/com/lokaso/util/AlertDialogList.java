package com.lokaso.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lokaso.R;
import com.lokaso.model.Profession;

import java.util.ArrayList;
import java.util.List;

public class AlertDialogList {
	
	protected static final String TAG = AlertDialogList.class.getSimpleName();

	private Context context;
	
	private Dialog dialog;
	
	private OnItemClickListener itemClickListener;
	
	private DialogListAdapter adapter;	
	
	private List<?> list;
	private List<?> unsortedList;
	
    private int scale 	= R.style.DialogScale;
	
	public AlertDialogList(Context context) {
		this.context = context;
		dialog = new Dialog(context, scale);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	public void show(List<?> list) {
		
		this.list = list;
		this.unsortedList = list;

		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(R.layout.search_member);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		
        RelativeLayout main 		= (RelativeLayout) dialog.findViewById(R.id.main);
        ListView listView 			= (ListView) dialog.findViewById(R.id.searchDropdownList);
        EditText searchEditText 	= (EditText) dialog.findViewById(R.id.searchEdittext);
        ImageView searchImageView 	= (ImageView) dialog.findViewById(R.id.searchImage);
        ProgressBar progressBar 	= (ProgressBar) dialog.findViewById(R.id.progressBarLoader);
		
        listView.setVisibility(View.VISIBLE);

		adapter = new DialogListAdapter(context, list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(listener);
		
		searchEditText.addTextChangedListener(textWatcher);
		
		dialog.show();
	}
	
	public void setOnClickListener(OnItemClickListener listener) {
		this.itemClickListener = listener;
	}
	
	public static interface OnItemClickListener {
		public void onItemClick(Object object);
	}
	
	AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
		
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

			Log.v(TAG, "list size : "+list.size());
			Log.v(TAG, "position : "+position);

			Log.v(TAG, "item  : "+list.get(position));
			itemClickListener.onItemClick(list.get(position));
			
			dialog.dismiss();
		}
	};
	
	private TextWatcher textWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			String text = s.toString();
			List<Object> sortedList = new ArrayList<Object>();
			if(text.length()>0)
			{	
				for (Object item : unsortedList) {
					
					String name="";
			    
					if(item instanceof String) {
			    		name=((String)item);
			    	}

					if(item instanceof Profession) {
			    		name=(((Profession) item).getName());
			    	}
/*
					if(item instanceof RouteSupplierDetail) {
			    		name=(((RouteSupplierDetail) item).getSupplier_name());
						Log.e(TAG,"NAME "+name);
			    	}
					
					if(item instanceof Supplytypes) {
			    		name=(((Supplytypes) item).getTypename());
			    	}
					
					if(item instanceof Svendordetail){
						name = (((Svendordetail) item).getOutname());
					}

					if(item instanceof Supplierdetail){
						name = (((Supplierdetail) item).getName());
					}
*/

					if(name!=null) {
						if (name.toLowerCase().contains(text.toLowerCase())) {
							sortedList.add(item);
						}
					}
				}
				list = sortedList;
			}
			else
			{
				list = unsortedList;
			}
			adapter.refresh(list);
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}
		@Override
		public void afterTextChanged(Editable s) {}
	};	
	
	public class DialogListAdapter extends BaseAdapter {
		
		private LayoutInflater inflater;
		private ViewHolder holder;
		private List<?> list;
		private Context context;
				
		/**
		 * Constructor
		 * 
		 * @param context
		 * @param list
		 */
		public DialogListAdapter(Context context, List<?> list) {
			
			this.context = context;
			inflater = LayoutInflater.from(context);
			this.list = list;
		}   
		
		public void clearMemoryCache() {
			
		}
		
		@Override
		public int getCount() {
			return list.size();
		}
		
		@Override
		public Object getItem(int position) {
			return list.get(position);
		}
		
		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		public void refresh(List<?> list) {
			this.list = list;
			notifyDataSetChanged();
		}
		
		View hView;
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
	        
	    	hView = convertView;
	    	
	    	final Object item = list.get(position);
			
	     	if (convertView == null) {
	     		
	            holder = new ViewHolder();
	            
	            hView = inflater.inflate(R.layout.item_simple_list, null);
	            holder.main			= (LinearLayout) hView.findViewById(R.id.main);
		        holder.name			= (TextView) hView.findViewById(R.id.textview);
		    		
	     		hView.setTag(holder);
	        }
	     	else {
	     		holder = (ViewHolder) hView.getTag();
	     	}
	     	
		    try {
		    	String name="";
		    	
		    	if(item instanceof String) {
		    		name=((String)item);
		    	}
		    			

		    	if(item instanceof Profession) {
		    		name=(((Profession) item).getName());
		    	}
/*
				if(item instanceof RouteSupplierDetail) {
		    		name=(((RouteSupplierDetail) item).getSupplier_name());
		    	}
				
				if(item instanceof Supplytypes) {
		    		name=(((Supplytypes) item).getTypename());
		    	}
				
				if(item instanceof Svendordetail) {
		    		name=(((Svendordetail) item).getOutname());
		    	}
		    	
				if(item instanceof Supplierdetail){
					name = (((Supplierdetail) item).getName());
				}
				*/
		        holder.name.setText(name);
		         
	    	} catch (Exception e) {
	          	e.printStackTrace();
	    	}
	        
	      	return hView;
		}	
		
		class ViewHolder
		{	
			 LinearLayout main;
			 ImageView im;
			 TextView name;
		}
	}	
}
