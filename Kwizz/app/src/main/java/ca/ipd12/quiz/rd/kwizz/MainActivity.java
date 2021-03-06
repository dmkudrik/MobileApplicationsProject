package ca.ipd12.quiz.rd.kwizz;


import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import org.jsoup.Jsoup;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static ca.ipd12.quiz.rd.kwizz.Globals.TAG;
import static ca.ipd12.quiz.rd.kwizz.Globals.VER;
import static ca.ipd12.quiz.rd.kwizz.Globals.allQuestions;
import static ca.ipd12.quiz.rd.kwizz.Globals.NOQ;
import static ca.ipd12.quiz.rd.kwizz.Globals.confirmedAnswers;
import static ca.ipd12.quiz.rd.kwizz.Globals.isLoggedIn;
import static ca.ipd12.quiz.rd.kwizz.Globals.currentQuestionNumber;
import static ca.ipd12.quiz.rd.kwizz.Globals.userEmail;

public class MainActivity extends MenuActivity {
    Question q;
    Answer a;
    int numberOfQuestions = 15;
    boolean correctIsDefined;
    int minAnswers = 2;
    int maxAnswers = 5;
    ArrayList<Answer> answers;
    Answer aIncorr;

    EditText initialET;
   public  static String initialEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Stetho.initializeWithDefaults(this); // Stetho

        Globals.isRunning=false;

        if(!isLoggedIn) {
            fetcher();


            //setting the visibility of buttons to 'gone' if not logged in
            View btGo = findViewById(R.id.btGo);
            btGo.setVisibility(View.GONE);

            View btScores = findViewById(R.id.btScores);
            btScores.setVisibility(View.GONE);

            // setting menu item logout to invisible
            //View miLogout = findViewById(R.id.miLogout);
            //miLogout.setEnabled(false);
            View btResume = findViewById(R.id.btResume);
            btResume.setVisibility(View.GONE);
        }

