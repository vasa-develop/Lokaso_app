package com.lokaso.retromodel;

import com.lokaso.model.Interest;
import com.lokaso.model.Place;

import java.util.List;

/**
 * Created by Androcid on 12-Aug-16.
 */
public class RetroPlace {

    private List<Place> details;
    private boolean success;
    private String message;

    public RetroPlace(List<Place> details, boolean success, String message) {
        this.details = details;
        this.success = success;
        this.message = message;
    }

    public List<Place> getPlaces() {
        return details;
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
