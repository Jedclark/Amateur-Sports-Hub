package com.example.jedcl.sportsstats;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddGameScoreActivity extends AppCompatActivity {

    private String compID;
    private String id;
    private ArrayList<String> ids;
    private String name;
    private String desc;
    private String participants;
    private String isActive;
    private String fixturesGenerated;
    private String sport;
    private String type;
    private String userID;
    private int maxRound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game_score);
        compID = getIntent().getStringExtra("compID");
        id = getIntent().getStringExtra("id");
        ids = getIntent().getStringArrayListExtra("ids");
        name = getIntent().getStringExtra("name");
        desc = getIntent().getStringExtra("description");
        participants = getIntent().getStringExtra("participants");
        isActive = getIntent().getStringExtra("isActive");
        fixturesGenerated = getIntent().getStringExtra("fixturesGenerated");
        sport = getIntent().getStringExtra("sport");
        type = getIntent().getStringExtra("type");
        userID = getIntent().getStringExtra(User.ID_EXTRA);
        maxRound = getIntent().getIntExtra("maxRound", -1);
        if(maxRound == -1){
            for(String id : ids){
                findGame(id);
            }
        } else {
            findGamesInRound(maxRound);
        }
    }

    private void findGamesInRound(int maxRound){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, APIHelper.FIND_GAMES_BY_ROUND + id + "/" + maxRound, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("####onResponse: " + response);
                try {
                    Gson gs = new Gson();
                    JSONArray jsonObject = new JSONArray(response);
                    Game[] games = gs.fromJson(jsonObject.toString(), Game[].class); //All the teams that fit the search query
                    for(Game g : games) renderGame(g);
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

    private void findGame(String id){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, APIHelper.FIND_SPECIFIC_FIXTURE + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("####onResponse: " + response);
                try {
                    Gson gs = new Gson();
                    JSONArray jsonObject = new JSONArray(response);
                    Game[] game = gs.fromJson(jsonObject.toString(), Game[].class); //All the teams that fit the search query
                    renderGame(game[0]);
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

    private void renderGame(final Game game){
        final LinearLayout container = findViewById(R.id.add_game_score_activity_container);
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, APIHelper.FIND_TEAM_BY_ID + game.getHomeTeamID(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("####onResponse: " + response);
                try {
                    Gson gs = new Gson();
                    JSONArray jsonObject = new JSONArray(response);
                    final Team[] homeTeam = gs.fromJson(jsonObject.toString(), Team[].class); //All the teams that fit the search query
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    StringRequest request = new StringRequest(Request.Method.GET, APIHelper.FIND_TEAM_BY_ID + game.getAwayTeamID(), new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println("####onResponse: " + response);
                            try {
                                Gson gs = new Gson();
                                JSONArray jsonObject = new JSONArray(response);
                                final Team[] awayTeam = gs.fromJson(jsonObject.toString(), Team[].class); //All the teams that fit the search query
                                Button gameInfo = new Button(getApplicationContext());
                                gameInfo.setText(String.format(Locale.getDefault(), "%s vs %s", homeTeam[0].getTeamName(), awayTeam[0].getTeamName()));
                                gameInfo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        chooseScoreSheet(sport, game.getSeasonID(), homeTeam[0].getId(), awayTeam[0].getId(), game.getId(), type);
                                    }
                                });
                                container.addView(gameInfo);
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

    private void chooseScoreSheet(String sport, String seasonID, String homeTeamID, String awayTeamID, String fixtureID, String type){
        switch(sport){
            case "football":
                Intent addFootballScore = new Intent(this, AddFootballScoreActivity.class);
                addFootballScore.putExtra(User.ID_EXTRA, userID);
                addFootballScore.putExtra("compID", compID);
                addFootballScore.putExtra("season_id", seasonID);
                addFootballScore.putExtra("id", id);
                addFootballScore.putExtra("homeTeamID", homeTeamID);
                addFootballScore.putExtra("awayTeamID", awayTeamID);
                addFootballScore.putExtra("fixtureID", fixtureID);
                addFootballScore.putExtra("type", type);
                addFootballScore.putExtra("round", maxRound);
                addFootballScore.putExtra("name", name);
                addFootballScore.putExtra("description", desc);
                addFootballScore.putExtra("participants", participants);
                addFootballScore.putExtra("isActive", isActive);
                addFootballScore.putExtra("fixturesGenerated", fixturesGenerated);
                addFootballScore.putExtra("sport", sport);
                startActivity(addFootballScore);
                break;
            case "tennis":
                Intent addTennisScore = new Intent(this, AddTennisScoreActivity.class);
                addTennisScore.putExtra("compID", compID);
                addTennisScore.putExtra(User.ID_EXTRA, userID);
                addTennisScore.putExtra("season_id", seasonID);
                addTennisScore.putExtra("id", id);
                addTennisScore.putExtra("homeTeamID", homeTeamID);
                addTennisScore.putExtra("awayTeamID", awayTeamID);
                addTennisScore.putExtra("fixtureID", fixtureID);
                addTennisScore.putExtra("type", type);
                addTennisScore.putExtra("round", maxRound);
                addTennisScore.putExtra("name", name);
                addTennisScore.putExtra("description", desc);
                addTennisScore.putExtra("participants", participants);
                addTennisScore.putExtra("isActive", isActive);
                addTennisScore.putExtra("fixturesGenerated", fixturesGenerated);
                addTennisScore.putExtra("sport", sport);
                startActivity(addTennisScore);
                break;
            case "basketball":
                Intent addBasketballScore = new Intent(this, AddBasketballScoreActivity.class);
                addBasketballScore.putExtra(User.ID_EXTRA, userID);
                addBasketballScore.putExtra("compID", compID);
                addBasketballScore.putExtra("season_id", seasonID);
                addBasketballScore.putExtra("id", id);
                addBasketballScore.putExtra("homeTeamID", homeTeamID);
                addBasketballScore.putExtra("awayTeamID", awayTeamID);
                addBasketballScore.putExtra("fixtureID", fixtureID);
                addBasketballScore.putExtra("type", type);
                addBasketballScore.putExtra("round", maxRound);
                addBasketballScore.putExtra("name", name);
                addBasketballScore.putExtra("description", desc);
                addBasketballScore.putExtra("participants", participants);
                addBasketballScore.putExtra("isActive", isActive);
                addBasketballScore.putExtra("fixturesGenerated", fixturesGenerated);
                addBasketballScore.putExtra("sport", sport);
                startActivity(addBasketballScore);
                break;
        }
    }

}
