package com.example.myfitnesssecure;

public class User {
    public String name, email, password;
    private String imageURL;
    private String status;
    private String search;

    //empty constructor
    public User(){

    }

    public User(String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(String name, String email, String password, String imageURL, String status, String search) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.imageURL = imageURL;
        this.status = status;
        this.search = search;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getStatus() {
        return status;
    }

    public String getSearch() {
        return search;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

