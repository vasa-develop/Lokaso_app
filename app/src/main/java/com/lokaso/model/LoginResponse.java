package com.lokaso.model;

/**
 * Created by Androcid on 06-Aug-16.
 */
public class LoginResponse {

    private int id;
    private String name;
    private String image;
    private String refer_code;
    private Profile profile;
    private boolean is_register;

    public LoginResponse(int id, String name, String image, String refer_code) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.refer_code = refer_code;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRefer_code() {
        return refer_code;
    }

    public void setRefer_code(String refer_code) {
        this.refer_code = refer_code;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public boolean is_register() {
        return is_register;
    }

    public void setIs_register(boolean is_register) {
        this.is_register = is_register;
    }
}
