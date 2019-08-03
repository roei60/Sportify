package com.example.Sportify.models;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post {

    private String mId;
    private User mAuthor; // will contain data of user created post
    private String mText; // actual post text
    private String mPicture; // url of picture in post from DB
    private List<String> mLikers; // list of IDs of users liked the post
    private List<Comment> mComments;
    private String mCreationDate;

    public Post(){
        // Need empty ctor for deserialization from DB
    }

    public Post(String text, String creationDate) {
        this(null, text, creationDate, null, new ArrayList<String>(), new ArrayList<Comment>());
    }

    public Post(String text, String creationDate, String picture) {
        this(null, text, creationDate, picture, new ArrayList<String>(), new ArrayList<Comment>());
    }

    public Post(User author, String text, String creationDate) {
        this(author, text, creationDate, null, new ArrayList<String>(), new ArrayList<Comment>());
    }

    public Post(User author, String text,String creationDate, String picture, List<String> likers, List<Comment> comments) {
        this.mId = java.util.UUID.randomUUID().toString();
        this.mAuthor = author;
        this.mText = text;
        this.mCreationDate = creationDate;
        this.mPicture = picture;
        this.mLikers = likers;
        this.mComments = comments;
    }

    public String getCreationDate() {
        return this.mCreationDate;
    }

    public void setCreationDate(String creationDate) {
        this.mCreationDate = creationDate;
    }

    public void setId(String id){
        this.mId = id;
    }
    public String getId(){
        return this.mId;
    }

    public User getAuthor(){
        return this.mAuthor;
    }
    public void setAuthor(User author){
        this.mAuthor = author;
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
