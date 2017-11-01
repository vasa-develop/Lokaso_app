package com.lokaso.model;

/**
 * Created by Androcid on 19-Aug-16.
 */
public class Action {

    private int action_id;
    private int action_status;
    private String message;
    private boolean success;

    public Action(int action_id, int action_status, String message, boolean success) {
        this.action_id = action_id;
        this.action_status = action_status;
        this.message = message;
        this.success = success;
    }

    public int getAction_id() {
        return action_id;
    }

    public void setAction_id(int action_id) {
        this.action_id = action_id;
    }

    public int getAction_status() {
        return action_status;
    }

    public void setAction_status(int action_status) {
        this.action_status = action_status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
