package com.lokaso.retromodel;

import com.lokaso.model.LoginResponse;

import java.util.List;

/**
 * Created by Androcid on 06-Aug-16.
 */
public class RetroLogin {

    private List<LoginResponse> details;
    private boolean success;
    private String message;
    private boolean is_new_user;
    private boolean is_new_user_update;

    public RetroLogin(List<LoginResponse> details, boolean success, String message) {
        this.details = details;
        this.success = success;
        this.message = message;
    }

    public List<LoginResponse> getDetails() {
        return details;
    }

    public void setDetails(List<LoginResponse> details) {
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


    public boolean is_new_user() {
        return is_new_user;
    }

    public boolean is_new_user_update() {
        return is_new_user_update;
    }
}
