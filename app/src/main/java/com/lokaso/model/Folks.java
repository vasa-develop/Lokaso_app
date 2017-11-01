package com.lokaso.model;

import java.io.Serializable;

/**
 * Created by Androcid on 20-Aug-16.
 */
public class Folks implements Serializable {

    private int id;
    private String name;
    private String email;
    private int num_asks;
    private int num_responses;
    private String about_me;
    private String profession;
    private String location;
    private String provider;
    private String facebook_id;
    private boolean user_followed;
    private String image;
    private String created_date;
    private int notification_flag;
    private int credits;
    private double distance;

    public Folks(int id, String name, String email, int num_asks, int num_responses, String about_me, String profession,
                 String location, String provider, String facebook_id, boolean user_followed, String image, String created_date,
                 int notification_flag, int credits, double distance) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.num_asks = num_asks;
        this.num_responses = num_responses;
        this.about_me = about_me;
        this.profession = profession;
        this.location = location;
        this.provider = provider;
        this.facebook_id = facebook_id;
        this.user_followed = user_followed;
        this.image = image;
        this.created_date = created_date;
        this.notification_flag = notification_flag;
        this.credits = credits;
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getNum_asks() {
        return num_asks;
    }

    public void setNum_asks(int num_asks) {
        this.num_asks = num_asks;
    }

    public int getNum_responses() {
        return num_responses;
    }

    public void setNum_responses(int num_responses) {
        this.num_responses = num_responses;
    }

    public String getAbout_me() {
        return about_me;
    }

    public void setAbout_me(String about_me) {
        this.about_me = about_me;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getFacebook_id() {
        return facebook_id;
    }

    public void setFacebook_id(String facebook_id) {
        this.facebook_id = facebook_id;
    }

    public boolean isUser_followed() {
        return user_followed;
    }

    public void setUser_followed(boolean user_followed) {
        this.user_followed = user_followed;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public int getNotification_flag() {
        return notification_flag;
    }

    public void setNotification_flag(int notification_flag) {
        this.notification_flag = notification_flag;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
