package dev.afnan.builders_hub.Models;

public class Workers {
    public String name;
    public String workerType;
    public String email;
    public String phone;
    public String isWorker;
    public String address;
    public String workerID;

    public Workers() {

    }

    public Workers(String name, String workerType, String email, String phone, String isWorker, String address) {
        this.name = name;
        this.workerType = workerType;
        this.email = email;
        this.phone = phone;
        this.isWorker = isWorker;
        this.address = address;
    }

    public Workers(String name, String workerType, String email, String phone, String isWorker, String address, String workerID) {
        this.name = name;
        this.workerType = workerType;
        this.email = email;
        this.phone = phone;
        this.isWorker = isWorker;
        this.address = address;
        this.workerID = workerID;
    }


    public Workers(String name, String email, String phone, String isWorker, String address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.isWorker = isWorker;
        this.address = address;
    }

    public Workers(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public Workers(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorkerType() {
        return workerType;
    }

    public void setWorkerType(String workerType) {
        this.workerType = workerType;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWorkerID() {
        return workerID;
    }

    public void setWorkerID(String workerID) {
        this.workerID = workerID;
    }
}
