package com.example.Sportify.viewModels;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.Sportify.dal.Dao;
import com.example.Sportify.models.Comment;
import com.example.Sportify.models.CommentAndUser;
import com.example.Sportify.models.Post;

import java.util.List;

public class CommentsListViewModel extends ViewModel {

    private MutableLiveData<List<CommentAndUser>> mCommentsLiveData = new MutableLiveData<>();
    private MutableLiveData<Post> mPostLiveData = new MutableLiveData<>();

    public void addComment(Comment comment, Dao.AddCommentListener listener) {
        Dao.instance.addComment(comment,listener);
    }

    public void observeCommentsList(LifecycleOwner lifecycleOwner, Observer<List<CommentAndUser>> observer) {
        mCommentsLiveData.observe(lifecycleOwner, observer);
    }

    public void SetPostId(String postId,LifecycleOwner lifecycleOwner, Observer<Post> observer) {
        mPostLiveData.observe(lifecycleOwner,observer);
        Dao.instance.observePostLiveData(lifecycleOwner,postId, posts -> mPostLiveData.postValue(posts));
    }

    public void init(LifecycleOwner lifecycleOwner) {
        String postId = mPostLiveData.getValue().getId();
        Dao.instance.observeCommentsListLiveData(lifecycleOwner, postId,comments -> mCommentsLiveData.postValue(comments));

    }
}
