package com.example.jedcl.sportsstats;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ChooseCompetitionActivity extends AppCompatActivity {

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_competition);
        userID = getIntent().getStringExtra(User.ID_EXTRA);
    }

    public void createCompetitionDetails(View view){
        Intent parentActivity = getIntent();
        Intent createComp = new Intent(this, CreateCompetitionDetailsActivity.class);
        createComp.putExtra("sport", parentActivity.getStringExtra("sport"));
        createComp.putExtra(User.ID_EXTRA, userID);
        sendCompType(createComp, view);
        startActivity(createComp);
    }

    private void sendCompType(Intent intent, View view){
        switch(view.getId()){
            case R.id.choose_comp_activity_tournament_button:
                intent.putExtra("comp", "tournament");
                break;
            case R.id.choose_comp_activity_league_button:
                intent.putExtra("comp", "league");
                break;
            default:
                break;
        }
    }

}
