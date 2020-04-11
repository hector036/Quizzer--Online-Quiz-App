package com.khan.quizzer_onlinequizapp;

import java.util.List;

public class CategoryModel {
    private String name,url,key;
    private List<TestClass> sets;
    private List<TestClass> chapters;

    public CategoryModel(){

    }

    public CategoryModel(String name, String url, String key, List<TestClass> sets, List<TestClass> chapters) {
        this.name = name;
        this.url = url;
        this.key = key;
        this.sets = sets;
        this.chapters = chapters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<TestClass> getSets() {
        return sets;
    }

    public void setSets(List<TestClass> sets) {
        this.sets = sets;
    }

    public List<TestClass> getChapters() {
        return chapters;
    }

    public void setChapters(List<TestClass> chapters) {
        this.chapters = chapters;
    }
}
