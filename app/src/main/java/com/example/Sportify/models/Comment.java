package com.example.Sportify.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.Sportify.room.ListConverters;
import com.example.Sportify.room.TimestampConverters;
import com.google.firebase.Timestamp;

import java.util.Date;

@SuppressWarnings({"unused", "NullableProblems"})
@Entity(tableName = "comment_table")
@TypeConverters({TimestampConverters.class})
public class Comment {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "comment_id")
    private String mId;
    private String mUserId;
    private User mAuthor; // will contain data of user created comment
    private String mText; // actual comment text
    private String mCreationDate;
    private Timestamp lastUpdate;

    public String getmPostId() {
        return mPostId;
    }

    public void setmPostId(String mPostId) {
        this.mPostId = mPostId;
    }

    private String mPostId;
    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        this.mUserId = userId;
    }

    public Comment(){
        // Need empty ctor for deserialization from DB
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getId()
    {
        return this.mId;
    }

    public Comment(String text, String creationDate, String userId){
        this.mId = java.util.UUID.randomUUID().toString();
        this.mText = text;
        this.mCreationDate = creationDate;
        this.mUserId = userId;
    }

    public User getAuthor() {
        return this.mAuthor;
    }
    public void setAuthor(User author) {
        this.mAuthor = author;
    }

    public String getText() {
        return this.mText;
    }
    public void setText(String text) {
        this.mText = text;
    }

    public String getCreationDate() {
        return this.mCreationDate;
    }

    public void setCreationDate(String creationDate) {
        this.mCreationDate = creationDate;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
