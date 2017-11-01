package com.lokaso.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.lokaso.R;
import com.lokaso.model.Question;
import com.lokaso.util.MyFont1;

import java.util.List;

/**
 * Created by Androcid on 12-Aug-16.
 */
public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    private String TAG = QuestionAdapter.class.getSimpleName();
    private Context context;
    private List<Question> questionList;

    public QuestionAdapter(Context context, List<Question> questionList) {
        this.context = context;
        this.questionList = questionList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Question question = questionList.get(position);
        holder.tvQuestion.setText(question.getName());

        holder.tvAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                questionList.get(position).setAnswer(s.toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvQuestion;
        protected EditText tvAnswer;

        public ViewHolder(View itemView) {
            super(itemView);

            tvQuestion = (TextView) itemView.findViewById(R.id.tvQuestion);
            tvAnswer = (EditText) itemView.findViewById(R.id.tvAnswer);

            MyFont1 myFont = new MyFont1(context);
            myFont.setAppFont((ViewGroup) itemView);
        }
    }
}
