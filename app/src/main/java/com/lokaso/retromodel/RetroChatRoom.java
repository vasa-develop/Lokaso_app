package com.lokaso.retromodel;

import com.lokaso.model.ChatRoom;

import java.util.List;

/**
 * Created by Androcid on 12-Aug-16.
 */
public class RetroChatRoom {

    private List<ChatRoom> details;
    private boolean success;
    private String message;

    public RetroChatRoom(List<ChatRoom> details, boolean success, String message) {
        this.details = details;
        this.success = success;
        this.message = message;
    }

    public List<ChatRoom> getDetails() {
        return details;
    }

    public void setDetails(List<ChatRoom> details) {
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
