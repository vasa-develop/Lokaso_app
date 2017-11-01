package com.lokaso.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.lokaso.R;
import com.lokaso.model.Interest;
import com.lokaso.util.MyFont1;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Androcid on 16-Aug-16.
 */
public class InterestAdapter extends RecyclerView.Adapter<InterestAdapter.ViewHolder> {

    private String TAG = InterestAdapter.class.getSimpleName();
    private Context context;
    private List<Interest> interestList = new ArrayList<>();
    private Picasso picasso;
    private InterestListener interestListener;

    private int width = 200;

    public interface InterestListener {
        public void onCheckChannge(int position, boolean isChecked);

//        public void add(int id);
//        public void remove(int id);
    }

    public void setInterestListener(InterestListener interestListener) {
        this.interestListener = interestListener;
    }

    public InterestAdapter(Context context, List<Interest> interestList, int width) {
        this.context = context;
        this.interestList = interestList;
        this.picasso = Picasso.with(context);
        this.width = width;
    }

    public void refresh(List<Interest> list) {
        this.interestList = new ArrayList<>();
        this.interestList = list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_interest, parent, false);

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width / 2));
        view.setLayoutParams(lp);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Interest interest = interestList.get(position);

        picasso.load(interest.getImage()).into(holder.ivInterest);

        holder.tvInterest.setText(interest.getName());

        if(interest.isChecked()) {
            holder.tvInterest.setBackgroundResource(R.drawable.ic_interest_select_tag);
            holder.myCheckBox.setChecked(true);
        }
        else {
            holder.myCheckBox.setChecked(false);
            holder.tvInterest.setBackgroundResource(R.drawable.ic_interest_tag);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(interestListener!=null) {
                    holder.myCheckBox.setChecked(!holder.myCheckBox.isChecked());
                    interestListener.onCheckChannge(position, holder.myCheckBox.isChecked());
                }

                /*
                if (!holder.myCheckBox.isChecked()) {
                    holder.myCheckBox.setChecked(true);
                    holder.tvInterest.setBackgroundResource(R.drawable.ic_interest_select_tag);
                    if (interestListener != null){
                        interestListener.add(interestList.get(position).getId());
                    }

                } else {
                    holder.myCheckBox.setChecked(false);
                    holder.tvInterest.setBackgroundResource(R.drawable.ic_interest_tag);
                    if (interestListener != null){
                        interestListener.remove(interestList.get(position).getId());
                    }
                }*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return interestList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        protected ImageView ivInterest;
        protected TextView tvInterest;
        protected CheckBox myCheckBox;

        public ViewHolder(View itemView) {
            super(itemView);

            ivInterest = (ImageView) itemView.findViewById(R.id.ivInterest);
            tvInterest = (TextView) itemView.findViewById(R.id.tvInterest);
            myCheckBox = (CheckBox) itemView.findViewById(R.id.myCheckBox);

            setTypeface(tvInterest);
        }
    }

    private void setTypeface(TextView tvInterest){
        MyFont1 myFont = new MyFont1(context);
        myFont.setFont(tvInterest);
    }
}
