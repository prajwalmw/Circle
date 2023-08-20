package com.example.circle.model;

public class ContentModel {

    private String userID;
    private String userName;
    private String userProfile;
    private String uuid;
    private String contentImageUrl;
    private String contenTitle;
    private int contentHeartCount;

    public ContentModel(String userID,String userName,String userProfile, String uuid, String contentImageUrl, String contenTitle, int contentHeartCount) {
        this.userID = userID;
        this.userName = userName;
        this.userProfile = userProfile;
        this.uuid = uuid;
        this.contentImageUrl = contentImageUrl;
        this.contenTitle = contenTitle;
        this.contentHeartCount = contentHeartCount;
    }

    public ContentModel() {
    }


    public String getContentImageUrl() {
        return contentImageUrl;
    }

    public void setContentImageUrl(String contentImageUrl) {
        this.contentImageUrl = contentImageUrl;
    }

    public String getContenTitle() {
        return contenTitle;
    }

    public void setContenTitle(String contenTitle) {
        this.contenTitle = contenTitle;
    }

    public int getContentHeartCount() {
        return contentHeartCount;
    }

    public void setContentHeartCount(int contentHeartCount) {
        this.contentHeartCount = contentHeartCount;
    }

    public String getUserID() {
        return userID;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
