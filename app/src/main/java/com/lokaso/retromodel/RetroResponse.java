package com.lokaso.retromodel;

/**
 * Created by Androcid on 06-Aug-16.
 */
public class RetroResponse {

    private String details;
    private boolean success;
    private String message;

    public RetroResponse(String details, boolean success, String message) {
        this.details = details;
        this.success = success;
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
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
