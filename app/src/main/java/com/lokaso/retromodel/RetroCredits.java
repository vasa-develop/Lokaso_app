package com.lokaso.retromodel;

import com.lokaso.model.Credits;

import java.util.List;

/**
 * Created by Androcid on 12-Aug-16.
 */
public class RetroCredits {

    private List<Credits> details;
    private boolean success;
    private String message;

    public RetroCredits(List<Credits> details, boolean success, String message) {
        this.details = details;
        this.success = success;
        this.message = message;
    }

    public List<Credits> getDetails() {
        return details;
    }

    public void setDetails(List<Credits> details) {
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
