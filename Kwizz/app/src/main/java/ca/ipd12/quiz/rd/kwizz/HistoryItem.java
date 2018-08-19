package ca.ipd12.quiz.rd.kwizz;

public class HistoryItem {
    public String email;
    public double result;
    public String resultTime;
    public String getResult(){
        return String.format("%.0f", result);
    }
}
