package com.example.jedcl.sportsstats;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private Thread displayHomePage = new Thread(new Runnable() {
        @Override
        public void run() {
            currentClass = HOME_PAGE;
            findFixtures();
        }
    });

    private Thread displayTeamsPage = new Thread(new Runnable() {
        @Override
        public void run() {
            currentClass = TEAM_PAGE;
            renderTeams();
        }
    });

    private Thread displayTournamentsPage = new Thread(new Runnable() {
        @Override
        public void run() {
            currentClass = LEAGUE_PAGE;
            renderCompetitions();
        }
    });

    private static int currentClass = 0;
    public static final int HOME_PAGE = 1;
    public static final int TEAM_PAGE = 2;
    public static final int LEAGUE_PAGE = 3;

    private Game[] myGames;

    private String userID;
    private String token;

    private InterfaceHelper interfaceHelper = new InterfaceHelper(this, HomeActivity.this);

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = new HomeFragment();

            switch (item.getItemId()) {
                case R.id.bottom_nav_home:
                    selectedFragment = new HomeFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .runOnCommit(displayHomePage)
                            .commit();
                    break;

                case R.id.bottom_nav_teams:
                    selectedFragment = new TeamsFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .runOnCommit(displayTeamsPage)
                            .commit();
                    break;

                case R.id.bottom_nav_competitions:
                    selectedFragment = new CompetitionsFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .runOnCommit(displayTournamentsPage)
                            .commit();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        userID = getIntent().getStringExtra(User.ID_EXTRA);
        token = getIntent().getStringExtra("token");

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            renderToastSuccessMessages(extras, navigation);
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .runOnCommit(displayHomePage)
                    .commit();
        }
    }

    private void renderToastSuccessMessages(Bundle extras, BottomNavigationView navigation){
        if(extras.getBoolean("isTeamCreated")){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new TeamsFragment())
                    .runOnCommit(displayTeamsPage)
                    .commit();
            navigation.getMenu().findItem(R.id.bottom_nav_teams).setChecked(true);
            Toast.makeText(this, R.string.enter_address_activity_team_create_success, Toast.LENGTH_SHORT).show();
        } else if(extras.getBoolean("isTeamJoined")){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new TeamsFragment())
                    .runOnCommit(displayTeamsPage)
                    .commit();
            navigation.getMenu().findItem(R.id.bottom_nav_teams).setChecked(true);
            Toast.makeText(this, R.string.find_team_activity_success, Toast.LENGTH_SHORT).show();
        }
        else if(extras.getBoolean("isCompCreated")){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CompetitionsFragment())
                    .runOnCommit(displayTournamentsPage)
                    .commit();
            navigation.getMenu().findItem(R.id.bottom_nav_competitions).setChecked(true);
            Toast.makeText(this, R.string.create_comp_details_activity_create_success, Toast.LENGTH_SHORT).show();
        } else if(extras.getBoolean("isSeasonCreated")){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CompetitionsFragment())
                    .runOnCommit(displayTournamentsPage)
                    .commit();
            navigation.getMenu().findItem(R.id.bottom_nav_competitions).setChecked(true);
            Toast.makeText(this, R.string.create_season_activity_create_success, Toast.LENGTH_SHORT).show();
        } else if(extras.getBoolean("isSeasonActive")){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CompetitionsFragment())
                    .runOnCommit(displayTournamentsPage)
                    .commit();
            navigation.getMenu().findItem(R.id.bottom_nav_competitions).setChecked(true);
            Toast.makeText(this, R.string.view_season_activity_season_active_text, Toast.LENGTH_SHORT).show();
        } else if(extras.getBoolean("isFixturesGenerated")){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CompetitionsFragment())
                    .runOnCommit(displayTournamentsPage)
                    .commit();
            navigation.getMenu().findItem(R.id.bottom_nav_competitions).setChecked(true);
            Toast.makeText(this, R.string.generate_fixtures_activity_success, Toast.LENGTH_SHORT).show();
        } else if(extras.getBoolean("isTeamAddedToSeason")){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CompetitionsFragment())
                    .runOnCommit(displayTournamentsPage)
                    .commit();
            navigation.getMenu().findItem(R.id.bottom_nav_competitions).setChecked(true);
            Toast.makeText(this, R.string.view_season_activity_team_add_success, Toast.LENGTH_SHORT).show();
        } else if(extras.getBoolean("isUserJoinedTeam")){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new TeamsFragment())
                    .runOnCommit(displayTeamsPage)
                    .commit();
            navigation.getMenu().findItem(R.id.bottom_nav_teams).setChecked(true);
            Toast.makeText(this, R.string.view_team_activity_user_add_success, Toast.LENGTH_SHORT).show();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .runOnCommit(displayHomePage)
                    .commit();
        }
    }

    public void searchTeamsActivity(View view){
        Intent searchTeams = new Intent(this, SearchTeamsActivity.class);
        searchTeams.putExtra(User.ID_EXTRA, userID);
        startActivity(searchTeams);
    }

    public void renderCompetitions(){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, APIHelper.FIND_COMPETITIONS + userID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("####onResponse: " + response);
                if(!(response.equals(""))){
                    try {
                        Gson gs = new Gson();
                        JSONArray jsonObject = new JSONArray(response);
                        Competition[] competitions = gs.fromJson(jsonObject.toString(), Competition[].class);
                        for(Competition c : competitions){
                            renderCompetitionInfo(c);
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
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-TOKEN", token);
                return headers;
            }
        };
        queue.add(request);
    }

    public void renderTeams(){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, APIHelper.FIND_TEAMS_I_PLAY_FOR + userID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("####onResponse: " + response);
                if(!(response.equals(""))){
                    try {
                        Gson gs = new Gson();
                        JSONArray jsonObject = new JSONArray(response);
                        Team[] teams = gs.fromJson(jsonObject.toString(), Team[].class);
                        for(Team t : teams){
                            renderTeamInfo(t);
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
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-TOKEN", token);
                return headers;
            }
        };
        queue.add(request);
    }

    public void chooseSport(View view){
        if(currentClass == TEAM_PAGE){
            Intent chooseSport = new Intent(this, ChooseSportActivity.class);
            chooseSport.putExtra("type", TEAM_PAGE);
            chooseSport.putExtra(User.ID_EXTRA, userID);
            startActivity(chooseSport);
        } else {
            Intent chooseSport = new Intent(this, ChooseSportActivity.class);
            chooseSport.putExtra("type", LEAGUE_PAGE);
            chooseSport.putExtra(User.ID_EXTRA, userID);
            startActivity(chooseSport);
        }
    }

    private void findFixtures(){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, APIHelper.FIND_MY_GAMES + userID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("####onResponse: " + response);
                if(!(response.equals(""))){
                    try {
                        Gson gs = new Gson();
                        JSONArray jsonObject = new JSONArray(response);
                        myGames = gs.fromJson(jsonObject.toString(), Game[].class);
                        for(Game g : myGames){
                            if(g.getScoresEntered().equals("0")){
                                renderGameInfo(g);
                            }
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
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-TOKEN", token);
                return headers;
            }
        };
        queue.add(request);
    }

    public void renderGameInfo(final Game g){
        LinearLayout gameContainer = new LinearLayout(this);
        gameContainer.setBackgroundResource(R.drawable.border);
        gameContainer.setGravity(Gravity.CENTER);

        // Set the game's sport type icon
        ImageView sportType = new ImageView(this);
        interfaceHelper.addLayoutParams(sportType, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        sportType.setImageResource(getSportsId(g.getSport()));

        //Set the game's information (what, when, where)
        LinearLayout infoContainer = new LinearLayout(this);
        interfaceHelper.addLayoutParams(infoContainer, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,3.0f);
        infoContainer.setPadding(50, 0, 15, 0);
        infoContainer.setOrientation(LinearLayout.VERTICAL);

        TextView what = interfaceHelper.createTextView(String.format(Locale.getDefault(), "%s vs %s",
                g.getTeams().get(0).getTeamName(), g.getTeams().get(1).getTeamName()));
        what.setTypeface(null, Typeface.BOLD);

        TextView when = interfaceHelper.createTextView(g.getDate());
        TextView where = interfaceHelper.createTextView(String.format(Locale.getDefault(), "%s, %s, %s",
                g.getTeams().get(0).getAddress(), g.getTeams().get(0).getTown(), g.getTeams().get(0).getPostCode()));

        interfaceHelper.addMultipleViews(infoContainer, new View[]{what, when, where});

        //Set the type of competition of the game (league, tournament, cup)
        ImageView gameType = new ImageView(this);
        interfaceHelper.addLayoutParams(gameType, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1.0f);
        gameType.setImageResource(getCompetitionId(g.getType()));

        interfaceHelper.addMultipleViews(gameContainer, new View[]{sportType, infoContainer, gameType});

        gameContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gameView = new Intent(getApplicationContext(), ViewGameActivity.class);
                gameView.putExtra("compID", g.getCompetitionID());
                gameView.putExtra("what", String.format(Locale.getDefault(), "%s vs %s",
                        g.getTeams().get(0).getTeamName(), g.getTeams().get(1).getTeamName()));
                gameView.putExtra("date", g.getDate());
                gameView.putExtra("where", String.format(Locale.getDefault(), "%s %s %s",
                        g.getTeams().get(0).getAddress(), g.getTeams().get(0).getTown(), g.getTeams().get(0).getPostCode()));
                startActivity(gameView);
            }
        });

        ((LinearLayout)findViewById(R.id.my_games_container)).addView(gameContainer);
    }

    public void renderTeamInfo(final Team t){
        LinearLayout teamContainer = new LinearLayout(this);
        teamContainer.setBackgroundResource(R.drawable.border);
        teamContainer.setGravity(Gravity.CENTER);

        // Set the game's sport type icon
        ImageView sportType = new ImageView(this);
        interfaceHelper.addLayoutParams(sportType, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1.0f);
        sportType.setImageResource(getSportsId(t.getSport()));

        //Set the game's information (what, when, where)
        LinearLayout infoContainer = new LinearLayout(this);
        interfaceHelper.addLayoutParams(infoContainer, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,3.0f);
        infoContainer.setPadding(50, 0, 15, 0);
        infoContainer.setOrientation(LinearLayout.VERTICAL);

        TextView team = interfaceHelper.createTextView(t.getTeamName());
        team.setTypeface(null, Typeface.BOLD);
        infoContainer.addView(team);
        interfaceHelper.addMultipleViews(teamContainer, new View[]{sportType, infoContainer});
        teamContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewTeam = new Intent(getApplicationContext(), ViewTeamActivity.class);
                viewTeam.putExtra(User.ID_EXTRA, userID);
                viewTeam.putExtra("id", t.getId());
                viewTeam.putExtra("teamName", t.getTeamName());
                viewTeam.putExtra("teamDesc", t.getDescription());
                startActivity(viewTeam);
            }
        });

        ((LinearLayout)findViewById(R.id.my_teams_container)).addView(teamContainer);
    }

    public void renderCompetitionInfo(final Competition c){
        LinearLayout compContainer = new LinearLayout(this);
        compContainer.setBackgroundResource(R.drawable.border);
        compContainer.setGravity(Gravity.CENTER);

        // Set the competition's sport type icon
        ImageView sportType = new ImageView(this);
        interfaceHelper.addLayoutParams(sportType, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1.0f);
        sportType.setImageResource(getSportsId(c.getSport()));

        //Set the game's information (what, when, where)
        LinearLayout infoContainer = new LinearLayout(this);
        interfaceHelper.addLayoutParams(infoContainer, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,3.0f);
        infoContainer.setPadding(50, 0, 15, 0);
        infoContainer.setOrientation(LinearLayout.VERTICAL);

        TextView team = interfaceHelper.createTextView(c.getName());
        team.setTypeface(null, Typeface.BOLD);

        infoContainer.addView(team);

        // Set the competition's type icon
        ImageView compType = new ImageView(this);
        compType.setImageResource(getCompetitionId(c.getType()));
        interfaceHelper.addLayoutParams(compType, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1.0f);
        interfaceHelper.addMultipleViews(compContainer, new View[]{sportType, infoContainer, compType});

        compContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewComp = new Intent(getApplicationContext(), ViewCompetitionActivity.class);
                viewComp.putExtra(User.ID_EXTRA, userID);
                viewComp.putExtra("compName", c.getName());
                viewComp.putExtra("id", c.getId());
                viewComp.putExtra("sport", c.getSport());
                viewComp.putExtra("type", c.getType());
                startActivity(viewComp);
            }
        });

        ((LinearLayout)findViewById(R.id.my_competitions_container)).addView(compContainer);
    }

    public int getSportsId(String sport){
        switch(sport){
            case "football":
                return R.drawable.ic_football_64;
            case "tennis":
                return R.drawable.ic_tennis_64;
            case "basketball":
                return R.drawable.ic_basketball_64;
            default:
                return -1;
        }
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
