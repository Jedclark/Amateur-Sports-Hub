package com.example.jedcl.sportsstats;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CreateTeamDetailsActivity extends AppCompatActivity {

    private String userID;

    private InterfaceHelper interfaceHelper = new InterfaceHelper(this, CreateTeamDetailsActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team_details);
        setTitle(R.string.create_team_details_activity_title);
        userID = getIntent().getStringExtra(User.ID_EXTRA);
    }

    public void next(View view){
        InputValidator iv = new InputValidator(this);
        boolean isValid = true;

        if(!iv.validYear(interfaceHelper.getTextFromEditText(R.id.create_team_details_activity_year_edit_text))) isValid = false;

        if(!iv.validString(Team.TEAM_DESC, interfaceHelper.getTextFromEditText(R.id.create_team_details_activity_desc_edit_text),
                Team.TEAM_DESC_MIN_LENGTH, Team.TEAM_DESC_MAX_LENGTH)){ isValid = false; }

        if(!iv.validString(Team.TEAM_NICK, interfaceHelper.getTextFromEditText(R.id.create_team_details_activity_nick_edit_text),
                Team.TEAM_NICK_MIN_LENGTH, Team.TEAM_NICK_MAX_LENGTH)){ isValid = false; }

        if(!iv.validString(Team.TEAM_NAME, interfaceHelper.getTextFromEditText(R.id.create_team_details_activity_name_edit_text),
                Team.TEAM_NAME_MIN_LENGTH, Team.TEAM_NAME_MAX_LENGTH)){ isValid = false; }

        if(isValid){
            Intent enterAddressDetails = new Intent(this, EnterAddressActivity.class);
            enterAddressDetails.putExtra("name", interfaceHelper.getTextFromEditText(R.id.create_team_details_activity_name_edit_text));
            enterAddressDetails.putExtra("nick", interfaceHelper.getTextFromEditText(R.id.create_team_details_activity_nick_edit_text));
            enterAddressDetails.putExtra("desc", interfaceHelper.getTextFromEditText(R.id.create_team_details_activity_desc_edit_text));
            enterAddressDetails.putExtra("year", interfaceHelper.getTextFromEditText(R.id.create_team_details_activity_year_edit_text));
            enterAddressDetails.putExtra("sport", getIntent().getStringExtra("sport"));
            enterAddressDetails.putExtra(User.ID_EXTRA, userID);
            startActivity(enterAddressDetails);
        }
    }

}
