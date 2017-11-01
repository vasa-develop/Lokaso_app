package com.lokaso.retromodel;

import com.lokaso.model.Profession;

import java.util.List;

/**
 * Created by Androcid on 06-Aug-16.
 */
public class RetroProfession {

    private List<Profession> details;
    private boolean success;
    private String message;

    public RetroProfession(List<Profession> details, boolean success, String message) {
        this.details = details;
        this.success = success;
        this.message = message;
    }

    public List<Profession> getDetails() {
        return details;
    }

    public void setDetails(List<Profession> details) {
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
