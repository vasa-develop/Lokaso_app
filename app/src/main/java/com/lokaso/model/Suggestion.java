package com.lokaso.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Androcid on 19-Aug-16.
 */
public class Suggestion implements Serializable {

    private int id;
    private int fav_count;
    private int comment_count;
    private boolean user_fav;
    private boolean user_discovery;
    private boolean suggestion_spam;
    private String image;
    private String caption;
    private String suggestion;
    private Profile profile;
    private String location;
    private double lat;
    private double lng;
    private int interest_id;
    private double distance;
    private String distance_display;
    private String created_date;
    private Interest interest;

    private boolean is_share_click;

    private int type = 1;
    private int ad_position = 0;
    private List<Ads> adsList;

    public Suggestion(int id, int fav_count, int comment_count, boolean user_fav, boolean user_discovery, boolean suggestion_spam, String image,
                      String caption, String suggestion, Profile profile, String location, double lat, double lng,
                      int interest_id, double distance, String created_date) {
        this.id = id;
        this.fav_count = fav_count;
        this.user_fav = user_fav;
        this.user_discovery = user_discovery;
        this.suggestion_spam = suggestion_spam;
        this.image = image;
        this.caption = caption;
        this.suggestion = suggestion;
        this.profile = profile;
        this.location = location;
        this.lat = lat;
        this.lng = lng;
        this.interest_id = interest_id;
        this.distance = distance;
        this.created_date = created_date;
    }

    public Suggestion(int type) {
        this.type = type;
    }

    public Suggestion(List<Ads> ads, int type) {
        this.adsList = ads;
        this.type = type;
    }

    // === FOR ADS
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getAd_position() {
        return ad_position;
    }

    public void setAd_position(int ad_position) {
        this.ad_position = ad_position;
    }

    public List<Ads> getAdsList() {
        return adsList;
    }

    public void setAdsList(List<Ads> ads) {
        this.adsList = ads;
    }
    // === FOR ADS





    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFav_count() {
        return fav_count;
    }

    public void setFav_count(int fav_count) {
        this.fav_count = fav_count;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }

    public boolean isUser_fav() {
        return user_fav;
    }

    public void setUser_fav(boolean user_fav) {
        this.user_fav = user_fav;
    }

    public boolean isUser_discovery() {
        return user_discovery;
    }

    public void setUser_discovery(boolean user_discovery) {
        this.user_discovery = user_discovery;
    }

    public boolean isSuggestion_spam() {
        return suggestion_spam;
    }

    public void setSuggestion_spam(boolean suggestion_spam) {
        this.suggestion_spam = suggestion_spam;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getInterest_id() {
        return interest_id;
    }

    public void setInterest_id(int interest_id) {
        this.interest_id = interest_id;
    }

    public Interest getInterest() {
        return interest;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getDistance_display() {
        return distance_display;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public boolean is_share_click() {
        return is_share_click;
    }

    public void setIs_share_click(boolean is_share_click) {
        this.is_share_click = is_share_click;
    }
}
