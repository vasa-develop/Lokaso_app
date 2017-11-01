package com.lokaso.retromodel;

import com.lokaso.model.Action;

/**
 * Created by Androcid on 06-Aug-16.
 */
public class RetroAction {

    private Action action;
    private boolean success;
    private String message;

    public RetroAction(Action action, boolean success, String message) {
        this.action = action;
        this.success = success;
        this.message = message;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
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
