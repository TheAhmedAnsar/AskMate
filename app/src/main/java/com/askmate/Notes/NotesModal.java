package com.askmate.Notes;

public class NotesModal {
    private String link;
    private String text;
    private String pushKey;

    public NotesModal() {
    }

    public NotesModal(String link, String text, String pushKey) {
        this.link = link;
        this.text = text;
        this.pushKey = pushKey;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPushKey() {
        return pushKey;
    }

    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }
}
