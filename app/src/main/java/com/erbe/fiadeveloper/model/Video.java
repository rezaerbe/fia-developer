package com.erbe.fiadeveloper.model;

public class Video {

    private String title;
    private String source;
    private String image;
    private String link;

    public Video() {
    }

    public Video(String title, String source, String image, String link) {
        this.title = title;
        this.source = source;
        this.image = image;
        this.link = link;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
