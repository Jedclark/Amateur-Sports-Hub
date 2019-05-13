package com.example.jedcl.sportsstats;

import com.google.gson.annotations.SerializedName;

public class Competition {

    @SerializedName("id") private String id;
    @SerializedName("user_id") private String userId;
    @SerializedName("name") private String name;
    @SerializedName("description") private String description;
    @SerializedName("sport") private String sport;
    @SerializedName("type") private String type;

    public Competition(String id, String userId, String name, String description, String sport, String type) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.sport = sport;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
