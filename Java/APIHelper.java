package com.example.jedcl.sportsstats;

import android.content.Context;
import android.content.Intent;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Map;

/**
 * @author Jed Clark
 * Class to manage API queries.
 */
public class APIHelper {

    private static final String API_ADDRESS = "http://142.93.36.99/android_sports/";

    /* USERS CONTROLLER */
    public static final String USER_REGISTER_QUERY = API_ADDRESS + "users/add/";
    public static final String TEAM_FIND = API_ADDRESS + "users/find_teams/";
    public static final String SEARCH_USERS = API_ADDRESS + "users/search_users/"; //$query, $logged_in_id, $team_id
    public static final String FIND_USER = API_ADDRESS + "users/find_user/"; //$username

    /* TEAMS CONTROLLER */
    public static final String CREATE_TEAM = API_ADDRESS + "teams/add";
    public static final String TEAM_SEARCH = API_ADDRESS + "teams/search_teams/";
    public static final String JOIN_TEAM = API_ADDRESS + "teams/join_team/";
    public static final String FIND_TEAM_BY_ID = API_ADDRESS + "teams/find_by_id/";
    public static final String FIND_TEAMS_I_PLAY_FOR = API_ADDRESS + "teams/find_teams_playing_for/";
    public static final String FIND_TEAMS_SEASONS = API_ADDRESS + "teams/find_teams_seasons/";
    public static final String SEARCH_ALL_TEAMS = API_ADDRESS + "teams/search_all_teams/";
    public static final String CHECK_TEAM_PERMISSIONS = API_ADDRESS + "teams/can_edit/"; //$user_id, $team_id
    public static final String FIND_HOME_AND_AWAY_TEAMS = API_ADDRESS + "teams/find_home_away_teams/"; //$home team, $away team

    /* COMPETITIONS CONTROLLER */
    public static final String ADD_COMPETITION = API_ADDRESS + "competitions/add/";
    public static final String FIND_COMPETITIONS = API_ADDRESS + "competitions/find_competitions/";
    public static final String FIND_SEASONS = API_ADDRESS + "competitions/find_seasons/";
    public static final String CHECK_COMPETITION_PERMISSIONS = API_ADDRESS + "competitions/can_edit/"; //$user_id, $comp_id

    /* SEASONS CONTROLLER */
    public static final String ADD_SEASON = API_ADDRESS + "seasons/add";
    public static final String ADD_TEAM_TO_SEASON = API_ADDRESS + "seasons/add_team_to_season";
    public static final String FIND_TEAMS_IN_SEASON = API_ADDRESS + "seasons/find_teams/";
    public static final String EDIT_SEASON = API_ADDRESS + "seasons/edit/";
    public static final String REMOVE_TEAM_FROM_SEASON = API_ADDRESS + "seasons/remove_team_from_season/";

    /* PERFORMANCES CONTROLLER */
    public static final String ADD_PERFORMANCE = API_ADDRESS + "performances/add";
    public static final String FIND_SEASON_PERFORMANCES = API_ADDRESS + "performances/find/";
    public static final String FIND_TEAMS_PERFORMANCE = API_ADDRESS + "performances/find_team_performance/"; //$team_id, $season_id
    public static final String EDIT_PERFORMANCE = API_ADDRESS + "performances/edit/";

    /* FIXTURES CONTROLLER */
    public static final String ADD_FIXTURE = API_ADDRESS + "fixtures/add";
    public static final String FIND_FIXTURES = API_ADDRESS + "fixtures/find/";
    public static final String FIND_ALL_FIXTURES = API_ADDRESS + "fixtures/find_all_games/" +
            ""; //$season_id
    public static final String FIND_SPECIFIC_FIXTURE = API_ADDRESS + "fixtures/find_by_id/";
    public static final String FIND_MY_GAMES = API_ADDRESS + "fixtures/find_my_games/";
    public static final String FIND_GAMES_LIMIT_3 = API_ADDRESS + "fixtures/find_teams_fixtures_limit_3/";
    public static final String FIND_TEAMS_GAMES = API_ADDRESS + "fixtures/find_all_teams_fixtures/";
    public static final String EDIT_FIXTURE = API_ADDRESS + "fixtures/edit/";
    public static final String FIND_SEASONS_FIXTURES = API_ADDRESS + "fixtures/find_games_in_season/"; //$id = $season_id
    public static final String FIND_GAMES_BY_ROUND = API_ADDRESS + "fixtures/find_games_by_round/"; //$season_id, $round

    /* TENNIS CONTROLLER */
    public static final String ADD_TENNIS_SCORE = API_ADDRESS + "tennis/add/";
    public static final String FIND_TENNIS_GAMES_IN_ROUND = API_ADDRESS + "tennis/find/"; //$season_id, $round
    public static final String FIND_TENNIS_TOURNAMENT_WINNER = API_ADDRESS + "tennis/find_tournament_winner/"; //$season_id, $round
    public static final String FIND_TENNIS_GAME_BY_ID = API_ADDRESS + "tennis/find_by_id/"; //$fixture_id

    /* FOOTBALL CONTROLLER */
    public static final String ADD_FOOTBALL_SCORE = API_ADDRESS + "footballs/add/";
    public static final String FIND_FOOTBALL_GAMES_IN_ROUND = API_ADDRESS + "footballs/find/"; //$season_id, $round
    public static final String FIND_FOOTBALL_TOURNAMENT_WINNER = API_ADDRESS + "footballs/find_tournament_winner/"; //$season_id, $round
    public static final String FIND_FOOTBALL_GAME_BY_ID = API_ADDRESS + "footballs/find_by_id";

    /* BASKETBALL CONTROLLER */
    public static final String ADD_BASKETBALL_SCORE = API_ADDRESS + "basketballs/add/";
    public static final String FIND_BASKETBALL_GAMES_IN_ROUND = API_ADDRESS + "basketballs/find/"; //$season_id, $round
    public static final String FIND_BASKETBALL_TOURNAMENT_WINNER = API_ADDRESS + "basketballs/find_tournament_winner/"; //$season_id, $round
    public static final String FIND_BASKETBALL_GAME_BY_ID = API_ADDRESS + "basketballs/find_by_id";

    /**
     * Inserts data in to the database
     * @param context The application context
     * @param METHOD The HTTP method for sending the data
     * @param API_CALL The API endpoint
     * @param MAP The Map containing the data to send
     */
    public static void sendData(Context context, final int METHOD, final String API_CALL, final Map<String, String> MAP){
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(METHOD, API_CALL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("####onResponse: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("####onErrorResponse: " + error);
                error.printStackTrace();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return MAP;
            }
        };
        queue.add(request);
    }

    /**
     * Inserts data in to the database, then sends the user to a new activity using an Intent
     * @param context The application context
     * @param METHOD The HTTP method for sending the data
     * @param API_CALL The API endpoint
     * @param MAP The Map containing the data to send
     * @param intent The Intent being used to transport the user
     */
    public static void sendDataWithIntent(final Context context, final int METHOD, final String API_CALL, final Map<String, String> MAP, final Intent intent){
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(METHOD, API_CALL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("####onResponse: " + response);
               context.startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("####onErrorResponse: " + error);
                error.printStackTrace();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return MAP;
            }
        };
        queue.add(request);
    }

}
