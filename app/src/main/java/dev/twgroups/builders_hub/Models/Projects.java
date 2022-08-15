package dev.twgroups.builders_hub.Models;

public class Projects {

    private String projectID;
    private String name;
    private String address;
    private String city;
    private String amountIn;
    private String amountOut;

    public Projects() {
    }

    public Projects(String projectID, String name, String address, String city, String amountIn, String amountOut) {
        this.projectID = projectID;
        this.name = name;
        this.address = address;
        this.city = city;
        this.amountIn = amountIn;
        this.amountOut = amountOut;
    }

    public Projects(String projectID, String name, String address, String city, String amountIn) {
        this.projectID = projectID;
        this.name = name;
        this.address = address;
        this.city = city;
        this.amountIn = amountIn;
        this.amountOut = "0";
    }

    public Projects(String projectID, String name, String address, String city) {
        this.projectID = projectID;
        this.name = name;
        this.address = address;
        this.city = city;
        this.amountIn = "0";
        this.amountOut = "0";
    }

    public Projects(String name, String address, String city) {
        this.name = name;
        this.address = address;
        this.city = city;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAmountIn() {
        return amountIn;
    }

    public void setAmountIn(String amountIn) {
        this.amountIn = amountIn;
    }

    public String getAmountOut() {
        return amountOut;
    }

    public void setAmountOut(String amountOut) {
        this.amountOut = amountOut;
    }
}
