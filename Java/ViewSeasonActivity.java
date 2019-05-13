package com.example.jedcl.sportsstats;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputContentInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ViewSeasonActivity extends AppCompatActivity {

    private String compID;
    private String id;
    private String name;
    private String desc;
    private String participants;
    private String isActive;
    private String fixturesGenerated;
    private String sport;
    private String type;
    private ListView teamList;
    private Team[] currentTeams;
    private String userID;

    private int maxRound;

    private InterfaceHelper interfaceHelper = new InterfaceHelper(this, ViewSeasonActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_season);
        compID = getIntent().getStringExtra("compID");
        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        desc = getIntent().getStringExtra("description");
        participants = getIntent().getStringExtra("participants");
        isActive = getIntent().getStringExtra("isActive");
        fixturesGenerated = getIntent().getStringExtra("fixturesGenerated");
        sport = getIntent().getStringExtra("sport");
        type = getIntent().getStringExtra("type");
        interfaceHelper.setTextView(R.id.view_season_activity_name_text, name);
        interfaceHelper.setTextView(R.id.view_season_activity_desc_text, desc);
        userID = getIntent().getStringExtra(User.ID_EXTRA);
        setTitle(R.string.view_season_activity_title);
        renderInterface();
        if(getIntent().getExtras() != null){
            if(getIntent().getExtras().getBoolean("isCupScoreEntered")){
                Toast.makeText(this, "The score has been added", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void renderInterface(){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, APIHelper.FIND_TEAMS_IN_SEASON + id + "/" + sport, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("#### GET CURRENT TEAMS - onResponse: " + response);
                if(!(response.equals(""))){
                    try {
                        Gson gs = new Gson();
                        JSONArray jsonObject = new JSONArray(response);
                        currentTeams = gs.fromJson(jsonObject.toString(), Team[].class);
                        //Render relevant interface
                        if(currentTeams != null){
                            if(Integer.parseInt(isActive) == Season.INACTIVE && currentTeams.length < Integer.parseInt(participants)){
                                //RENDER AN INACTIVE SEASON THAT DOESN'T HAVE THE RIGHT AMOUNT OF TEAMS
                                System.out.println("RENDER AN INACTIVE SEASON THAT DOESN'T HAVE THE RIGHT AMOUNT OF TEAMS");
                                renderInactiveSeason();
                            } else if(currentTeams.length == Integer.parseInt(participants) && Integer.parseInt(isActive) == Season.INACTIVE){
                                //RENDER A SEASON THAT HASN'T STARTED YET, BUT HAS THE RIGHT AMOUNT OF TEAMS
                                System.out.println("RENDER A SEASON THAT HASN'T STARTED YET, BUT HAS THE RIGHT AMOUNT OF TEAMS");
                                if(type.equals("league")){
                                    renderInactiveSeasonWithCorrectTeams();
                                }
                                else {
                                    renderInactiveCupWithCorrectTeams();
                                }
                            } else if(currentTeams.length == Integer.parseInt(participants) && Integer.parseInt(isActive) == Season.ACTIVE
                                    && Integer.parseInt(fixturesGenerated) == Season.FIXTURES_NOT_GENERATED){
                                //RENDER A SEASON THAT IS ACTIVE BUT YET TO GENERATE FIXTURES
                                System.out.println("RENDER A SEASON THAT IS ACTIVE BUT YET TO GENERATE FIXTURES");
                                renderActiveSeasonWithoutFixtures();
                            } else if(Integer.parseInt(fixturesGenerated) == Season.FIXTURES_GENERATED){
                                //RENDER A SEASON THAT IS ACTIVE AND ITS FIXTURES HAVE BEEN GENERATED
                                System.out.println("RENDER A SEASON THAT IS ACTIVE AND ITS FIXTURES HAVE BEEN GENERATED");
                                if(type.equals("league")){
                                    renderActiveSeasonWithFixtures();
                                } else {
                                    renderActiveCupWithFixtures();
                                }
                            }
                        }
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }  else {
                    //RENDER A NEWLY-CREATED SEASON WITHOUT ANY TEAMS
                    System.out.println("//RENDER A NEWLY-CREATED SEASON WITHOUT ANY TEAMS");
                    findViewById(R.id.view_season_activity_start_season_button).setVisibility(View.GONE);
                    findViewById(R.id.view_season_activity_table_container).setVisibility(View.GONE);
                    findViewById(R.id.view_season_activity_tournament_fixtures_container).setVisibility(View.GONE);

                    teamList = findViewById(R.id.view_season_activity_list_view);
                    renderSearchView(R.id.view_season_activity_find_team_search_view);
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

    private void renderInactiveSeason(){
        findViewById(R.id.view_season_activity_table_container).setVisibility(View.GONE);
        findViewById(R.id.view_season_activity_start_season_button).setVisibility(View.GONE);
        findViewById(R.id.view_season_activity_tournament_fixtures_container).setVisibility(View.GONE);
        findViewById(R.id.view_season_activity_tournament_fixtures_container).setVisibility(View.GONE);

        TextView activeText = interfaceHelper.createTextView(String.format(Locale.getDefault(), "%s %d %s",
                "Your season still doesn't have enough teams to start. You need", Integer.parseInt(participants), "teams."));
        ((LinearLayout)findViewById(R.id.view_season_activity_container)).addView(activeText, 2);

        teamList = findViewById(R.id.view_season_activity_list_view);
        renderSearchView(R.id.view_season_activity_find_team_search_view);
        renderCurrentTeams();
    }

    private void renderInactiveCupWithCorrectTeams(){
        findViewById(R.id.view_season_activity_list_view).setVisibility(View.GONE);
        findViewById(R.id.view_season_activity_table_container).setVisibility(View.GONE);
        findViewById(R.id.view_season_activity_find_team_search_view).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.view_season_activity_start_season_button)).setText("Start Tournament");
        renderCurrentTeams();

        findViewById(R.id.view_season_activity_start_season_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Generate the tournament's initial round of fixtures, and add to the database
                ArrayList<String> IDs = new ArrayList<>();
                for(Team t : currentTeams){
                    IDs.add(t.getId());
                }
                int numberOfGames = currentTeams.length / 2;
                final String[] date = interfaceHelper.getTextFromTextView(R.id.view_season_activity_games_start_text).split("/");
                final String[] time = interfaceHelper.getTextFromTextView(R.id.view_season_activity_game_time_text).split(":");
                boolean validDateTime = true;
                for(String s : date) if(s.equals("")) validDateTime = false;
                for(String s : time) if(s.equals("")) validDateTime = false;
                if(validDateTime){
                    for(int i = 0; i < numberOfGames; i++){
                        System.out.println("VALUE OF I: " + i);
                        if(i < numberOfGames-1){ //Last two teams
                            Map<String, String> map = new HashMap<>();
                            map.put("season_id", id);
                            map.put("home_team_id", IDs.get(0));
                            IDs.remove(0);
                            int index = (int)(Math.random() * (IDs.size()));
                            map.put("away_team_id", IDs.get(index));
                            map.put("date", getFormattedDateString(date[2], date[1], date[0], time[0], time[1], 1));
                            map.put("round", "1");
                            IDs.remove(index);
                            APIHelper.sendData(getApplicationContext(), Request.Method.POST, APIHelper.ADD_FIXTURE, map);
                        } else { //If there are more than two teams left
                            Map<String, String> map = new HashMap<>();
                            map.put("season_id", id);
                            map.put("home_team_id", IDs.get(0));
                            map.put("away_team_id", IDs.get(1));
                            map.put("date", getFormattedDateString(date[2], date[1], date[0], time[0], time[1], 1));
                            map.put("round", "1");
                            APIHelper.sendData(getApplicationContext(), Request.Method.POST, APIHelper.ADD_FIXTURE, map);
                        }
                    }
                    //Mark the tournament as active, and the fixtures as generated
                    Map<String, String> map = new HashMap<>();
                    map.put("is_active", "1");
                    map.put("fixtures_generated", "1");
                    APIHelper.sendData(getApplicationContext(), Request.Method.POST, APIHelper.EDIT_SEASON + id, map);

                    //Send user back to HomeActivity
                    Intent cupIsActive = new Intent(getApplicationContext(), HomeActivity.class);
                    cupIsActive.putExtra(User.ID_EXTRA, userID);
                    cupIsActive.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    cupIsActive.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    cupIsActive.putExtra("isCupActive", true);
                    startActivity(cupIsActive);
                } else {
                    Toast.makeText(getApplicationContext(), "You must enter a date and time", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void renderInactiveSeasonWithCorrectTeams(){
        findViewById(R.id.view_season_activity_list_view).setVisibility(View.GONE);
        findViewById(R.id.view_season_activity_table_container).setVisibility(View.GONE);
        findViewById(R.id.view_season_activity_find_team_search_view).setVisibility(View.GONE);
        findViewById(R.id.view_season_activity_tournament_fixtures_container).setVisibility(View.GONE);

        renderCurrentTeams();

        findViewById(R.id.view_season_activity_start_season_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Generate the performance rows for each team in the season
                for(final Team t : currentTeams){
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    StringRequest addPerformances = new StringRequest(Request.Method.POST, APIHelper.ADD_PERFORMANCE, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println("####onResponse: " + response);
                            //Set season's is_active status to 1
                            Map<String, String> data = new HashMap<>();
                            data.put("is_active", "1");
                            Intent seasonIsActive = new Intent(getApplicationContext(), HomeActivity.class);
                            seasonIsActive.putExtra(User.ID_EXTRA, userID);
                            seasonIsActive.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            seasonIsActive.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            seasonIsActive.putExtra("isSeasonActive", true);
                            APIHelper.sendDataWithIntent(getApplicationContext(), Request.Method.POST, APIHelper.EDIT_SEASON + id, data, seasonIsActive);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("####onErrorResponse: " + error);
                        }
                    })
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> data = new HashMap<>();
                            data.put("season_id", id);
                            data.put("team_id", t.getId());
                            return data;
                        }
                    };
                    queue.add(addPerformances);
                }
            }
        });
    }

    private void renderActiveSeasonWithoutFixtures(){
        findViewById(R.id.view_season_activity_list_view).setVisibility(View.GONE);
        findViewById(R.id.view_season_activity_start_season_button).setVisibility(View.GONE);
        findViewById(R.id.view_season_activity_find_team_search_view).setVisibility(View.GONE);
        findViewById(R.id.view_season_activity_tournament_fixtures_container).setVisibility(View.GONE);

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.GET, APIHelper.FIND_SEASON_PERFORMANCES + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("####onResponse: " + response);
                try {
                    Gson gs = new Gson();
                    JSONArray jsonObject = new JSONArray(response);
                    Performance[] performances = gs.fromJson(jsonObject.toString(), Performance[].class);
                    for(Team t : currentTeams){
                        renderTeamForLeagueTable(t, performances);
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

        LinearLayout container = findViewById(R.id.view_season_activity_container);
        Button generateFixtures = interfaceHelper.createButton(R.string.view_season_activity_generate_fixtures_button);
        generateFixtures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent generateFixtures = new Intent(getApplicationContext(), GenerateFixturesActivity.class);
                generateFixtures.putExtra(User.ID_EXTRA, userID);
                generateFixtures.putExtra("id", id);
                generateFixtures.putExtra("sport", sport);
                startActivity(generateFixtures);
            }
        });
        container.addView(generateFixtures);
    }

    private void renderActiveCupWithFixtures(){
        findViewById(R.id.view_season_activity_list_view).setVisibility(View.GONE);
        findViewById(R.id.view_season_activity_start_season_button).setVisibility(View.GONE);
        findViewById(R.id.view_season_activity_find_team_search_view).setVisibility(View.GONE);
        findViewById(R.id.view_season_activity_table_container).setVisibility(View.GONE);
        findViewById(R.id.view_season_activity_tournament_fixtures_container).setVisibility(View.GONE);

        final TextView teamsText = interfaceHelper.createTextView("Games Remaining:", 20.0f);
        final LinearLayout teamsContainer = interfaceHelper.createLinearLayout(LinearLayout.VERTICAL);
        interfaceHelper.addLayoutParams(teamsContainer, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        teamsContainer.setGravity(Gravity.CENTER);

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.GET, APIHelper.FIND_ALL_FIXTURES + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("####onResponse: " + response);
                try {
                    Gson gs = new Gson();
                    JSONArray jsonObject = new JSONArray(response);
                    final Game[] games = gs.fromJson(jsonObject.toString(), Game[].class);
                    maxRound = 1;
                    int numberOfGamesInThisRound = 0;
                    boolean isRoundComplete = true;
                    for(Game g : games){
                        if(Integer.parseInt(g.getRound()) > maxRound) maxRound = Integer.parseInt(g.getRound());
                        if(g.getScoresEntered().equals("0")) isRoundComplete = false;
                    }
                    for(Game g : games){ if(Integer.parseInt(g.getRound()) == maxRound) numberOfGamesInThisRound++; }
                    boolean isSeasonFinished = false;
                    if(numberOfGamesInThisRound == 1){
                        for(Game g : games){
                            if(Integer.parseInt(g.getRound()) == maxRound){
                                if(g.getScoresEntered().equals("1")) isSeasonFinished = true;
                            }
                        }
                    }

                    if(isRoundComplete && numberOfGamesInThisRound > 1){ //Semi finals or earlier, at least 2 games played, all games in this round are complete
                        findViewById(R.id.view_season_activity_tournament_fixtures_container).setVisibility(View.VISIBLE);
                        teamsText.setVisibility(View.GONE);
                        Button startNextRound = interfaceHelper.createButton(R.string.view_season_activity_start_next_round_button, R.drawable.border);
                        interfaceHelper.setMargin(startNextRound, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,
                                new int[]{0, 10, 0, 0});
                        startNextRound.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Generate the next round of fixtures
                                final ArrayList<String> winnerIDs = new ArrayList<>();
                                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                StringRequest request = new StringRequest(Request.Method.GET, getMongoQuery(sport), new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        System.out.println("MONGO RESPONSE: " + response);
                                        try {
                                            Gson gs = new Gson();
                                            JSONArray jsonObject = new JSONArray(response);
                                            Object[] games;
                                            if(sport.equals("tennis")){
                                                games = gs.fromJson(jsonObject.toString(), TennisGame[].class);
                                            } else if(sport.equals("football")){
                                                games = gs.fromJson(jsonObject.toString(), FootballGame[].class);
                                            } else {
                                                games = gs.fromJson(jsonObject.toString(), BasketballGame[].class);
                                            }

                                            for(Object g : games){
                                                if(g instanceof TennisGame) winnerIDs.add(((TennisGame)g).getWinnerID());
                                                if(g instanceof FootballGame) winnerIDs.add(((FootballGame)g).getWinnerID());
                                                if(g instanceof BasketballGame) winnerIDs.add(((BasketballGame)g).getWinnerID());
                                            }
                                            int numberOfGames = winnerIDs.size() / 2;
                                            final String[] date = interfaceHelper.getTextFromTextView(R.id.view_season_activity_games_start_text).split("/");
                                            final String[] time = interfaceHelper.getTextFromTextView(R.id.view_season_activity_game_time_text).split(":");
                                            boolean validDateTime = true;
                                            for(String s : date) if(s.equals("")) validDateTime = false;
                                            for(String s : time) if(s.equals("")) validDateTime = false;
                                            if(validDateTime){
                                                for(int i = 0; i < numberOfGames; i++){
                                                    if(i < numberOfGames-1){ //Last two teams
                                                        Map<String, String> map = new HashMap<>();
                                                        map.put("season_id", id);
                                                        map.put("home_team_id", winnerIDs.get(0));
                                                        winnerIDs.remove(0);
                                                        int index = (int)(Math.random() * (winnerIDs.size()));
                                                        map.put("away_team_id", winnerIDs.get(index));
                                                        map.put("date", getFormattedDateString(date[2], date[1], date[0], time[0], time[1], 1));
                                                        map.put("round", String.valueOf(maxRound + 1));
                                                        winnerIDs.remove(index);
                                                        APIHelper.sendData(getApplicationContext(), Request.Method.POST, APIHelper.ADD_FIXTURE, map);
                                                    } else { //If there are more than two teams left
                                                        Map<String, String> map = new HashMap<>();
                                                        map.put("season_id", id);
                                                        map.put("home_team_id", winnerIDs.get(0));
                                                        map.put("away_team_id", winnerIDs.get(1));
                                                        map.put("date", getFormattedDateString(date[2], date[1], date[0], time[0], time[1], 1));
                                                        map.put("round", String.valueOf(maxRound + 1));
                                                        APIHelper.sendData(getApplicationContext(), Request.Method.POST, APIHelper.ADD_FIXTURE, map);
                                                    }
                                                }

                                                Intent refresh = new Intent(getApplicationContext(), ViewSeasonActivity.class);
                                                refresh.putExtra(User.ID_EXTRA, userID);
                                                refresh.putExtra("compID", compID);
                                                refresh.putExtra("id", id);
                                                refresh.putExtra("name", name);
                                                refresh.putExtra("description", desc);
                                                refresh.putExtra("participants", participants);
                                                refresh.putExtra("isActive", isActive);
                                                refresh.putExtra("fixturesGenerated", fixturesGenerated);
                                                refresh.putExtra("sport", sport);
                                                refresh.putExtra("type", type);
                                                refresh.putExtra("maxRound", maxRound);
                                                refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(refresh);
                                            } else {
                                                Toast.makeText(getApplicationContext(), "You must enter a date and time", Toast.LENGTH_SHORT).show();
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
                        });
                        ((LinearLayout)findViewById(R.id.view_season_activity_container)).addView(startNextRound);
                    } else if(isSeasonFinished){
                        teamsText.setVisibility(View.GONE);

                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        String API_CALL;
                        if(sport.equals("tennis")){
                            API_CALL = APIHelper.FIND_TENNIS_TOURNAMENT_WINNER + id + "/" + maxRound;
                        } else if(sport.equals("football")){
                            API_CALL = APIHelper.FIND_FOOTBALL_TOURNAMENT_WINNER + id + "/" + maxRound;
                        } else {
                            API_CALL = APIHelper.FIND_BASKETBALL_TOURNAMENT_WINNER + id + "/" + maxRound;
                        }
                        StringRequest request = new StringRequest(Request.Method.GET, API_CALL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                System.out.println("####onResponse: " + response);
                                try {
                                    Gson gs = new Gson();
                                    JSONArray jsonObject = new JSONArray(response);
                                    Team[] winningTeam = gs.fromJson(jsonObject.toString(), Team[].class);
                                    Team team = winningTeam[0];
                                    TextView winner = interfaceHelper.createTextView("This tournament was won by " + team.getTeamName(), 20.0f);
                                    ((LinearLayout)findViewById(R.id.view_season_activity_container)).addView(winner, 2);
                                    interfaceHelper.addLayoutParams(winner, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    winner.setGravity(Gravity.CENTER);
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
                    } else { //Round is still ongoing, also captures numberOfGamesInThisRound == 1 (the final)
                        final ArrayList<Game> validGames = new ArrayList<>();
                        for(Game g : games){
                            if(Integer.parseInt(g.getRound()) == maxRound && g.getScoresEntered().equals("0")) validGames.add(g);
                        }
                        for(Game g : validGames){
                            if(Integer.parseInt(g.getRound()) == maxRound){
                                Team home = g.getTeams().get(0);
                                Team away = g.getTeams().get(1);
                                TextView gameInfo = interfaceHelper.createTextView(String
                                        .format(Locale.getDefault(), "%s vs %s", home.getTeamName(), away.getTeamName()));
                                teamsContainer.addView(gameInfo);
                            }
                        }

                        //Render the ability to edit scores if the user has permission
                        checkUserCanEditScores(userID);
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

        Button viewFixtures = interfaceHelper.createButton(R.string.view_season_activity_view_fixtures_button, R.drawable.border);
        interfaceHelper.setMargin(viewFixtures, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,
                new int[]{0, 10, 0, 0});
        viewFixtures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewAllFixtures = new Intent(getApplicationContext(), ViewAllSeasonsFixturesActivity.class);
                viewAllFixtures.putExtra(User.ID_EXTRA, userID);
                viewAllFixtures.putExtra("id", id);
                startActivity(viewAllFixtures);
            }
        });

        interfaceHelper.addMultipleViews(R.id.view_season_activity_container,
                new View[]{teamsText, teamsContainer, viewFixtures});
    }

    private void checkUserCanEditScores(final String userID){
        RequestQueue queue = Volley.newRequestQueue(this);
        String API_CALL = APIHelper.CHECK_COMPETITION_PERMISSIONS + userID + "/" + compID;
        StringRequest request = new StringRequest(Request.Method.GET, API_CALL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("####onResponse: " + response);
                if(!(response.equals("{\"success\":0}"))){
                    Button enterScores = interfaceHelper.createButton(R.string.view_season_activity_add_score_button, R.drawable.border);
                    interfaceHelper.setMargin(enterScores, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,
                            new int[]{0, 10, 0, 0});
                    enterScores.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent addGameScore = new Intent(getApplicationContext(), AddGameScoreActivity.class);
                            //ViewSeasonActivity Parameters: id, name, desc, participants, isActive, fixturesGenerated, sport, type, userID
                            addGameScore.putExtra(User.ID_EXTRA, userID);
                            addGameScore.putExtra("compID", compID);
                            addGameScore.putExtra("id", id);
                            addGameScore.putExtra("name", name);
                            addGameScore.putExtra("description", desc);
                            addGameScore.putExtra("participants", participants);
                            addGameScore.putExtra("isActive", isActive);
                            addGameScore.putExtra("fixturesGenerated", fixturesGenerated);
                            addGameScore.putExtra("sport", sport);
                            addGameScore.putExtra("type", type);
                            addGameScore.putExtra("maxRound", maxRound);
                            startActivity(addGameScore);
                        }
                    });
                    ((LinearLayout)findViewById(R.id.view_season_activity_container)).addView(enterScores);
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

    private String getMongoQuery(String sport){
        switch(sport){
            case "tennis":
                return APIHelper.FIND_TENNIS_GAMES_IN_ROUND + id + "/" + maxRound;
            case "football":
                return APIHelper.FIND_FOOTBALL_GAMES_IN_ROUND + id + "/" + maxRound;
            case "basketball":
                return APIHelper.FIND_BASKETBALL_GAMES_IN_ROUND + id + "/" + maxRound;
            default:
                return "";
        }
    }

    private void renderActiveSeasonWithFixtures(){
        findViewById(R.id.view_season_activity_list_view).setVisibility(View.GONE);
        findViewById(R.id.view_season_activity_start_season_button).setVisibility(View.GONE);
        findViewById(R.id.view_season_activity_find_team_search_view).setVisibility(View.GONE);
        findViewById(R.id.view_season_activity_tournament_fixtures_container).setVisibility(View.GONE);

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.GET, APIHelper.FIND_SEASON_PERFORMANCES + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("####onResponse: " + response);
                try {
                    Gson gs = new Gson();
                    JSONArray jsonObject = new JSONArray(response);
                    Performance[] performances = gs.fromJson(jsonObject.toString(), Performance[].class);
                    for(Team t : currentTeams){
                        for(Performance p : performances){
                            if(t.getId().equals(p.getTeamId())){
                                t.setPerformance(p);
                                break;
                            }
                        }
                    }
                    Arrays.sort(currentTeams);
                    for(Team t : currentTeams){
                        renderTeamForLeagueTable(t, performances);
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

        LinearLayout container = findViewById(R.id.view_season_activity_container);

        Button viewAllFixtures = interfaceHelper.createButton(R.string.view_season_activity_view_fixtures_button, R.drawable.border);
        interfaceHelper.setMargin(viewAllFixtures, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,
                new int[]{0, 10, 0, 0});
        viewAllFixtures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewAllFixtures = new Intent(getApplicationContext(), ViewAllSeasonsFixturesActivity.class);
                viewAllFixtures.putExtra("id", id);
                viewAllFixtures.putExtra(User.ID_EXTRA, userID);
                startActivity(viewAllFixtures);
            }
        });
        container.addView(viewAllFixtures);

        checkUserCanAddScoresToSeason(userID, container);
    }

    private void checkUserCanAddScoresToSeason(final String userID, final LinearLayout container){
        RequestQueue queue = Volley.newRequestQueue(this);
        String API_CALL = APIHelper.CHECK_COMPETITION_PERMISSIONS + userID + "/" + compID;
        StringRequest request = new StringRequest(Request.Method.GET, API_CALL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("####onResponse: " + response);
                if(!(response.equals("{\"success\":0}"))){
                    Button enterScores = interfaceHelper.createButton(R.string.view_season_activity_add_score_button, R.drawable.border);
                    interfaceHelper.setMargin(enterScores, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,
                            new int[]{0, 10, 0, 0});
                    enterScores.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent addGameScore = new Intent(getApplicationContext(), ChooseGameWeekActivity.class);
                            //ViewSeasonActivity Parameters: id, name, desc, participants, isActive, fixturesGenerated, sport, type, userID
                            addGameScore.putExtra(User.ID_EXTRA, userID);
                            addGameScore.putExtra("compID", compID);
                            addGameScore.putExtra("id", id);
                            addGameScore.putExtra("name", name);
                            addGameScore.putExtra("description", desc);
                            addGameScore.putExtra("participants", participants);
                            addGameScore.putExtra("isActive", isActive);
                            addGameScore.putExtra("fixturesGenerated", fixturesGenerated);
                            addGameScore.putExtra("sport", sport);
                            addGameScore.putExtra("type", type);
                            addGameScore.putExtra("maxRound", maxRound);
                            startActivity(addGameScore);
                        }
                    });
                    container.addView(enterScores);
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

    private void renderCurrentTeams(){
        final LinearLayout names = findViewById(R.id.view_season_activity_current_teams_name);
        final LinearLayout buttons = findViewById(R.id.view_season_activity_current_teams_remove);
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, APIHelper.FIND_TEAMS_IN_SEASON + id + "/" + sport, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("####onResponse: " + response);
                if(!response.equals("")){
                    try {
                        Gson gs = new Gson();
                        JSONArray jsonObject = new JSONArray(response);
                        currentTeams = gs.fromJson(jsonObject.toString(), Team[].class);
                        for(final Team t : currentTeams){
                            //Add name
                            TextView temp = interfaceHelper.createTextView(t.getTeamName(), 20.0f);
                            temp.setPadding(0,0,0, 10);
                            temp.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent viewTeam = new Intent(getApplicationContext(), ViewOtherTeamActivity.class);
                                    viewTeam.putExtra(User.ID_EXTRA, userID);
                                    viewTeam.putExtra("name", t.getTeamName());
                                    viewTeam.putExtra("desc", t.getDescription());
                                    viewTeam.putExtra("address", t.getAddress());
                                    startActivity(viewTeam);
                                }
                            });
                            names.addView(temp);

                            //Add remove button
                            ImageView remove = new ImageView(getApplicationContext());
                            remove.setImageResource(R.drawable.error);
                            remove.setPadding(0,0,0, 10);
                            remove.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                    StringRequest request = new StringRequest(Request.Method.POST, APIHelper.REMOVE_TEAM_FROM_SEASON, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Intent refresh = new Intent(getApplicationContext(), ViewSeasonActivity.class);
                                            //ViewSeasonActivity Parameters: id, name, desc, participants, isActive, fixturesGenerated, sport, type, userID
                                            refresh.putExtra(User.ID_EXTRA, userID);
                                            refresh.putExtra("compID", compID);
                                            refresh.putExtra("id", id);
                                            refresh.putExtra("name", name);
                                            refresh.putExtra("description", desc);
                                            refresh.putExtra("participants", participants);
                                            refresh.putExtra("isActive", isActive);
                                            refresh.putExtra("fixturesGenerated", fixturesGenerated);
                                            refresh.putExtra("sport", sport);
                                            refresh.putExtra("type", type);
                                            refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(refresh);
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            System.out.println("####onErrorResponse: " + error);
                                            error.printStackTrace();
                                        }
                                    })
                                    {
                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            Map<String, String> map = new HashMap<>();
                                            map.put("season_id", id);
                                            map.put("team_id", t.getId());
                                            return map;
                                        }
                                    };
                                    queue.add(request);
                                }
                            });
                            buttons.addView(remove);
                        }
                    } catch(Exception e){
                        e.printStackTrace();
                    }
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

    private void getTeamData(String query){
        RequestQueue queue = Volley.newRequestQueue(this);
        String address = APIHelper.TEAM_SEARCH + query + "/" + sport;
        StringRequest request = new StringRequest(Request.Method.GET, address, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("####onResponse: " + response);
                try {
                    Gson gs = new Gson();
                    JSONArray jsonObject = new JSONArray(response);
                    Team[] teams = gs.fromJson(jsonObject.toString(), Team[].class); //All the teams that fit the search query

                    if(currentTeams != null){
                        ArrayList<Team> temp = new ArrayList<>(Arrays.asList(teams)); //Teams that fit the search query that are not already in the season
                        temp.removeAll(Arrays.asList(currentTeams));
                        teams = temp.toArray(new Team[temp.size()]);
                    }

                    for(Team t : teams){
                        t.setSeasonId(id);
                    }
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
        TeamSearchAdapter teamSearchAdapter = new TeamSearchAdapter(this, teams, ViewSeasonActivity.class, userID);
        Intent refresh = new Intent(this, ViewSeasonActivity.class);
        //ViewSeasonActivity Parameters: id, name, desc, participants, isActive, fixturesGenerated, sport, type, userID
        refresh.putExtra(User.ID_EXTRA, userID);
        refresh.putExtra("compID", compID);
        refresh.putExtra("id", id);
        refresh.putExtra("name", name);
        refresh.putExtra("description", desc);
        refresh.putExtra("participants", participants);
        refresh.putExtra("isActive", isActive);
        refresh.putExtra("fixturesGenerated", fixturesGenerated);
        refresh.putExtra("sport", sport);
        refresh.putExtra("type", type);
        teamSearchAdapter.setIntent(refresh);
        teamList.setAdapter(teamSearchAdapter);
    }

    private void renderTeamForLeagueTable(final Team t, Performance[] performances){
        for(Performance p : performances){
            if(t.getId().equals(p.getTeamId())){
                t.setPerformance(p);
                break;
            }
        }

        LinearLayout teamContainer = (new LinearLayout(this));
        teamContainer.setOrientation(LinearLayout.HORIZONTAL);

        int num = Integer.parseInt(t.getPerformance().getWins()) + Integer.parseInt(t.getPerformance().getDraws()) + Integer.parseInt(t.getPerformance().getLosses());

        TextView teamName = renderTeamElement(t.getTeamName(), 3.0f);
        TextView played = renderTeamElement(String.valueOf(num), 1.0f);
        TextView wins = renderTeamElement(t.getPerformance().getWins(), 1.0f);
        TextView draws = renderTeamElement(t.getPerformance().getDraws(), 1.0f);
        TextView losses = renderTeamElement(t.getPerformance().getLosses(), 1.0f);
        TextView teamPoints = renderTeamElement(t.getPerformance().getPoints(), 2.0f);
        teamPoints.setTypeface(teamPoints.getTypeface(), Typeface.BOLD);

        interfaceHelper.addMultipleViews(teamContainer, new View[]{teamName, played, wins, draws, losses, teamPoints});
        teamContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewTeam = new Intent(getApplicationContext(), ViewOtherTeamActivity.class);
                viewTeam.putExtra(User.ID_EXTRA, userID);
                viewTeam.putExtra("name", t.getTeamName());
                viewTeam.putExtra("desc", t.getDescription());
                viewTeam.putExtra("address", t.getAddress());
                startActivity(viewTeam);
            }
        });

        ((LinearLayout)findViewById(R.id.view_season_activity_league_container)).addView(teamContainer);
    }

    private TextView renderTeamElement(String name, float weight){
        TextView temp = new TextView(this);
        temp.setText(name);
        temp.setGravity(Gravity.CENTER);
        temp.setBackgroundResource(R.drawable.border);
        temp.setLayoutParams(createLayoutParams(weight));
        return temp;
    }

    private LinearLayout.LayoutParams createLayoutParams(float weight){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = weight;
        return params;
    }

    public void chooseDate(View view){
        DialogFragment date = new DatePickerFragment();
        ((DatePickerFragment) date).setParentClass(DatePickerFragment.CREATE_CUP_FIXTURES);
        date.show(getSupportFragmentManager(), "show");
    }

    public void chooseTime(View view){
        DialogFragment time = new TimePickerFragment();
        ((TimePickerFragment) time).setParentClass(TimePickerFragment.CREATE_CUP_FIXTURES);
        time.show(getSupportFragmentManager(), "show");
    }

    private String getFormattedDateString(String year, String month, String day, String hours, String minutes, int freq){
        Calendar c = Calendar.getInstance();
        c.set(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day),
                Integer.parseInt(hours), Integer.parseInt(minutes));
        c.add(Calendar.WEEK_OF_YEAR, freq);
        Date date = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

}
