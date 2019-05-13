package com.example.jedcl.sportsstats;

import com.google.gson.annotations.SerializedName;

public class Season {

    @SerializedName("id") private String id;
    @SerializedName("competition_id") private String competitionId;
    @SerializedName("name") private String name;
    @SerializedName("description") private String description;
    @SerializedName("participants") private String participants;
    @SerializedName("is_active") private String isActive;
    @SerializedName("fixtures_generated") private String fixturesGenerated;
    @SerializedName("sport") private String sport;
    @SerializedName("type") private String type;

    public static final int INACTIVE = 0;
    public static final int ACTIVE = 1;
    public static final int FIXTURES_NOT_GENERATED = 0;
    public static final int FIXTURES_GENERATED = 1;

    public Season(String id, String competitionId, String name, String description, String participants, String isActive, String fixturesGenerated) {
        this.id = id;
        this.competitionId = competitionId;
        this.name = name;
        this.description = description;
        this.participants = participants;
        this.isActive = isActive;
        this.fixturesGenerated = fixturesGenerated;
    }

    public Season(String id, String competitionId, String name, String description, String participants, String isActive, String fixturesGenerated, String sport, String type) {
        this.id = id;
        this.competitionId = competitionId;
        this.name = name;
        this.description = description;
        this.participants = participants;
        this.isActive = isActive;
        this.fixturesGenerated = fixturesGenerated;
        this.sport = sport;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(String competitionId) {
        this.competitionId = competitionId;
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

    public String getParticipants() {
        return participants;
    }

    public void setParticipants(String participants) {
        this.participants = participants;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getFixturesGenerated() { return fixturesGenerated; }

    public void setFixturesGenerated(String fixturesGenerated) { this.fixturesGenerated = fixturesGenerated; }

    public String getSport() { return sport; }

    public void setSport(String sport) { this.sport = sport; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }
}
