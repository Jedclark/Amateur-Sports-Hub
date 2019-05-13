package com.example.jedcl.sportsstats;

import com.google.gson.annotations.SerializedName;

public class Team implements Comparable<Team> {

    public static final String TEAM_NAME = "Team name";
    public static final int TEAM_NAME_MIN_LENGTH = 5;
    public static final int TEAM_NAME_MAX_LENGTH = 30;

    public static final String TEAM_NICK = "Nickname";
    public static final int TEAM_NICK_MIN_LENGTH = 3;
    public static final int TEAM_NICK_MAX_LENGTH = 20;

    public static final String TEAM_DESC = "Description";
    public static final int TEAM_DESC_MIN_LENGTH = 10;
    public static final int TEAM_DESC_MAX_LENGTH = 100;

    public static final String TEAM_ADDRESS = "Address";
    public static final int TEAM_ADDRESS_MIN_LENGTH = 10;
    public static final int TEAM_ADDRESS_MAX_LENGTH = 100;

    public static final String TEAM_TOWN = "Town";
    public static final int TEAM_TOWN_MIN_LENGTH = 0;
    public static final int TEAM_TOWN_MAX_LENGTH = 100;

    public static final String TEAM_POST_CODE = "Postcode";
    public static final int TEAM_POST_CODE_MIN_LENGTH = 6;
    public static final int TEAM_POST_CODE_MAX_LENGTH = 100;

    public static final String TEAM_INSTRUCTIONS = "Instructions";
    public static final int TEAM_INSTRUCTIONS_MIN_LENGTH = 0;
    public static final int TEAM_INSTRUCTIONS_MAX_LENGTH = 500;

    @SerializedName("id") private String id;
    @SerializedName("user_id") private String user_id;
    @SerializedName("sport") private String sport;
    @SerializedName("team_name") private String teamName;
    @SerializedName("nick_name") private String nickName;
    @SerializedName("description") private String description;
    @SerializedName("year_created") private String yearCreated;
    @SerializedName("address") private String address;
    @SerializedName("town") private String town;
    @SerializedName("post_code") private String postCode;
    @SerializedName("instructions") private String instructions;

    private String seasonId;
    private Performance performance;

    @Override
    public String toString(){
        return "Team Name: " + teamName;
    }

    public Team(String id){
        this.id = id;
    }

    public Team(String id, String user_id, String sport, String teamName, String nickName, String description, String yearCreated, String address, String town, String postCode, String instructions) {
        this.id = id;
        this.user_id = user_id;
        this.sport = sport;
        this.teamName = teamName;
        this.nickName = nickName;
        this.description = description;
        this.yearCreated = yearCreated;
        this.address = address;
        this.town = town;
        this.postCode = postCode;
        this.instructions = instructions;
    }

    @Override
    public int compareTo(Team o) {
        final int thisPoints = Integer.parseInt(performance.getPoints());
        final int otherPoints = Integer.parseInt(o.getPerformance().getPoints());
        if(performance != null){
            return Integer.compare(otherPoints, thisPoints);
        }
        return 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeam_name(String team_name) {
        this.teamName = team_name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getYearCreated() {
        return yearCreated;
    }

    public void setYearCreated(String yearCreated) {
        this.yearCreated = yearCreated;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getSeasonId() { return seasonId; }

    public void setSeasonId(String seasonId) { this.seasonId = seasonId; }

    public Performance getPerformance() { return performance; }

    public void setPerformance(Performance performance) { this.performance = performance; }

    @Override
    public boolean equals(Object o){
        return id.equals(((Team) o).getId());
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + Integer.parseInt(id);
        return result;
    }

}
