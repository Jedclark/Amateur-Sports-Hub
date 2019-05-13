package com.example.jedcl.sportsstats;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class EnterAddressActivity extends AppCompatActivity {

    private String userID;

    private InterfaceHelper interfaceHelper = new InterfaceHelper(this, EnterAddressActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_address);
        setTitle(R.string.enter_address_activity_title);
        userID = getIntent().getStringExtra(User.ID_EXTRA);
    }

    public void createTeam(View view){
        InputValidator iv = new InputValidator(this);
        boolean isValid = true;

        if(!iv.validString(Team.TEAM_ADDRESS, interfaceHelper.getTextFromEditText(R.id.enter_address_activity_address_edit_text),
                Team.TEAM_ADDRESS_MIN_LENGTH, Team.TEAM_ADDRESS_MAX_LENGTH)){ isValid = false; }

        if(!iv.validString(Team.TEAM_TOWN, interfaceHelper.getTextFromEditText(R.id.enter_address_activity_town_edit_text),
                Team.TEAM_TOWN_MIN_LENGTH, Team.TEAM_TOWN_MAX_LENGTH)){ isValid = false; }

        if(!iv.validString(Team.TEAM_POST_CODE, interfaceHelper.getTextFromEditText(R.id.enter_address_activity_post_code_edit_text),
                Team.TEAM_POST_CODE_MIN_LENGTH, Team.TEAM_POST_CODE_MAX_LENGTH)){ isValid = false; }

        if(!iv.validString(Team.TEAM_INSTRUCTIONS, interfaceHelper.getTextFromEditText(R.id.enter_address_activity_instructions_edit_text),
                Team.TEAM_INSTRUCTIONS_MIN_LENGTH, Team.TEAM_INSTRUCTIONS_MAX_LENGTH)){ isValid = false; }

        if(isValid){
            //Create the team
            Map<String, String> data = new HashMap<>();
            data.put("user_id", userID);
            data.put("sport", getIntent().getStringExtra("sport"));
            data.put("team_name", getIntent().getStringExtra("name"));
            data.put("nick_name", getIntent().getStringExtra("nick"));
            data.put("description", getIntent().getStringExtra("desc"));
            data.put("year_created", getIntent().getStringExtra("year"));
            data.put("address", interfaceHelper.getTextFromEditText(R.id.enter_address_activity_address_edit_text));
            data.put("town", interfaceHelper.getTextFromEditText(R.id.enter_address_activity_town_edit_text));
            data.put("post_code", interfaceHelper.getTextFromEditText(R.id.enter_address_activity_post_code_edit_text));
            data.put("instructions", interfaceHelper.getTextFromEditText(R.id.enter_address_activity_instructions_edit_text));
            APIHelper.sendData(this, Request.Method.POST, APIHelper.CREATE_TEAM + "/" + userID, data);

            Intent createTeam = new Intent(this, HomeActivity.class);
            createTeam.putExtra(User.ID_EXTRA, userID);
            createTeam.putExtra("isTeamCreated", true);
            createTeam.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(createTeam);
        }
    }

}
