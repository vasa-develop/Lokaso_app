package com.lokaso.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Laksh on 18-Feb-17.
 */

public class PlaceListLatLng implements Serializable {

    @SerializedName("results")
    private List<Detail> detail;

    public List<Detail> getPlaces() {
        return detail;
    }

    public class Detail {

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

        public String getDescription() {
            return description;
        }

        public String getName() {
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
