package ca.ipd12.quiz.rd.kwizz;

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

public class MainActivity extends MenuActivity {
    Question q;
    Answer a;
    int numberOfQuestions = 15;
    boolean correctIsDefined;
    int minAnswers = 2;
    int maxAnswers = 5;
    ArrayList<Answer> answers;
    Answer aIncorr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Stetho.initializeWithDefaults(this); // Stetho

        //quizGenerator();
        //fetcher();
    }
    public void getData(View view){

        URL apiURL;
        try{
            apiURL= new URL("https://opentdb.com/api.php?amount="+Globals.VER);
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
           // TextView tv = findViewById(R.id.tvResult);

           getDataFromJSON(s);
            quizGenerator();
            fetcher();
            //tv.setText(question);
        }
        private void getDataFromJSON(String s) {
            final   String RESULTS = "results";
            final   String QUESTION = "question";
            final   String CORR_ANSWER = "correct_answer";
            final   String INCORR_ANSWER = "incorrect_answers";




            String question="";
            String corAnswer ="";
            String incorAnswer="";

            JSONObject Json = null;
            JSONObject Json2 = null;
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



            JSONObject JSON=null;
            Globals.allQuestions = new ArrayList<>();
            for(int i = 0; i < resultsArray.length(); i++) {

                q = new Question();
                a = new Answer();
                answers = new ArrayList<>();
               // aIncorr= new Answer();
                try {
                    JSON = resultsArray.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                JSONArray resultsArray2;
                try {
                    question = Jsoup.parse(JSON.getString(QUESTION)).text();////////////////Jsoup library
                    q.question = question;


                    corAnswer= Jsoup.parse(JSON.getString(CORR_ANSWER)).text();////////////////Jsoup library
                    System.out.println(corAnswer);
                    a.answer=corAnswer;
                    a.isCorrect=true;


                    answers.add(a);
                    resultsArray2 = JSON.getJSONArray(INCORR_ANSWER);

                    for(int j = 0; j < resultsArray2.length(); j++) {
                           //aIncorr=new Answer();
                        incorAnswer = Jsoup.parse(resultsArray2.get(j).toString()).text();////////////////Jsoup library

                        System.out.println(incorAnswer);

                        a.answer = incorAnswer;
                        answers.add(a);

                    }
                    q.answers=answers;
                    allQuestions.add(q);
            System.out.println(q.question.toString()+allQuestions.size());
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
        for (int i = 0; i < allQuestions.size(); i++) {System.out.println(allQuestions.size()+"    Hereeeeeeeeeeeeeeeeeeeeeeeeee"+allQuestions.get(i).question.toString());
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

            for (int ans = 0; ans < allQuestions.get(i).answers.size(); ans++) {
                ContentValues values2 = new ContentValues();
                values2.put("qid", rowId);
                values2.put("answer", (allQuestions.get(i).answers.get(ans).answer));
                values2.put("iscorrect", (allQuestions.get(i).answers.get(ans).isCorrect));
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
        Intent myIntent = new Intent(MainActivity.this, QuizActivity.class);
//        myIntent.putExtra("key", value); //Optional parameters
        MainActivity.this.startActivity(myIntent);
    }

    public void showScores(View view) {
    }


}
