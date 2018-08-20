package ca.ipd12.quiz.rd.kwizz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Random;

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

        }
        else if(selectedItem == R.id.miGo){


        }
        else if(selectedItem == R.id.miScores){


        }

        else if(selectedItem == R.id.miLogout){
            Globals.isLoggedIn=false;
            Intent myIntent = new Intent(MenuActivity.this, MainActivity.class);
            MenuActivity.this.startActivity(myIntent);
        }
        else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }


}
