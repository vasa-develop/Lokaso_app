package com.lokaso.retromodel;

import com.lokaso.model.Queries;

import java.util.List;

/**
 * Created by Androcid on 12-Aug-16.
 */
public class RetroQueries {

    private List<Queries> details;
    private boolean success;
    private String message;

    public RetroQueries(List<Queries> details, boolean success, String message) {
        this.details = details;
        this.success = success;
        this.message = message;
    }

    public List<Queries> getDetails() {
        return details;
    }

    public void setDetails(List<Queries> details) {
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
