package com.example.jedcl.sportsstats;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    /**
     * Validates a user's attempt to register to the system.
     * @param view The view being used for the onClick method.
     */
    public void register(View view){

        InputValidator iv = new InputValidator(this);
        InterfaceHelper interfaceHelper = new InterfaceHelper(this, RegisterActivity.this);
        boolean isValid = true;

        //Validate a user's username
        if(!iv.validString(User.USERNAME, interfaceHelper.getTextFromEditText(R.id.register_activity_name_edit_text),
                User.USERNAME_MIN_LENGTH, User.USERNAME_MAX_LENGTH)){ isValid = false; }

        //Validate a user's first name
        if(!iv.validString(User.FIRST_NAME, interfaceHelper.getTextFromEditText(R.id.register_activity_first_name_edit_text),
                User.FIRST_NAME_MIN_LENGTH, User.FIRST_NAME_MAX_LENGTH)){ isValid = false; }

        //Validate a user's surname
        if(!iv.validString(User.SURNAME, interfaceHelper.getTextFromEditText(R.id.register_activity_surname_edit_text),
                User.SURNAME_MIN_LENGTH, User.SURNAME_MAX_LENGTH)){ isValid = false; }

        //Validate a user's password
        if(!iv.validString(User.PASSWORD, interfaceHelper.getTextFromEditText(R.id.register_activity_password_edit_text),
                User.PASSWORD_MIN_LENGTH, User.PASSWORD_MAX_LENGTH)){ isValid = false; }

        //Validate a user's re-entered password
        if(!iv.validString(User.PASSWORD, interfaceHelper.getTextFromEditText(R.id.register_activity_password_confirm_edit_text),
                User.PASSWORD_MIN_LENGTH, User.PASSWORD_MAX_LENGTH)){ isValid = false; }

        //Validate the user's email
        if(!iv.validString(User.EMAIL, interfaceHelper.getTextFromEditText(R.id.register_activity_email_edit_text),
                User.EMAIL_MIN_LENGTH, User.EMAIL_MAX_LENGTH)){ isValid = false; }

        //Validate that the user's passwords match
        if(!(interfaceHelper.getTextFromEditText(R.id.register_activity_password_edit_text)
                .equals(interfaceHelper.getTextFromEditText(R.id.register_activity_password_confirm_edit_text)))){
            isValid = false;
            Toast.makeText(this, R.string.register_activity_password_match_validation, Toast.LENGTH_SHORT).show();
        }

        if(isValid){
            Map<String, String> data = new HashMap<>();
            data.put("first_name", interfaceHelper.getTextFromEditText(R.id.register_activity_first_name_edit_text));
            data.put("last_name", interfaceHelper.getTextFromEditText(R.id.register_activity_surname_edit_text));
            data.put("username", interfaceHelper.getTextFromEditText(R.id.register_activity_name_edit_text));
            String password = interfaceHelper.getTextFromEditText(R.id.register_activity_password_edit_text);
            String hash = BCrypt.hashpw(password, BCrypt.gensalt(10));
            data.put("password", hash);
            data.put("email", interfaceHelper.getTextFromEditText(R.id.register_activity_email_edit_text));
            APIHelper.sendData(this, Request.Method.POST, APIHelper.USER_REGISTER_QUERY, data);
            finish();
        }
    }

}
