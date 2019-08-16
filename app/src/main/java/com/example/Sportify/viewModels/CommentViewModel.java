package com.example.Sportify.viewModels;

import androidx.lifecycle.ViewModel;

import com.example.Sportify.dal.Dao;
import com.example.Sportify.models.Comment;

public class CommentViewModel extends ViewModel {

    public void addComment(Comment comment, Dao.AddCommentListener listener) {
        Dao.instance.addComment(comment,listener);
    }
}
