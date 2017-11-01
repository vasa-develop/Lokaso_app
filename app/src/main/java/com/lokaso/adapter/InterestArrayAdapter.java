package com.lokaso.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lokaso.R;
import com.lokaso.model.Interest;
import com.lokaso.util.MyLog;

import java.util.List;

/**
 * Created by Androcid on 12-Aug-16.
 */
public class InterestArrayAdapter extends BaseAdapter {

    private String TAG = InterestArrayAdapter.class.getSimpleName();
    private LayoutInflater layoutInflater;
    private List<Interest> list;
    private Context context;

    public InterestArrayAdapter(Context context, List<Interest> interestList) {
        this.context = context;
        this.list = interestList;
        layoutInflater = LayoutInflater.from(context);
    }

    public void refresh(List<Interest> interestList) {
        this.list = interestList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Interest getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = layoutInflater.inflate(R.layout.layout_profession, null);
        TextView tvProfession = (TextView) view.findViewById(R.id.tv);
        tvProfession.setText(list.get(position).getName());
        MyLog.e(TAG, list.get(position).getName());
        return view;
    }
}
