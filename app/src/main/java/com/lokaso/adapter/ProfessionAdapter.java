package com.lokaso.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lokaso.R;
import com.lokaso.model.Interest;
import com.lokaso.model.Profession;
import com.lokaso.util.MyLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Androcid on 12-Aug-16.
 */
public class ProfessionAdapter extends BaseAdapter {

    private String TAG = ProfessionAdapter.class.getSimpleName();
    private LayoutInflater layoutInflater;
    private List<Profession> professionList;
    private Context context;

    public ProfessionAdapter(Context context, List<Profession> professionList) {
        this.context = context;
        this.professionList = professionList;
        layoutInflater = LayoutInflater.from(context);
    }

    public void refresh(List<Profession> list) {
        this.professionList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return professionList.size();
    }

    @Override
    public Profession getItem(int position) {
        return professionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return professionList.get(position).getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = layoutInflater.inflate(R.layout.layout_profession, null);
        TextView tvProfession = (TextView) view.findViewById(R.id.tv);
        tvProfession.setText(professionList.get(position).getName());
        MyLog.e(TAG, professionList.get(position).getName());
        return view;
    }
}
