package com.iuh.stream.models;

public class IdToken {

    private String idToken;

    public IdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    @Override
    public String toString() {
        return "IdToken{" +
                "idToken='" + idToken + '\'' +
                '}';
    }
}
