package com.example.jedcl.sportsstats;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
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

public class TeamSearchAdapter extends BaseAdapter {

    private Class parentClass;
    Context context;
    Team[] data;
    private String userID;
    private static LayoutInflater inflater = null;
    private Intent intent;

    private static final Class searchTeamsActivity = SearchTeamsActivity.class;
    private static final Class viewSeasonActivity = ViewSeasonActivity.class;

    public TeamSearchAdapter(Context context, Team[] data, Class x, String userID){
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        parentClass = x;
        this.userID = userID;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if(vi == null){
            vi = inflater.inflate(R.layout.search_list_item, null);
        }

        ((TextView)vi.findViewById(R.id.team_name)).setText(data[position].getTeamName());
        ((TextView)vi.findViewById(R.id.team_desc)).setText(data[position].getDescription());

        ImageView sport = vi.findViewById(R.id.sport_picture);
        switch(data[position].getSport()){
            case "football":
                sport.setImageResource(R.drawable.ic_football_64);
                break;
            case "tennis":
                sport.setImageResource(R.drawable.ic_tennis_64);
                break;
            case "swimming":
                sport.setImageResource(R.drawable.ic_swimming_64);
                break;
            case "basketball":
                sport.setImageResource(R.drawable.ic_basketball_64);
                break;
            default:
                break;
        }

        final int index = position;
        Button applyToTeam = vi.findViewById(R.id.apply_button);
        applyToTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(parentClass == viewSeasonActivity){
                    addTeamToSeason(index);
                } else if(parentClass == searchTeamsActivity){
                    joinTeam(index);
                }
            }
        });
        return vi;
    }

    public void setIntent(Intent intent){ this.intent = intent; }

    private void addTeamToSeason(final int index){
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, APIHelper.ADD_TEAM_TO_SEASON, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                intent.putExtra("isTeamAddedToSeason", true);
                intent.putExtra(User.ID_EXTRA, userID);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
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
                Map<String, String> packet = new HashMap<>();
                packet.put("season_id", data[index].getSeasonId());
                packet.put("team_id", data[index].getId());
                return packet;
            }
        };
        queue.add(request);
    }

    private void joinTeam(final int index){
        final String id = data[index].getId(); //team_id
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, APIHelper.JOIN_TEAM, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Intent refresh = new Intent(context, HomeActivity.class);
                refresh.putExtra("isTeamJoined", true);
                refresh.putExtra(User.ID_EXTRA, userID);
                refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(refresh);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> packet = new HashMap<>();
                packet.put("user_id", userID);
                packet.put("team_id", id);
                return packet;
            }
        };
        queue.add(request);
    }



}
