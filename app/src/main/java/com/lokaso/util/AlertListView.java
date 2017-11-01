package com.lokaso.util;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lokaso.R;

import java.util.ArrayList;


public class AlertListView {

	private static final String TAG = AlertListView.class.getSimpleName();
	private static AlertDialog alertDialog;
	private static AlertDialog.Builder builder;

	private ListView listView;
	private EditText editText;

    private ArrayList<String> tList;
    private ArrayList<String> oriList;

	private OnItemClickListener itemClickListener;

	private DialogListAdapter adapter;

	public AlertListView(Context context, ArrayList<String> list) {
		
		LayoutInflater inflater = LayoutInflater.from(context);
		View contentView = inflater.inflate(R.layout.popup_window, null);
		listView = (ListView) contentView.findViewById(R.id.ad_search_container);
		editText = (EditText) contentView.findViewById(R.id.editText);
		//listView.setExpanded(true);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String str = charSequence.toString();

                ArrayList<String> tempList = new ArrayList<>();

                for (int x=0; x<tList.size(); x++) {
                    if (tList.get(x).contains(str)) {
                        tempList.add(tList.get(x));
                    }
                }

                tList = new ArrayList<>(tempList);
                adapter.refresh(tList);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tList = new ArrayList<>(list);

        oriList = list;

		adapter = new DialogListAdapter(context, list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(listener);

        builder = new AlertDialog.Builder(context);
        builder.setView(contentView);

        alertDialog = builder.create();

	}
	
	AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
		
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
						
			itemClickListener.onItemClick(adapterView, view, position, id);
			
			if(alertDialog!=null) {
                alertDialog.dismiss();
			}
		}
	};
	

	

	public void setOnClickListener(OnItemClickListener listener) {
		this.itemClickListener = listener;
	}
	
	public static interface OnItemClickListener {
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long id);
	}	

	private class DialogListAdapter extends BaseAdapter {
		
		private LayoutInflater inflater;
		private ViewHolder holder;
		private ArrayList<String> list;
		private Context context;
		private MyFont1 myFont;
		
		/**
		 * Constructor
		 * 
		 * @param context
		 * @param list
		 */
		public DialogListAdapter(Context context, ArrayList<String> list) {
			
			this.context = context;
			inflater = LayoutInflater.from(context);
			myFont = new MyFont1(context);
			this.list = list;
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
		
		public void refresh(ArrayList<String> list) {
			this.list = list;
			notifyDataSetChanged();
		}
		
		View hView;
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
	        
	    	hView = convertView;
	    	
	    	final String name = list.get(position);
	    	
			holder = new ViewHolder();
			hView = inflater.inflate(R.layout.popup_item, null);
			holder.name			= (TextView) hView.findViewById(R.id.name);
			hView.setTag(holder);
	       
	     	
	     	 
	     	
		    try {		    	
		    	 
		        holder.name.setText(name);
		         
	    	} catch (Exception e) {
	          	e.printStackTrace();
	    	}
	        
	      	return hView;
		}	
		
		class ViewHolder
		{	
			 LinearLayout main;
			 TextView name;
			 View divider;
		}
	}	
}
