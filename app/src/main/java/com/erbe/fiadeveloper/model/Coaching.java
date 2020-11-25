package com.erbe.fiadeveloper.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Coaching {

    private String userId;
    private String userName;
    private String coachId;
    private String coachName;
    private String coachImage;
    private String userImage;
    private String status;
    private Date from;
    private Date to;

    public Coaching() {
    }

    public Coaching(String userId, String userName, String coachId, String coachName, String coachImage, String userImage, String status) {
        this.userId = userId;
        this.userName = userName;
        this.coachId = coachId;
        this.coachName = coachName;
        this.coachImage = coachImage;
        this.userImage = userImage;
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCoachId() {
        return coachId;
    }

    public void setCoachId(String coachId) {
        this.coachId = coachId;
    }

    public String getCoachName() {
        return coachName;
    }

    public void setCoachName(String coachName) {
        this.coachName = coachName;
    }

    public String getCoachImage() {
        return coachImage;
    }

    public void setCoachImage(String coachImage) {
        this.coachImage = coachImage;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }
}
