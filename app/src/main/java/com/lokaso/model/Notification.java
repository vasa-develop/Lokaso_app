package com.lokaso.model;

/**
 * Created by Androcid on 25-Aug-16.
 */
public class Notification {

    private int id;
    private int user_id;
    private String message;
    private String created_date;
    private Profile profile;

    public Notification() {
    }

    public Notification(int id, int user_id, String message, String created_date, Profile profile) {
        this.id = id;
        this.user_id = user_id;
        this.message = message;
        this.created_date = created_date;
        this.profile = profile;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
