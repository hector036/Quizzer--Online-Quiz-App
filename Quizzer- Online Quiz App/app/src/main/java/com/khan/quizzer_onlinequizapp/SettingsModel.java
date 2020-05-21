package com.khan.quizzer_onlinequizapp;

import android.widget.Switch;

public class SettingsModel {

    public static final int SETTINGS_HEADER = 0;
    public static final int SETTINGS_ITEM_WITH_SWITCH = 1;
    public static final int SETTINGS_ITEM_WITH_OUT_SWITCH = 2;
    public static final int SETTINGS_ITEM_WITH_OUT_SWITCH_AND_WITH_URL = 3;
    public static final int NOTIFICATION_ITEM = 4;

    private int type;
    private String header;
    /////////////// Header Settings ///////////////////

    public SettingsModel(int type, String header) {
        this.type = type;
        this.header = header;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    /////////////// Header Settings ///////////////////

    //////////////////settings item ////////////////////

    private int image;
    private String title;
    private boolean enable;

    public SettingsModel(int type, int image, String title, boolean enable) {
        this.type = type;
        this.image = image;
        this.title = title;
        this.enable = enable;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    //////////////////settings item ////////////////////

    //////////////////settings item with url////////////////////

    private String url;

    public SettingsModel(int type, int image, String title, String url) {
        this.type = type;
        this.image = image;
        this.title = title;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    //////////////////settings item with url////////////////////
    //////////////////notification item////////////////////

    private String notificationType,notificationTitle,notificationDescription,notificationPhotoUrl, notificationLink;
    private long notificationTime;

    public SettingsModel(int type, String notificationType, String notificationTitle, String notificationDescription, String notificationPhotoUrl, String notificationLink,long notificationTime) {
        this.type = type;
        this.notificationType = notificationType;
        this.notificationTitle = notificationTitle;
        this.notificationDescription = notificationDescription;
        this.notificationPhotoUrl = notificationPhotoUrl;
        this.notificationLink = notificationLink;
        this.notificationTime = notificationTime;
    }

    public long getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(long notificationTime) {
        this.notificationTime = notificationTime;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationDescription() {
        return notificationDescription;
    }

    public void setNotificationDescription(String notificationDescription) {
        this.notificationDescription = notificationDescription;
    }

    public String getNotificationPhotoUrl() {
        return notificationPhotoUrl;
    }

    public void setNotificationPhotoUrl(String notificationPhotoUrl) {
        this.notificationPhotoUrl = notificationPhotoUrl;
    }

    public String getNotificationLink() {
        return notificationLink;
    }

    public void setNotificationLink(String notificationLink) {
        this.notificationLink = notificationLink;
    }

    //////////////////notification item////////////////////
}
