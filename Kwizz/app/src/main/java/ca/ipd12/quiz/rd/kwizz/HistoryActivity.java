package ca.ipd12.quiz.rd.kwizz;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

import static ca.ipd12.quiz.rd.kwizz.Globals.VER;

public class HistoryActivity extends MenuActivity {

    HistoryAdapter historyAdapter;
    ArrayList<HistoryItem> historyItems = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getHistoryFromDB();
        initRecyclerView();
    }

    private void getHistoryFromDB() {
        MyDbHelper dbHelper = new MyDbHelper(this, "kwizzdb", null, VER);
        SQLiteDatabase db  = dbHelper.getReadableDatabase();

        String[]columns = {"email", "points", "seconds", "correct"};

        Cursor cursor = db.query("history", columns,null,
                null,null,null,null);
        //cursor.moveToFirst();
        HistoryItem hi;
        historyItems = new ArrayList<>();
        while (cursor.moveToNext()){
            hi = new HistoryItem();
            hi.email = cursor.getString(0);
            hi.points = cursor.getInt(1);
            hi.seconds= cursor.getInt(2);
            hi.correct = cursor.getInt(3);
            historyItems.add(hi);

            //Log.i("db2 ValueFromDB", cursor.getString(0) + " (" + cursor.getString(1)+ ") "+ cursor.getString(2));
        }
    }

    private void initRecyclerView() {

        RecyclerView myRecView = findViewById(R.id.rc_view_history);
        historyAdapter = new HistoryAdapter(historyItems);

        myRecView.setAdapter(historyAdapter);
        myRecView.setLayoutManager(new LinearLayoutManager(this));

    }
}
