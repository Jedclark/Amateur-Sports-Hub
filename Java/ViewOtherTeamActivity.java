package com.example.jedcl.sportsstats;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

public class ViewOtherTeamActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String name;
    private String desc;
    private String address;
    private GoogleMap map;
    private LatLng location;
    private Marker teamLocationMarker;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_other_team);
        ((TextView)findViewById(R.id.view_other_team_activity_name)).setText(getIntent().getStringExtra("name"));
        ((TextView)findViewById(R.id.view_other_team_activity_desc)).setText(getIntent().getStringExtra("desc"));
        name = getIntent().getStringExtra("name");
        desc = getIntent().getStringExtra("desc");
        address = getIntent().getStringExtra("address");
        userID = getIntent().getStringExtra(User.ID_EXTRA);

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.view_other_team_activity_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        findLatLngFromAddress(address, map);
    }

    private void findLatLngFromAddress(String address, final GoogleMap map){
        address = address.replaceAll(" ", "%20");
        final String API_CALL = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=AIzaSyDqeEH9WYQeNc91QphK2riIROhJcfYl1wE";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, API_CALL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Double lat = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                    Double lng = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                    location = new LatLng(lat, lng);
                    teamLocationMarker = map.addMarker(new MarkerOptions().position(location).title("Team's Location"));
                    teamLocationMarker.showInfoWindow();
                    map.moveCamera(CameraUpdateFactory.newLatLng(location));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 12.0f));
                    map.getUiSettings().setMapToolbarEnabled(true);
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("####onErrorResponse: " + error);
            }
        });
        queue.add(request);
    }



}
