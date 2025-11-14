package com.example.seacargo.models;

public class Cargo {
    private int id;
    private String name;
    private String weight;
    private String sender;
    private String receiver;
    private int userId;
    private String status;

    public Cargo(int id, String name, String weight, String sender, String receiver, int userId, String status) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.sender = sender;
        this.receiver = receiver;
        this.userId = userId;
        this.status = status;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getWeight() { return weight; }
    public String getSender() { return sender; }
    public String getReceiver() { return receiver; }
    public int getUserId() { return userId; }
    public String getStatus() { return status; }
}
