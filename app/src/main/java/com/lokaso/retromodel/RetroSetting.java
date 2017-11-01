package com.lokaso.retromodel;

import com.lokaso.model.Setting;

/**
 * Created by Androcid on 12-Aug-16.
 */
public class RetroSetting {

    private Setting details;
    private boolean success;
    private String message;

    public RetroSetting(Setting details, boolean success, String message) {
        this.details = details;
        this.success = success;
        this.message = message;
    }

    public Setting getDetails() {
        return details;
    }

    public void setDetails(Setting details) {
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
