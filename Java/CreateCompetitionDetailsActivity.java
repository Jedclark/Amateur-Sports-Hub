package com.example.jedcl.sportsstats;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.android.volley.Request;

import java.util.HashMap;
import java.util.Map;

public class CreateCompetitionDetailsActivity extends AppCompatActivity {

    private String sport;
    private String typeText;
    private String userID;

    private InterfaceHelper interfaceHelper = new InterfaceHelper(this, CreateCompetitionDetailsActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_competition_details);
        userID = getIntent().getStringExtra(User.ID_EXTRA);
        Intent parent = getIntent();
        String comp = parent.getStringExtra("comp");
        if(comp.equals("tournament")){
            interfaceHelper.setTextView(R.id.create_comp_details_activity_type_text, R.string.create_comp_details_activity_name_tournament_text);
            interfaceHelper.setEditTextHint(R.id.create_comp_details_activity_type_edit, R.string.create_comp_details_activity_name_tournament_hint);
            typeText = "tournament";
        } else {
            interfaceHelper.setTextView(R.id.create_comp_details_activity_type_text, R.string.create_comp_details_activity_name_league_text);
            interfaceHelper.setEditTextHint(R.id.create_comp_details_activity_type_edit, R.string.create_comp_details_activity_name_league_hint);
            typeText = "league";
        }

        sport = parent.getStringExtra("sport");
    }

    public void finish(View view){
        //Add the competition to the database
        Map<String, String> data = new HashMap<>();
        data.put("name", interfaceHelper.getTextFromEditText(R.id.create_comp_details_activity_type_edit));
        data.put("description", interfaceHelper.getTextFromEditText(R.id.create_comp_details_activity_desc_edit));
        data.put("user_id", userID);
        data.put("sport", sport);
        data.put("type", typeText);
        APIHelper.sendData(this, Request.Method.POST, APIHelper.ADD_COMPETITION, data);

        Intent refresh = new Intent(this, HomeActivity.class);
        refresh.putExtra(User.ID_EXTRA, userID);
        refresh.putExtra("isCompCreated", true);
        refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(refresh);
    }

}
