package com.lokaso.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lokaso.R;
import com.lokaso.model.Credits;

import java.util.List;

/**
 * Created by Androcid on 12-Aug-16.
 */
public class CreditsAdapter extends RecyclerView.Adapter<CreditsAdapter.ViewHolder> {

    private String TAG = CreditsAdapter.class.getSimpleName();
    private Context context;
    private List<Credits> creditsList;

    public CreditsAdapter(Context context, List<Credits> creditsList) {
        this.context = context;
        this.creditsList = creditsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_credits, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Credits credits = creditsList.get(position);
//        holder.tvQuestion.setText(credits.getQuestion());
//        holder.tvAnswer.setText(credits.getAnswer_name());
    }

    @Override
    public int getItemCount() {
        return creditsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvQuestion, tvAnswer;

        public ViewHolder(View itemView) {
            super(itemView);

            tvQuestion = (TextView) itemView.findViewById(R.id.tvQuestion);
            tvAnswer = (TextView) itemView.findViewById(R.id.tvAnswer);
        }
    }
}
