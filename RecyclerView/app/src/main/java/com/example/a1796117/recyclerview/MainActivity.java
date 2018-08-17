package com.example.a1796117.recyclerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    UserAdapter useradapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initRecyclerView();
    }

    private void initRecyclerView() {
        String[]strings= {"foo", "bar", "guitar", "khattar", "omar"};

        RecyclerView myRecView = findViewById(R.id.rc_view);
        useradapter = new UserAdapter(strings);


        myRecView.setAdapter(useradapter);
        myRecView.setLayoutManager(new LinearLayoutManager(this));
        updateData();
    }

    public void updateData() {
        String[] strings= {"1", "2", "fgf", "ffd", "ghdhg"};
        useradapter.setNewData(strings);
    }
}
