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

import java.util.ArrayList;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.OneItemViewHolder> {
    ArrayList<HistoryItem> historyItems = new ArrayList<>();
    HistoryAdapter(ArrayList<HistoryItem> hItems){
        historyItems = hItems;
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
        oneItemViewHolder.tvUser.setText(historyItems.get(i).email);
        oneItemViewHolder.tvResult.setText(historyItems.get(i).getResult());
        oneItemViewHolder.tvTime.setText(historyItems.get(i).result+"");
        double r= historyItems.get(i).result;
        if(r>950) oneItemViewHolder.ivStar.setImageResource(R.drawable.star10);
        else if(r>850) oneItemViewHolder.ivStar.setImageResource(R.drawable.star9);
        else if(r>750) oneItemViewHolder.ivStar.setImageResource(R.drawable.star8);
        else if(r>650) oneItemViewHolder.ivStar.setImageResource(R.drawable.star7);
        else if(r>550) oneItemViewHolder.ivStar.setImageResource(R.drawable.star6);
        else if(r>450) oneItemViewHolder.ivStar.setImageResource(R.drawable.star5);
        else if(r>350) oneItemViewHolder.ivStar.setImageResource(R.drawable.star4);
        else if(r>250) oneItemViewHolder.ivStar.setImageResource(R.drawable.star3);
        else if(r>150) oneItemViewHolder.ivStar.setImageResource(R.drawable.star2);
        else if(r>50) oneItemViewHolder.ivStar.setImageResource(R.drawable.star1);
        else oneItemViewHolder.ivStar.setImageResource(R.drawable.star0);



        oneItemViewHolder.singleParentLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
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
