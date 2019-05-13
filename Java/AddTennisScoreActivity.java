package com.example.jedcl.sportsstats;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Locale;
import java.util.Map;

public class AddTennisScoreActivity extends AppCompatActivity {

    private Spinner numberOfSetsSpinner;
    private String compID;
    private String id;
    private String seasonID;
    private String homeTeamID;
    private String awayTeamID;
    private String fixtureID;
    private String name;
    private String desc;
    private String participants;
    private String isActive;
    private String fixturesGenerated;
    private String sport;
    private String type;
    private String userID;
    private int maxRound;

    private ArrayList<EditText> homeTeamSets = new ArrayList<>();
    private ArrayList<EditText> awayTeamSets = new ArrayList<>();

    private final int WIN = 3;
    private final int DRAW = 1;

    private InterfaceHelper interfaceHelper = new InterfaceHelper(this, AddTennisScoreActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tennis_score);
        numberOfSetsSpinner = generateSpinner(R.id.add_tennis_score_sets_spinner, R.array.add_tennis_score_num_sets_spinner);
        compID = getIntent().getStringExtra("compID");
        id = getIntent().getStringExtra("id");
        seasonID = getIntent().getStringExtra("season_id");
        homeTeamID = getIntent().getStringExtra("homeTeamID");
        awayTeamID = getIntent().getStringExtra("awayTeamID");
        fixtureID = getIntent().getStringExtra("fixtureID");
        name = getIntent().getStringExtra("name");
        desc = getIntent().getStringExtra("description");
        participants = getIntent().getStringExtra("participants");
        isActive = getIntent().getStringExtra("isActive");
        fixturesGenerated = getIntent().getStringExtra("fixturesGenerated");
        sport = getIntent().getStringExtra("sport");
        type = getIntent().getStringExtra("type");
        userID = getIntent().getStringExtra(User.ID_EXTRA);
        maxRound = getIntent().getIntExtra("round", -1);
    }

    public void enterScores(View view){
        final int num = switchSpinnerString(numberOfSetsSpinner.getSelectedItem().toString());
        findViewById(R.id.add_tennis_score_activity_info_container).setVisibility(View.GONE);
        final LinearLayout container = findViewById(R.id.add_tennis_score_activity_parent_container);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, APIHelper.FIND_TEAM_BY_ID + homeTeamID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("####onResponse: " + response);
                try {
                    Gson gs = new Gson();
                    JSONArray jsonObject = new JSONArray(response);
                    final Team[] homeTeam = gs.fromJson(jsonObject.toString(), Team[].class); //All the teams that fit the search query
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    StringRequest request = new StringRequest(Request.Method.GET, APIHelper.FIND_TEAM_BY_ID + awayTeamID, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println("####onResponse: " + response);
                            try {
                                Gson gs = new Gson();
                                JSONArray jsonObject = new JSONArray(response);
                                final Team[] awayTeam = gs.fromJson(jsonObject.toString(), Team[].class); //All the teams that fit the search query
                                for(int i = 0; i < num; i++){
                                    createSetInput(homeTeam[0],awayTeam[0],i+1);
                                }
                                final Button finish = interfaceHelper.createButton(R.string.add_tennis_score_finish_button);
                                finish.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //Validate the user's input to make sure every field has been entered
                                        boolean allFieldsValid = true;
                                        boolean validScore = true;
                                        for(int i = 0; i< homeTeamSets.size(); i++){
                                            String homeScore = getEditText(homeTeamSets.get(i));
                                            String awayScore = getEditText(awayTeamSets.get(i));
                                            if(homeTeamSets.get(i) == null || homeScore.equals("")) allFieldsValid = false;
                                            if(awayTeamSets.get(i) == null || awayScore.equals("")) allFieldsValid = false;
                                            if(homeScore.equals(awayScore)) validScore = false;
                                        }

                                        //Make sure the input is a valid score (no draws in tennis)
                                        if(allFieldsValid){
                                            if(countSets(homeTeamSets, awayTeamSets) == countSets(awayTeamSets, homeTeamSets)){
                                                validScore = false;
                                            }
                                        }

                                        if(allFieldsValid && validScore){
                                            //Send tennis score to Mongo Collection
                                            Map<String, String> data = new HashMap<>();
                                            data.put("round", String.valueOf(maxRound));
                                            data.put("season_id", seasonID);
                                            data.put("fixture_id", fixtureID);
                                            data.put("winner_id", determineWinner(homeTeam[0], awayTeam[0]).getId());
                                            data.put("sets", getScoresArray());
                                            APIHelper.sendData(getApplicationContext(), Request.Method.POST, APIHelper.ADD_TENNIS_SCORE, data);

                                            //Update the participant's performance records
                                            if(type.equals("league")){
                                                updateTennisPerformance(countSets(homeTeamSets, awayTeamSets), countSets(awayTeamSets, homeTeamSets));
                                            }

                                            //Mark the fixture as complete
                                            Map<String, String> editMap = new HashMap<>();
                                            editMap.put("scores_entered", "1");
                                            APIHelper.sendData(getApplicationContext(), Request.Method.POST, APIHelper.EDIT_FIXTURE + fixtureID, editMap);

                                            if(type.equals("league")){
                                                Intent goBack = new Intent(getApplicationContext(), ChooseGameWeekActivity.class);
                                                goBack.putExtra("compID", compID);
                                                goBack.putExtra("id", id);
                                                goBack.putExtra("sport", sport);
                                                goBack.putExtra("type", type);
                                                goBack.putExtra(User.ID_EXTRA, userID);
                                                goBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(goBack);
                                            } else {
                                                Intent goBack = new Intent(getApplicationContext(), ViewSeasonActivity.class);
                                                goBack.putExtra(User.ID_EXTRA, userID);
                                                goBack.putExtra("compID", compID);
                                                goBack.putExtra("id", id);
                                                goBack.putExtra("name", name);
                                                goBack.putExtra("description", desc);
                                                goBack.putExtra("participants", participants);
                                                goBack.putExtra("isActive", isActive);
                                                goBack.putExtra("fixturesGenerated", fixturesGenerated);
                                                goBack.putExtra("sport", sport);
                                                goBack.putExtra("type", type);
                                                goBack.putExtra("isCupScoreEntered", true);
                                                goBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(goBack);
                                            }
                                        } else if(!validScore){
                                            Toast.makeText(getApplicationContext(), "This is not a valid score", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(getApplicationContext(), "You must enter all fields", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                container.addView(finish);
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

    private Team determineWinner(Team home, Team away){
        int homeSets = 0;
        int awaySets = 0;
        for(int i = 0; i < homeTeamSets.size(); i++){
            int homeTeamNum = Integer.parseInt(getEditText(homeTeamSets.get(i)));
            int awayTeamNum = Integer.parseInt(getEditText(awayTeamSets.get(i)));
            if(homeTeamNum > awayTeamNum){
                homeSets++;
            } else {
                awaySets++;
            }
        }
        if(homeSets > awaySets) {
            return home;
        } else {
            return away;
        }
    }

    private void updateTennisPerformance(final int homeTeamSets, final int awayTeamSets){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, APIHelper.FIND_TEAMS_PERFORMANCE + homeTeamID + "/" + seasonID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("####onResponse: " + response);
                try {
                    Gson gs = new Gson();
                    JSONArray jsonObject = new JSONArray(response);
                    Performance[] homePerformance = gs.fromJson(jsonObject.toString(), Performance[].class);
                    Performance home = homePerformance[0];
                    Map<String, String> homeMap = new HashMap<>();
                    if(homeTeamSets > awayTeamSets){
                        int points = Integer.parseInt(home.getPoints()) + WIN;
                        int wins = Integer.parseInt(home.getWins()) + 1;
                        homeMap.put("points", String.valueOf(points));
                        homeMap.put("wins", String.valueOf(wins));
                    } else {
                        int losses = Integer.parseInt(home.getLosses()) + 1;
                        homeMap.put("losses", String.valueOf(losses));
                    }
                    APIHelper.sendData(getApplicationContext(), Request.Method.POST, APIHelper.EDIT_PERFORMANCE + home.getId(), homeMap);

                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    StringRequest request = new StringRequest(Request.Method.GET, APIHelper.FIND_TEAMS_PERFORMANCE + awayTeamID + "/" + seasonID, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println("####onResponse: " + response);
                            try {
                                Gson gs = new Gson();
                                JSONArray jsonObject = new JSONArray(response);
                                Performance[] awayPerformance = gs.fromJson(jsonObject.toString(), Performance[].class);
                                Performance away = awayPerformance[0];
                                Map<String, String> awayMap = new HashMap<>();
                                if(awayTeamSets > homeTeamSets){
                                    int points = Integer.parseInt(away.getPoints()) + WIN;
                                    int wins = Integer.parseInt(away.getWins()) + 1;
                                    awayMap.put("points", String.valueOf(points));
                                    awayMap.put("wins", String.valueOf(wins));
                                } else {
                                    int losses = Integer.parseInt(away.getLosses()) + 1;
                                    awayMap.put("losses", String.valueOf(losses));
                                }
                                APIHelper.sendData(getApplicationContext(), Request.Method.POST, APIHelper.EDIT_PERFORMANCE + away.getId(), awayMap);
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

    private int countSets(ArrayList<EditText> home, ArrayList<EditText> away){
        int homeTeamSets = 0;
        for(int i = 0; i < home.size(); i++){
            int homeNum = Integer.parseInt(getEditText(home.get(i)));
            int awayNum = Integer.parseInt(getEditText(away.get(i)));
            if(homeNum > awayNum){
                homeTeamSets++;
            }
        }
        return homeTeamSets;
    }

    private String getScoresArray(){
        String[][] scores = new String[homeTeamSets.size()][2];
        for(int i = 0; i < homeTeamSets.size(); i++){
            scores[i][0] = getEditText(homeTeamSets.get(i));
            scores[i][1] = getEditText(awayTeamSets.get(i));
        }
        System.out.println("Array Scores: " + Arrays.deepToString(scores));
        return Arrays.deepToString(scores);
    }

    private String getEditText(EditText e){
        return e.getText().toString();
    }

    private void createSetInput(Team homeTeam, Team awayTeam, int setNumber){
        LinearLayout parent = interfaceHelper.updateLinearLayout(R.id.add_tennis_score_activity_parent_container, LinearLayout.VERTICAL);
        TextView set = interfaceHelper.createTextView(String.format(Locale.getDefault(), "Set %d:", setNumber), 18.0f);

        LinearLayout homeTeamLayout = interfaceHelper.createLinearLayout(LinearLayout.HORIZONTAL);
        TextView homeText = interfaceHelper.createTextView(homeTeam.getTeamName() + ":", 15.0f);
        EditText homeEdit = interfaceHelper.createEditText(InputType.TYPE_CLASS_NUMBER);
        homeTeamSets.add(homeEdit);
        interfaceHelper.addMultipleViews(homeTeamLayout, new View[]{homeText, homeEdit});

        LinearLayout awayTeamLayout = interfaceHelper.createLinearLayout(LinearLayout.HORIZONTAL);
        TextView awayText = interfaceHelper.createTextView(awayTeam.getTeamName() + ":", 15.0f);
        EditText awayEdit = interfaceHelper.createEditText(InputType.TYPE_CLASS_NUMBER);
        awayTeamSets.add(awayEdit);
        interfaceHelper.addMultipleViews(awayTeamLayout, new View[]{awayText, awayEdit});

        interfaceHelper.addMultipleViews(parent, new View[]{set, homeTeamLayout, awayTeamLayout});
    }

    private Spinner generateSpinner(int layoutID, int stringArrayID){
        Spinner spinner = findViewById(layoutID);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, stringArrayID, R.layout.support_simple_spinner_dropdown_item);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        return spinner;
    }

    private int switchSpinnerString(String selected){
        switch(selected){
            case "Two":
                return 2;
            case "Three":
                return 3;
            case "Four":
                return 4;
            case "Five":
                return 5;
            default:
                return -1;
        }
    }

}
