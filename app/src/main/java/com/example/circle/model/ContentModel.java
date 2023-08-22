package com.example.circle.model;

import java.util.List;

public class ContentModel {

    private String userID;
    private String userName;
    private String userProfile;
    private String uuid;
    private String contentImageUrl;
    private String contenTitle;
    private int contentHeartCount;
  //  private likedBy likedBy;

    private List<String> likedBy;

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

    public List<String> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(List<String> likedBy) {
        this.likedBy = likedBy;
    }

    //    public likedBy getLikedBy() {
//        return likedBy;
//    }
//
//    public void setLikedBy(likedBy likedBy) {
//        this.likedBy = likedBy;
//    }


    /* public likedBy getLikedBy() {
        return likedBy;
    }

    public void setlikedBy(likedBy likedBy) {
        this.likedBy = likedBy;
    }

    public class likedBy {

        public likedBy() {
        }

        private boolean userIDKey;

        public boolean isUserIDKey() {
            return userIDKey;
        }

        public void setUserIDKey(boolean userIDKey) {
            this.userIDKey = userIDKey;
        }
    }*/
}
