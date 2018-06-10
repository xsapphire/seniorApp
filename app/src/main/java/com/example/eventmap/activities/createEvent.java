package com.example.eventmap.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.eventmap.R;
import com.example.eventmap.models.Club;
import com.example.eventmap.models.Event;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class createEvent extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String currentUserIdInFirebase;

    private String placeName;
    private String placeAddress;
    private String placeLat;
    private String placeLog;

    TextView b_pick_start, b_pick_end;
    int start_day, start_month, start_year, start_hour, start_minute;
    int start_dayFinal, start_monthFinal, start_yearFinal, start_hourFinal, start_minuteFinal;

    int end_day, end_month, end_year, end_hour, end_minute;
    int end_dayFinal, end_monthFinal, end_yearFinal, end_hourFinal, end_minuteFinal;

    DatePickerDialog.OnDateSetListener start_dateListener, end_dateListener;
    TimePickerDialog.OnTimeSetListener start_timeListener, end_timeListener;

    private Button finishBtn;
    private EditText eventName;
    private EditText roomNumber;
    private EditText description;
    private TextView selectedPlace;
    private Spinner clubSpinner;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        Toolbar toolbar = (Toolbar)findViewById(R.id.include);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.create_view_title);

        FirebaseApp.initializeApp(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUserIdInFirebase = getIntent().getStringExtra("currentUserId");

        b_pick_start = (TextView) findViewById(R.id.pickStartTime);
        b_pick_end = (TextView) findViewById(R.id.pickEndTime);
        eventName = (EditText) findViewById(R.id.eventNameInput);
        selectedPlace = (TextView) findViewById(R.id.selectedPlace);
        roomNumber = (EditText) findViewById(R.id.roomInput);
        description = (EditText) findViewById(R.id.descriptionInput);
        clubSpinner = (Spinner) findViewById(R.id.clubSpinner);

        finishBtn = (Button) findViewById(R.id.finishBtn);
        placeName = getIntent().getStringExtra("placeName");
        placeAddress = getIntent().getStringExtra("placeAddress");
        placeLat = getIntent().getStringExtra("placeLatitude");
        placeLog = getIntent().getStringExtra("placeLongitude");

        System.out.println("passed in data: " + placeName + " " + placeAddress + " " + placeLat + " " + placeLog );
        System.out.println("passed in user id" + currentUserIdInFirebase);
        selectedPlace.setText(placeName);

        final List<String> clubNamesOfUser = new ArrayList<String>();
        final String noNotification = "Do not notify any club members. ";
        clubNamesOfUser.add(noNotification);

        DatabaseReference usersClubDatabase = mDatabase.child("users").child(currentUserIdInFirebase).child("clubIds");
        usersClubDatabase.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot clubOfUserSnapshot: dataSnapshot.getChildren()){
                    System.out.println("dsp+getValue: " + clubOfUserSnapshot.getValue().toString());
                    String clubId = clubOfUserSnapshot.getValue().toString();
                    DatabaseReference clubRef = mDatabase.child("clubs").child(clubId);
                    clubRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            System.out.println("fetch club name dsp" + dataSnapshot);
                            Club c = dataSnapshot.getValue(Club.class);
                            clubNamesOfUser.add(c.getClubName());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, clubNamesOfUser);
                clubSpinner.setAdapter(adapter);
                clubSpinner.setSelection(adapter.getPosition(noNotification));
                clubSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        Toast.makeText(getBaseContext(), "Notify club members in " + adapterView.getItemAtPosition(i), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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



        finishBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String getEventName = eventName.getText().toString();
                String getRoom = roomNumber.getText().toString();
                String latitude = placeLat;
                String longitude = placeLog;
                String getDescription = description.getText().toString();
                String clubSpinnerSelection = clubSpinner.getSelectedItem().toString();
                System.out.println("club Spinner selection" + clubSpinnerSelection);
                String startTime = start_yearFinal + "-" + start_monthFinal + "-" + start_dayFinal +
                        " " + start_hourFinal + ":" + start_minuteFinal;
                String endTime = end_yearFinal + "-" + end_monthFinal + "-" + end_dayFinal +
                        " " + end_hourFinal + ":" + end_minuteFinal;

                // Here, if clubSpinnerSelection.equals(no selection), then do not notify anyone
                Event newEvent = new Event(getEventName, startTime, endTime, longitude, latitude, placeName, placeAddress,
                        getRoom, getDescription, clubSpinnerSelection);
                DatabaseReference eventDatabase = mDatabase.child("events");
                String eventId = eventDatabase.push().getKey();
                eventDatabase.child(eventId).setValue(newEvent);

                // Add to "eventIds" in the user field
                DatabaseReference thisUserDatabase = mDatabase.child("users").child(currentUserIdInFirebase);
                thisUserDatabase.child("eventIds").push().setValue(eventId);

                // Go to my hosted events
                Intent intent = new Intent(createEvent.this, myEvents.class);
                intent.putExtra("currentUserId", currentUserIdInFirebase);
                startActivity(intent);
            }});
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home){
            this.finish();

        }

        return super.onOptionsItemSelected(item);
    }

}
