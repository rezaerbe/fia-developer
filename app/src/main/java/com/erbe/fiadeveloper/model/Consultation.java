package com.erbe.fiadeveloper.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Consultation {

    private String userId;
    private String userName;
    private String consultantId;
    private String consultantName;
    private String consultantImage;
    private String userImage;
    private String status;
    private Date from;
    private Date to;

    public Consultation() {
    }

    public Consultation(String userId, String userName, String consultantId, String consultantName, String consultantImage, String userImage, String status) {
        this.userId = userId;
        this.userName = userName;
        this.consultantId = consultantId;
        this.consultantName = consultantName;
        this.consultantImage = consultantImage;
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

    public String getConsultantId() {
        return consultantId;
    }

    public void setConsultantId(String consultantId) {
        this.consultantId = consultantId;
    }

    public String getConsultantName() {
        return consultantName;
    }

    public void setConsultantName(String consultantName) {
        this.consultantName = consultantName;
    }

    public String getConsultantImage() {
        return consultantImage;
    }

    public void setConsultantImage(String consultantImage) {
        this.consultantImage = consultantImage;
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
