package com.erbe.fiadeveloper.model;

import com.google.firebase.auth.FirebaseUser;

public class Report {

    private String userId;
    private String userName;
    private String status;
    private String type;
    private String chronology;

    public Report() {
    }

    public Report(FirebaseUser user, String status, String type, String chronology) {
        this.userId = user.getUid();
        this.userName = user.getDisplayName();
        this.status = status;
        this.type = type;
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

    public String getChronology() {
        return chronology;
    }

    public void setChronology(String chronology) {
        this.chronology = chronology;
    }
}
