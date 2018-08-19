package ca.ipd12.quiz.rd.kwizz;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
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
import static ca.ipd12.quiz.rd.kwizz.Globals.confirmedAnswers;
import static ca.ipd12.quiz.rd.kwizz.Globals.currentQuestionNumber;
import static ca.ipd12.quiz.rd.kwizz.Globals.currentQuestions;

public class QuizActivity extends MenuActivity {

    RadioButton rb;
    RadioGroup rg;
    TextView tv;
    Button bt;
    boolean isDone = false;
    RadioButton [] rbb = new RadioButton[10]; //array of questions indicators


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        setIndicators(); //set rbb - array of questions indicators
        addAnswersListener();//adding RadioGroup listener
        questionManager();
    }


    //Formatting 10 small question indicators
    private void setIndicators() {
        //
        LinearLayout ll = findViewById(R.id.indicators);
        for (int i =0; i<10;i++ ){
            rbb[i]= (RadioButton) ll.getChildAt(i);
        }
        //if there are confirmed questions -> set indicators
        if(confirmedAnswers == 10){
            showResults();
        }
        else if(confirmedAnswers>0){
            for (int i =0; i<10;i++ ){
                if(currentQuestions.get(i).isConfirmed)
                    rbb[i].setChecked(true);
            }
        }
    }

    //user checked a answer-radiobutton - event listener
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

    //Shows a question OR Load and Show in case quiz just started
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

    //fetches a set of questions from an external source
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
        int currentQuestionId = -1; //current question id
        while ( !cursor.isAfterLast()) {
            if(cursor.getInt(0)==currentQuestionId){
                a= new Answer();
                a.answer = cursor.getString(2);
                a.isCorrect = cursor.getInt(3) == 1 ? true : false ;
                q.answers.add(a);
            }else {
                if (currentQuestionId!=-1) qq.add(q);
                q = new Question();
                q.answers=new ArrayList<>();
                currentQuestionId = cursor.getInt(0);
                q.question = cursor.getString(1);
                a = new Answer();
                a.answer = cursor.getString(2);
                a.isCorrect = cursor.getInt(3) == 1 ? true : false ;
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

        //generate radio buttons and show answers
        rg = findViewById(R.id.rgAnswers);
        rg.removeAllViews();
        Question q = currentQuestions.get(currQN);
        int numberOfAnswers = q.answers.size();
        int ca=-1; //if question was checked set to 'ca' the checked number
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
        //set background for active indicator (current question)
        if(confirmedAnswers==10) showResults();
        else rbb[currentQuestionNumber].setBackgroundColor(getResources().getColor(R.color.colorCurrent));
    }

    //Go to previous question
    public void goBack(View view) {
        //Show previous answer
        if(currentQuestionNumber>0){
            rbb[currentQuestionNumber].setBackgroundColor(Color.TRANSPARENT);
            currentQuestionNumber--;
        }
        addQuestion(currentQuestionNumber);
    }

    //Confirm answer
    public void confirm(View view) {
        //Confirm and go to the next
        Question cq = currentQuestions.get(currentQuestionNumber);
        if(cq.isChecked){
            cq.isConfirmed = true;
            bt = findViewById(R.id.btConfirm);
            bt.setEnabled(false);
            confirmedAnswers++;
            rbb[currentQuestionNumber].setChecked(true);
            if (confirmedAnswers==10){
                //show the results
                showResults();
                Toast.makeText(QuizActivity.this, "Quiz is done!", Toast.LENGTH_SHORT).show();
                //saveResults to DB
                addToHistory();
                openResultDetails();
            }else{
                //show the next question
                goForward(view);
            }

        }
    }

    private void addToHistory() {
        //Save question into db - transaction
        MyDbHelper dbHelper = new MyDbHelper(this, "kwizzdb", null, Globals.VER);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("email", "john@mail.ca");
        values.put("result", "737");
        long rowId = db.insert("history", null, values);

        Log.i(TAG, "Result was added to the History");
    }

    private void showResults() {
        for(int i = 0; i<10;i++){
            Question q = currentQuestions.get(i);
            if(q.checkedAnswer==q.getCorrectAnswer()){
                //set indicator to green
                rbb[i].setChecked(true);
                rbb[i].setBackgroundColor(Color.GREEN);
            }else{
                //set indicator to red
                rbb[i].setChecked(true);
                rbb[i].setBackgroundColor(Color.RED);
            }
        }
    }

    //Go to the next question
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

    //If there are confirmed answers, ask for confirmation
    public void newQuiz(View view) {
        if(confirmedAnswers>0 && confirmedAnswers<9 ){
            //Show confirmation dialog
            new AlertDialog.Builder(this)
                    .setTitle("Start a new quiz?")
                    .setMessage("Press 'Ok' to begin a new quiz or 'Cancel' to continue.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            startNewQuiz(); //confirmed!
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
            return; // Continue current quiz
        }else{
            startNewQuiz();
        }
    }

    //Reset current results, shuffle questions, and begin a new one
    public void startNewQuiz(){
        for(int i = 0; i<10;i++){
            rbb[i].setChecked(false);
            rbb[i].setBackgroundColor(Color.TRANSPARENT);
        }
        //changes to Globals
        setCurrentQuestions(); //get new 10 random questions
        currentQuestionNumber=0;
        confirmedAnswers=0;
        addQuestion(Globals.currentQuestionNumber); // 1 - show number + text + answers for the first question
    }

    private void openResultDetails(){
        Intent myIntent = new Intent(QuizActivity.this, ResultActivity.class);
//      myIntent.putExtra("key", value); //Optional parameters
        QuizActivity.this.startActivity(myIntent);
    }

}
