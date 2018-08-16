package ca.ipd12.quiz.rd.kwizz;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static ca.ipd12.quiz.rd.kwizz.Globals.TAG;
import static ca.ipd12.quiz.rd.kwizz.Globals.VER;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Stetho.initializeWithDefaults(this); // Stetho

        quizGenerator();
        fetcher();
    }


    //method is used to Generate random questions and answers on the development stage
    public void quizGenerator(){
        Question q;
        Answer a;
        int numberOfQuestions = 15;
        boolean correctIsDefined;
        int minAnswers = 2;
        int maxAnswers = 5;
        ArrayList<Answer> answers;

        //generating numberOfQuestions questions
        for (int i = 0; i<numberOfQuestions; i++){
            q = new Question();
            q.question = "Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum?";
//            Log.i(TAG, "Q"+ (i+1)+": "+q.question);

            //2 to 5 - number of answers generator
            int numberOfAnswers = ThreadLocalRandom.current().nextInt(minAnswers, maxAnswers + 1);

            //correct answer definer
            int correctAnswer = ThreadLocalRandom.current().nextInt(1, numberOfAnswers + 1);

            answers = new ArrayList<>();
            correctIsDefined = false;
            for (int k = 0; k<numberOfAnswers; k++){
                a = new Answer();
                a.answer = "Lorem Ipsum Lorem Ipsum Lorem Ipsum.";
                if(k==(correctAnswer-1)) a.isCorrect = true;
//                Log.i(TAG, "A"+ (k+1)+": "+a.answer + (a.isCorrect ? " - Correct one!": ""));
                answers.add(a);
            }

            //Save question into db - transaction
            MyDbHelper dbHelper = new MyDbHelper(this, "kwizzdb", null, Globals.VER);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("question", q.question);
            long rowId = db.insert("questions", null, values);
            Log.i(TAG, "Row number of added question is " + rowId);

            for(int ans=0; ans<numberOfAnswers; ans++) {
                ContentValues values2 = new ContentValues();
                values2.put("qid", rowId);
                values2.put("answer", (answers.get(ans)).answer);
                values2.put("iscorrect", answers.get(ans).isCorrect);
                long rowId2 = db.insert("answers", null, values2);
                Log.i(TAG, "Row number of added answer is " + rowId2);
            }

        }
    }
    public void fetcher(){
        MyDbHelper dbHelper = new MyDbHelper(this, "kwizzdb", null, VER);
        SQLiteDatabase db  = dbHelper.getReadableDatabase();

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
        while ( !cursor.isAfterLast()) {
            Log.i(TAG, cursor.getString(0) + " - " + cursor.getString(1));
            cursor.moveToNext();
        }



    }


}
