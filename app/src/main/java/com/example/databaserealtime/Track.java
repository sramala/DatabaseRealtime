package com.example.databaserealtime;

public class Track {

    private String trackId;
    private String trackName;
    private int rating;



    public Track() {


    }

    public Track(String trackId, String trackName, int rating) {
        this.trackId = trackId;
        this.rating = rating;
        this.trackName = trackName;

    }

    public String getTrackName() {
        return trackName;
    }


    public int getRating() {
        return rating;
    }


}
