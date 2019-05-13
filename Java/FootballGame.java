package com.example.jedcl.sportsstats;

import com.google.gson.annotations.SerializedName;

public class FootballGame {

    @SerializedName("season_id") private String seasonID;
    @SerializedName("fixture_id") private String fixtureID;
    @SerializedName("winner_id") private String winnerID;
    @SerializedName("goals") private int[] goals;
    @SerializedName("round") private String round;

    public FootballGame(String seasonID, String fixtureID, String winnerID, int[] goals, String round) {
        this.seasonID = seasonID;
        this.fixtureID = fixtureID;
        this.winnerID = winnerID;
        this.goals = goals;
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

    public int[] getGoals() {
        return goals;
    }

    public void setGoals(int[] goals) {
        this.goals = goals;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

}
