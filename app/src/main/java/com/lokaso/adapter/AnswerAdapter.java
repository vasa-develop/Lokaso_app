package com.lokaso.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lokaso.R;
import com.lokaso.model.Answer;
import com.lokaso.util.MyFont1;

import java.util.List;

/**
 * Created by Androcid on 12-Aug-16.
 */
public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.ViewHolder> {

    private String TAG = AnswerAdapter.class.getSimpleName();
    private Context context;
    private List<Answer> answerList;

    public AnswerAdapter(Context context, List<Answer> answerList) {
        this.context = context;
        this.answerList = answerList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_answer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Answer answer = answerList.get(position);
        holder.tvQuestion.setText(answer.getQuestion());
        if (answer.getAnswer_name() == null) {
            holder.tvAnswer.setText("");
        } else {
            holder.tvAnswer.setText(answer.getAnswer_name());
        }
    }

    @Override
    public int getItemCount() {
        return answerList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvQuestion, tvAnswer, tvQuestionHint, tvAnswerHint;

        public ViewHolder(View itemView) {
            super(itemView);

            tvQuestion = (TextView) itemView.findViewById(R.id.tvQuestion);
            tvAnswer = (TextView) itemView.findViewById(R.id.tvAnswer);
            tvQuestionHint = (TextView) itemView.findViewById(R.id.tvQuestionHint);
            tvAnswerHint = (TextView) itemView.findViewById(R.id.tvAnswerHint);

            MyFont1 myFont = new MyFont1(context);
            myFont.setAppFont((ViewGroup) itemView);
        }
    }
}
