package ca.ipd12.quiz.rd.kwizz;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.OneItemViewHolder> {
    ResultAdapter(){
    }
    @NonNull
    @Override
    public OneItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View inflatedView = inflater.inflate(R.layout.vh_result_item,viewGroup,false);
        return new OneItemViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(@NonNull OneItemViewHolder oneItemViewHolder, final int i) {
        Question q = Globals.currentQuestions.get(i);
        int correctAnswerNumber = q.getCorrectAnswer();
        String correctAnswer = q.answers.get(correctAnswerNumber).answer;

        oneItemViewHolder.tvQuestion.setText((i+1+". " + q.question));
        oneItemViewHolder.tvCorrect.setText("Correct answer: " + correctAnswer);
        if(correctAnswerNumber!=q.checkedAnswer) {
            oneItemViewHolder.tvAnswer.setText("Your answer: " + q.answers.get(q.checkedAnswer).answer);
            oneItemViewHolder.ivOk.setImageResource(R.drawable.no);
        }
        else{
            oneItemViewHolder.tvAnswer.setVisibility(View.GONE);
        }

        oneItemViewHolder.singleParentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    class OneItemViewHolder extends RecyclerView.ViewHolder{
        TextView tvQuestion;
        TextView tvAnswer;
        TextView tvCorrect;
        ImageView ivOk;
        FrameLayout singleParentLayout;

        public OneItemViewHolder(View itemView){
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.vh_tv_question);
            tvAnswer = itemView.findViewById(R.id.vh_tv_answered);
            tvCorrect= itemView.findViewById(R.id.vh_tv_correct);
            singleParentLayout = itemView.findViewById(R.id.vh_result_item);
            ivOk = itemView.findViewById((R.id.vh_ok_icon));

        }
    }

}
