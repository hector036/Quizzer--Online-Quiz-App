package com.khan.quizzer_onlinequizapp;

import java.io.Serializable;
import java.util.ArrayList;

public class Test implements Serializable {
    String Name;
   // ArrayList<Question> Questions;
    Long time;
    String date;
    String startTime;
    String description;
    String setId;

    public Test() {
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

//    public ArrayList<Question> getQuestions() {
//        return Questions;
//    }
//
//    public void setQuestions(ArrayList<Question> questions) {
//        Questions = questions;
//    }

}