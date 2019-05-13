package com.example.jedcl.sportsstats;

import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.Arrays;

public class ViewAllSeasonsFixturesActivity extends ListActivity {

    private String id;
    private Game[] games;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_seasons_fixtures);
        id = getIntent().getStringExtra("id");
        userID = getIntent().getStringExtra(User.ID_EXTRA);
        findGames();
    }

    private void findGames(){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, APIHelper.FIND_SEASONS_FIXTURES + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("####onResponse: " + response);
                try {
                    Gson gs = new Gson();
                    JSONArray jsonObject = new JSONArray(response);
                    games = gs.fromJson(jsonObject.toString(), Game[].class);
                    FixtureSearchAdapter fixtureSearchAdapter = new FixtureSearchAdapter(getApplicationContext(), games, userID);
                    setListAdapter(fixtureSearchAdapter);
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
