package com.example.jedcl.sportsstats;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

import java.util.Arrays;
import java.util.Locale;

public class ViewTeamActivity extends AppCompatActivity {

    private String id;
    private String myTeamName;
    private String userID;

    private InterfaceHelper interfaceHelper = new InterfaceHelper(this, ViewTeamActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_team);
        Bundle extras = getIntent().getExtras();
        renderTitle(extras);
        id = getIntent().getStringExtra("id");
        myTeamName = getIntent().getStringExtra("teamName");
        userID = getIntent().getStringExtra(User.ID_EXTRA);
        renderUpcomingGames();
        checkUserCanAddUsers(userID);
    }

    private void checkUserCanAddUsers(final String userID){
        RequestQueue queue = Volley.newRequestQueue(this);
        String API_CALL = APIHelper.CHECK_TEAM_PERMISSIONS + userID + "/" + id;
        StringRequest request = new StringRequest(Request.Method.GET, API_CALL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("####onResponse: " + response);
                if(response.equals("{\"success\":0}")){
                    findViewById(R.id.view_team_activity_add_user_button).setVisibility(View.GONE);
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

    public void addUser(View view){
        Intent addUser = new Intent(this, FindUserActivity.class);
        addUser.putExtra(User.ID_EXTRA, userID);
        addUser.putExtra("teamID", id);
        startActivity(addUser);
    }

    public void viewAllSeasons(View view){
        Intent viewSeasons = new Intent(this, ViewTeamsSeasonsActivity.class);
        viewSeasons.putExtra(User.ID_EXTRA, userID);
        viewSeasons.putExtra("id", id);
        startActivity(viewSeasons);
    }

    public void viewAllFixtures(View view){
        Intent viewFixtures = new Intent(this, ViewUpcomingFixturesActivity.class);
        viewFixtures.putExtra(User.ID_EXTRA, userID);
        viewFixtures.putExtra("id", id);
        viewFixtures.putExtra("myTeamName", myTeamName);
        startActivity(viewFixtures);
    }

    private void renderUpcomingGames(){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, APIHelper.FIND_GAMES_LIMIT_3 + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("####onResponse: " + response);
                try {
                    Gson gs = new Gson();
                    JSONArray jsonObject = new JSONArray(response);
                    Game[] games = gs.fromJson(jsonObject.toString(), Game[].class);
                    Arrays.sort(games);
                    for(Game g : games){
                        renderGame(g);
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

    private void renderTitle(Bundle extras){
        if(extras != null){
            if(extras.get("teamName") != null){
                setTitle(extras.get("teamName").toString());
            }
        }
    }

    private void renderGame(final Game g){
        LinearLayout gameContainer = new LinearLayout(this);
        gameContainer.setBackgroundResource(R.drawable.border);
        gameContainer.setGravity(Gravity.CENTER);

        // Set the game's sport type icon
        ImageView sportType = new ImageView(this);
        sportType.setImageResource(getCompetitionId(g.getType()));
        interfaceHelper.addLayoutParams(sportType, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1.0f);
        gameContainer.addView(sportType);

        //Set the game's information (what, when, where)
        LinearLayout infoContainer = interfaceHelper.createLinearLayout(LinearLayout.VERTICAL);
        infoContainer.setPadding(50, 0, 15, 0);
        interfaceHelper.addLayoutParams(infoContainer, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,2.0f);

        TextView what = interfaceHelper.createTextView(String.format(Locale.getDefault(), "%s vs %s",
                myTeamName, g.getOpponentTeam().get(0).getTeamName()));
        what.setTypeface(null, Typeface.BOLD);
        TextView when = interfaceHelper.createTextView(g.getDate());
        interfaceHelper.addMultipleViews(infoContainer, new View[]{what, when});
        gameContainer.addView(infoContainer);

        gameContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewGame = new Intent(getApplicationContext(), ViewGameActivity.class);
                viewGame.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                viewGame.putExtra(User.ID_EXTRA, userID);
                viewGame.putExtra("what", String.format(Locale.getDefault(), "%s vs %s", myTeamName, g.getOpponentTeam().get(0).getTeamName()));
                viewGame.putExtra("date", g.getDate());
                viewGame.putExtra("where", String.format(Locale.getDefault(), "%s %s %s", g.getAddress(), g.getTown(), g.getPostCode()));
                startActivity(viewGame);
            }
        });

        ((LinearLayout)findViewById(R.id.view_team_activity_upcoming_games_container)).addView(gameContainer);
    }

    public int getCompetitionId(String competition){
        switch(competition){
            case "tournament":
                return R.drawable.ic_tournament_64;
            case "league":
                return R.drawable.ic_league_64;
            default:
                return -1;
        }
    }

}
