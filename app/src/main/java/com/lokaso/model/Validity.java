package com.lokaso.model;

/**
 * Created by Androcid on 14-Oct-16.
 */

public class Validity {

    private String valid_until;
    private String valid_date;

    public Validity(String valid_until, String valid_date) {
        this.valid_until = valid_until;
        this.valid_date = valid_date;
    }

    public String getValid_until() {
        return valid_until;
    }

    public void setValid_until(String valid_until) {
        this.valid_until = valid_until;
    }

    public String getValid_date() {
        return valid_date;
    }

    public void setValid_date(String valid_date) {
        this.valid_date = valid_date;
    }
}
