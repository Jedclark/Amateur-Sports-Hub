package com.example.jedcl.sportsstats;

import com.google.gson.annotations.SerializedName;

public class BasketballGame {

    @SerializedName("season_id") private String seasonID;
    @SerializedName("fixture_id") private String fixtureID;
    @SerializedName("winner_id") private String winnerID;
    @SerializedName("points") private int[] points;
    @SerializedName("round") private String round;

    public BasketballGame(String seasonID, String fixtureID, String winnerID, int[] points, String round) {
        this.seasonID = seasonID;
        this.fixtureID = fixtureID;
        this.winnerID = winnerID;
        this.points = points;
        this.round = round;
    }

    public String getSeasonID() {
        return seasonID;
    }

    public void setSeasonID(String seasonID) {
        this.seasonID = seasonID;
    }

    public String getFixtureID() {
        return fixtureID;
    }

    public void setFixtureID(String fixtureID) {
        this.fixtureID = fixtureID;
    }

    public String getWinnerID() {
        return winnerID;
    }

    public void setWinnerID(String winnerID) {
        this.winnerID = winnerID;
    }

    public int[] getPoints() {
        return points;
    }

    public void setPoints(int[] points) {
        this.points = points;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

}
