package ca.ipd12.quiz.rd.kwizz;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import static ca.ipd12.quiz.rd.kwizz.Globals.TAG;
import static ca.ipd12.quiz.rd.kwizz.Globals.VER;
import static ca.ipd12.quiz.rd.kwizz.Globals.currentQuestionNumber;
import static ca.ipd12.quiz.rd.kwizz.Globals.currentQuestions;

public class QuizActivity extends MenuActivity {

    RadioButton rb;
    RadioGroup rg;
    TextView tv;
    Button bt;
    RadioButton [] rbb = new RadioButton[10]; //array of questions indicators

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        setIndicators(); //set rbb - array of questions indicators
        addAnswersListener();//adding RadioGroup listener
        questionManager();
    }

    private void setIndicators() {
        LinearLayout ll = findViewById(R.id.indicators);
            for (int i =0; i<10;i++ ){
                rbb[i]= (RadioButton) ll.getChildAt(i);
            }
    }

    //user checked a radiobutton
    private void addAnswersListener() {
        rg = (RadioGroup) findViewById(R.id.rgAnswers);
        rg.clearCheck();
        rg.setOnCheckedChangeListener(new  RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && (checkedId != -1)) {
                    Globals.currentQuestions.get(currentQuestionNumber).checkedAnswer=checkedId;
                    Globals.currentQuestions.get(currentQuestionNumber).isChecked = true;
                }
            }
        });
    }

    protected void questionManager(){
        if(currentQuestionNumber==-1) {
            Globals.allQuestions = getAllQuestions(); //load questions from SQLite
            setCurrentQuestions(); //get 10 random questions
            currentQuestionNumber=0;
        }
        addQuestion(Globals.currentQuestionNumber); // 1 - show number + text + answers for the first question
    }

    private void setCurrentQuestions() {
        //shuffle questions
        Collections.shuffle(Globals.allQuestions);

        //set 10 first from shuffled as Current Set Of Questions
        Globals.currentQuestions = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            Globals.currentQuestions.add(Globals.allQuestions.get(i));

        }
        //Clear previous answers
        for(Question sq : currentQuestions){
            sq.checkedAnswer=777;
            sq.isChecked=false;
            sq.isConfirmed=false;

        }
    }

    private ArrayList<Question> getAllQuestions() {
        MyDbHelper dbHelper = new MyDbHelper(this, "kwizzdb", null, VER);
        SQLiteDatabase db  = dbHelper.getReadableDatabase();
        String MY_QUERY = "SELECT q.id, q.question, a.answer, a.iscorrect FROM questions q INNER JOIN answers a ON q.id=a.qid";
        Cursor cursor = db.rawQuery(MY_QUERY, new String[]{});
        cursor.moveToFirst();
        ArrayList<Question> qq = new ArrayList<>();
        Question q = new Question();
        q.answers = new ArrayList<>();
        Answer a;
        int currQ = -1; //current question id
        while ( !cursor.isAfterLast()) {
            if(cursor.getInt(0)==currQ){
                a= new Answer();
                a.answer = cursor.getString(2);
                a.isCorrect = Boolean.parseBoolean( cursor.getString(3) );
                q.answers.add(a);
            }else {
                if (currQ!=-1) qq.add(q);
                q = new Question();
                q.answers=new ArrayList<>();
                currQ = cursor.getInt(0);
                q.question = cursor.getString(1);
                a = new Answer();
                a.answer = cursor.getString(2);
                a.isCorrect = Boolean.parseBoolean( cursor.getString(3) );
                q.answers.add(a);
                //Log.i(TAG, cursor.getString(0) + " - " + cursor.getString(1));
            }

            cursor.moveToNext();
        }
        qq.add(q);
        return qq;
    }

    //Add answers for the current question to the layout
    public void addQuestion(int currQN){ //current Question Number

        //show enumeration
        tv = findViewById(R.id.tvEnum);
        tv.setText("Question " + (currQN + 1) + " of 10!");

        //show question
        tv = findViewById(R.id.tvQuestion);
        tv.setText(currentQuestions.get(currQN).question);

        //show answers
        rg = findViewById(R.id.rgAnswers);
        rg.removeAllViews();
        Question q = currentQuestions.get(currQN);
        int numberOfAnswers = q.answers.size();

        //if question was checked set to 'ca' the checked number
        int ca=-1;
        if(Globals.currentQuestions.get(currentQuestionNumber).isChecked){
            ca= Globals.currentQuestions.get(currentQuestionNumber).checkedAnswer;
        }
        rg.clearCheck();
        for(int i=0; i<numberOfAnswers; i++){

            rb  = new RadioButton(this);
            rb.setText(q.answers.get(i).answer);
            rb.setId(i);
            if(ca==i)rb.setChecked(true); // in case question was already checked
            rg.addView(rb);
        }

        //deactivate Confirm button if it is already confirmed
        bt = findViewById(R.id.btConfirm);
        if(currentQuestions.get(currentQuestionNumber).isConfirmed){
            bt.setEnabled(false);
        }else{
            bt.setEnabled(true);
        }
        //set background for indicator
        rbb[currentQuestionNumber].setBackgroundColor(getResources().getColor(R.color.colorCurrent));
    }

    public void goBack(View view) {
        //Show previous answer
        if(currentQuestionNumber>0){
            rbb[currentQuestionNumber].setBackgroundColor(Color.TRANSPARENT);
            currentQuestionNumber--;
        }
        addQuestion(currentQuestionNumber);
    }

    public void confirm(View view) {
        //Confirm and go to the next
        Question cq = currentQuestions.get(currentQuestionNumber);
        if(cq.isChecked){
            cq.isConfirmed = true;
            bt = findViewById(R.id.btConfirm);
            bt.setEnabled(false);
            rbb[currentQuestionNumber].setChecked(true);
            goForward(view);
        }
    }

    public void goForward(View view) {
        if(currentQuestionNumber<9){
            rbb[currentQuestionNumber].setBackgroundColor(Color.TRANSPARENT);
            currentQuestionNumber++;
        }
        addQuestion(currentQuestionNumber);
    }

    public void sendEmail(View view) {
    }

    public void resetQuiz(View view) {
    }

    public void newQuiz(View view) {
        //If there are questions answered, ask for confirmation
        //TO DO
        int answered = 0;
        for(int i = 0; i<10;i++){
            if(currentQuestions.get(i).isConfirmed) answered++;
        }
        if(answered>0 && answered<9 ){
            new AlertDialog.Builder(this)
                    .setTitle("New quiz confirmation")
                    .setMessage("You have answered questions !!! Press 'Ok' to leave current quiz or 'Cancel' to continue.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {

                            Toast.makeText(QuizActivity.this, "Yaay", Toast.LENGTH_SHORT).show();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
            return; // ASK user for confirmation
        }

        for(int i = 0; i<10;i++){
            rbb[i].setChecked(false);
            rbb[i].setBackgroundColor(Color.TRANSPARENT);
        }
        //changes to Globals
        setCurrentQuestions(); //get new 10 random questions
        currentQuestionNumber=0;
        addQuestion(Globals.currentQuestionNumber); // 1 - show number + text + answers for the first question
    }


}
