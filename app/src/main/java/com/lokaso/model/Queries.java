package com.lokaso.model;

import java.io.Serializable;

/**
 * Created by Androcid on 20-Aug-16.
 */
public class Queries implements Serializable {

    private int id;
    private Profile profile;
    private boolean query_fav;
    private boolean user_followed;
    private String description;
    private String valid_date;
    private boolean flag;
    private String valid_until;
    private int response_count;
    private String created_date;
    private String location;
    private int interest_id;
    private int flag_solved;
    private int marked_inappropriate;
    private int distance;

    public Queries() {
    }

    public Queries(int id, Profile profile, boolean query_fav, boolean user_followed, String description, String valid_date,
                   boolean flag, String valid_until, int response_count, String created_date, String location, int interest_id,
                   int flag_solved, int marked_inappropriate, int distance) {
        this.id = id;
        this.profile = profile;
        this.query_fav = query_fav;
        this.user_followed = user_followed;
        this.description = description;
        this.valid_date = valid_date;
        this.flag = flag;
        this.valid_until = valid_until;
        this.response_count = response_count;
        this.created_date = created_date;
        this.location = location;
        this.interest_id = interest_id;
        this.flag_solved = flag_solved;
        this.marked_inappropriate = marked_inappropriate;
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public boolean isQuery_fav() {
        return query_fav;
    }

    public void setQuery_fav(boolean query_fav) {
        this.query_fav = query_fav;
    }

    public boolean isUser_followed() {
        return user_followed;
    }

    public void setUser_followed(boolean user_followed) {
        this.user_followed = user_followed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValid_date() {
        return valid_date;
    }

    public void setValid_date(String valid_date) {
        this.valid_date = valid_date;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getValid_until() {
        return valid_until;
    }

    public void setValid_until(String valid_until) {
        this.valid_until = valid_until;
    }

    public int getResponse_count() {
        return response_count;
    }

    public void setResponse_count(int response_count) {
        this.response_count = response_count;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getInterest_id() {
        return interest_id;
    }

    public void setInterest_id(int interest_id) {
        this.interest_id = interest_id;
    }

    public int getFlag_solved() {
        return flag_solved;
    }

    public void setFlag_solved(int flag_solved) {
        this.flag_solved = flag_solved;
    }

    public int getMarked_inappropriate() {
        return marked_inappropriate;
    }

    public void setMarked_inappropriate(int marked_inappropriate) {
        this.marked_inappropriate = marked_inappropriate;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
