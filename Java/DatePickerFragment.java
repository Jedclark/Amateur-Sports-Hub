package com.example.jedcl.sportsstats;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private String parentClass;

    public static final String CREATE_CUP_FIXTURES = "ViewSeasonActivity";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        System.out.println("PARENT CLASS: " + parentClass);
        if(parentClass.equals(CREATE_CUP_FIXTURES)){
            ((TextView)getActivity().findViewById(R.id.view_season_activity_games_start_text))
                    .setText(String.format(Locale.getDefault(), "%d/%d/%d", dayOfMonth, month+1, year));
        } else {
            ((TextView)getActivity().findViewById(R.id.generate_fixtures_activity_date_text))
                    .setText(String.format(Locale.getDefault(), "%d/%d/%d", dayOfMonth, month+1, year));
        }

    }

    public void setParentClass(String parentClass){ this.parentClass = parentClass; }

}
