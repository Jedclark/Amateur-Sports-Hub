package com.example.jedcl.sportsstats;

import com.google.gson.annotations.SerializedName;

public class Performance {

    @SerializedName("id") public String id;
    @SerializedName("season_id") public String seasonId;
    @SerializedName("team_id") public String teamId;
    @SerializedName("points") public String points;
    @SerializedName("wins") public String wins;
    @SerializedName("losses") public String losses;
    @SerializedName("draws") public String draws;

    public Performance(String id, String seasonId, String teamId, String points, String wins, String losses, String draws) {
        this.id = id;
        this.seasonId = seasonId;
        this.teamId = teamId;
        this.points = points;
        this.wins = wins;
        this.losses = losses;
        this.draws = draws;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(String seasonId) {
        this.seasonId = seasonId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getWins() {
        return wins;
    }

    public void setWins(String wins) {
        this.wins = wins;
    }

    public String getLosses() {
        return losses;
    }

    public void setLosses(String losses) {
        this.losses = losses;
    }

    public String getDraws() {
        return draws;
    }

    public void setDraws(String draws) {
        this.draws = draws;
    }

}
