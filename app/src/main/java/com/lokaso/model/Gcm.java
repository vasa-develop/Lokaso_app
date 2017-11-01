package com.lokaso.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Androcid-6 on 08-11-2016.
 */

public class Gcm {

    @SerializedName("to_user_id")
    int to_user_id;

    @SerializedName("notification_type")
    int notificationType;

    @SerializedName("title")
    String title;

    @SerializedName("message")
    String message;

    @SerializedName("created_date")
    String created_date;

    @SerializedName("details")
    GcmDetail details;

    public static class GcmDetail {

        @SerializedName("chat_id")
        int chat_id;

        @SerializedName("chat_message")
        String chat_message;

        @SerializedName("profile")
        Profile profile;

        @SerializedName("suggestion")
        Suggestion suggestion;

        @SerializedName("query")
        Queries query;

        @SerializedName("query_response")
        QueryResponse queryResponse;

        @SerializedName("adhoc")
        Adhoc adhoc;

        public int getChat_id() {
            return chat_id;
        }

        public void setChat_id(int chat_id) {
            this.chat_id = chat_id;
        }

        public String getChat_message() {
            return chat_message;
        }

        public void setChat_message(String chat_message) {
            this.chat_message = chat_message;
        }

        public Profile getProfile() {
            return profile;
        }

        public void setProfile(Profile profile) {
            this.profile = profile;
        }

        public Suggestion getSuggestion() {
            return suggestion;
        }

        public void setSuggestion(Suggestion suggestion) {
            this.suggestion = suggestion;
        }

        public Queries getQuery() {
            return query;
        }

        public void setQuery(Queries query) {
            this.query = query;
        }

        public QueryResponse getQueryResponse() {
            return queryResponse;
        }

        public void setQueryResponse(QueryResponse queryResponse) {
            this.queryResponse = queryResponse;
        }

        public Adhoc getAdhoc() {
            return adhoc;
        }
    }

    public int getTo_user_id() {
        return to_user_id;
    }

    public void setTo_user_id(int to_user_id) {
        this.to_user_id = to_user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public GcmDetail getDetails() {
        return details;
    }

    public void setDetails(GcmDetail details) {
        this.details = details;
    }
}
