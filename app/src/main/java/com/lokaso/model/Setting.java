package com.lokaso.model;

/**
 * Created by Androcid on 12-Aug-16.
 */
public class Setting {

    private int version_code;
    private String version_name;
    private int version_update_type;

    public Setting(int version_code, String version_name, int version_update_type) {
        this.version_code = version_code;
        this.version_name = version_name;
        this.version_update_type = version_update_type;
    }

    public int getVersion_code() {
        return version_code;
    }

    public void setVersion_code(int version_code) {
        this.version_code = version_code;
    }

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public int getVersion_update_type() {
        return version_update_type;
    }

    public void setVersion_update_type(int version_update_type) {
        this.version_update_type = version_update_type;
    }
}
