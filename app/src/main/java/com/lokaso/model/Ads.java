package com.lokaso.model;

import java.io.Serializable;

/**
 * Created by Androcid on 22-Aug-16.
 */
public class Ads implements Serializable {

    private int id;
    private String title;
    private String message;
    private String url;
    private String youtube_id;
    private int type;
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

    public String getUrl() {
        return url;
    }

    public String getYoutube_id() {
        return youtube_id;
    }

    public int getType() {
        return type;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setType(int type) {
        this.type = type;
    }
}
