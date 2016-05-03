package com.ahmedsameha1.peninbin.adapters;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ahmed on 9/7/2015.
 */
public class WrongItem implements Serializable {
    private String question;
    private String answer;
    private ArrayList<String> wrongAnswers;

    public WrongItem( String question, String answer, ArrayList<String> wrongAnswers ) {
        this.question = question;
        this.answer = answer;
        this.wrongAnswers = wrongAnswers;

    }
    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public ArrayList<String> getWrongAnswers() {
        return wrongAnswers;
    }
}