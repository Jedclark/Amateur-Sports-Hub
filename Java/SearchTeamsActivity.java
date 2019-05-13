package com.example.jedcl.sportsstats;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;

public class SearchTeamsActivity extends AppCompatActivity {

    private ListView teamList;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_teams);
        userID = getIntent().getStringExtra(User.ID_EXTRA);
        renderSearchView(R.id.search_teams_activity_search);
        teamList = findViewById(R.id.search_teams_activity_list_view);
    }

    private void renderSearchView(int id){
        android.widget.SearchView searchView = findViewById(id);
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.length() > 0){
                    getTeamData(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length() > 0){
                    getTeamData(newText);
                }
                return true;
            }
        });
    }

    private void getTeamData(String query){
        RequestQueue queue = Volley.newRequestQueue(this);
        String address = APIHelper.SEARCH_ALL_TEAMS + query + "/" + userID;
        StringRequest request = new StringRequest(Request.Method.GET, address, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("####onResponse: " + response);
                try {
                    Gson gs = new Gson();
                    JSONArray jsonObject = new JSONArray(response);
                    Team[] teams = gs.fromJson(jsonObject.toString(), Team[].class); //All the teams that fit the search query
                    processSearch(teams);
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

    private void processSearch(Team[] teams){
        teamList.setAdapter(new TeamSearchAdapter(this, teams, SearchTeamsActivity.class, userID));
    }

}
