package com.example.Sportify.viewModels;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.Sportify.dal.Model;
import com.example.Sportify.models.Comment;
import com.example.Sportify.models.CommentAndUser;
import com.example.Sportify.models.Post;

import java.util.List;

public class CommentsListViewModel extends ViewModel {

    private MutableLiveData<List<CommentAndUser>> mCommentsLiveData = new MutableLiveData<>();
    private MutableLiveData<Post> mPostLiveData = new MutableLiveData<>();

    public void addComment(Comment comment, Model.AddCommentListener listener) {
        Model.instance.addComment(comment,listener);
    }

    public void deleteComment(Comment comment, Model.DeleteCommentListener listener){
        Model.instance.deleteComment(comment, listener);
    }

    public void observeCommentsList(LifecycleOwner lifecycleOwner, Observer<List<CommentAndUser>> observer) {
        mCommentsLiveData.observe(lifecycleOwner, observer);
    }

    public void SetPostId(String postId,LifecycleOwner lifecycleOwner, Observer<Post> observer) {
        mPostLiveData.observe(lifecycleOwner,observer);
        Model.instance.observePostLiveData(lifecycleOwner,postId, posts -> mPostLiveData.postValue(posts));
    }

    public void init(LifecycleOwner lifecycleOwner) {
        String postId = mPostLiveData.getValue().getId();
        Model.instance.observeCommentsListLiveData(lifecycleOwner, postId, comments -> mCommentsLiveData.postValue(comments));

    }
}
