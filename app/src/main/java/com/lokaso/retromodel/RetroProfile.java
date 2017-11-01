package com.lokaso.retromodel;

import com.lokaso.model.Profile;

import java.util.List;

/**
 * Created by Androcid on 12-Aug-16.
 */
public class RetroProfile {

    private List<Profile> details;
    private boolean success;
    private String message;

    public RetroProfile(List<Profile> details, boolean success, String message) {
        this.details = details;
        this.success = success;
        this.message = message;
    }

    public List<Profile> getDetails() {
        return details;
    }

    public void setDetails(List<Profile> details) {
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
