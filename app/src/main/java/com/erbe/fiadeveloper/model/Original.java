package com.erbe.fiadeveloper.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Original {

    private String title;
    private String source;
    private String description;
    private String image;
    private @ServerTimestamp Date timestamp;

    public Original() {
    }

    public Original(String title, String source, String description, String image) {
        this.title = title;
        this.source = source;
        this.description = description;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
