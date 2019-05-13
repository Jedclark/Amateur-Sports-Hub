package com.example.jedcl.sportsstats;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SearchView;

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

public class FindUserActivity extends AppCompatActivity {

    private String teamID;
    private ListView usersList;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);
        teamID = getIntent().getStringExtra("teamID");
        usersList = findViewById(R.id.find_user_activity_list);
        userID = getIntent().getStringExtra(User.ID_EXTRA);
        renderSearchView(R.id.find_users_activity_search);
    }


    private void renderSearchView(int id){
        SearchView searchView = findViewById(id);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        String address = APIHelper.SEARCH_USERS + query + "/" + userID + "/" + teamID;
        StringRequest request = new StringRequest(Request.Method.GET, address, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("####onResponse: " + response);
                try {
                    Gson gs = new Gson();
                    JSONArray jsonObject = new JSONArray(response);
                    User[] users = gs.fromJson(jsonObject.toString(), User[].class); //All the users that fit the search query
                    processSearch(users);
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

    private void processSearch(User[] users){
        usersList.setAdapter(new UserSearchAdapter(this, users, teamID, userID));
    }

}
