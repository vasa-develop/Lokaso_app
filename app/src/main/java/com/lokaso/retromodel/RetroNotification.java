package com.lokaso.retromodel;

import com.lokaso.model.Notification;

import java.util.List;

/**
 * Created by Androcid on 12-Aug-16.
 */
public class RetroNotification {

    private List<Notification> details;
    private boolean success;
    private String message;

    public RetroNotification(List<Notification> details, boolean success, String message) {
        this.details = details;
        this.success = success;
        this.message = message;
    }

    public List<Notification> getDetails() {
        return details;
    }

    public void setDetails(List<Notification> details) {
        this.details = details;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
