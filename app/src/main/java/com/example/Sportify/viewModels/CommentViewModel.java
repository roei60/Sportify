package com.example.Sportify.viewModels;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.Sportify.dal.Model;
import com.example.Sportify.models.Comment;
import com.example.Sportify.models.CommentAndUser;

public class CommentViewModel extends ViewModel {

    private MutableLiveData<CommentAndUser> mCommentLiveData = new MutableLiveData<>();
    public CommentViewModel() {
    }

    public void setCommentId(String commentId, LifecycleOwner lifecycleOwner, Observer<CommentAndUser> observer) {
        mCommentLiveData.observe(lifecycleOwner,observer);
        Model.instance.observeCommentLiveData(lifecycleOwner,commentId, comment -> mCommentLiveData.postValue(comment));
    }

    public void updateComment(Comment comment, Model.UpdateCommentListener listener){
        Model.instance.updateComment(comment, listener);
    }

}

