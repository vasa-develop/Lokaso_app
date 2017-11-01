package com.lokaso.retromodel;

import com.lokaso.model.Ads;
import com.lokaso.model.Suggestion;

import java.util.List;

/**
 * Created by Androcid on 19-Aug-16.
 */
public class RetroSuggestion {

    private List<Suggestion> details;
    private boolean success;
    private String message;

    private List<Ads> ads_list;
    private int ad_position = 0;


    public RetroSuggestion(List<Suggestion> details, boolean success, String message) {
        this.details = details;
        this.success = success;
        this.message = message;
    }


    public List<Ads> getAds_list() {
        return ads_list;
    }

    public int getAd_position() {
        return ad_position;
    }

    public List<Suggestion> getDetails() {
        return details;
    }

    public void setDetails(List<Suggestion> details) {
        this.details = details;
    }

    public boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
