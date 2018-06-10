package com.example.eventmap.models;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by SS on 5/25/2018.
 */

public class Event {
    public String eventName;
    public String startTime;
    public String endTime;
    public String longitude;
    public String latitude;
    public String placeName;
    public String placeAddress;
    public String roomNumber;
    public String description;
    //public ArrayList<String> invitedEmails;
    public String notifyClub;
    public Event(){

    }

    public Event(String eventName, String startTime, String endTime, String longitude,
                 String latitude, String placeName, String placeAddress, String roomNumber, String description, String clubName){
        this.eventName = eventName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.longitude = longitude;
        this.latitude = latitude;
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.roomNumber = roomNumber;
        this.description = description;
        //invitedEmails = new ArrayList(Arrays.asList(emails.split(",")));
        this.notifyClub = clubName;
    }

    public String getEventName(){
        return this.eventName;
    }

    public String getStartTime(){
        return this.startTime;
    }

    public String getEndTime(){
        return this.endTime;
    }

    public String getPlaceName(){
        return this.placeName;
    }

    public String getPlaceAddress() {return this.placeAddress;}

    public String getRoomNumber(){
        return this.roomNumber;
    }

    public String getDescription(){ return this.description;}

    public String getLongitude() {return this.longitude;}

    public String getLatitude() {return this.latitude;}

    public String getNotifyClub() {return this.notifyClub;}

}
