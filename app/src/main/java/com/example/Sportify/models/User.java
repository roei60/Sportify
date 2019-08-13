package com.example.Sportify.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.Sportify.room.ListConverters;
import com.example.Sportify.room.TimestampConverters;
import com.google.firebase.Timestamp;

import java.util.List;
import java.util.Vector;

@SuppressWarnings({"unused", "NullableProblems"})
@Entity(tableName = "user_table")
@TypeConverters({TimestampConverters.class, ListConverters.class})
public class User {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "user_id")
    private String mId;

    private String mName;
    private String mEmail;
    private Timestamp lastUpdate;
    private String mImageUri;
    @Ignore
    private List<Post> mUserPosts;

    public User(){
        mUserPosts=new Vector<Post>();
    }

    public User(User user) {
        this.mId=user.mId;
        this.mName=user.mName;
        this.mEmail=user.mEmail;
        mImageUri=user.mImageUri;
        lastUpdate=user.lastUpdate;
        mUserPosts=new Vector<Post>(user.mUserPosts);
    }

    public User(String name, String email, String imageUri){
        mName = name;
        mEmail = email;
        mImageUri = imageUri;
    }

    public  List<Post> getPosts(){return mUserPosts;}

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getName(){  return this.mName;   }
    public String getEmail(){ return this.mEmail; }
    public Timestamp getLastUpdate() { return lastUpdate;}
    public String getImageUri() {return this.mImageUri;}

    public void setLastUpdate(@Nullable Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    public void setUserPosts(List<Post> posts){
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

    public void setmUserPosts(List<Post> mUserPosts) {
        this.mUserPosts = mUserPosts;
    }
}
