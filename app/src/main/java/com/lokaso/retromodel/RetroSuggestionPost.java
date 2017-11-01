package com.lokaso.retromodel;

import com.lokaso.model.Suggestion;
import com.lokaso.model.SuggestionPost;

import java.util.List;

/**
 * Created by Androcid on 19-Aug-16.
 */
public class RetroSuggestionPost {

    private SuggestionPost details;
    private boolean success;
    private String message;

    public SuggestionPost getDetails() {
        return details;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
