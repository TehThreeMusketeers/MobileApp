package com.sunicola.setapp.objects;

/**
 * Created by soaresbo on 09/02/2018.
 */

public class User {
    private String email, first_name, last_name, session_token;
    private String id, refresh_token, access_token;

    public User(String first_name, String last_name, String email, String session_token) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.session_token = session_token;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getSession_token() {
        return session_token;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getRefresh_token() {return refresh_token;}

    public void setId(String id) {
        this.id = id;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }
}
