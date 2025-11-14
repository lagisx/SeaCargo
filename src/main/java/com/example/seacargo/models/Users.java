package com.example.seacargo.models;

public class Users {
    private int id;
    private String username;
    private String email;
    private String password;
    private String role;
    private String created_at;

    public Users(int id, String username, String email, String password, String role, String created_at) {
        this.id=id;
        this.username=username;
        this.email=email;
        this.password= password;
        this.role=role;
        this.created_at=created_at;
    }


    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}
