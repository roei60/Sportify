package com.example.Sportify.models;

import java.util.Date;

public class Comment {

    private String mId;
    private String mAuthor; // will contain ID of user created comment
    private String mText; // actual comment text
    private Date mCreationDate;

    public Comment(){
        // Need empty ctor for deserialization from DB
    }

    public Comment(String author, String text, Date creationDate){
        this.mAuthor = author;
        this.mText = text;
        this.mCreationDate = creationDate;
    }

    public String getmAuthor() {
        return this.mAuthor;
    }

    public String getText() {
        return this.mText;
    }

    public void setText(String text) {
        this.mText = text;
    }
}
