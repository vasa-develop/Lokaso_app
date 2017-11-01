package com.lokaso.model;

import java.io.Serializable;

/**
 * Created by Androcid on 22-Aug-16.
 */
public class Adhoc implements Serializable {

    private int id;
    private String title;
    private String message;
    private String image;
    private String created_date;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getImage() {
        return image;
    }

    public String getCreated_date() {
        return created_date;
    }
}
