package com.example.eventmap.models;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by SS on 5/25/2018.
 */

public class eventCardModel {
    private String eventName;
    private String startTime;
    private String endTime;
    private String placeName;
    private String roomNumber;
    private String description;
    private String eventId;

    public eventCardModel(String eventName, String startTime, String endTime, String placeName,
                          String roomNumber, String eventId){
        this.eventName = eventName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.placeName = placeName;
        this.roomNumber = roomNumber;
        this.eventId = eventId;
    }

    public String getEventId() {return this.eventId;}

    public String getEventName() {
        return this.eventName;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public String getEndTime(){
        return this.endTime;
    }

    public String getRoomNumber(){
        return this.roomNumber;
    }

    public String getDescription(){
        return this.description;
    }

    public String getPlaceName(){return this.placeName; }

    public void setEventName(String eventName){
        this.eventName = eventName;
    }

    public void setStartTime(String startTime){
        this.startTime = startTime;
    }

    public void setEndTime(String endTime){
        this.endTime = endTime;
    }


    public void setRoomNumber(String roomNumber){
        this.roomNumber = roomNumber;
    }

    public void setDescription(String description){
        this.description = description;
    }

}
