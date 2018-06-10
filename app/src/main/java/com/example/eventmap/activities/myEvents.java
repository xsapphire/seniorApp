package com.example.eventmap.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventmap.R;
import com.example.eventmap.adapter.showClubsAdapter;
import com.example.eventmap.adapter.showEventAdapter;
import com.example.eventmap.models.Club;
import com.example.eventmap.models.Event;
import com.example.eventmap.models.clubCardModel;
import com.example.eventmap.models.eventCardModel;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class myEvents extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String currentUserIdInDatabase;

    private ArrayList<eventCardModel> eventCardModels = new ArrayList<eventCardModel>();
    private showEventAdapter adapter;
    private ListView eventListView;
    private TextView noItemsFoundText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        Toolbar toolbar = (Toolbar)findViewById(R.id.include);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.my_event_title);

        FirebaseApp.initializeApp(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        currentUserIdInDatabase = getIntent().getStringExtra("currentUserId");

        eventListView = (ListView)findViewById(R.id.eventListView);
        noItemsFoundText = (TextView)findViewById(R.id.noItemFound);
        DatabaseReference userEventDatabase = mDatabase.child("users").child(currentUserIdInDatabase).child("eventIds");
        userEventDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("events datasnapshot: " + dataSnapshot);
                long eventChildrenCount = dataSnapshot.getChildrenCount();
                if (eventChildrenCount == 0) {
                    System.out.println("no hosted events found.");
                    noItemsFoundText.setVisibility(View.VISIBLE);
                } else {
                    for (DataSnapshot eventInUserDsp : dataSnapshot.getChildren()){
                        final String eventId = eventInUserDsp.getValue().toString();
                        DatabaseReference eventDatabase = mDatabase.child("events").child(eventId);
                        eventDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                System.out.println("datasnapShot in my events: " + dataSnapshot);
                                Event e = dataSnapshot.getValue(Event.class);
                                String curEventName = e.getEventName();
                                String curStartTime = e.getStartTime();
                                String curEndTime = e.getEndTime();
                                String curPlaceName = e.getPlaceName();
                                String curRoomNumber = e.getRoomNumber();

                                eventCardModels.add(new eventCardModel(curEventName, curStartTime, curEndTime,
                                       curPlaceName, curRoomNumber, eventId));

                                adapter = new showEventAdapter(myEvents.this, 0, eventCardModels, currentUserIdInDatabase);
                                eventListView.setAdapter(adapter);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //handle databaseError
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
            Intent intent = new Intent(myEvents.this, MainActivity.class);
            startActivity(intent);
            return true;
            //this.finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
