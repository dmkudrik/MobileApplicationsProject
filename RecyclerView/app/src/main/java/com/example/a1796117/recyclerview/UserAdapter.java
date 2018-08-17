package com.example.a1796117.recyclerview;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.OneItemViewHolder> {
    private static final String TAG = "UserAdapter";
    String [] strings ;
    UserAdapter(String[]newStrings){
        strings=newStrings;
    }
    @NonNull
    @Override
    public OneItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d(TAG, "onCreateViewHolder: ");
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View inflatedView = inflater.inflate(R.layout.vh_item,viewGroup,false);
        return new OneItemViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(@NonNull OneItemViewHolder oneItemViewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder: ");
        //oneItemViewHolder.tvOneItem.setText(Integer.toString(i));
        oneItemViewHolder.tvOneItem.setText(strings[i]);
        oneItemViewHolder.singleParentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("RecycleView", "Clicked card number: " + Integer.toString(i));
            }
        });
    }

    @Override
    public int getItemCount() {
        return strings.length;
    }

    public void setNewData(String[] newStrings) {
        strings=newStrings;
    }
    class OneItemViewHolder extends RecyclerView.ViewHolder{
        TextView tvOneItem;
        FrameLayout singleParentLayout;

        public OneItemViewHolder(View itemView){
            super(itemView);
            tvOneItem = itemView.findViewById(R.id.vh_tv);
            singleParentLayout = itemView.findViewById(R.id.vh_irem_parent_layout);

        }
    }

}
