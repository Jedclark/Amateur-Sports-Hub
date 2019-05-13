package com.example.jedcl.sportsstats;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UserSearchAdapter extends BaseAdapter {

    Context context;
    User[] data;
    private String teamID;
    private String userID;
    private static LayoutInflater inflater = null;

    public UserSearchAdapter(Context context, User[] data, String teamID, String userID){
        this.context = context;
        this.data = data;
        this.teamID = teamID;
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
        if(vi == null) vi = inflater.inflate(R.layout.user_list_item, null);

        ((TextView)vi.findViewById(R.id.user_list_view_name)).setText(String.format(Locale.getDefault(), "%s", data[position].getUsername()));
        Button addToTeam = vi.findViewById(R.id.user_list_view_add_button);
        addToTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> map = new HashMap<>();
                map.put("team_id", teamID);
                map.put("user_id", userID);
                APIHelper.sendData(context, Request.Method.POST, APIHelper.JOIN_TEAM, map);
                Intent finish = new Intent(context, HomeActivity.class);
                finish.putExtra("isUserJoinedTeam", true);
                finish.putExtra(User.ID_EXTRA, userID);
                finish.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                (context).startActivity(finish);
            }
        });

        return vi;
    }
}
