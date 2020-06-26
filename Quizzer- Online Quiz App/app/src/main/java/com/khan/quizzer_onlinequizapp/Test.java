package com.khan.quizzer_onlinequizapp;

import java.io.Serializable;

public class Test implements Serializable {
    String Name;
    // ArrayList<Question> Questions;
    Long time;
    Long dateTime;
    String description;
    String setId;
    private double socreDe,socreInc;

    public Test() {
    }

    public Test(String name, Long dateTime, String description, String setId, double socreInc, double socreDe) {
        this.Name = name;
        this.dateTime = dateTime;
        this.description = description;
        this.setId = setId;
        this.socreInc = socreInc;
        this.socreDe = socreDe;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
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

    public double getSocreDe() {
        return socreDe;
    }

    public void setSocreDe(double socreDe) {
        this.socreDe = socreDe;
    }

    public double getSocreInc() {
        return socreInc;
    }

    public void setSocreInc(double socreInc) {
        this.socreInc = socreInc;
    }
}