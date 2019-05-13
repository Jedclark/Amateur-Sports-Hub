package com.example.jedcl.sportsstats;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class CreateSeasonActivity extends AppCompatActivity {

    private String comp_id;
    private String type;
    private String sport;
    private String userID;

    private InterfaceHelper interfaceHelper = new InterfaceHelper(this, CreateSeasonActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_season);
        setTitle(R.string.create_season_activity_title);
        comp_id = getIntent().getStringExtra("id");
        type = getIntent().getStringExtra("type");
        sport = getIntent().getStringExtra("sport");
        userID = getIntent().getStringExtra(User.ID_EXTRA);
        if(type.equals("tournament")){
            ((TextView)findViewById(R.id.create_season_activity_name_text)).setText(R.string.create_season_activity_tournament_text);
        }
    }

    public void createSeason(View view){
        Map<String, String> data = new HashMap<>();
        data.put("competition_id", comp_id);
        data.put("name", interfaceHelper.getTextFromEditText(R.id.create_season_activity_name_edit));
        data.put("description", interfaceHelper.getTextFromEditText(R.id.create_season_activity_desc_edit));
        data.put("participants", interfaceHelper.getTextFromEditText(R.id.create_season_activity_participants_edit));
        APIHelper.sendData(this, Request.Method.POST, APIHelper.ADD_SEASON, data);

        Intent refresh = new Intent(this, ViewCompetitionActivity.class);
        refresh.putExtra(User.ID_EXTRA, userID);
        refresh.putExtra("id", comp_id);
        refresh.putExtra("sport", sport);
        refresh.putExtra("type", type);
        refresh.putExtra("isTournamentCreated", true);
        refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(refresh);
    }

}
