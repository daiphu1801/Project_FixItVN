package com.fixit.data.model;

public class Appointment {
    private String time;
    private String serviceTitle;
    private String address;

    public Appointment(String time, String serviceTitle, String address) {
        this.time = time;
        this.serviceTitle = serviceTitle;
        this.address = address;
    }

    public String getTime() { return time; }
    public String getServiceTitle() { return serviceTitle; }
    public String getAddress() { return address; }
}
