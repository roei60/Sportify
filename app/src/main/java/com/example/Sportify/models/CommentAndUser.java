package com.example.Sportify.models;

import androidx.room.Embedded;

public class CommentAndUser {

    @Embedded
    User user;
    @Embedded
    Comment comment;

    public CommentAndUser() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}
