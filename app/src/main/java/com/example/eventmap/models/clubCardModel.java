package com.example.eventmap.models;

/**
 * Created by SS on 5/24/2018.
 */

public class clubCardModel {
    private String clubName;
    private String affliation;

    public clubCardModel(String clubName, String affliation){
        this.clubName = clubName;
        this.affliation = affliation;
    }

    public void setClubName(String clubNameInput){
        this.clubName = clubNameInput;
    }

    public void setAffliation(String affliationInput){
        this.affliation = affliationInput;
    }

    public String getClubName(){
        return clubName;
    }

    public String getAffliation(){
        return affliation;
    }

}
