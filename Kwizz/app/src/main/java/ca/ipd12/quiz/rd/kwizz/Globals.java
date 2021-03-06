package ca.ipd12.quiz.rd.kwizz;

import java.util.ArrayList;

public class Globals {
    public static String userEmail;//to identify current user
    public  static boolean isLoggedIn=false;//flag


    public static int NOQ=15; //Number of questions to load from API
    public static final int VER = 38;//DB version
    public static final String TAG = "kwizzz";//Debugging tag

    //Spent time to solve quiz - in seconds
    public static int kwizzTime = 0;

    //To check whether quiz is running or not
    public static volatile boolean isRunning = false;

    //To manage menus buttons (e.g. Resume or Start New)
    public static boolean currentQuizIsDone = false;

    public static int currentQuestionNumber = -1;
    public static int confirmedAnswers = 0;
    public static ArrayList<Question> allQuestions;//load from API
    public static ArrayList<Question> currentQuestions= new ArrayList<>();

}
