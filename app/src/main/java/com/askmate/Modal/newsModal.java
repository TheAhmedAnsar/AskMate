package com.askmate.Modal;

public class newsModal {

    private String category, link, image, title, content, time, newsBy , newsID;

    public newsModal(String category, String link, String image, String title, String content, String time, String newsBy, String newsID) {
        this.category = category;
        this.link = link;
        this.image = image;
        this.title = title;
        this.content = content;
        this.time = time;
        this.newsBy = newsBy;
        this.newsID = newsID;
    }

    public newsModal() {
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNewsBy() {
        return newsBy;
    }

    public void setNewsBy(String newsBy) {
        this.newsBy = newsBy;
    }

    public String getNewsID() {
        return newsID;
    }

    public void setNewsID(String newsID) {
        this.newsID = newsID;
    }
}
