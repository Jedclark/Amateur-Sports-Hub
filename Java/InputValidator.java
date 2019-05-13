package com.example.jedcl.sportsstats;

import android.content.Context;
import android.widget.Toast;

/**
 * @author Jed Clark
 * A class containing methods to validate user input.
 */
public class InputValidator {

    private Context context;

    public InputValidator(Context context){
        this.context = context;
    }

    /**
     * Checks if a user-input String is valid.
     * @param s The input String being tested.
     * @param minLength The minimum length of the String.
     * @param maxLength The maximum length of the String.
     * @return True if the String is valid, false otherwise.
     */
    /*
    public boolean validString(String s, int minLength, int maxLength){
        return s.length() >= minLength && s.length() <= maxLength;
    }*/
    public boolean validString(String type, String s, int minLength, int maxLength){
        if(s.length() >= minLength && s.length() <= maxLength){
            return true;
        } else {
            Toast.makeText(context, renderInputOutOfBoundsError(type, minLength, maxLength), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * Checks if a user-input year is valid.
     * @param s The year being tested.
     * @return True if the year is valid, false otherwise.
     */
    public boolean validYear(String s){
        if(s.length() != 4){
            Toast.makeText(context, R.string.create_teams_details_year_validation, Toast.LENGTH_SHORT).show();
            return false;
        }
        char[] x = s.toCharArray();
        for(int i = 0; i < x.length; i++){
            if(!Character.isDigit(x[i])){
                Toast.makeText(context, R.string.create_teams_details_year_validation, Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    /**
     * Create a String representing a specific length-error.
     * @param type The field you are creating the error for, e.g. "Username".
     * @param minLength The minimum length of the field.
     * @param maxLength The maximum length of the field.
     * @return A String representing a specific length-error.
     */
    public String renderInputOutOfBoundsError(String type, int minLength, int maxLength){
        return type + " must be between " + minLength + " and " + maxLength + " characters";
    }

}
