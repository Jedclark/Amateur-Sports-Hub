package com.example.jedcl.sportsstats;

import com.google.gson.annotations.SerializedName;

public class TennisGame {

    @SerializedName("season_id") private String seasonID;
    @SerializedName("fixture_id") private String fixtureID;
    @SerializedName("winner_id") private String winnerID;
    @SerializedName("sets") private int[][] sets;
    @SerializedName("round") private String round;

    public TennisGame(String seasonID, String fixtureID, String winnerID, int[][] sets, String round) {
        this.seasonID = seasonID;
        this.fixtureID = fixtureID;
        this.winnerID = winnerID;
        this.sets = sets;
        this.round = round;
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

    public int[][] getSets() {
        return sets;
    }

    public void setSets(int[][] sets) {
        this.sets = sets;
    }

    public String getSeasonID() {
        return seasonID;
    }

    public void setSeasonID(String seasonID) {
        this.seasonID = seasonID;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }


}
