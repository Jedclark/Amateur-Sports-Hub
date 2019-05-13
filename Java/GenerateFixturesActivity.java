package com.example.jedcl.sportsstats;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenerateFixturesActivity extends AppCompatActivity {

    private Spinner numberOfGames;
    private Spinner frequency;
    private String id;
    private String sport;
    private String howManyTimesPlayed;
    private String frequencyOfGames;
    private String userID;

    private final String ONCE = "Once";
    private final String TWICE = "Twice";

    private final String EVERY_WEEK = "Every week";
    private final String EVERY_TWO_WEEKS = "Every 2 weeks";

    private InterfaceHelper interfaceHelper = new InterfaceHelper(this, GenerateFixturesActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_fixtures);
        numberOfGames = generateSpinner(R.id.generate_fixtures_activity_number_games_spinner, R.array.generate_fixtures_activity_number_of_fixtures_spinner);
        frequency = generateSpinner(R.id.generate_fixtures_activity_frequency_spinner, R.array.generate_fixtures_activity_frequency_spinner);
        id = getIntent().getStringExtra("id");
        sport = getIntent().getStringExtra("sport");
        userID = getIntent().getStringExtra(User.ID_EXTRA);
    }

    private Spinner generateSpinner(int layoutID, int stringArrayID){
        Spinner spinner = findViewById(layoutID);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, stringArrayID, R.layout.support_simple_spinner_dropdown_item);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        return spinner;
    }

    public void showDatePickerDialog(View view){
        DialogFragment date = new DatePickerFragment();
        ((DatePickerFragment) date).setParentClass("GenerateFixturesActivity");
        date.show(getSupportFragmentManager(), "show");
    }

    public void showTimePickerDialog(View view){
        DialogFragment time = new TimePickerFragment();
        ((TimePickerFragment) time).setParentClass("GenerateFixturesActivity");
        time.show(getSupportFragmentManager(), "show");
    }

    public void generateFixtures(View view){
        howManyTimesPlayed = numberOfGames.getSelectedItem().toString();
        frequencyOfGames = frequency.getSelectedItem().toString();
        String[] dateElements = interfaceHelper.getTextFromTextView(R.id.generate_fixtures_activity_date_text).split("/");
        String[] timeElements = interfaceHelper.getTextFromTextView(R.id.generate_fixtures_activity_time_text).split(":");
        boolean validDateTime = true;
        for(String s : dateElements) if(s.equals("")) validDateTime = false;
        for(String s : timeElements) if(s.equals("")) validDateTime = false;
        if(validDateTime){
            generateFixturesAlgorithm(howManyTimesPlayed, frequencyOfGames, dateElements[0], dateElements[1], dateElements[2], timeElements[0], timeElements[1]);
            setFixturesGenerated();
        } else {
            Toast.makeText(getApplicationContext(), "You must enter a date and time", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void generateFixturesAlgorithm(final String numberOfGames, final String frequency,
                                           final String day, final String month, final String year,
                                           final String hours, final String minutes){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, APIHelper.FIND_TEAMS_IN_SEASON + id + "/" + sport, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("####onResponse: " + response);
                try {
                    Gson gs = new Gson();
                    JSONArray jsonObject = new JSONArray(response);
                    Team[] teams = gs.fromJson(jsonObject.toString(), Team[].class);
                    List<Team> teamsList = Arrays.asList(teams);

                    final boolean includeReverseFixtures = numberOfGames.equals(TWICE);

                    FixtureGenerator<Team> fixtureGenerator = new FixtureGenerator();
                    List<List<Fixture<Team>>> rounds = fixtureGenerator.getFixtures(teamsList, includeReverseFixtures);
                    int freq = 0;
                    for(int i=0; i<rounds.size(); i++){
                        final int roundNum = i+1;
                        List<Fixture<Team>> round = rounds.get(i);
                        for(final Fixture<Team> fixture : round){
                            String dateText = getFormattedDateString(year, month, day, hours, minutes, freq);
                            Map<String, String> data = new HashMap<>();
                            data.put("season_id", id);
                            data.put("home_team_id", fixture.getHomeTeam().getId());
                            data.put("away_team_id", fixture.getAwayTeam().getId());
                            data.put("date", dateText);
                            data.put("round", String.valueOf(roundNum));
                            APIHelper.sendData(getApplicationContext(), Request.Method.POST, APIHelper.ADD_FIXTURE, data);
                        }
                        freq += (frequency.equals(EVERY_WEEK)) ? 1 : 2;
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

    private void setFixturesGenerated(){RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest generateFixtures = new StringRequest(Request.Method.POST, APIHelper.EDIT_SEASON + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("####onResponse: " + response);
                Intent fixturesGenerated = new Intent(getApplicationContext(), HomeActivity.class);
                fixturesGenerated.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                fixturesGenerated.putExtra("isFixturesGenerated", true);
                fixturesGenerated.putExtra(User.ID_EXTRA, userID);
                startActivity(fixturesGenerated);
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
                data.put("fixtures_generated", "1");
                return data;
            }
        };
        queue.add(generateFixtures);
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
