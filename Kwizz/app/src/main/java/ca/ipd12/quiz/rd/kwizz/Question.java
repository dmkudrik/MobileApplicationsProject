package ca.ipd12.quiz.rd.kwizz;

import java.util.ArrayList;

public class Question {
    public String question;
    public ArrayList<Answer> answers; // every question must have at least 2 answers
    public boolean isChecked=false; //true when user already checked one of the answers
    public boolean isConfirmed=false; // true when user confirmed an answer
    public int checkedAnswer=777; // get value 0 to 4

//    private int getCorrectNumber(){
//
//    }
}





