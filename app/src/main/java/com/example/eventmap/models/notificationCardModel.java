package com.example.eventmap.models;

/**
 * Created by SS on 6/5/2018.
 */

public class notificationCardModel {
    private String eventName;
    private String startTime;
    private String endTime;
    private String place;
    private String roomNumber;

    public notificationCardModel(String name, String sTime, String eTime,
                                 String placeName, String room) {
        this.eventName = name;
        this.startTime = sTime;
        this.endTime = eTime;
        this.place = placeName;
        this.roomNumber = room;
    }

    public String getEventName() {return this.eventName;}
    public String getStartTime() {return this.startTime;}
    public String getEndTime() {return this.endTime;}
    public String getPlace() {return this.place;}
    public String getRoomNumber() {return this.roomNumber;}
}
