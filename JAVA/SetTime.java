package com.parse.DiabetsApplication;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

class SetTime implements View.OnFocusChangeListener, TimePickerDialog.OnTimeSetListener {

    //Declaration of variables

    private EditText editText;
    private Calendar myCalendar;
    private Context ctx;

    public SetTime(EditText editText, Context ctx){
        this.editText = editText;
        this.editText.setOnFocusChangeListener(this);
        this.myCalendar = Calendar.getInstance();
        this.ctx=ctx;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus){
            int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = myCalendar.get(Calendar.MINUTE);
            new TimePickerDialog(ctx, this, hour, minute, true).show();
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        String hour=hourOfDay+"";
        if(hourOfDay<10)hour="0"+hourOfDay;
        String strMinute=minute+"";
        if(minute<10)strMinute="0"+minute;
        String formatTime=hour+":"+strMinute;
        this.editText.setText( formatTime);
    }

}