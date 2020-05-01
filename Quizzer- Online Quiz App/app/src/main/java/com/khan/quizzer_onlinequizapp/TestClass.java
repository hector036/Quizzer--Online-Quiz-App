package com.khan.quizzer_onlinequizapp;

public class TestClass {

    private String setId,setName;
    private long order;

    public TestClass(String setId, String setName,long order) {
        this.setId = setId;
        this.setName = setName;
        this.order = order;
    }

    public long getOrder() {
        return order;
    }

    public void setOrder(long order) {
        this.order = order;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }
}
