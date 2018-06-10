package com.example.eventmap.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventmap.R;
import com.example.eventmap.activities.EditEvents;
import com.example.eventmap.models.Event;
import com.example.eventmap.models.eventCardModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by SS on 5/26/2018.
 */

public class showEventAdapter extends ArrayAdapter<eventCardModel> {
    private Context context;
    private ArrayList<eventCardModel> eventModels;
    private DatabaseReference mDatabase;
    private String userId;
    public showEventAdapter(Context context, int position, ArrayList<eventCardModel> eventModels, String userId){
        super(context, position);
        this.context = context;
        this.eventModels = eventModels;
        this.userId = userId;
    }

    @Override
    public int getCount() {
        return eventModels.size();
    }

    @Override
    public eventCardModel getItem(int i) {
        return eventModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if(view == null){
            view=View.inflate(context, R.layout.event_list_items, null);
        }

        TextView eventNameHolder = view.findViewById(R.id.eventNameHolder);
        TextView timeHolder = view.findViewById(R.id.timeHolder);
        TextView placeHolder = view.findViewById(R.id.placeHolder);
        TextView roomHolder = view.findViewById(R.id.roomHolder);
        Button editBtn = view.findViewById(R.id.editBtn);
        Button deleteBtn = view.findViewById(R.id.deleteBtn);

        final eventCardModel eventModel = eventModels.get(i);
        eventNameHolder.setText(eventModel.getEventName());
        timeHolder.setText(eventModel.getStartTime() + " - " + eventModel.getEndTime());
        placeHolder.setText(eventModel.getPlaceName());
        roomHolder.setText(eventModel.getRoomNumber());

        editBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                System.out.println("Button was clicked");
                String eventId = eventModel.getEventId();

                Intent intent = new Intent(context, EditEvents.class);
                intent.putExtra("eventId", eventId);
                intent.putExtra("currentUserId", userId);
                context.startActivity(intent);
            }
        });

        final eventCardModel model = eventModels.get(i);

        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
            System.out.println("Delete Button was clicked");
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(true);
            builder.setTitle("Title");
            builder.setMessage("Message");
            builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Delete
                        final String eventId = eventModel.getEventId();
                        Query eventQuery = mDatabase.child("events").child(eventId);
                        eventQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot eventIdSnapshot: dataSnapshot.getChildren()){
                                    System.out.println("eventIdSnapshot: " + eventIdSnapshot);
                                    eventIdSnapshot.getRef().removeValue();
                                    eventModels.remove(model);
                                    notifyDataSetChanged();

                                    //Remove from user->eventIds
                                    DatabaseReference usersEventDatabase = mDatabase.child("users").child(userId).child("eventIds");
                                    usersEventDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot dsp: dataSnapshot.getChildren()){
                                                System.out.println("dsp: " + dsp);
                                                if (dsp.getValue().equals(eventId)){
                                                    dsp.getRef().removeValue();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });


                                }

                                Toast.makeText(context, model.getEventName() + " has been deleted.", Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "onCancelled", databaseError.toException());
                            }
                        });


                    }
                });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();



            }
        });

        return view;
    }

}
