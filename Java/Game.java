package com.example.jedcl.sportsstats;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class Game implements Comparable<Game> {

    @SerializedName("id") private String id;
    @SerializedName("season_id") private String seasonID;
    @SerializedName("home_team_id") private String homeTeamID;
    @SerializedName("away_team_id") private String awayTeamID;
    @SerializedName("date") private String date;
    @SerializedName("round") private String round;
    @SerializedName("scores_entered") private String scoresEntered;
    @SerializedName("sport") private String sport;
    @SerializedName("type") private String type;
    @SerializedName("competition_id") private String competitionID;
    @SerializedName("teams") private List<Team> teams;
    @SerializedName("opponent") private List<Team> opponentTeam;
    @SerializedName("address") private String address;
    @SerializedName("town") private String town;
    @SerializedName("post_code") private String postCode;

    public Game(){ }

    public Game(String id, String seasonID, String homeTeamID, String awayTeamID, String date, String round) {
        this.id = id;
        this.seasonID = seasonID;
        this.homeTeamID = homeTeamID;
        this.awayTeamID = awayTeamID;
        this.date = date;
        this.round = round;
    }

    public Game(String id, String seasonID, String homeTeamID, String awayTeamID, String date, String round, String scoresEntered) {
        this.id = id;
        this.seasonID = seasonID;
        this.homeTeamID = homeTeamID;
        this.awayTeamID = awayTeamID;
        this.date = date;
        this.round = round;
        this.scoresEntered = scoresEntered;
    }

    public Game(String id, String seasonID, String homeTeamID, String awayTeamID, String date, String round, String sport, String type, String competitionID) {
        this.id = id;
        this.seasonID = seasonID;
        this.homeTeamID = homeTeamID;
        this.awayTeamID = awayTeamID;
        this.date = date;
        this.round = round;
        this.sport = sport;
        this.type = type;
        this.competitionID = competitionID;
    }

    public Game(String id, String seasonID, String homeTeamID, String awayTeamID, String date, String round, String sport, String type, String competitionID, List<Team> teams) {
        this.id = id;
        this.seasonID = seasonID;
        this.homeTeamID = homeTeamID;
        this.awayTeamID = awayTeamID;
        this.date = date;
        this.round = round;
        this.sport = sport;
        this.type = type;
        this.competitionID = competitionID;
        this.teams = teams;
    }

    public Game(String id, String seasonID, String homeTeamID, String awayTeamID, String date, String round, String type, List<Team> opponentTeam) {
        this.id = id;
        this.seasonID = seasonID;
        this.homeTeamID = homeTeamID;
        this.awayTeamID = awayTeamID;
        this.date = date;
        this.round = round;
        this.type = type;
        this.opponentTeam = opponentTeam;
    }

    public Game(String id, String seasonID, String homeTeamID, String awayTeamID, String date, String round, String type, List<Team> opponentTeam, String address, String town, String postCode) {
        this.id = id;
        this.seasonID = seasonID;
        this.homeTeamID = homeTeamID;
        this.awayTeamID = awayTeamID;
        this.date = date;
        this.round = round;
        this.type = type;
        this.opponentTeam = opponentTeam;
        this.address = address;
        this.town = town;
        this.postCode = postCode;
    }

    @Override
    public String toString() {
        return "Game ID: " + id + ", Date: " + date;
    }

    @Override
    public int compareTo(Game o) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date thisDate = sdf.parse(date);
            Date otherDate = sdf.parse(o.getDate());
            return thisDate.compareTo(otherDate);
        } catch(ParseException pe){
            pe.printStackTrace();
        }
        return 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSeasonID() {
        return seasonID;
    }

    public void setSeasonID(String seasonID) {
        this.seasonID = seasonID;
    }

    public String getHomeTeamID() {
        return homeTeamID;
    }

    public void setHomeTeamID(String homeTeamID) {
        this.homeTeamID = homeTeamID;
    }

    public String getAwayTeamID() {
        return awayTeamID;
    }

    public void setAwayTeamID(String awayTeamID) {
        this.awayTeamID = awayTeamID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public String getScoresEntered() { return scoresEntered; }

    public void setScoresEntered(String scoresEntered) { this.scoresEntered = scoresEntered; }

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

    public String getCompetitionID() {
        return competitionID;
    }

    public void setCompetitionID(String competitionID) {
        this.competitionID = competitionID;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public List<Team> getOpponentTeam() {
        return opponentTeam;
    }

    public void setOpponentTeam(List<Team> opponentTeam) {
        this.opponentTeam = opponentTeam;
    }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public String getTown() { return town; }

    public void setTown(String town) { this.town = town; }

    public String getPostCode() { return postCode; }

    public void setPostCode(String postCode) { this.postCode = postCode; }

}
