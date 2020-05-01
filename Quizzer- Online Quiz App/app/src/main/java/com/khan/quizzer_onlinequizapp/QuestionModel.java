package com.khan.quizzer_onlinequizapp;

public class QuestionModel {

    private String id,question, optionA,optionB,optionC,optionD,correctAns,set,url;
    private String yourAns;
    private int initPosition;
    //private String id,question,A,B,C,D,answer,set;


    public QuestionModel(){

    }

    public QuestionModel(String id, String question, String optionA, String optionB, String optionC, String optionD, String correctAns, String set,String url) {
        this.id = id;
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAns = correctAns;
        this.set = set;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getYourAns() {
        return yourAns;
    }

    public void setYourAns(String yourAns) {
        this.yourAns = yourAns;
    }

    public int getInitPosition() {
        return initPosition;
    }

    public void setInitPosition(int initPosition) {
        this.initPosition = initPosition;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public String getCorrectAns() {
        return correctAns;
    }

    public void setCorrectAns(String correctAns) {
        this.correctAns = correctAns;
    }

    public String getSet() {
        return set;
    }

    public void setSet(String set) {
        this.set = set;
    }
}
