package com.example.jedcl.sportsstats;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.vision.text.Line;

public class InterfaceHelper {

    private Context context;
    private Activity activity;

    public InterfaceHelper(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
    }

    /*
     * ---------------------------------------------------------------------------------------------
     * EDIT TEXT METHODS
     * ---------------------------------------------------------------------------------------------
     */

    public String getTextFromEditText(int resourceID){
        return ((EditText)activity.findViewById(resourceID)).getText().toString();
    }

    public EditText createEditText(int inputType){
        EditText temp = new EditText(context);
        temp.setInputType(inputType);
        return temp;
    }

    public void setEditTextHint(int resourceID, String hint){
        ((EditText)activity.findViewById(resourceID)).setHint(hint);
    }

    public void setEditTextHint(int resourceID, int stringResourceID){
        ((EditText)activity.findViewById(resourceID)).setHint(stringResourceID);
    }

    /*
     * ---------------------------------------------------------------------------------------------
     * TEXT VIEW METHODS
     * ---------------------------------------------------------------------------------------------
     */

    public void setTextView(int resourceID, String text){
        ((TextView)activity.findViewById(resourceID)).setText(text);
    }

    public void setTextView(int resourceID, int stringResourceID){
        ((TextView)activity.findViewById(resourceID)).setText(stringResourceID);
    }

    public void setTextView(int resourceID, String text, float size){
        ((TextView)activity.findViewById(resourceID)).setText(text);
        ((TextView)activity.findViewById(resourceID)).setTextSize(size);
    }

    public TextView createTextView(String text, float size){
        TextView temp = new TextView(context);
        temp.setText(text);
        temp.setTextSize(size);
        return temp;
    }

    public TextView createTextView(int resourceStringID, float size){
        TextView temp = new TextView(context);
        temp.setText(resourceStringID);
        temp.setTextSize(size);
        return temp;
    }

    public TextView createTextView(String text){
        TextView temp = new TextView(context);
        temp.setText(text);
        return temp;
    }

    public String getTextFromTextView(int resourceID){
        return ((TextView)activity.findViewById(resourceID)).getText().toString();
    }

    /*
     * ---------------------------------------------------------------------------------------------
     * BUTTON METHODS
     * ---------------------------------------------------------------------------------------------
     */

    /**
     *
     * @param stringResourceID
     * @return
     */
    public Button createButton(int stringResourceID){
        Button temp = new Button(context);
        temp.setText(stringResourceID);
        return temp;
    }

    public Button createButton(int stringResourceID, int drawableResourceID){
        Button temp = new Button(context);
        temp.setText(stringResourceID);
        temp.setBackgroundResource(drawableResourceID);
        return temp;
    }

    /**
     *
     * @param text
     * @return
     */
    public Button createButton(String text){
        Button temp = new Button(context);
        temp.setText(text);
        return temp;
    }

    /*
     * ---------------------------------------------------------------------------------------------
     * LINEAR LAYOUT METHODS
     * ---------------------------------------------------------------------------------------------
     */

    public LinearLayout updateLinearLayout(int resourceID, int orientation){
        LinearLayout temp = activity.findViewById(resourceID);
        temp.setOrientation(orientation);
        return temp;
    }

    public LinearLayout createLinearLayout(int orientation){
        LinearLayout temp = new LinearLayout(context);
        temp.setOrientation(orientation);
        return temp;
    }

    public void addMultipleViews(int resourceID, View[] views){
        for(View v : views){
            ((LinearLayout)activity.findViewById(resourceID)).addView(v);
        }
    }

    public void addMultipleViews(LinearLayout container, View[] views){
        for(View v : views){
            container.addView(v);
        }
    }

    /*
     * ---------------------------------------------------------------------------------------------
     * GENERAL VIEW METHODS
     * ---------------------------------------------------------------------------------------------
     */

    public void addLayoutParams(View view, int width, int height, float weight){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height, weight);
        view.setLayoutParams(params);
    }

    public void addLayoutParams(View view, int width, int height){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        view.setLayoutParams(params);
    }

    public void setMargin(View view, int width, int height, int[] marginParams){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        params.setMargins(marginParams[0], marginParams[1], marginParams[2], marginParams[3]);
        view.setLayoutParams(params);
    }

}
