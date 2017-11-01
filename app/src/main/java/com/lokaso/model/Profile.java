package com.lokaso.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Androcid on 22-Aug-16.
 */
public class Profile implements Serializable {

    private int id;
    private int to_user_id;
    private String name;
    private String fname;
    private String lname;
    private String email;
    private int num_asks;
    private int num_responses;
    private int discovery_count;
    private int query_count;
    private int following_count;
    private String about_me;
    private boolean report;
    private int profession_id;
    private String profession;
    private String location;
    private double current_lat;
    private double current_lng;
    private String provider;
    private String facebook_id;
    private boolean user_followed;
    private String image;
    private String created_date;
    private int notification_flag;
    private int credits;
    private String credits_display;
    private int distance;
    private String distance_display;
    private int chat_unread_count;
    private String refer_code;

    @SerializedName("interest_list")
    private List<AddInterest> interestList;

    public Profile() {
    }

    public Profile(int id, int to_user_id, String name, String email, int num_asks, int num_responses, int discovery_count,
                   int query_count, int following_count, String about_me, boolean report, String profession, String location,
                   double current_lat, double current_lng, String provider, String facebook_id, boolean user_followed, String image,
                   String created_date, int notification_flag, int credits, int distance) {
        this.id = id;
        this.to_user_id = to_user_id;
        this.name = name;
        this.email = email;
        this.num_asks = num_asks;
        this.num_responses = num_responses;
        this.discovery_count = discovery_count;
        this.query_count = query_count;
        this.following_count = following_count;
        this.about_me = about_me;
        this.report = report;
        this.profession = profession;
        this.location = location;
        this.current_lat = current_lat;
        this.current_lng = current_lng;
        this.provider = provider;
        this.facebook_id = facebook_id;
        this.user_followed = user_followed;
        this.image = image;
        this.created_date = created_date;
        this.notification_flag = notification_flag;
        this.credits = credits;
        this.distance = distance;
    }

    public Profile(int id, String name, String email, int num_asks, int num_responses, int discovery_count, int query_count,
                   int following_count, String about_me, boolean report, String profession, String location, double current_lat,
                   double current_lng, String provider, String facebook_id, boolean user_followed, String image, String created_date,
                   int notification_flag, int credits, int distance) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.num_asks = num_asks;
        this.num_responses = num_responses;
        this.discovery_count = discovery_count;
        this.query_count = query_count;
        this.following_count = following_count;
        this.about_me = about_me;
        this.report = report;
        this.profession = profession;
        this.location = location;
        this.current_lat = current_lat;
        this.current_lng = current_lng;
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

    public int getTo_user_id() {
        return to_user_id;
    }

    public void setTo_user_id(int to_user_id) {
        this.to_user_id = to_user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
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

    public int getDiscovery_count() {
        return discovery_count;
    }

    public void setDiscovery_count(int discovery_count) {
        this.discovery_count = discovery_count;
    }

    public int getQuery_count() {
        return query_count;
    }

    public void setQuery_count(int query_count) {
        this.query_count = query_count;
    }

    public int getFollowing_count() {
        return following_count;
    }

    public void setFollowing_count(int following_count) {
        this.following_count = following_count;
    }

    public String getAbout_me() {
        return about_me;
    }

    public void setAbout_me(String about_me) {
        this.about_me = about_me;
    }

    public boolean isReport() {
        return report;
    }

    public void setReport(boolean report) {
        this.report = report;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public int getProfession_id() {
        return profession_id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getCurrent_lat() {
        return current_lat;
    }

    public void setCurrent_lat(double current_lat) {
        this.current_lat = current_lat;
    }

    public double getCurrent_lng() {
        return current_lng;
    }

    public void setCurrent_lng(double current_lng) {
        this.current_lng = current_lng;
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

    public String getCredits_display() {
        return credits_display;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getDistance_display() {
        return distance_display;
    }

    public String getRefer_code() {
        return refer_code;
    }

    public void setRefer_code(String refer_code) {
        this.refer_code = refer_code;
    }

    public int getChat_unread_count() {
        return chat_unread_count;
    }

    public void setChat_unread_count(int chat_unread_count) {
        this.chat_unread_count = chat_unread_count;
    }

    public List<AddInterest> getInterestList() {
        return interestList;
    }

    public void setInterestList(List<AddInterest> interestList) {
        this.interestList = interestList;
    }
}
