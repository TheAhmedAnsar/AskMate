package com.askmate.Modal;

public class myQuestionModal {

    public myQuestionModal(String question, String questionId) {
        this.question = question;
        this.questionId = questionId;
    }

    public myQuestionModal() {
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    private String question;
    private String questionId;
}
