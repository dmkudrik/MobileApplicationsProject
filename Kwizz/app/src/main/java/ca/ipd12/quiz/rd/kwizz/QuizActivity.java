package ca.ipd12.quiz.rd.kwizz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class QuizActivity extends MenuActivity {

    RadioButton rb;
    RadioGroup rg = findViewById(R.id.rgAnswers);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        addRadioButtons();
    }

    //Add answers for the current question to the layout
    public void addRadioButtons(){
        int numberOfAnswers = 3;
        for(int i=0; i<numberOfAnswers; i++){
            rb  = new RadioButton(this);
            rb.setText("Question " + (i+1));
            rb.setId(i);
            rg.addView(rb);
        }
    }

    public void goBack(View view) {
        //Show previous answer
    }

    public void confirm(View view) {
        //Confirm and go to the next
    }

    public void goForward(View view) {
    }

    public void sendEmail(View view) {
    }

    //User checked a radiobutton
    public void answerChecked(View view) {
    }

    public void resetQuiz(View view) {
    }

    public void newQuiz(View view) {
        //If there are questions answered, ask for confirmation

    }
}
