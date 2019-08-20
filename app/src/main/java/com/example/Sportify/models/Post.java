package com.example.Sportify.models;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.Sportify.room.ListConverters;
import com.example.Sportify.room.TimestampConverters;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.google.gson.internal.$Gson$Types.arrayOf;

@SuppressWarnings({"unused", "NullableProblems"})

@Entity(tableName = "post_table")

//@Entity(tableName = "post_table")
@TypeConverters({TimestampConverters.class, ListConverters.class})
public class Post {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "post_id")
    private String mId;
    @Ignore
    private User mAuthor; // will contain data of user created post
    private String mAuthorId;
    private String mText; // actual post text
    private String mPicture; // url of picture in post from DB
    @Ignore
    private List<String> mLikers; // list of IDs of users liked the post
    @Ignore
    private List<Comment> mComments;
    private String mCreationDate;
    @ColumnInfo(name = "post_lastUpdate")
    private Timestamp lastUpdate;
    private boolean mIsDeleted;



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

    public Post(@NonNull String mId, User mAuthor, String mAuthorId, String mText, String mPicture, List<String> mLikers, List<Comment> mComments, String mCreationDate, Timestamp lastUpdate) {
        this.mId = mId;
        this.mAuthor = mAuthor;
        this.mAuthorId = mAuthorId;
        this.mText = mText;
        this.mPicture = mPicture;
        this.mLikers = mLikers;
        this.mComments = mComments;
        this.mCreationDate = mCreationDate;
        this.lastUpdate = lastUpdate;
    }

    public Post(User author, String text, String creationDate, String picture, List<String> likers, List<Comment> comments) {
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

    public List<String> getLikers() {
        return mLikers;
    }

    public void setLikers(List<String> likers) {
        this.mLikers = likers;
    }

    public List<Comment> getComments() {
        return mComments;
    }

    public void setComments(List<Comment> comments) {
        this.mComments = comments;
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

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getAuthorId() {
        return mAuthorId;
    }

    public void setAuthorId(String AuthorId) {
        this.mAuthorId = AuthorId;
    }

    public boolean getIsDeleted() {
        return mIsDeleted;
    }

    public void setIsDeleted(boolean mIsDeleted) {
        this.mIsDeleted = mIsDeleted;
    }
}
