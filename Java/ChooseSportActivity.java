package com.example.jedcl.sportsstats;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ChooseSportActivity extends AppCompatActivity {

    public static final String SPORT = "sport";
    private String type;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_sport);
        setTitle(R.string.choose_sport_activity_title);
        userID = getIntent().getStringExtra(User.ID_EXTRA);
    }


    public void createTeamDetails(View view){
        Intent parentActivity = getIntent();
        final int type = parentActivity.getIntExtra("type", -1);
        if(type == HomeActivity.TEAM_PAGE){
            Intent createTeamDetails = new Intent(this, CreateTeamDetailsActivity.class);
            createTeamDetails.putExtra(User.ID_EXTRA, userID);
            sendChosenSport(createTeamDetails, view);
            startActivity(createTeamDetails);
        } else {
            Intent createCompDetails = new Intent(this, ChooseCompetitionActivity.class);
            createCompDetails.putExtra(User.ID_EXTRA, userID);
            sendChosenSport(createCompDetails, view);
            startActivity(createCompDetails);
        }
    }

    private void sendChosenSport(Intent intent, View view){
        switch(view.getId()){
            case R.id.choose_sport_activity_football_button:
                intent.putExtra(SPORT, "football");
                break;
            case R.id.choose_sport_activity_basketball_button:
                intent.putExtra(SPORT, "basketball");
                break;
            case R.id.choose_sport_activity_tennis_button:
                intent.putExtra(SPORT, "tennis");
                break;
            default:
                break;
        }
    }

}
