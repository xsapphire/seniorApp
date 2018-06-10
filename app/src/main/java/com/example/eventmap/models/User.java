package com.example.eventmap.models;

import java.util.ArrayList;

/**
 * Created by SS on 5/18/2018.
 */

public class User {

    public String email;
    public ArrayList<String> clubIds= new ArrayList<String>();;
    public ArrayList<String> eventIds= new ArrayList<String>();;
    public ArrayList<String> notifiedEvents = new ArrayList<String>();

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email) {
        this.email = email;
    }



}