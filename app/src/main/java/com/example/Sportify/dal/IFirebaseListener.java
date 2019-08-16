package com.example.Sportify.dal;

import com.example.Sportify.models.Comment;
import com.example.Sportify.models.Post;
import com.example.Sportify.models.User;

import java.util.List;

public interface IFirebaseListener {

    void updatePosts(List<Post> posts);

    void updatedCommentsForPosts(List<Comment> commentList);

    void updateUsers(List<User> users);
}
