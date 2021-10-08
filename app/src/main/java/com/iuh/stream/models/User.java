package com.iuh.stream.models;

import java.util.Date;
import java.util.List;

public class User {
    private String _id;
    private String firstName;
    private String lastName;
    private String gender;
    private Date dateOfBirth;
    private String imageURL;
    private String phoneNumber;
    private String email;
    private Date lastOnline;
    private boolean isOnline;
    private boolean isActive;
    private List<String> contacts;

    public User() {
    }

    public User(String _id, String firstName, String lastName, String gender, Date dateOfBirth, String imageURL, String phoneNumber, String email, Date lastOnline, boolean isOnline, boolean isActive, List<String> contacts) {
        this._id = _id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.imageURL = imageURL;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.lastOnline = lastOnline;
        this.isOnline = isOnline;
        this.isActive = isActive;
        this.contacts = contacts;
    }



    public User(String _id, String firstName, String lastName, String gender, String phoneNumber, String email, boolean isOnline, boolean isActive, List<String> contacts) {
        this._id = _id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.isOnline = isOnline;
        this.isActive = isActive;
        this.contacts = contacts;
    }


    public User(String _id, String firstName, String lastName, String gender, Date dateOfBirth, String phoneNumber, String email, boolean isOnline, boolean isActive, List<String> contacts) {
        this._id = _id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.isOnline = isOnline;
        this.isActive = isActive;
        this.contacts = contacts;
    }

    public User(String _id, String firstName, String lastName, String gender, String imageURL, String phoneNumber, String email, boolean isOnline, boolean isActive, List<String> contacts) {
        this._id = _id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.imageURL = imageURL;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.isOnline = isOnline;
        this.isActive = isActive;
        this.contacts = contacts;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(Date lastOnline) {
        this.lastOnline = lastOnline;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<String> getContacts() {
        return contacts;
    }

    public void setContacts(List<String> contacts) {
        this.contacts = contacts;
    }

    @Override
    public String toString() {
        return "User{" +
                "_id='" + _id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", imageURL='" + imageURL + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", lastOnline=" + lastOnline +
                ", isOnline=" + isOnline +
                ", isActive=" + isActive +
                ", contacts=" + contacts +
                '}';
    }
}