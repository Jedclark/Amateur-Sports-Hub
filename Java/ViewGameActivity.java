package com.example.jedcl.sportsstats;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

public class ViewGameActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private String address;
    private LatLng location;
    private Marker gameLocationMarker;
    private String compID;
    private String userID;
    private String isFinished;
    private String fixtureID;
    private String sport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_game);
        setTitle(R.string.view_game_activity_title);
        compID = getIntent().getStringExtra("compID");
        userID = getIntent().getStringExtra(User.ID_EXTRA);
        isFinished = getIntent().getStringExtra("isFinished");
        fixtureID = getIntent().getStringExtra("fixtureID");
        sport = getIntent().getStringExtra("sport");
        System.out.println("Sport: " + sport);
        System.out.println("isFinished: " + isFinished);
        System.out.println("fixtureID: " + fixtureID);
        System.out.println("compID: " + compID);
        renderGame(getIntent().getExtras());

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.view_game_activity_map);
        mapFragment.getMapAsync(this);

        address = getIntent().getStringExtra("where");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        findLatLngFromAddress(address, map);
    }

    private void findLatLngFromAddress(String address, final GoogleMap map){
        address = address.replaceAll(" ", "%20");
        final String API_CALL = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=AIzaSyDqeEH9WYQeNc91QphK2riIROhJcfYl1wE";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, API_CALL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Double lat = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                    Double lng = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                    location = new LatLng(lat, lng);
                    gameLocationMarker = map.addMarker(new MarkerOptions().position(location).title("Your Game"));
                    gameLocationMarker.showInfoWindow();
                    map.moveCamera(CameraUpdateFactory.newLatLng(location));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 12.0f));
                    map.getUiSettings().setMapToolbarEnabled(true);
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

    private void renderGame(Bundle extras){
        if(extras != null){
            if(extras.get("what") != null){
                TextView nameText = findViewById(R.id.view_game_activity_game_text);
                nameText.setText(extras.getString("what"));
            }
            if(extras.get("date") != null){
                TextView whenText = findViewById(R.id.view_game_activity_when_text);
                whenText.setText(extras.getString("date"));
            }
        }
        if(isFinished != null && sport != null && isFinished.equals("1")){
            String API_CALL;
            if(sport.equals("tennis")) API_CALL = APIHelper.FIND_TENNIS_GAME_BY_ID + fixtureID;
            else if(sport.equals("football")) API_CALL = APIHelper.FIND_FOOTBALL_GAME_BY_ID + fixtureID;
            else API_CALL = APIHelper.FIND_BASKETBALL_GAME_BY_ID + fixtureID;

            //view_game_activity_score_text
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest request = new StringRequest(Request.Method.POST, API_CALL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    System.out.println("####onResponse: " + response);
                    try {
                        Gson gs = new Gson();
                        JSONArray jsonObject = new JSONArray(response);
                        Object[] gameArray;
                        if(sport.equals("tennis")){
                            gameArray = gs.fromJson(jsonObject.toString(), TennisGame[].class);
                            TennisGame game = (TennisGame)gameArray[0];
                            renderScore(game);
                        }
                        if(sport.equals("football")){
                            gameArray = gs.fromJson(jsonObject.toString(), FootballGame[].class);
                            FootballGame game = (FootballGame)gameArray[0];
                            renderScore(game);
                        }
                        if(sport.equals("basketball")){
                            gameArray = gs.fromJson(jsonObject.toString(), BasketballGame[].class);
                            BasketballGame game = (BasketballGame)gameArray[0];
                            renderScore(game);
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
        }
    }

    private void renderScore(Object game){
        if(game instanceof TennisGame){
            RequestQueue queue = Volley.newRequestQueue(this);
            String API_CALL = APIHelper.FIND_TEAM_BY_ID + ((TennisGame) game).getWinnerID();
            StringRequest request = new StringRequest(Request.Method.GET, API_CALL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    System.out.println("####onResponse: " + response);
                    try {
                        Gson gs = new Gson();
                        JSONArray jsonObject = new JSONArray(response);
                        Team[] teams = gs.fromJson(jsonObject.toString(), Team[].class);
                        //TODO: change to showing the sets?
                        ((TextView)findViewById(R.id.view_game_activity_score_text)).setText(String.format(
                                Locale.getDefault(), " Game won by %s", teams[0].getTeamName()));
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
        } else if(game instanceof FootballGame){
            final int[] goals = ((FootballGame) game).getGoals();
            RequestQueue queue = Volley.newRequestQueue(this);
            String API_CALL = APIHelper.FIND_TEAM_BY_ID + ((FootballGame) game).getWinnerID();
            StringRequest request = new StringRequest(Request.Method.GET, API_CALL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    System.out.println("####onResponse: " + response);
                    try {
                        Gson gs = new Gson();
                        JSONArray jsonObject = new JSONArray(response);
                        Team[] teams = gs.fromJson(jsonObject.toString(), Team[].class);
                        ((TextView)findViewById(R.id.view_game_activity_score_text)).setText(String.format(
                                Locale.getDefault(), " Game won by %s\n%d - %d", teams[0].getTeamName(),
                                goals[0], goals[1]));
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
        } else if(game instanceof BasketballGame){
            RequestQueue queue = Volley.newRequestQueue(this);
            String API_CALL = APIHelper.FIND_TEAM_BY_ID + ((BasketballGame) game).getWinnerID();
            StringRequest request = new StringRequest(Request.Method.GET, API_CALL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    System.out.println("####onResponse: " + response);
                    try {
                        Gson gs = new Gson();
                        JSONArray jsonObject = new JSONArray(response);
                        Team[] teams = gs.fromJson(jsonObject.toString(), Team[].class);
                        ((TextView)findViewById(R.id.view_game_activity_score_text)).setText(String.format(
                                Locale.getDefault(), " Game won by %s", teams[0].getTeamName()));
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

}
