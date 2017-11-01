package com.lokaso.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Androcid on 14-Oct-16.
 */

public class PlaceDetail implements Serializable {

    @SerializedName("result")
    private Detail detail;

    public String getPlace_id() {
        return detail.getPlace_id();
    }

    public String getDescription() {
        return detail.getDescription();
    }

    public String getName() {

        String name = "";
        if(detail!=null) {
            name = detail.getName();
        }
        return name;
    }

    public String getLatitude() {
        return detail.getLatitude();
    }

    public String getLongitude() {
        return detail.getLongitude();
    }


    class Detail {

        @SerializedName("place_id")
        String place_id;

        @SerializedName("name")
        String name;

        @SerializedName("formatted_address")
        String description;

        @SerializedName("geometry")
        Geometry geometry;

        public String getPlace_id() {
            return place_id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getLatitude() {
            return geometry.getLatitude();
        }

        public String getLongitude() {
            return geometry.getLongitude();
        }

        //xgoabo

        class Geometry {

            @SerializedName("location")
            Location location;

            public String getLatitude() {
                return location.getLatitude();
            }

            public String getLongitude() {
                return location.getLongitude();
            }

            class Location {

                @SerializedName("lat")
                String latitude;

                @SerializedName("lng")
                String longitude;

                public String getLatitude() {
                    return latitude;
                }

                public String getLongitude() {
                    return longitude;
                }
            }
        }
    }
}
