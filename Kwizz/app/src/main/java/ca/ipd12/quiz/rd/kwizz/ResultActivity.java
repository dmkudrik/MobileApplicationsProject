package ca.ipd12.quiz.rd.kwizz;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends MenuActivity {
    ResultAdapter resultAdapter;
    TextView tv;
    String resultString="";
    String messageBody="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Globals.isRunning=false;//stop time counter

        tv = findViewById(R.id.tv_result);
        Bundle extras = getIntent().getExtras();
        int points = extras.getInt("points");
        int rightAnswers = extras.getInt("rightAnswers");
        resultString = "Correct answers: "+ rightAnswers + "/10, " + "Points: " + points +", \nDone: " +   Globals.kwizzTime + " seconds";
        tv.setText(resultString);
        initRecyclerView();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.miEmail);
            item.setEnabled(true);
            return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initRecyclerView() {

        RecyclerView myRecView = findViewById(R.id.rc_view_result);
        resultAdapter = new ResultAdapter();
        myRecView.setAdapter(resultAdapter);
        myRecView.setLayoutManager(new LinearLayoutManager(this));
    }

    //Creating Email
    private void mailSender(){
        messageBody+="Hello!!!<br/><br/>Here are result of your quiz, made in KWIZZ mobile application:<br/><h3>";
        messageBody+=resultString;
        messageBody+="</h3><br/>Details:<br/><br/>";
        for(int i = 0;i<10;i++){
            Question q = Globals.currentQuestions.get(i);
            int correctAnswerNumber = q.getCorrectAnswer();
            String correctAnswer = q.answers.get(correctAnswerNumber).answer;

            messageBody+=(i+1) + ". " + q.question + "<br/>";
            messageBody+="<span style=\"color:Green;\">Correct answer: " + correctAnswer+ "</span><br/>";
            if(correctAnswerNumber!=q.checkedAnswer) {
                messageBody+="<span style=\"color:Red;\">Your answer: " + q.answers.get(q.checkedAnswer).answer+"</span><br/>";
            }
            messageBody+="<br/>";
        }
        messageBody+="Thank you for using KWIZZ!<br/><br/>";
        messageBody+="Best regards,<br/><br/>KWIZZ team";

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender("ogouman@gmail.com",
                            "transcend");
                    sender.sendMail("Your KWIZZ result!", messageBody,
                            "ogouman@gmail.com", Globals.userEmail);
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
            }

        }).start();
    }

    public void sendEmail(View view) {
        mailSender();
        Toast.makeText(ResultActivity.this,
                "Quiz result was sent on your email", Toast.LENGTH_LONG).show();
    }
}
