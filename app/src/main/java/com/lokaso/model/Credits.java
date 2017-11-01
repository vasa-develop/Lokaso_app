package com.lokaso.model;

/**
 * Created by Androcid on 26-Aug-16.
 */
public class Credits {

    private int id;
    private int user_id;
    private String credit_name;
    private int points;
    private String created_date;

    public Credits(int id, int user_id, String credit_name, int points, String created_date) {
        this.id = id;
        this.user_id = user_id;
        this.credit_name = credit_name;
        this.points = points;
        this.created_date = created_date;
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

    public String getCredit_name() {
        return credit_name;
    }

    public void setCredit_name(String credit_name) {
        this.credit_name = credit_name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }
}
