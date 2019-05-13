package com.example.jedcl.sportsstats;

import com.google.gson.annotations.SerializedName;

public class User {

    public static final String ID_EXTRA = "user_id";

    public static final String USERNAME = "Username";
    public static final int USERNAME_MIN_LENGTH = 6;
    public static final int USERNAME_MAX_LENGTH = 12;

    public static final String FIRST_NAME = "First name";
    public static final int FIRST_NAME_MIN_LENGTH = 1;
    public static final int FIRST_NAME_MAX_LENGTH = 15;

    public static final String SURNAME = "Surname";
    public static final int SURNAME_MIN_LENGTH = 1;
    public static final int SURNAME_MAX_LENGTH = 15;

    public static final String PASSWORD = "Password";
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 16;

    public static final String EMAIL = "Email";
    public static final int EMAIL_MIN_LENGTH = 8;
    public static final int EMAIL_MAX_LENGTH = 50;

    @SerializedName("id") private String id;
    @SerializedName("first_name") private String firstName;
    @SerializedName("last_name") private String lastName;
    @SerializedName("username") private String username;
    @SerializedName("password") private String password;
    @SerializedName("email") private String email;
    @SerializedName("date_created") private String dateCreated;
    @SerializedName("last_modified") private String lastModified;

    public User(String id, String firstName, String lastName, String username, String password, String email, String dateCreated, String lastModified) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.dateCreated = dateCreated;
        this.lastModified = lastModified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

}
