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

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.OneItemViewHolder> {
    HistoryAdapter(){

    }
    @NonNull
    @Override
    public OneItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View inflatedView = inflater.inflate(R.layout.vh_history_item,viewGroup,false);
        return new OneItemViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(@NonNull OneItemViewHolder oneItemViewHolder, final int i) {
        oneItemViewHolder.tvUser.setText("USER NAME");
        oneItemViewHolder.tvResult.setText("SCORE RESULT");
        oneItemViewHolder.tvTime.setText("TIME RESULT");
        oneItemViewHolder.ivStar.setImageResource(R.drawable.star);


        oneItemViewHolder.singleParentLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return 10;
    }

//    public void setNewData(String[] newStrings) {
//    }
    class OneItemViewHolder extends RecyclerView.ViewHolder{
        TextView tvUser;
        TextView tvResult;
        TextView tvTime;
        ImageView ivStar;
        FrameLayout singleParentLayout2;

        public OneItemViewHolder(View itemView){
            super(itemView);
            tvUser = itemView.findViewById(R.id.vhh_user);
            tvResult = itemView.findViewById(R.id.vhh_result);
            tvTime= itemView.findViewById(R.id.vhh_time);
            singleParentLayout2 = itemView.findViewById(R.id.vhh_history_item);
            ivStar = itemView.findViewById((R.id.vhh_star));

        }
    }

}
