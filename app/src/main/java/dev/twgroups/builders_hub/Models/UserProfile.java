package dev.twgroups.builders_hub.Models;

public class UserProfile {

    String name, email, phone, userID;
    int imageID;

    public UserProfile(String name, int imageID) {
        this.name = name;
        this.imageID = imageID;
    }

    public UserProfile(String name, String email, String phone, String userID) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
