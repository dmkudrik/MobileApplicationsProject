package ca.ipd12.quiz.rd.kwizz;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class MyDbHelper extends SQLiteOpenHelper {

    MainActivity ma;
    public MyDbHelper(Context context, String name,
                      SQLiteDatabase.CursorFactory factory, int version){

        super(context, name, factory, version);
        try{
        ma = (MainActivity) context;}
        catch (Exception e ){}
    }

    @Override
    public void onCreate(SQLiteDatabase db){

        db.execSQL("CREATE TABLE questions (\n" +
                " id integer PRIMARY KEY AUTOINCREMENT, \n" +
                " question text NOT NULL \n" +
                ")");
        Log.i(Globals.TAG, "Table questions was created");
        db.execSQL("CREATE TABLE [answers] (\n" +
//                "  [id] INTEGER NOT NULL, \n" +
                "  [qid] INTEGER NOT NULL CONSTRAINT [answerToQuestion] REFERENCES [questions]([id]) ON DELETE CASCADE ON UPDATE CASCADE, \n" +
                "  [answer] TEXT NOT NULL, \n" +
                "  [iscorrect] BOOLEAN NOT NULL DEFAULT 'false'\n" +
                ")");
        Log.i(Globals.TAG, "Table answers was created");

        db.execSQL("CREATE TABLE history (\n" +
                " id integer PRIMARY KEY AUTOINCREMENT, \n" +
                " email text NOT NULL, \n" +
                " points integer NOT NULL, \n" +
                " seconds integer NOT NULL,\n" +
                " correct integer NOT NULL\n" +
                ")");
        Log.i(Globals.TAG, "Table history was created");
        if(ma!=null){
        ma.getData();}
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists answers");
        Log.i(Globals.TAG, "Table answers was dropped");

        sqLiteDatabase.execSQL("drop table if exists questions");
        Log.i(Globals.TAG, "Table questions was dropped");

        sqLiteDatabase.execSQL("drop table if exists history");
        Log.i(Globals.TAG, "Table history was dropped");
        onCreate(sqLiteDatabase);
    }


}


