package com.example.jedcl.sportsstats;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SeasonSearchAdapter extends BaseAdapter {

    Context context;
    Season[] data;
    private String userID;
    private static LayoutInflater inflater = null;

    public SeasonSearchAdapter(Context context, Season[] data, String userID){
        this.context = context;
        this.data = data;
        this.userID = userID;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        if(vi == null) vi = inflater.inflate(R.layout.season_list_item, null);

        ((TextView)vi.findViewById(R.id.season_name)).setText(data[position].getName());
        ((TextView)vi.findViewById(R.id.season_desc)).setText(data[position].getDescription());

        ImageView comp = vi.findViewById(R.id.season_type_picture);
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
                Intent viewSeason = new Intent(context, ViewSeasonActivity.class);
                viewSeason.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                viewSeason.putExtra(User.ID_EXTRA, userID);
                viewSeason.putExtra("compID", data[position].getCompetitionId());
                viewSeason.putExtra("id", data[position].getId());
                viewSeason.putExtra("name", data[position].getName());
                viewSeason.putExtra("desc", data[position].getDescription());
                viewSeason.putExtra("participants", data[position].getParticipants());
                viewSeason.putExtra("isActive", data[position].getIsActive());
                viewSeason.putExtra("fixturesGenerated", data[position].getFixturesGenerated());
                viewSeason.putExtra("sport", data[position].getSport());
                viewSeason.putExtra("type", data[position].getType());
                (context).startActivity(viewSeason);
            }
        });

        return vi;
    }

}
