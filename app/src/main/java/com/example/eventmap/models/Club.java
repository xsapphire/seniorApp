package com.example.eventmap.models;

import java.util.ArrayList;

/**
 * Created by SS on 5/19/2018.
 */

public class Club {
    public String name;
    public String founder;
    public ArrayList<String> members = new ArrayList<String>();
    public ArrayList<String> events = new ArrayList<String>();

    public Club(){

    }

    public Club(String name, String userId) {
        this.name = name;
        this.founder = userId;
    }

    public String getClubName(){
        return this.name;
    }

    public String getFounder() {
        return this.founder;
    }

    public ArrayList<String> getMembers(){
        return this.members;
    }

    public ArrayList<String> getEvents(){
        return this.events;
    }
}
