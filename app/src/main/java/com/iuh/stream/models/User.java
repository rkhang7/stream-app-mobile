package com.iuh.stream.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class User implements Serializable {
    private String _id;
    private String firstName;
    private String lastName;
    private String gender;
    private Date dateOfBirth;
    private String imageURL;
    private String phoneNumber;
    private String email;
    private Date lastOnline;
    private boolean online;
    private List<String> contacts;
    private List<String> friendRequests;
    private List<String> friendInvitations;

    public User() {
    }

    public User(String _id, String firstName, String lastName, String gender, Date dateOfBirth, String imageURL, String phoneNumber, String email, Date lastOnline, boolean online, List<String> contacts, List<String> friendRequests, List<String> friendInvitations) {
        this._id = _id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.imageURL = imageURL;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.lastOnline = lastOnline;
        this.online = online;
        this.contacts = contacts;
        this.friendRequests = friendRequests;
        this.friendInvitations = friendInvitations;
    }

    public User(String _id, String firstName, String lastName, String gender, String imageURL, String phoneNumber, String email, boolean online, List<String> contacts) {
        this._id = _id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.imageURL = imageURL;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.online = online;
        this.contacts = contacts;
    }

    public User(String _id, String firstName, String lastName, String gender, Date dateOfBirth, String phoneNumber, String email, boolean online) {
        this._id = _id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.online = online;
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
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public List<String> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(List<String> friendRequests) {
        this.friendRequests = friendRequests;
    }

    public List<String> getFriendInvitations() {
        return friendInvitations;
    }

    public void setFriendInvitations(List<String> friendInvitations) {
        this.friendInvitations = friendInvitations;
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
                ", online=" + online +
                ", contacts=" + contacts +
                ", friendRequests=" + friendRequests +
                ", friendInvitations=" + friendInvitations +
                '}';
    }
}
