package com.khan.quizzer_onlinequizapp;

import java.util.List;

public class MainPageModel {

    public static final int PROFILE_VIEW = 0;
    public static final int GRID_VIEW = 1;
    public static final int WEEKLY_TEST_VIEW = 2;
    public static final int BANNER_VIEW = 3;
    public static final int HORIZONTAL_BANNER_LAYOUT_VIEW = 4;

    private int type;

    public MainPageModel() {
    }

    ////////////// PROFILE_VIEW ////////////////

    private String profileImg;
    private String name;
    private String instrituteName;

    public MainPageModel(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstrituteName() {
        return instrituteName;
    }

    public void setInstrituteName(String instrituteName) {
        this.instrituteName = instrituteName;
    }

    ////////////// PROFILE_VIEW ////////////////


    ////////////// GRID_VIEW ///////////////////

    private List<HomeModel> homeModelList;

    public MainPageModel(int type, List<HomeModel> homeModelList) {
        this.type = type;
        this.homeModelList = homeModelList;
    }

    public List<HomeModel> getHomeModelList() {
        return homeModelList;
    }

    public void setHomeModelList(List<HomeModel> homeModelList) {
        this.homeModelList = homeModelList;
    }

    ////////////// GRID_VIEW ///////////////////

    ////////////// WEEKLY_TEST_VIEW ////////////

    private String header,title,description,setId;
    private long date;
    private int imageWeeklyTest;

    public MainPageModel(int type,String header,long date, String title, String description, String setId,int imageWeeklyTest) {
        this.type = type;
        this.header = header;
        this.date = date;
        this.title = title;
        this.description = description;
        this.setId = setId;
        this.imageWeeklyTest = imageWeeklyTest;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getImageWeeklyTest() {
        return imageWeeklyTest;
    }

    public void setImageWeeklyTest(int imageWeeklyTest) {
        this.imageWeeklyTest = imageWeeklyTest;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    ////////////// WEEKLY_TEST_VIEW ////////////


    ////////////// BANNER_VIEW /////////////////

    private String bannerImg;
    private String bannerActionText;
    private String bannerUrl;
    private boolean bannerEnable;

    public MainPageModel(int type, String bannerImg, String bannerActionText,String bannerUrl,boolean bannerEnable) {
        this.type = type;
        this.bannerImg = bannerImg;
        this.bannerActionText = bannerActionText;
        this.bannerUrl = bannerUrl;
        this.bannerEnable = bannerEnable;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public boolean isBannerEnable() {
        return bannerEnable;
    }

    public void setBannerEnable(boolean bannerEnable) {
        this.bannerEnable = bannerEnable;
    }

    public String getBannerImg() {
        return bannerImg;
    }

    public void setBannerImg(String bannerImg) {
        this.bannerImg = bannerImg;
    }

    public String getBannerActionText() {
        return bannerActionText;
    }

    public void setBannerActionText(String bannerActionText) {
        this.bannerActionText = bannerActionText;
    }

    ////////////// BANNER_VIEW /////////////////


    ////////////// HORIZONTAL_SCROLL_BANNER_VIEW ///////////

    private String horizontalScrollLayoutTitle;
    private String horizontalScrollLayoutViewAllUrl;
    private List<MainPageModel> horizontalScrollBannerList;

    public MainPageModel(int type, String horizontalScrollLayoutTitle, String horizontalScrollLayoutViewAllUrl, List<MainPageModel> horizontalScrollBannerList) {
        this.type = type;
        this.horizontalScrollLayoutTitle = horizontalScrollLayoutTitle;
        this.horizontalScrollLayoutViewAllUrl = horizontalScrollLayoutViewAllUrl;
        this.horizontalScrollBannerList = horizontalScrollBannerList;
    }

    public String getHorizontalScrollLayoutTitle() {
        return horizontalScrollLayoutTitle;
    }

    public void setHorizontalScrollLayoutTitle(String horizontalScrollLayoutTitle) {
        this.horizontalScrollLayoutTitle = horizontalScrollLayoutTitle;
    }

    public String getHorizontalScrollLayoutViewAllUrl() {
        return horizontalScrollLayoutViewAllUrl;
    }

    public void setHorizontalScrollLayoutViewAllUrl(String horizontalScrollLayoutViewAllUrl) {
        this.horizontalScrollLayoutViewAllUrl = horizontalScrollLayoutViewAllUrl;
    }

    public List<MainPageModel> getHorizontalScrollBannerList() {
        return horizontalScrollBannerList;
    }

    public void setHorizontalScrollBannerList(List<MainPageModel> horizontalScrollBannerList) {
        this.horizontalScrollBannerList = horizontalScrollBannerList;
    }

    ////////////// HORIZONTAL_SCROLL_BANNER_VIEW ///////////
}
