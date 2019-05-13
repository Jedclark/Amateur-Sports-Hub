package com.example.jedcl.sportsstats;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Locale;

public class FixtureSearchAdapter extends BaseAdapter {

    Context context;
    Game[] data;
    private static LayoutInflater inflater = null;
    private String myTeamName;
    private final String VIEW_ALL_SEASONS_FIXTURES = "ViewAllSeasonFixturesActivity";
    private String parentClass = "";
    private String userID;

    public FixtureSearchAdapter(Context context, Game[] data, String myTeamName, String userID){
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.myTeamName = myTeamName;
        this.userID = userID;
    }

    public FixtureSearchAdapter(Context context, Game[] data, String userID){
        this.context = context;
        this.data = data;
        this.userID = userID;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        parentClass = VIEW_ALL_SEASONS_FIXTURES;
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if(vi == null) vi = inflater.inflate(R.layout.fixture_list_item, null);

        if(parentClass.equals(VIEW_ALL_SEASONS_FIXTURES)){
            final Team home = data[position].getTeams().get(0);
            final Team away = data[position].getTeams().get(1);
            ((TextView)vi.findViewById(R.id.fixture_team_name)).setText(String.format(Locale.getDefault(), "%s vs %s", home.getTeamName(), away.getTeamName()));
            ((TextView)vi.findViewById(R.id.fixture_date)).setText(data[position].getDate());
        } else {
            final Team away = data[position].getOpponentTeam().get(0);
            ((TextView)vi.findViewById(R.id.fixture_team_name)).setText(String.format(Locale.getDefault(), "%s vs %s", myTeamName, away.getTeamName()));
            ((TextView)vi.findViewById(R.id.fixture_date)).setText(data[position].getDate());
        }

        if(parentClass.equals(VIEW_ALL_SEASONS_FIXTURES)){
            ((TextView)vi.findViewById(R.id.fixture_round)).setText(String.format(Locale.getDefault(), "Round %s", data[position].getRound()));
        }

        ImageView comp = vi.findViewById(R.id.fixture_competition_picture);
        switch(data[position].getType()){
            case "tournament":
                comp.setImageResource(R.drawable.ic_tournament_64);
                break;
            case "league":
                comp.setImageResource(R.drawable.ic_league_64);
                break;
            default:
                break;
        }

        vi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewGame = new Intent(context, ViewGameActivity.class);
                viewGame.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                viewGame.putExtra(User.ID_EXTRA, userID);
                if(parentClass.equals(VIEW_ALL_SEASONS_FIXTURES)){
                    final Team home = data[position].getTeams().get(0);
                    final Team away = data[position].getTeams().get(1);
                    viewGame.putExtra("where", String.format(Locale.getDefault(), "%s %s %s", home.getAddress(), home.getTown(), home.getPostCode()));
                    viewGame.putExtra("what", String.format(Locale.getDefault(), "%s vs %s", home.getTeamName(), away.getTeamName()));
                    viewGame.putExtra("sport", home.getSport());
                } else {
                    String opponentTeam = data[position].getOpponentTeam().get(0).getTeamName();
                    viewGame.putExtra("what", String.format(Locale.getDefault(), "%s vs %s", myTeamName, opponentTeam));
                    viewGame.putExtra("where", String.format(Locale.getDefault(), "%s %s %s", data[position].getAddress(), data[position].getTown(), data[position].getPostCode()));
                    viewGame.putExtra("sport", data[position].getOpponentTeam().get(0).getSport());
                }
                viewGame.putExtra("date", data[position].getDate());
                viewGame.putExtra("compID", data[position].getCompetitionID());
                viewGame.putExtra("isFinished", data[position].getScoresEntered());
                viewGame.putExtra("fixtureID", data[position].getId());
                (context).startActivity(viewGame);
            }
        });

        return vi;
    }

}
