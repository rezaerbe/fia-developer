package com.erbe.fiadeveloper.model;

import java.util.Date;

public class Coach {

    private String coachId;
    private String coachName;
    private String topic;
    private String description;
    private String photo;
    private int numRatings;
    private double avgRating;

    public Coach() {
    }

    public Coach(String coachId, String coachName, String topic, String description, String photo, int numRatings, double avgRating) {
        this.coachId = coachId;
        this.coachName = coachName;
        this.topic = topic;
        this.description = description;
        this.photo = photo;
        this.numRatings = numRatings;
        this.avgRating = avgRating;
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
