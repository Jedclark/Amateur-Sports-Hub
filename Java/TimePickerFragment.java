package com.example.jedcl.sportsstats;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.TextView;
import android.widget.TimePicker;
import android.text.format.DateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private String parentClass;

    public static final String CREATE_CUP_FIXTURES = "ViewSeasonActivity";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(parentClass.equals(CREATE_CUP_FIXTURES)){
            ((TextView)getActivity().findViewById(R.id.view_season_activity_game_time_text))
                    .setText(String.format(Locale.getDefault(), "%d:%s", hourOfDay, formatMinutes(minute)));
        } else {
            ((TextView)getActivity().findViewById(R.id.generate_fixtures_activity_time_text))
                    .setText(String.format(Locale.getDefault(), "%d:%s", hourOfDay, formatMinutes(minute)));
        }

    }

    private String formatMinutes(int minute){
        return (minute < 10) ? "0" + String.valueOf(minute) : String.valueOf(minute);
    }

    public void setParentClass(String parentClass){ this.parentClass = parentClass; }

}
