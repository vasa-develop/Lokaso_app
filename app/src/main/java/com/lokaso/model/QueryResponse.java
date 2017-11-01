package com.lokaso.model;

import java.io.Serializable;

/**
 * Created by Androcid on 25-Aug-16.
 */
public class QueryResponse implements Serializable {

    private int id;
    private int query_id;
    private String response;
    private int user_fav;
    private boolean spam;
    private int mark_inappropriate;
    private int upvotes;
    private int downvotes;
    private int comment_count;
    private String created_date;
    private Profile profile;

    public QueryResponse() {
    }

    public QueryResponse(int id, int query_id, String response, int user_fav, boolean spam, int mark_inappropriate, int upvotes,
                         int downvotes, int comment_count, String created_date, Profile profile) {
        this.id = id;
        this.query_id = query_id;
        this.response = response;
        this.user_fav = user_fav;
        this.spam = spam;
        this.mark_inappropriate = mark_inappropriate;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.comment_count = comment_count;
        this.created_date = created_date;
        this.profile = profile;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuery_id() {
        return query_id;
    }

    public void setQuery_id(int query_id) {
        this.query_id = query_id;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public int getUser_fav() {
        return user_fav;
    }

    public void setUser_fav(int user_fav) {
        this.user_fav = user_fav;
    }

    public boolean isSpam() {
        return spam;
    }

    public void setSpam(boolean spam) {
        this.spam = spam;
    }

    public int getMark_inappropriate() {
        return mark_inappropriate;
    }

    public void setMark_inappropriate(int mark_inappropriate) {
        this.mark_inappropriate = mark_inappropriate;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
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
