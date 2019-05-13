package com.example.jedcl.sportsstats;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChooseGameWeekActivity extends AppCompatActivity {

    private String compID;
    private String id;
    private String sport;
    private String type;
    private String userID;
    private InterfaceHelper interfaceHelper = new InterfaceHelper(this, ChooseGameWeekActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_game_week);
        compID = getIntent().getStringExtra("compID");
        id = getIntent().getStringExtra("id");
        sport = getIntent().getStringExtra("sport");
        type = getIntent().getStringExtra("type");
        userID = getIntent().getStringExtra(User.ID_EXTRA);
        renderWeeks();
    }

    private void renderWeeks(){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, APIHelper.FIND_FIXTURES + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("####onResponse: " + response);
                try {
                    //TODO: to optimise later on, create MultiMap class, MultiMap<String, List<Game>>, map.get("1") gets [1,4,9], etc.
                    Gson gs = new Gson();
                    JSONArray jsonObject = new JSONArray(response);
                    Game[] games = gs.fromJson(jsonObject.toString(), Game[].class); //All the teams that fit the search query
                    int maxRound = 0;
                    for(Game g : games){ if(Integer.parseInt(g.getRound()) > maxRound) maxRound = Integer.parseInt(g.getRound()); }

                    for(int i = 0; i < maxRound; i++){
                        ArrayList<String> ids = new ArrayList<>();
                        for(Game g : games){
                            if(g.getRound().equals(String.valueOf(i + 1))) ids.add(g.getId());
                        }
                        if(ids.size() > 0) generateButton(String.valueOf(i + 1), ids);
                    }

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

    private void generateButton(final String round, final ArrayList<String> ids){
        Button button = interfaceHelper.createButton(String.format(Locale.getDefault(), "Game Week %s", round));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addGameScores = new Intent(getApplicationContext(), AddGameScoreActivity.class);
                addGameScores.putExtra(User.ID_EXTRA, userID);
                addGameScores.putExtra("compID", compID);
                addGameScores.putExtra("id", id);
                addGameScores.putExtra("ids", ids);
                addGameScores.putExtra("sport", sport);
                addGameScores.putExtra("type", type);
                startActivity(addGameScores);
            }
        });
        ((LinearLayout)findViewById(R.id.choose_game_week_activity_container)).addView(button);
    }

}
