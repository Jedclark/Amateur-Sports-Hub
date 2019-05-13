package com.example.jedcl.sportsstats;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AddBasketballScoreActivity extends AppCompatActivity {

    private String compID;
    private String id;
    private String seasonID;
    private String homeTeamID;
    private String awayTeamID;
    private String fixtureID;
    private String userID;
    private String name;
    private String desc;
    private String participants;
    private String isActive;
    private String fixturesGenerated;
    private String sport;
    private String type;
    private int maxRound;

    private final String WIN = "3";
    private final String DRAW = "1";

    private InterfaceHelper interfaceHelper = new InterfaceHelper(this, AddBasketballScoreActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_basketball_score);
        compID = getIntent().getStringExtra("compID");
        id = getIntent().getStringExtra("id");
        seasonID = getIntent().getStringExtra("season_id");
        homeTeamID = getIntent().getStringExtra("homeTeamID");
        awayTeamID = getIntent().getStringExtra("awayTeamID");
        fixtureID = getIntent().getStringExtra("fixtureID");
        userID = getIntent().getStringExtra(User.ID_EXTRA);
        name = getIntent().getStringExtra("name");
        desc = getIntent().getStringExtra("description");
        participants = getIntent().getStringExtra("participants");
        isActive = getIntent().getStringExtra("isActive");
        fixturesGenerated = getIntent().getStringExtra("fixturesGenerated");
        sport = getIntent().getStringExtra("sport");
        type = getIntent().getStringExtra("type");
        maxRound = getIntent().getIntExtra("round", -1);
        generateTeamNames();
    }

    public void enterScores(View view){
        final String homeTeamGoals = interfaceHelper.getTextFromEditText(R.id.add_basketball_score_activity_home_score_edit);
        final String awayTeamGoals = interfaceHelper.getTextFromEditText(R.id.add_basketball_score_activity_away_score_edit);

        if(homeTeamGoals.equals("") || awayTeamGoals.equals("")){
            Toast.makeText(this, "You must enter all fields", Toast.LENGTH_SHORT).show();
        } else {
            String[] goalsArray = {homeTeamGoals, awayTeamGoals};
            final String points = Arrays.deepToString(goalsArray);

            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest request = new StringRequest(Request.Method.POST, APIHelper.FIND_TEAM_BY_ID + homeTeamID, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    System.out.println("####onResponse: " + response);
                    try {
                        Gson gs = new Gson();
                        JSONArray jsonObject = new JSONArray(response);
                        final Team[] homeTeam = gs.fromJson(jsonObject.toString(), Team[].class);

                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        StringRequest request = new StringRequest(Request.Method.POST, APIHelper.FIND_TEAM_BY_ID + awayTeamID, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                System.out.println("####onResponse: " + response);
                                try {
                                    Gson gs = new Gson();
                                    JSONArray jsonObject = new JSONArray(response);
                                    final Team[] awayTeam = gs.fromJson(jsonObject.toString(), Team[].class);

                                    //Determine the winner
                                    String winnerID;
                                    if(determinePoints(homeTeamGoals, awayTeamGoals) == 3) winnerID = homeTeamID;
                                    else if(determinePoints(awayTeamGoals, homeTeamGoals) == 3) winnerID = awayTeamID;
                                    else winnerID = "-1";

                                    //Send football score to Mongo collection
                                    Map<String, String> map = new HashMap<>();
                                    map.put("fixture_id", fixtureID);
                                    map.put("round", String.valueOf(maxRound));
                                    map.put("points", points);
                                    map.put("winner_id", winnerID);
                                    map.put("season_id", seasonID);
                                    APIHelper.sendData(getApplicationContext(), Request.Method.POST, APIHelper.ADD_BASKETBALL_SCORE, map);

                                    //If it is a league, update the league table
                                    if(type.equals("league")){
                                        int home = Integer.parseInt(homeTeamGoals);
                                        int away = Integer.parseInt(awayTeamGoals);
                                        updateBasketballPerformance(home, away);
                                    }

                                    //Mark score as entered
                                    Map<String, String> editMap = new HashMap<>();
                                    editMap.put("scores_entered", "1");
                                    APIHelper.sendData(getApplicationContext(), Request.Method.POST, APIHelper.EDIT_FIXTURE + fixtureID, editMap);

                                    if(type.equals("league")){
                                        Intent goBack = new Intent(getApplicationContext(), ChooseGameWeekActivity.class);
                                        goBack.putExtra("compID", compID);
                                        goBack.putExtra("id", id);
                                        goBack.putExtra("sport", sport);
                                        goBack.putExtra("type", type);
                                        goBack.putExtra(User.ID_EXTRA, userID);
                                        goBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(goBack);
                                    } else {
                                        Intent goBack = new Intent(getApplicationContext(), ViewSeasonActivity.class);
                                        goBack.putExtra("compID", compID);
                                        goBack.putExtra(User.ID_EXTRA, userID);
                                        goBack.putExtra("id", id);
                                        goBack.putExtra("name", name);
                                        goBack.putExtra("description", desc);
                                        goBack.putExtra("participants", participants);
                                        goBack.putExtra("isActive", isActive);
                                        goBack.putExtra("fixturesGenerated", fixturesGenerated);
                                        goBack.putExtra("sport", sport);
                                        goBack.putExtra("type", type);
                                        goBack.putExtra("isCupScoreEntered", true);
                                        goBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(goBack);
                                    }
                                } catch(JSONException e1){
                                    e1.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.println("####onErrorResponse: " + error);
                                error.printStackTrace();
                            }
                        });
                        queue.add(request);
                    } catch(JSONException e1){
                        e1.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("####onErrorResponse: " + error);
                    error.printStackTrace();
                }
            });
            queue.add(request);
        }
    }

    private void updateBasketballPerformance(final int homeTeamGoals, final int awayTeamGoals){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, APIHelper.FIND_TEAMS_PERFORMANCE + homeTeamID + "/" + seasonID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("####onResponse: " + response);
                try {
                    Gson gs = new Gson();
                    JSONArray jsonObject = new JSONArray(response);
                    Performance[] homePerformance = gs.fromJson(jsonObject.toString(), Performance[].class);
                    Performance home = homePerformance[0];
                    Map<String, String> homeMap = new HashMap<>();
                    if(homeTeamGoals > awayTeamGoals){
                        int points = Integer.parseInt(home.getPoints()) + Integer.parseInt(WIN);
                        int wins = Integer.parseInt(home.getWins()) + 1;
                        homeMap.put("points", String.valueOf(points));
                        homeMap.put("wins", String.valueOf(wins));
                    } else if(homeTeamGoals == awayTeamGoals){
                        int points = Integer.parseInt(home.getPoints()) + Integer.parseInt(DRAW);
                        int draws = Integer.parseInt(home.getWins()) + 1;
                        homeMap.put("points", String.valueOf(points));
                        homeMap.put("draws", String.valueOf(draws));
                    } else {
                        int losses = Integer.parseInt(home.getLosses()) + 1;
                        homeMap.put("losses", String.valueOf(losses));
                    }
                    APIHelper.sendData(getApplicationContext(), Request.Method.POST, APIHelper.EDIT_PERFORMANCE + home.getId(), homeMap);

                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    StringRequest request = new StringRequest(Request.Method.GET, APIHelper.FIND_TEAMS_PERFORMANCE + awayTeamID + "/" + seasonID, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println("####onResponse: " + response);
                            try {
                                Gson gs = new Gson();
                                JSONArray jsonObject = new JSONArray(response);
                                Performance[] awayPerformance = gs.fromJson(jsonObject.toString(), Performance[].class);
                                Performance away = awayPerformance[0];
                                Map<String, String> awayMap = new HashMap<>();
                                if(awayTeamGoals > homeTeamGoals){
                                    int points = Integer.parseInt(away.getPoints()) + Integer.parseInt(WIN);
                                    int wins = Integer.parseInt(away.getWins()) + 1;
                                    awayMap.put("points", String.valueOf(points));
                                    awayMap.put("wins", String.valueOf(wins));
                                } else if(homeTeamGoals == awayTeamGoals){
                                    int points = Integer.parseInt(away.getPoints()) + Integer.parseInt(DRAW);
                                    int draws = Integer.parseInt(away.getDraws()) + 1;
                                    awayMap.put("points", String.valueOf(points));
                                    awayMap.put("draws", String.valueOf(draws));
                                } else {
                                    int losses = Integer.parseInt(away.getLosses()) + 1;
                                    awayMap.put("losses", String.valueOf(losses));
                                }
                                APIHelper.sendData(getApplicationContext(), Request.Method.POST, APIHelper.EDIT_PERFORMANCE + away.getId(), awayMap);
                            } catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("####onErrorResponse: " + error);
                        }
                    });
                    queue.add(request);

                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("####onErrorResponse: " + error);
            }
        });
        queue.add(request);
    }

    private int determinePoints(String teamOneGoals, String teamTwoGoals){
        int teamOne = Integer.parseInt(teamOneGoals);
        int teamTwo = Integer.parseInt(teamTwoGoals);
        if(teamOne > teamTwo){
            return 3;
        } else if(teamOne == teamTwo){
            return 1;
        } else {
            return 0;
        }
    }

    private void generateTeamNames(){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, APIHelper.FIND_TEAM_BY_ID + homeTeamID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("####onResponse: " + response);
                try {
                    Gson gs = new Gson();
                    JSONArray jsonObject = new JSONArray(response);
                    final Team[] homeTeam = gs.fromJson(jsonObject.toString(), Team[].class);
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    StringRequest request = new StringRequest(Request.Method.GET, APIHelper.FIND_TEAM_BY_ID + awayTeamID, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println("####onResponse: " + response);
                            try {
                                Gson gs = new Gson();
                                JSONArray jsonObject = new JSONArray(response);
                                final Team[] awayTeam = gs.fromJson(jsonObject.toString(), Team[].class);
                                interfaceHelper.setTextView(R.id.add_football_score_activity_home_team_name, homeTeam[0].getTeamName());
                                interfaceHelper.setTextView(R.id.add_football_score_activity_away_team_name, awayTeam[0].getTeamName());
                            } catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("####onErrorResponse: " + error);
                        }
                    });
                    queue.add(request);
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("####onErrorResponse: " + error);
            }
        });
        queue.add(request);
    }

}
