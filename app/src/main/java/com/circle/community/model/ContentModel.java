package com.circle.community.model;

import java.io.Serializable;
import java.util.List;

public class ContentModel implements Serializable {

    private String userID;
    private String userName;
    private String userProfile;
    private String uuid;
    private String contentImageUrl;
    private String contenTitle;
    private int contentHeartCount;
    private int contentViewCount;

    private List<String> likedBy;
    private Long lastUpdatedAt;
    private String category_value;
    private String link = "";
    private String instagramURL = "", youtubeURL = "", profileDescription = "";

    public ContentModel(String userID,String userName,String userProfile,
                        String uuid, String contentImageUrl, String contenTitle,
                        int contentHeartCount, int contentViewCount, long lastUpdatedAt, String category_value, String link) {
        this.userID = userID;
        this.userName = userName;
        this.userProfile = userProfile;
        this.uuid = uuid;
        this.contentImageUrl = contentImageUrl;
        this.contenTitle = contenTitle;
        this.contentHeartCount = contentHeartCount;
        this.contentViewCount = contentViewCount;
        this.lastUpdatedAt = lastUpdatedAt;
        this.category_value = category_value;
        this.link = link;
    }

    public ContentModel() {
    }

    public String getCategory_value() {
        return category_value;
    }

    public void setCategory_value(String category_value) {
        this.category_value = category_value;
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

    public Long getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(Long lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getContentViewCount() {
        return contentViewCount;
    }

    public void setContentViewCount(int contentViewCount) {
        this.contentViewCount = contentViewCount;
    }

    public String getInstagramURL() {
        return instagramURL;
    }

    public void setInstagramURL(String instagramURL) {
        this.instagramURL = instagramURL;
    }

    public String getYoutubeURL() {
        return youtubeURL;
    }

    public void setYoutubeURL(String youtubeURL) {
        this.youtubeURL = youtubeURL;
    }

    public String getProfileDescription() {
        return profileDescription;
    }

    public void setProfileDescription(String profileDescription) {
        this.profileDescription = profileDescription;
    }
}