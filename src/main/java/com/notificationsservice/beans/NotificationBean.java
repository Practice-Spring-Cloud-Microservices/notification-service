package com.notificationsservice.beans;


public class NotificationBean {

    private long sentAt;

    private String type;

    private String senderId;

    private String recipientUids;

    private String recipientEmails;

    private String title;

    private String message;

    private String details;

    public NotificationBean() {
    }

    public NotificationBean(long sentAt, String type, String senderId, String recipientUids, String recipientEmails, String title, String message, String details) {
        this.sentAt = sentAt;
        this.type = type;
        this.senderId = senderId;
        this.recipientUids = recipientUids;
        this.recipientEmails = recipientEmails;
        this.title = title;
        this.message = message;
        this.details = details;
    }

    public long getSentAt() {
        return sentAt;
    }

    public void setSentAt(long sentAt) {
        this.sentAt = sentAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRecipientUids() {
        return recipientUids;
    }

    public void setRecipientUids(String recipientUids) {
        this.recipientUids = recipientUids;
    }

    public String getRecipientEmails() {
        return recipientEmails;
    }

    public void setRecipientEmails(String recipientEmails) {
        this.recipientEmails = recipientEmails;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
