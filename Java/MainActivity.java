package com.example.jedcl.sportsstats;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private InterfaceHelper interfaceHelper = new InterfaceHelper(this, MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void login(View view){
        final String username = interfaceHelper.getTextFromEditText(R.id.login_activity_id_username_edit);
        final String password = interfaceHelper.getTextFromEditText(R.id.login_activity_id_password_edit);
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, APIHelper.FIND_USER, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                System.out.println("response: " + response);
                if(response.equals("")){
                    Toast.makeText(getApplicationContext(), "Your login details are incorrect", Toast.LENGTH_SHORT).show();
                } else if(response.equals("{\"success\":0}")){
                    Toast.makeText(getApplicationContext(), "Your login details are incorrect", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");

                        //JSONArray reader = new JSONArray(response);
                        //JSONObject data = reader.getJSONObject(0);
                        //String success = data.getString("success");
                        if(success.equals("1")){
                            String id = jsonObject.getString("id");
                            String token = jsonObject.getString("token");
                            Intent login = new Intent(getApplicationContext(), HomeActivity.class);
                            login.putExtra(User.ID_EXTRA, id);
                            login.putExtra("token", token);
                            startActivity(login);
                        }
                    } catch(JSONException e1){
                        e1.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String>  params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };
        queue.add(request);
    }

    public void register(View view){
        Intent register = new Intent(this, RegisterActivity.class);
        startActivity(register);
    }

}
