package com.hajjhackathon.fifthpillar.models;

public class HajiBO {

    private String hajiID;
    private String sender;
    private String locationGroup;
    private long timeStamp;
    private double lat;
    private double lng;

    public String getHajiID() {
        return hajiID;
    }

    public void setHajiID(String hajiID) {
        this.hajiID = hajiID;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getLocationGroup() {
        return locationGroup;
    }

    public void setLocationGroup(String locationGroup) {
        this.locationGroup = locationGroup;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
