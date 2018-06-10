package com.example.eventmap.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.eventmap.R;
import com.example.eventmap.activities.myProfile;
import com.example.eventmap.models.Event;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by SS on 6/8/2018.
 */

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{
    private final View mWindow;
    private Context mContext;

    private HashMap<Marker, ArrayList<Event>> markerList;

    public CustomInfoWindowAdapter(Context context, HashMap<Marker, ArrayList<Event>> markersList) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_marker_info, null);
        markerList = markersList;
    }

    private void renderWindowText(Marker marker, View view){
        String title = marker.getTitle();
        TextView placeHolder = (TextView)view.findViewById(R.id.placeNameHolder);;
        placeHolder.setText(title);
        ListView eventInfoList = (ListView)view.findViewById(R.id.eventListAtThisPalce);


        ArrayList<Event> eventsAtThisPlace = markerList.get(marker);
        for (Event e: eventsAtThisPlace){
            System.out.println("eName: " + e.getEventName());
            System.out.println("eTime: " + e.getStartTime());
            System.out.println("eName: " + e.getEndTime());
        }
        infoListAdapter adapter = new infoListAdapter(mContext, 0, eventsAtThisPlace);
        eventInfoList.setAdapter(adapter);

    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }
}
