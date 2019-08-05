package com.example.Sportify.models;

import java.util.List;
import java.util.Vector;

public class User {
    private String mId;
    private String mName;
    private String mEmail;
    private String mToken;
    private String mImageUri;
    private List<Post> mUserPosts;

    public  List<Post> getPosts(){return mUserPosts;}

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getName(){  return this.mName;   }
    public String getEmail(){ return this.mEmail; }
    public String getToken() { return this.mToken; }
    public String getImageUri() {return this.mImageUri;}

    public User(){
        mUserPosts=new Vector<Post>();
    }

    public void setmUserPosts(List<Post> posts){
        mUserPosts=posts;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public void setEmail(String email) {
        this.mEmail = email;
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
