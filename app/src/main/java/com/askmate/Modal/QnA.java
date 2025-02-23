package com.askmate.Modal;

public class QnA {
    private String name;
    private String timestamp;
    private String answer;
    private String like;
    private String image;
    private String uid;

    public QnA() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAnswerid() {
        return answerid;
    }

    public void setAnswerid(String answerid) {
        this.answerid = answerid;
    }

    public QnA(String name, String timestamp, String answer, String like, String image, String uid, String answerid) {
        this.name = name;
        this.timestamp = timestamp;
        this.answer = answer;
        this.like = like;
        this.image = image;
        this.uid = uid;
        this.answerid = answerid;
    }

    private String answerid;





}
