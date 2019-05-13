package com.example.jedcl.sportsstats;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.vision.text.Line;
import com.google.android.gms.vision.text.Text;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class ViewCompetitionActivity extends AppCompatActivity {

    private String id; //Competition ID
    private String sport;
    private String type;
    private String userID;

    private InterfaceHelper interfaceHelper = new InterfaceHelper(this, ViewCompetitionActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_competition);
        Bundle extras = getIntent().getExtras();
        renderTitle(extras);
        id = getIntent().getStringExtra("id");
        sport = getIntent().getStringExtra("sport");
        type = getIntent().getStringExtra("type");
        userID = getIntent().getStringExtra(User.ID_EXTRA);
        if(extras.getBoolean("isTournamentCreated")){
            Toast.makeText(this, R.string.create_season_activity_create_success, Toast.LENGTH_SHORT).show();
        }
        checkPermissionsToCreateSeasons(id, userID);
    }

    private void checkPermissionsToCreateSeasons(final String id, String userID){
        RequestQueue queue = Volley.newRequestQueue(this);
        String API_CALL = APIHelper.CHECK_COMPETITION_PERMISSIONS + userID + "/" + id;
        StringRequest request = new StringRequest(Request.Method.GET, API_CALL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("response: " + response);
                //Non-admins
                if(response.equals("{\"success\":0}")){
                    //Hide create season button from non-admins
                    findViewById(R.id.view_comp_activity_season_button).setVisibility(View.GONE);
                    renderSeasons(id, false); //Render only the running seasons
                } else { //Admins
                    renderSeasons(id, true); //Render all seasons
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

    public void createSeason(View view){
        Intent createSeason = new Intent(this, CreateSeasonActivity.class);
        createSeason.putExtra(User.ID_EXTRA, userID);
        createSeason.putExtra("id", id);
        createSeason.putExtra("type", type);
        createSeason.putExtra("sport", sport);
        startActivity(createSeason);
    }

    public void renderSeasons(String id, final boolean viewAll){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, APIHelper.FIND_SEASONS + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("####onResponse: " + response);
                try {
                    Gson gs = new Gson();
                    JSONArray jsonObject = new JSONArray(response);
                    Season[] seasons = gs.fromJson(jsonObject.toString(), Season[].class);
                    for(Season s : seasons){
                        if(viewAll){
                            renderSeasonInfo(s);
                        } else {
                            if(s.getIsActive().equals("1") && s.getFixturesGenerated().equals("1")){
                                renderSeasonInfo(s);
                            }
                        }
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

    public void renderSeasonInfo(final Season season){
        LinearLayout seasonContainer = interfaceHelper.createLinearLayout(LinearLayout.VERTICAL);
        seasonContainer.setBackgroundResource(R.drawable.border);
        interfaceHelper.addLayoutParams(seasonContainer, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        TextView seasonName = interfaceHelper.createTextView(season.getName(), 17.0f);
        seasonName.setTypeface(null, Typeface.BOLD);
        TextView seasonDesc = interfaceHelper.createTextView(season.getDescription(), 15.0f);
        interfaceHelper.addMultipleViews(seasonContainer, new View[]{seasonName, seasonDesc});
        seasonContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewSeason = new Intent(getApplicationContext(), ViewSeasonActivity.class);
                viewSeason.putExtra(User.ID_EXTRA, userID);
                viewSeason.putExtra("compID", id);
                viewSeason.putExtra("id", season.getId());
                viewSeason.putExtra("name", season.getName());
                viewSeason.putExtra("description", season.getDescription());
                viewSeason.putExtra("participants", season.getParticipants());
                viewSeason.putExtra("isActive", season.getIsActive());
                viewSeason.putExtra("fixturesGenerated", season.getFixturesGenerated());
                viewSeason.putExtra("sport", sport);
                viewSeason.putExtra("type", type);
                startActivity(viewSeason);
            }
        });

        ((LinearLayout)findViewById(R.id.seasons_container)).addView(seasonContainer);
    }

    private void renderTitle(Bundle extras){
        if(extras != null){
            if(extras.get("compName") != null){
                setTitle(extras.get("compName").toString());
            }
        }
    }

}