        //quizGenerator();
        //fetcher();


    }

    @Override
    protected void onStop() {//saving email to variable
        super.onStop();
        initialET= findViewById(R.id.tbEmail);
        initialEmail= initialET.getText().toString();


    }



    public void getData(){

        URL apiURL;
        try{
            apiURL= new URL("https://opentdb.com/api.php?amount="+NOQ);
            new FetchDataFromApi().execute(apiURL);
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }


    }
    public  class FetchDataFromApi extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String response="";
            URL myURL =urls[0];
            try {
                response =  NetworkUtility.getResponseFromHttpUrl(myURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
        @Override
        protected  void  onPostExecute (String s){
            // super.onPostExecute(s);


           getDataFromJSON(s);
            quizGenerator();
            //fetcher();

        }
        //this method converts JSON to strings and puts data to our objects
        private void getDataFromJSON(String s) {
            //string titles in the JSON
            final   String RESULTS = "results";
            final   String QUESTION = "question";
            final   String CORR_ANSWER = "correct_answer";
            final   String INCORR_ANSWER = "incorrect_answers";


            String question;
            String corAnswer;
            String incorAnswer;

            JSONObject Json = null;

            try {
                Json = new JSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray resultsArray=null;
            try {
                resultsArray = Json.getJSONArray(RESULTS);
            } catch (JSONException e) {
                e.printStackTrace();
            }



            JSONObject Json2=null;
            Globals.allQuestions = new ArrayList<>();
            for(int i = 0; i < resultsArray.length(); i++) {

                q = new Question();
                a = new Answer();
                answers = new ArrayList<>();
               // aIncorr= new Answer();
                try {
                    Json2 = resultsArray.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONArray resultsArray2;
                try {
                    question = Jsoup.parse(Json2.getString(QUESTION)).text();//using Jsoup library to decode special characters
                    q.question = question;//adding data to question object


                    corAnswer= Jsoup.parse(Json2.getString(CORR_ANSWER)).text();//using Jsoup library to decode special characters

                    a.answer=corAnswer;//adding data to answer object
                    a.isCorrect=true;


                    answers.add(a);//adding answer to answers list
                    resultsArray2 = Json2.getJSONArray(INCORR_ANSWER);

                    for(int j = 0; j < resultsArray2.length(); j++) {
                           aIncorr=new Answer();
                        incorAnswer = Jsoup.parse(resultsArray2.get(j).toString()).text();//using Jsoup library to decode special characters

                        aIncorr.answer = incorAnswer;
                        answers.add(aIncorr);//adding answer to answers list

                    }
                    q.answers=answers;
                    allQuestions.add(q);//adding question to the list

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }


    }

    //method is used to Generate random questions and answers on the development stage


    public void quizGenerator() {
      //  readFromFile();


        //generating numberOfQuestions questions
        for (int i = 0; i < allQuestions.size(); i++) {
//            q = new Question();
//            q.question = "Lorem Ipsum"+(i+1) +" Question?";
////            Log.i(TAG, "Q"+ (i+1)+": "+q.question);
//
//            //2 to 5 - number of answers generator
//            int numberOfAnswers = ThreadLocalRandom.current().nextInt(minAnswers, maxAnswers + 1);
//
//            //correct answer definer
//            int correctAnswer = ThreadLocalRandom.current().nextInt(1, numberOfAnswers + 1);
//
//            answers = new ArrayList<>();
//            correctIsDefined = false;
//            for (int k = 0; k < numberOfAnswers; k++) {
//                a = new Answer();
//                a.answer = "Answer " + (k+1);
//                if (k == (correctAnswer - 1)) a.isCorrect = true;
////                Log.i(TAG, "A"+ (k+1)+": "+a.answer + (a.isCorrect ? " - Correct one!": ""));
//                answers.add(a);
//            }

            //Save question into db - transaction
            MyDbHelper dbHelper = new MyDbHelper(this, "kwizzdb", null, Globals.VER);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("question", allQuestions.get(i).question);
            long rowId = db.insert("questions", null, values);
            Log.i(TAG, "Row number of added question is " + rowId);

            for (int j = 0; j < allQuestions.get(i).answers.size(); j++) {

                ContentValues values2 = new ContentValues();
                values2.put("qid", rowId);
                values2.put("answer", (allQuestions.get(i).answers.get(j).answer));
                values2.put("iscorrect", (allQuestions.get(i).answers.get(j).isCorrect));
                long rowId2 = db.insert("answers", null, values2);
                Log.i(TAG, "Row number of added answer is " + rowId2);
            }

        }
    }


    public void fetcher() {
        MyDbHelper dbHelper = new MyDbHelper(this, "kwizzdb", null, VER);
        SQLiteDatabase db = dbHelper.getReadableDatabase();


//        if your SQL query is this
//        SELECT name,roll FROM student WHERE name='Amit' AND roll='7'
//
//        then rawQuery will be
//
//        String query="SELECT id, name FROM people WHERE name = ? AND roll = ?";
//        String[] selectionArgs = {"Amit","7"}
//        db.rawQuery(query, selectionArgs);

        final String MY_QUERY = "SELECT a.answer, a.iscorrect FROM questions q INNER JOIN answers a ON q.id=a.qid"; //WHERE b.property_id=?";
//        db.rawQuery(MY_QUERY, new String[]{String.valueOf(propertyId)});
        Cursor cursor = db.rawQuery(MY_QUERY, new String[]{});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Log.i(TAG, cursor.getString(0) + " - " + cursor.getString(1));
            cursor.moveToNext();
        }


    }


    public void openQuiz(View view) {
        Globals.isRunning=true;
        Intent myIntent = new Intent(MainActivity.this, QuizActivity.class);
//        myIntent.putExtra("key", value); //Optional parameters
        MainActivity.this.startActivity(myIntent);
    }

    public void showScores(View view) {
        Intent myIntent = new Intent(MainActivity.this, HistoryActivity.class);
        MainActivity.this.startActivity(myIntent);
    }
    //the onClick listener for Login button
    public void login(View view) {

        EditText emailField =  findViewById(R.id.tbEmail);
        View btGo = findViewById(R.id.btGo);
        View btScores = findViewById(R.id.btScores);
        View btResume = findViewById(R.id.btResume);
        if(!isLoggedIn) {//if not logged in

            Globals.userEmail = emailField.getText().toString();
            if (Globals.userEmail.isEmpty()) {
                Toast.makeText(MainActivity.this,
                        "Please enter email.", Toast.LENGTH_SHORT).show();
                return;
            }
            isLoggedIn = true;
            //when logged in setting the visibility of the buttons to true

            btGo.setVisibility(View.VISIBLE);


            btScores.setVisibility(View.VISIBLE);


            btResume.setVisibility(View.VISIBLE);
            btResume.setEnabled(false);
            //turning login button to logout
            Button btLogin = findViewById(R.id.btLogin);
            btLogin.setText("Log Out");

            emailField.setText(userEmail);
            emailField.setEnabled(false);

            // setting menu item logout to visible
           // View miLogout = findViewById(R.id.miLogout);
            //miLogout.setEnabled(true);
        }else {
            isLoggedIn = false;

            btGo.setVisibility(View.GONE);


            btScores.setVisibility(View.GONE);


            btResume.setVisibility(View.GONE);

            emailField.setText("");
            emailField.setEnabled(true);

            Button btLogin = findViewById(R.id.btLogin);
            btLogin.setText("Log In");
        }

    }
    @Override
    protected void onResume() {
        super.onResume();



        EditText emailField = findViewById(R.id.tbEmail);
        if(isLoggedIn) {

            //when logged in setting the visibility of the buttons to true
            View btGo = findViewById(R.id.btGo);
            View btResume = findViewById(R.id.btResume);
            View btScores = findViewById(R.id.btScores);


            btGo.setVisibility(View.VISIBLE);
            btResume.setVisibility(View.VISIBLE);

            btScores.setVisibility(View.VISIBLE);


            //turning login button to logout
            Button btLogin = findViewById(R.id.btLogin);
            btLogin.setText("Log Out");

            emailField.setText(userEmail);
            emailField.setEnabled(false);




            //if else for resume and start new buttons

            if(currentQuestionNumber==-1){//if the quiz has not been initiated

                btResume.setEnabled(false);

                btGo.setEnabled(true);
            }
            else{

                btResume.setEnabled(true);

                btGo.setEnabled(true);
            }

        }else {
            initialET= findViewById(R.id.tbEmail);//to place email if the screen position changed
            initialET.setText(initialEmail);

            emailField.setText(initialEmail);//log out
            emailField.setEnabled(true);

            View btGo = findViewById(R.id.btGo);
            btGo.setVisibility(View.GONE);

            View btScores = findViewById(R.id.btScores);
            btScores.setVisibility(View.GONE);

            View btResume = findViewById(R.id.btResume);
            btResume.setVisibility(View.GONE);

            Button btLogin = findViewById(R.id.btLogin);
            btLogin.setText("Log In");
        }
    }


    public void newKwizz(View view){
        //if there are already answered questions - confirmation dialog
        //Confirmation dialog listener
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Resume time counting and start new quiz
                        createNewQuiz();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        if(confirmedAnswers>0 && confirmedAnswers<9 ){
            //Stop time counting
            QuizActivity.afterPause=true;
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
            createNewQuiz();
        }
    }

}
