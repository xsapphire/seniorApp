package com.example.eventmap.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventmap.R;
import com.example.eventmap.models.Event;
import com.example.eventmap.models.clubCardModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;


/**
 * Created by SS on 6/9/2018.
 */

public class infoListAdapter extends ArrayAdapter<Event> {
    private Context context;
    private ArrayList<Event> events;
    private DatabaseReference mDatabase;

    public infoListAdapter(Context context, int position, ArrayList<Event> events) {
        super(context, position);
        this.context = context;
        this.events = events;

    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Event getItem(int i) {
        return events.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (view == null){
            view = View.inflate(context, R.layout.infowindow_event_list, null);
        }

        TextView nameHolder = (TextView)view.findViewById(R.id.infoEventNameHolder);
        TextView timeHolder = (TextView)view.findViewById(R.id.infoEventTimeHolder);
        TextView roomHolder = (TextView)view.findViewById(R.id.infoEventRoomHolder);
        TextView descriptHolder = (TextView)view.findViewById(R.id.infoEventDescriptionHolder);
        final Event thisEvent = events.get(i);
        nameHolder.setText(thisEvent.getEventName());
        timeHolder.setText(thisEvent.getStartTime() + " - " + thisEvent.getEndTime());
        String room = thisEvent.getRoomNumber();
        if (room == ""){
            roomHolder.setVisibility(view.GONE);
        } else {
            roomHolder.setText("Room: " + room);
        }
        String description = thisEvent.getDescription();
        if (description == ""){
            descriptHolder.setVisibility(view.GONE);
        } else {
            descriptHolder.setText("Description: " + description);
        }

        return view;
    }

}
