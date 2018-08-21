package ca.ipd12.quiz.rd.kwizz;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import static ca.ipd12.quiz.rd.kwizz.Globals.confirmedAnswers;
import static ca.ipd12.quiz.rd.kwizz.Globals.isLoggedIn;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int selectedItem = item.getItemId();
        if(selectedItem == R.id.miHome){
            Intent myIntent = new Intent(MenuActivity.this, MainActivity.class);
            MenuActivity.this.startActivity(myIntent);
        }
        else if(selectedItem == R.id.miGo){
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

        else if(selectedItem == R.id.miScores){
            Intent myIntent = new Intent(MenuActivity.this, HistoryActivity.class);
            MenuActivity.this.startActivity(myIntent);

        }
        else if(selectedItem == R.id.miEmail){
            ResultActivity ra = new ResultActivity();
            ra.mailSender();
            Toast.makeText(MenuActivity.this,
                    "Quiz result was sent on your email", Toast.LENGTH_LONG).show();

        }
        else if(selectedItem == R.id.miLogout){
            if(!isLoggedIn) {
                Toast.makeText(MenuActivity.this,
                        "Please log in.", Toast.LENGTH_SHORT).show();
            }

            isLoggedIn=false;
            Globals.isRunning=false;
            Intent myIntent = new Intent(MenuActivity.this, MainActivity.class);
            MenuActivity.this.startActivity(myIntent);
        }
        else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void createNewQuiz(){
        Globals.currentQuestionNumber = -1;
        Globals.confirmedAnswers = 0;
        Globals.kwizzTime = 0;
        Intent myIntent = new Intent(MenuActivity.this, QuizActivity.class);
        MenuActivity.this.startActivity(myIntent);
    }


}
