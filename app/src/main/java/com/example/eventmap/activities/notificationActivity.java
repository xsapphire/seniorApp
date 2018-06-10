package com.example.eventmap.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.eventmap.R;
import com.example.eventmap.adapter.showClubsAdapter;
import com.example.eventmap.adapter.showEventAdapter;
import com.example.eventmap.adapter.showNotificationAdapter;
import com.example.eventmap.models.Club;
import com.example.eventmap.models.Event;
import com.example.eventmap.models.clubCardModel;
import com.example.eventmap.models.eventCardModel;
import com.example.eventmap.models.notificationCardModel;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class notificationActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String currentUserIdInDatabase;

    private ArrayList<notificationCardModel> notificationCardModels = new ArrayList<notificationCardModel>();
    private showNotificationAdapter adapter;
    private ListView notificiationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Toolbar toolbar = (Toolbar)findViewById(R.id.include);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.notification_title);

        FirebaseApp.initializeApp(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        currentUserIdInDatabase = getIntent().getStringExtra("currentUserId");

        notificiationView = (ListView)findViewById(R.id.notificationListView);
        final DatabaseReference eventDatabase = mDatabase.child("events");
        final DatabaseReference notifiedEventsDatabase =
                mDatabase.child("users").child(currentUserIdInDatabase).child("notifiedEvents");
        // fetch notifiedEvent list
        notifiedEventsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long eventChildrenCount = dataSnapshot.getChildrenCount();
                if (eventChildrenCount == 0){
                    // No clubs found
                    System.out.println("No notification found");
                } else {
                    System.out.println("Found notification");
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        String eventId = dsp.getValue().toString();
                        DatabaseReference clubInDatabase = eventDatabase.child(eventId);
                        clubInDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Event e = dataSnapshot.getValue(Event.class);
                                String eventName = e.getEventName();
                                String placeName = e.getPlaceName();
                                String startTime = e.getStartTime();
                                String endTime = e.getEndTime();
                                String roomNumber = e.getRoomNumber();
                                notificationCardModels.add(new notificationCardModel(
                                        eventName,startTime,endTime, placeName, roomNumber));
                                adapter = new showNotificationAdapter(notificationActivity.this, notificationCardModels);
                                notificiationView.setAdapter(adapter);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //handle databaseError
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
}
