package com.lokaso.model;

/**
 * Created by Androcid on 25-Aug-16.
 */
public class ResponseComment {

    private int id;
    private int response_id;
    private String comment;
    private String created_date;
    private Profile profile;

    public ResponseComment() {
    }

    public ResponseComment(int id, int response_id, String comment, String created_date, Profile profile) {
        this.id = id;
        this.response_id = response_id;
        this.comment = comment;
        this.created_date = created_date;
        this.profile = profile;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getResponse_id() {
        return response_id;
    }

    public void setResponse_id(int response_id) {
        this.response_id = response_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
