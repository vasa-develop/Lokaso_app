package com.lokaso.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Androcid on 14-Oct-16.
 */

public class PlaceResult implements Serializable {

    @SerializedName("predictions")
    private List<PlaceResult.Place> placeList;

    public List<PlaceResult.Place> getPlaces() {
        return placeList;
    }

    public static class Place implements Serializable {

        @SerializedName("place_id")
        String place_id;

        @SerializedName("description")
        String description;

        String name;

        @SerializedName("structured_formatting")
        StructuredFormatting structuredFormatting;

        public Place(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getPlace_id() {
            return place_id;
        }

        public String getDescription() {
            return description;
        }

        public String getName() {

            if(structuredFormatting!=null)
                name = structuredFormatting.getMain_text();
            return name;
        }

        class StructuredFormatting {

            @SerializedName("main_text")
            String main_text;

            public String getMain_text() {
                return main_text;
            }
        }

    }

}
