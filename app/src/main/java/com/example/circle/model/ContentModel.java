package com.example.circle.model;

public class ContentModel {

    private String userID;
    private String uuid;
    private String contentImageUrl;
    private String contenTitle;
    private String contentHeartCount;

    public ContentModel(String userID, String uuid, String contentImageUrl, String contenTitle, String contentHeartCount) {
        this.userID = userID;
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

    public String getContentHeartCount() {
        return contentHeartCount;
    }

    public void setContentHeartCount(String contentHeartCount) {
        this.contentHeartCount = contentHeartCount;
    }

    public String getUserID() {
        return userID;
    }

    public String getUuid() {
        return uuid;
    }
}
