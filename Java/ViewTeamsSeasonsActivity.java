package com.example.jedcl.sportsstats;

import android.app.ListActivity;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;

public class ViewTeamsSeasonsActivity extends ListActivity {

    private String userID;
    private String id;
    private Season[] seasons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_teams_seasons);
        id = getIntent().getStringExtra("id");
        userID = getIntent().getStringExtra(User.ID_EXTRA);
        findSeasons();
    }

    private void findSeasons(){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, APIHelper.FIND_TEAMS_SEASONS + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("####onResponse: " + response);
                try {
                    Gson gs = new Gson();
                    JSONArray jsonObject = new JSONArray(response);
                    seasons = gs.fromJson(jsonObject.toString(), Season[].class);
                    SeasonSearchAdapter seasonAdapter = new SeasonSearchAdapter(getApplicationContext(), seasons, userID);
                    setListAdapter(seasonAdapter);
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
