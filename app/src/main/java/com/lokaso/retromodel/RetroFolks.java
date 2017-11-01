package com.lokaso.retromodel;

import com.lokaso.model.Folks;

import java.util.List;

/**
 * Created by Androcid on 12-Aug-16.
 */
public class RetroFolks {

    private List<Folks> details;
    private boolean success;
    private String message;

    public RetroFolks(List<Folks> details, boolean success, String message) {
        this.details = details;
        this.success = success;
        this.message = message;
    }

    public List<Folks> getDetails() {
        return details;
    }

    public void setDetails(List<Folks> details) {
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
