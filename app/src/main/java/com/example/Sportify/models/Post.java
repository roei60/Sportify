package com.example.Sportify.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post {

    private String mId;
    private String mAuthor; // will contain ID of user created post
    private String mText; // actual post text
    private String mPicture; // url of picture in post from DB
    private List<String> mLikers; // list of IDs of users liked the post
    private List<Comment> mComments;
    private Date mCreationDate;

    public Post(String author, String text, Date creationDate) {
        this(author, text, creationDate, null, new ArrayList<String>(), new ArrayList<Comment>());
    }

    public Post(String author, String text,Date creationDate, String picture, List<String> likers, List<Comment> comments) {
        this.mId = java.util.UUID.randomUUID().toString();
        this.mAuthor = author;
        this.mText = text;
        this.mCreationDate = creationDate;
        this.mPicture = picture;
        this.mLikers = likers;
        this.mComments = comments;
    }

    public String getId(){
        return this.mId;
    }

    public String getAuthor(){
        return this.mAuthor;
    }

    public String getText() {
        return this.mText;
    }

    public void setText(String text) {
        this.mText = text;
    }

    public String getPicture() {
        return this.mPicture;
    }

    public void setPicture(String picture) {
        this.mPicture = picture;
    }

    public void addLike(String userId){

    }

    public void removeLike(String userId){

    }

    public void addComment(Comment comment){
        // create comment and insert to DB and comments list
    }

    public void removeComment(String commentId){
        // remove comment from DB and comments list
    }
}
