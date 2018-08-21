package ca.ipd12.quiz.rd.kwizz;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import static ca.ipd12.quiz.rd.kwizz.Globals.currentQuizIsDone;
import static ca.ipd12.quiz.rd.kwizz.Globals.isRunning;
import static ca.ipd12.quiz.rd.kwizz.Globals.kwizzTime;

public class QuizActivity extends MenuActivity {
    public static String resultStr = "";
    RadioButton rb;
    RadioGroup rg;
    TextView tv;
    Button bt;

    int rightAnswers = 0;
    int points = 0;
    int weightSum=0;
    int weight=0;
    Question q;

    RadioButton [] rbb = new RadioButton[10]; //array of questions indicators
    Handler mHandler = new Handler();
    ActionBar actionBar;
    Runnable runnable;
    public static volatile boolean afterPause = false; //to manage time counting inside this Activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        setIndicators(); //set rbb - array of questions indicators
        addAnswersListeners();//adding RadioGroup listener
        questionManager();

    }

    //User able to send results on email after test is finished
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(confirmedAnswers==10){
        MenuItem item = menu.findItem(R.id.miEmail);
        item.setEnabled(true);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(confirmedAnswers==10){
            isRunning=false;
            Button btr = findViewById(R.id.btResult);
            btr.setEnabled(true);
        }else{
            isRunning = true; //initial start or come back from another Activity/Inactivity
        }
        //Run time counter
        afterPause=false; //to start again after pause in the current activity
        secCounter();
    }

    public void secCounter(){
        actionBar = getSupportActionBar();
        //Start new thread to calculate time
        runnable = new Runnable() {
            int num3 = 0;
            private Boolean stop = false;
            @Override
            public void run() {
                {
                    if(afterPause || !isRunning) {
                        this.stop=true;
                    }
                    else{
                        actionBar.setTitle("Kwizz : "+ kwizzTime + " sec");
                        kwizzTime++;
                        num3++;
                        Log.i(TAG,num3+" - stopwatch thread");
                        if (Globals.isRunning)
                        {
                            mHandler.postDelayed(this, 1000);
                        }else{
                            this.stop=true;
                        }
                    }
                }
            }
        };
        mHandler.post(runnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //pause stopwatch
        afterPause=true;
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
    private void addAnswersListeners() {
        // Aanswers-radiobuttons listener
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
    public void addQuestion(int currQN){ //currQN - current Question Number

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
                currentQuizIsDone=true;
                isRunning=false;
                showResults();
                Toast.makeText(QuizActivity.this, "Quiz is done!", Toast.LENGTH_SHORT).show();
                //saveResults to DB
                addToHistory();
                resultStr = "Correct answers: "+ rightAnswers + "/10, " + "Points: " + points +", Done: " +   Globals.kwizzTime + " seconds";

                Button btr = findViewById(R.id.btResult);
                btr.setEnabled(true);
                //openResultDetails();
            }else{
                //show the next question
                goForward(view);
            }
        }
    }
    private void openResultDetails(){
        Intent myIntent = new Intent(QuizActivity.this, ResultActivity.class);
        myIntent.putExtra("points", points); //Optional parameters
        myIntent.putExtra("rightAnswers", rightAnswers); //Optional parameters
        QuizActivity.this.startActivity(myIntent);
    }

    //Saves a result to the DB
    private void addToHistory() {
        //Save question into db - transaction
        MyDbHelper dbHelper = new MyDbHelper(this, "kwizzdb", null, Globals.VER);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Calculating ranking
        calculateRanking();

        ContentValues values = new ContentValues();
        values.put("email", Globals.userEmail);
        values.put("points", points);
        values.put("seconds", kwizzTime);
        values.put("correct", rightAnswers);
        long rowId = db.insert("history", null, values);
    }

    private void calculateRanking() {
        rightAnswers=0;
        weight=0;weightSum=0;
        for(int i = 0;i<10;i++ ){
            q = currentQuestions.get(i);
            weightSum+=q.answers.size();
            if(q.checkedAnswer==q.getCorrectAnswer()) {
                rightAnswers++;
                weight+=q.answers.size();
            }
        }

        double timeKoefficient = 30.0/kwizzTime;
        if(timeKoefficient>3){
            timeKoefficient = 1.2;
        }
        else if(timeKoefficient>=1){
            timeKoefficient=1.2*(timeKoefficient/3);
        }else{
            timeKoefficient = 1-(timeKoefficient*0.2);
        }
        double result = 1000*((weight*1.0)/weightSum)*timeKoefficient;
        points = Integer.parseInt(String.format("%.0f", result));
    }

    //Set questions indicators by quiz results
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
        //Confirmation dialog listener
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Resume time counting and start new quiz
                        afterPause=false;
                        secCounter();
                        startNewQuiz();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //Resume time counting and continue current quiz
                        afterPause=false;
                        secCounter();
                        break;
                }
            }
        };

        if(confirmedAnswers>0 && confirmedAnswers<9 ){
            //Stop time counting
            afterPause=true;
            //Show confirmation dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                    .setTitle("Start a new quiz?")
                    .setMessage("You already have have answered question(s) in the current quiz.")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener)
                    .show();
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
        if(confirmedAnswers==10) {
            isRunning=true;
            secCounter();
        }
        currentQuestionNumber=0;
        confirmedAnswers=0;
        kwizzTime=0;
        Button btr = findViewById(R.id.btResult);
        btr.setEnabled(false);
        addQuestion(Globals.currentQuestionNumber); // 1 - show number + text + answers for the first question
    }


    public void openResult(View view) {
        calculateRanking();
        openResultDetails();
    }
}
