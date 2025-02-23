package com.askmate.Modal;

public class myAnswerModal {

    private String answerId;
    private String questionId;
    private String answer;
    private String question;

    public myAnswerModal(String answerId, String questionId, String answer, String question) {
        this.answerId = answerId;
        this.questionId = questionId;
        this.answer = answer;
        this.question = question;
    }

    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
