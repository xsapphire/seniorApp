package com.example.eventmap;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.text.format.DateFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class createEvent extends AppCompatActivity
        //implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener
        {

    TextView b_pick_start, b_pick_end;
    int start_day, start_month, start_year, start_hour, start_minute;
    int start_dayFinal, start_monthFinal, start_yearFinal, start_hourFinal, start_minuteFinal;

    int end_day, end_month, end_year, end_hour, end_minute;
    int end_dayFinal, end_monthFinal, end_yearFinal, end_hourFinal, end_minuteFinal;

    DatePickerDialog.OnDateSetListener start_dateListener, end_dateListener;
    TimePickerDialog.OnTimeSetListener start_timeListener, end_timeListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        Toolbar toolbar = (Toolbar)findViewById(R.id.include);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.create_view_title);

        b_pick_start = (TextView) findViewById(R.id.pickStartTime);
        b_pick_end = (TextView) findViewById(R.id.pickEndTime);

        start_timeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                start_hourFinal = i;
                start_minuteFinal = i1;

                b_pick_start.setText(start_yearFinal+"-"+start_monthFinal+"-"+start_dayFinal+" "+start_hourFinal+":"+start_minuteFinal);
            }
        };

        end_timeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                end_hourFinal = i;
                end_minuteFinal = i1;

                b_pick_end.setText(end_yearFinal+"-"+end_monthFinal+"-"+end_dayFinal+" "+end_hourFinal+":"+end_minuteFinal);
            }
        };

        start_dateListener = new DatePickerDialog.OnDateSetListener(){
            public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                start_yearFinal = arg1;
                start_monthFinal = arg2+1;
                start_dayFinal = arg3;

                Calendar c = Calendar.getInstance();
                start_hour = c.get(Calendar.HOUR_OF_DAY);
                start_minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(createEvent.this,
                        start_timeListener, start_hour, start_minute, true);
                timePickerDialog.show();
            }
        };

        end_dateListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                end_yearFinal = arg1;
                end_monthFinal = arg2+1;
                end_dayFinal = arg3;

                Calendar c = Calendar.getInstance();
                end_hour = c.get(Calendar.HOUR_OF_DAY);
                end_minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(createEvent.this,
                        end_timeListener, end_hour, end_minute, true);
                timePickerDialog.show();
            }
        };

        b_pick_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                start_year = c.get(Calendar.YEAR);
                start_month = c.get(Calendar.MONTH);
                start_day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(createEvent.this, start_dateListener, start_year, start_month, start_day);
                datePickerDialog.show();
            }
        });
        b_pick_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                end_year = c.get(Calendar.YEAR);
                end_month = c.get(Calendar.MONTH);
                end_day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(createEvent.this, end_dateListener, end_year, end_month, end_day);
                datePickerDialog.show();
            }
        });
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home){
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }



   /* @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        yearFinal = i;
        monthFinal = i1+1;
        dayFinal = i2;

        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(createEvent.this,
                createEvent.this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        hourFinal = i;
        minuteFinal = i1;

        b_pick_start.setText(yearFinal+"-"+monthFinal+"-"+dayFinal+" "+hourFinal+":"+minuteFinal);
    }*/
}
