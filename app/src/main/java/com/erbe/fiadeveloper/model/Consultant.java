package com.erbe.fiadeveloper.model;

import java.util.Date;

public class Consultant {

    private String consultantId;
    private String consultantName;
    private String topic;
    private String description;
    private String photo;
    private int numRatings;
    private double avgRating;

    public Consultant() {
    }

    public Consultant(String consultantId, String consultantName, String topic, String description, String photo, int numRatings, double avgRating) {
        this.consultantId = consultantId;
        this.consultantName = consultantName;
        this.topic = topic;
        this.description = description;
        this.photo = photo;
        this.numRatings = numRatings;
        this.avgRating = avgRating;
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

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getNumRatings() {
        return numRatings;
    }

    public void setNumRatings(int numRatings) {
        this.numRatings = numRatings;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }
}
