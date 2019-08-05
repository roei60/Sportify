package com.example.Sportify.models;

import java.util.Date;

public class Comment {

    private String mId;
    private String mUserId;
    private User mAuthor; // will contain data of user created comment
    private String mText; // actual comment text
    private String mCreationDate;

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        this.mUserId = userId;
    }

    public Comment(){
        // Need empty ctor for deserialization from DB
    }

    public Comment(String text, String creationDate) {
        this(null, text, creationDate);
    }

    public void setId(String id) {
        this.mId = id;
    }

    public Comment(User author, String text, String creationDate){
        this.mAuthor = author;
        this.mText = text;
        this.mCreationDate = creationDate;
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
}
