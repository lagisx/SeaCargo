package com.example.seacargo.models;


import java.time.LocalDate;

public class Flight {
    private int id;
    private String flightNumber;
    private String departure;
    private String destination;
    private LocalDate date;

    public Flight(int id, String flightNumber, String departure, String destination, LocalDate date) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.departure = departure;
        this.destination = destination;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDeparture() {
        return departure;
    }

    public String getDestination() {
        return destination;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

}

