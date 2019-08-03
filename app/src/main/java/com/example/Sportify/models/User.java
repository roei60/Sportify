package com.example.Sportify.models;

import java.util.Vector;

public class User {
    private String mName;
    private String mEmail;
    private String mToken;
    private String mImageUri;

    public String getName(){  return this.mName;   }
    public String getEmail(){ return this.mEmail; }
    public String getToken() { return this.mToken; }
    public String getImageUri() {return this.mImageUri;}

    public User(){}

    public void setName(String name) {
        this.mName = name;
    }

    public void setImageUri(String imageUri){
        this.mImageUri = imageUri;
    }

    public User(String name, String email, String token, String imageUri){
        mName = name;
        mEmail = email;
        mToken = token;
        mImageUri = imageUri;
    }
}
