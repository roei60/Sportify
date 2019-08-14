package com.example.Sportify.models;

import androidx.room.Embedded;


public class PostAndUser {
   @Embedded
   User user;
   @Embedded
    Post post;

    public PostAndUser() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
