package com.lokaso.retromodel;

import com.lokaso.model.AddInterest;

import java.util.List;

/**
 * Created by Androcid on 12-Aug-16.
 */
public class RetroCreateChatroom {

    private int details;
    private boolean success;
    private String message;

    public RetroCreateChatroom(int details, boolean success, String message) {
        this.details = details;
        this.success = success;
        this.message = message;
    }

    public int getDetails() {
        return details;
    }

    public void setDetails(int details) {
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
