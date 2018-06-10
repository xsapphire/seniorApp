package com.example.eventmap.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eventmap.adapter.showClubsAdapter;
import com.example.eventmap.models.Club;
import com.example.eventmap.R;
import com.example.eventmap.models.clubCardModel;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class myProfile extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private TextView userNameView;
    private TextView emailView;
    private ImageView picView;
    private CardView cardView;

    private TextView noClubsFoundText;

    private TextView clubNameView;
    private Spinner affliationSelection;
    private LinearLayout clubListArea;
    private ListView clubListView;
    private String currentUserIdInDatabase;


    private Button addClubBtn;
    private LinearLayout addNewClubRow;
    private Button saveClubBtn;
    private String newlyAddedClubId;

    private ArrayList<clubCardModel> clubCardModels = new ArrayList<clubCardModel>();
    private showClubsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        Toolbar toolbar = (Toolbar)findViewById(R.id.include);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.my_profile_title);

        FirebaseApp.initializeApp(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null){
            FirebaseUser currentUser = mAuth.getCurrentUser();
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            String imgUrl = currentUser.getPhotoUrl().toString();

            cardView = (CardView)findViewById(R.id.view);
            userNameView = (TextView)findViewById(R.id.profile_name);
            emailView = (TextView)findViewById(R.id.profile_email);
            picView = (ImageView)findViewById(R.id.profile_image);
            noClubsFoundText = (TextView)findViewById(R.id.noClubsFound);

            userNameView.setText(name);
            emailView.setText(email);
            Glide.with(this).load(imgUrl).into(picView);
        }

        currentUserIdInDatabase = getIntent().getStringExtra("currentUserId");

        addClubBtn = (Button)findViewById(R.id.addClubBtn);
        addNewClubRow = (LinearLayout)findViewById(R.id.add_new_club_row);
        saveClubBtn = (Button)findViewById(R.id.save_new_club);
        clubListArea = (LinearLayout)findViewById(R.id.clubListArea);
        clubListView = (ListView)findViewById(R.id.clubListView);
        harvestAndShowClubs(currentUserIdInDatabase);

        clubNameView = (TextView)findViewById(R.id.clubName_field);
        affliationSelection = (Spinner)findViewById(R.id.club_affliation);


        addClubBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
               addNewClubRow.setVisibility(View.VISIBLE);
               saveClubBtn.setVisibility(View.VISIBLE);
               addClubBtn.setVisibility(View.GONE);
            }
        });

        saveClubBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                final String selectedOption = affliationSelection.getSelectedItem().toString();

                // Check whether the club is created in the database
                final String clubName = clubNameView.getText().toString();
                final DatabaseReference clubDatabse = mDatabase.child("clubs");
                clubDatabse.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean createANewUserNode = true;
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            String currentNodeName = dsp.child("name").getValue().toString();
                            System.out.println("current club name on firebase: " + currentNodeName);
                            System.out.println("input club name" + clubName);
                            if (currentNodeName.equals(clubName)){
                                System.out.println("found same club");
                                if (selectedOption.equals("Owner")){
                                    Toast.makeText(myProfile.this, "This club is owned by someone " +
                                            "else.", Toast.LENGTH_LONG).show();
                                } else {
                                    // add a member to current node -> member
                                    String key = dsp.getKey();
                                    mDatabase.child("clubs").child(key).child("members").push().setValue(currentUserIdInDatabase);
                                    addToUserObject(key, currentUserIdInDatabase);
                                }

                                createANewUserNode = false;
                                restartActivity(myProfile.this);
                            }
                        }

                        if (createANewUserNode){
                            // if the newly registered one is founder
                            System.out.println(selectedOption);
                            if (selectedOption.equals("Owner")){
                                // Add new club node with userId as founderId
                                Club newClub = new Club(clubName, currentUserIdInDatabase);
                                DatabaseReference clubRef = mDatabase.child("clubs");
                                newlyAddedClubId = clubRef.push().getKey();
                                clubRef.child(newlyAddedClubId).setValue(newClub);
                                addToUserObject(newlyAddedClubId, currentUserIdInDatabase);
                                restartActivity(myProfile.this);
                            } else {
                                Toast.makeText(myProfile.this, "We can't find the club "
                                        + clubName, Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

                addNewClubRow.setVisibility(View.GONE);
                saveClubBtn.setVisibility(View.GONE);
                addClubBtn.setVisibility(View.VISIBLE);
                noClubsFoundText.setVisibility(View.GONE);
                clubNameView.setText("");
            }

        });

    }

    public static void restartActivity(Activity act){
        Intent intent = act.getIntent();
        act.finish();
        act.startActivity(intent);
    }


    public void harvestAndShowClubs(final String userId){
        System.out.println("userIdInHarvest: " + userId);
        DatabaseReference clubOfUserNode = mDatabase.child("users").child(userId).child("clubIds");
        clubOfUserNode.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long clubChildrenCount = dataSnapshot.getChildrenCount();
                if (clubChildrenCount == 0){
                    // No clubs found
                    System.out.println("No club found");
                    noClubsFoundText.setVisibility(View.VISIBLE);
                } else {
                    System.out.println("Found club");
                    noClubsFoundText.setVisibility(View.GONE);
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        System.out.println("Datasnapshot of clubofUser:" + dsp);
                        String clubId = dsp.getValue().toString();
                        System.out.println("Datasnapshot of clubofUser clubId: "+ clubId);
                        DatabaseReference clubInDatabase = mDatabase.child("clubs").child(clubId);
                        clubInDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Club c = dataSnapshot.getValue(Club.class);
                                if (c != null){
                                    String clubName = c.getClubName();
                                    String clubFounder = c.getFounder();
                                    System.out.println("club name" + clubName);
                                    System.out.println("club gounfrt" + clubFounder);
                                    if (clubFounder.equals(userId)){
                                        clubCardModels.add(new clubCardModel(clubName, "Owner"));
                                        System.out.println("added a new owner card");
                                    } else {
                                        clubCardModels.add(new clubCardModel(clubName, "Member"));
                                        System.out.println("added a new member card");
                                    }
                                }
                                System.out.println("count" + clubCardModels.size());
                                adapter = new showClubsAdapter(myProfile.this, 0, clubCardModels, currentUserIdInDatabase);
                                clubListView.setAdapter(adapter);
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

    private void addToUserObject(String clubId, String currentUserIdInDatabase){
        mDatabase.child("users").child(currentUserIdInDatabase).child("clubIds").push().setValue(clubId);
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
