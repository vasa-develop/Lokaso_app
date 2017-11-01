package com.lokaso.model;

import java.io.Serializable;

/**
 * Created by Androcid on 12-Aug-16.
 */
public class AddInterest implements Serializable {

    private int id;
    private int user_id;
    private int interest_id;

    public AddInterest(int id, int user_id, int interest_id) {
        this.id = id;
        this.user_id = user_id;
        this.interest_id = interest_id;
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

    public int getInterest_id() {
        return interest_id;
    }

    public void setInterest_id(int interest_id) {
        this.interest_id = interest_id;
    }
}
