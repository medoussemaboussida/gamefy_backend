package com.turki.gamefyback.emailManager;

public class Email {
    String to;
    String from;
    String subject;
    String body;

    public Email() {
    }

    public Email(String to, String from, String subject, String body) {
        this.to = to;
        this.from = from;
        this.subject = subject;
        this.body = body;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }



    public void setBody(String body) {
        this.body = body;
    }
}
