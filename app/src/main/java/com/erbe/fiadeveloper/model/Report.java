package com.erbe.fiadeveloper.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Report {

    private String userId;
    private String userName;
    private String status;
    private String type;
    private String phone;
    private String chronology;
    private @ServerTimestamp Date timestamp;

    public Report() {
    }

    public Report(String userId, String userName, String status, String type, String phone, String chronology) {
        this.userId = userId;
        this.userName = userName;
        this.status = status;
        this.type = type;
        this.phone = phone;
        this.chronology = chronology;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getChronology() {
        return chronology;
    }

    public void setChronology(String chronology) {
        this.chronology = chronology;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
