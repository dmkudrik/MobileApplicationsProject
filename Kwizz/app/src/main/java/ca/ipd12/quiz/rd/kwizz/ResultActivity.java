package ca.ipd12.quiz.rd.kwizz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

public class ResultActivity extends MenuActivity {
    ResultAdapter resultAdapter;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Globals.isRunning=false;//stop time counter

        tv = findViewById(R.id.tv_result);
        Bundle extras = getIntent().getExtras();
        int points = extras.getInt("points");
        int rightAnswers = extras.getInt("rightAnswers");

        tv.setText("Correct answers: "+ rightAnswers + "/10, " + "Points: " + points +", Done: " +   Globals.kwizzTime + " seconds");
        initRecyclerView();
    }

    private void initRecyclerView() {

        RecyclerView myRecView = findViewById(R.id.rc_view_result);
        resultAdapter = new ResultAdapter();
        myRecView.setAdapter(resultAdapter);
        myRecView.setLayoutManager(new LinearLayoutManager(this));


    }
}
