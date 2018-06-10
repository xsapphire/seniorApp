package com.example.eventmap.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.example.eventmap.models.PlaceInfo;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditEvents extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String currentUserIdInFirebase;
    private DatabaseReference mDatabase;
    private GoogleApiClient placeGoogleApiClient;

    private Button saveBtn;
    private EditText eventName;
    private EditText roomNumber;
    private EditText description;
    private TextView selectedPlace;
    private TextView selectedAddress;
    private Spinner clubSpinner;

    private String placeName;
    private String placeAddress;
    private String placeLat;
    private String placeLog;

    private boolean changedStartTime = false;
    private boolean changedEndTime = false;

    private static final int PLACE_PICKER_REQUEST = 1;
    private static final String TAG = "EditEventsActivity";

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
        setContentView(R.layout.activity_edit_events);
        Toolbar toolbar = (Toolbar)findViewById(R.id.include);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.create_view_title);

        FirebaseApp.initializeApp(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        currentUserIdInFirebase = getIntent().getStringExtra("currentUserId");
        final String eventId = getIntent().getStringExtra("eventId"); //TODO: PASS IN EventID


        b_pick_start = (TextView) findViewById(R.id.pickStartTime);
        b_pick_end = (TextView) findViewById(R.id.pickEndTime);
        eventName = (EditText) findViewById(R.id.eventNameInput);
        selectedPlace = (TextView) findViewById(R.id.selectedPlace);
        selectedAddress = (TextView) findViewById(R.id.address);
        roomNumber = (EditText) findViewById(R.id.roomInput);
        description = (EditText) findViewById(R.id.descriptionInput);
        clubSpinner = (Spinner) findViewById(R.id.clubSpinner);
        saveBtn = (Button) findViewById(R.id.saveBtn);

        // Fetch original data from database
        DatabaseReference thisEventDatabase = mDatabase.child("events").child(eventId);
        thisEventDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event e = dataSnapshot.getValue(Event.class);

                if (e!= null){
                    setDefaultInformation(e);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        placeGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .build();

        placeGoogleApiClient.connect();

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

                TimePickerDialog timePickerDialog = new TimePickerDialog(EditEvents.this,
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

                TimePickerDialog timePickerDialog = new TimePickerDialog(EditEvents.this,
                        end_timeListener, end_hour, end_minute, true);
                timePickerDialog.show();
            }
        };

        b_pick_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changedStartTime = true;
                Calendar c = Calendar.getInstance();
                start_year = c.get(Calendar.YEAR);
                start_month = c.get(Calendar.MONTH);
                start_day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditEvents.this, start_dateListener, start_year, start_month, start_day);
                datePickerDialog.show();
            }
        });
        b_pick_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changedEndTime = true;
                Calendar c = Calendar.getInstance();
                end_year = c.get(Calendar.YEAR);
                end_month = c.get(Calendar.MONTH);
                end_day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditEvents.this, end_dateListener, end_year, end_month, end_day);
                datePickerDialog.show();
            }
        });

        selectedPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(EditEvents.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.e(TAG, "onClick: GooglePlayServicesRepairableException: " + e.getMessage() );
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.e(TAG, "onClick: GooglePlayServicesNotAvailableException: " + e.getMessage() );
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> eventUpdates = new HashMap<>();
                String getEventName = eventName.getText().toString();
                String getRoom = roomNumber.getText().toString();
                String getDescription = description.getText().toString();

                if (changedStartTime){
                    String startTime = start_yearFinal + "-" + start_monthFinal + "-" + start_dayFinal +
                            " " + start_hourFinal + ":" + start_minuteFinal;
                    eventUpdates.put("startTime", startTime);
                }
                if (changedEndTime){
                    String endTime = end_yearFinal + "-" + end_monthFinal + "-" + end_dayFinal +
                            " " + end_hourFinal + ":" + end_minuteFinal;
                    eventUpdates.put("endTime", endTime);
                }

                String clubSpinnerSelection = clubSpinner.getSelectedItem().toString();

                eventUpdates.put("eventName", getEventName);
                eventUpdates.put("roomNumber", getRoom);
                eventUpdates.put("placeName", placeName);
                eventUpdates.put("placeAddress", placeAddress);
                eventUpdates.put("latitude", placeLat);
                eventUpdates.put("longitude", placeLog);
                eventUpdates.put("description", getDescription);
                eventUpdates.put("notifyClub", clubSpinnerSelection);

                // update this event in database
                DatabaseReference thisEventDatabase = mDatabase.child("events").child(eventId);
                thisEventDatabase.updateChildren(eventUpdates);
                // Go to my hosted events
                Intent intent = new Intent(EditEvents.this, myEvents.class);
                intent.putExtra("currentUserId", currentUserIdInFirebase);
                startActivity(intent);
            }
        });

    }

    private void setDefaultInformation(Event e){
        String eName = e.getEventName();
        String sTime = e.getStartTime();
        String eTime = e.getEndTime();
        placeName = e.getPlaceName();
        placeAddress = e.getPlaceAddress();
        String rNumber = e.getRoomNumber();
        String descript = e.getDescription();
        placeLog = e.getLongitude();
        placeLat = e.getLatitude();
        final String notifyClub = e.getNotifyClub();

        eventName.setText(eName);
        b_pick_start.setText(sTime);
        b_pick_end.setText(eTime);
        selectedPlace.setText(placeName);
        selectedAddress.setText(placeAddress);
        roomNumber.setText(rNumber);
        description.setText(descript);

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
                for (int i = 0 ; i < clubNamesOfUser.size(); i++){
                    if (clubNamesOfUser.get(i).equals(notifyClub)){
                        clubSpinner.setSelection(i);
                    }
                }

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

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_PICKER_REQUEST){
            System.out.println("in place picker request");
            System.out.println("result code: " + resultCode);
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                String toastMsg = String.format("Place: %s", place.getName());
                System.out.println("toast msg: " + toastMsg);
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(placeGoogleApiClient, place.getId());
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home){
            this.finish();
        }
        return true;
    }
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            System.out.println("In callback function. ");
            if (!places.getStatus().isSuccess()){
                Log.d(TAG, "onResult: Place query did not complete successfully: " + places.getStatus().toString());
                places.release();;
                return;
            }
            final Place place = places.get(0);

            try{
                placeName = place.getName().toString();
                placeAddress = place.getAddress().toString();
                placeLat = Double.toString(place.getLatLng().latitude);
                placeLog = Double.toString(place.getLatLng().longitude);

                selectedPlace.setText(placeName);
                selectedAddress.setText(placeAddress);

            } catch (NullPointerException e){
                Log.e(TAG, "onResult: NullPointerException: " + e.getMessage());
            }
        }
    };

}
