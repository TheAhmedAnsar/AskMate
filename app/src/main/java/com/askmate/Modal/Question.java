package com.askmate.Modal;

public class Question {
    private String question;
    private String image;
    private String uid;
    private String category;
    private String timestamp;
    private String questionId;

    public Question() {
        // Default constructor required for Firebase
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public Question(String question, String image, String uid, String category, String timestamp) {
        this.question = question;
        this.image = image;
        this.uid = uid;
        this.category = category;
        this.timestamp = timestamp;

    }


}
