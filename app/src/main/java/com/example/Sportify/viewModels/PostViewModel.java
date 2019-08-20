package com.example.Sportify.viewModels;

import android.net.Uri;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.Sportify.dal.Model;
import com.example.Sportify.models.Post;

public class PostViewModel extends ViewModel {

    private MutableLiveData<Post> mPostLiveData = new MutableLiveData<>();
    public PostViewModel() {
    }

    public void setPostId(String postId, LifecycleOwner lifecycleOwner, Observer<Post> observer) {
        mPostLiveData.observe(lifecycleOwner,observer);
        Model.instance.observePostLiveData(lifecycleOwner,postId, post -> mPostLiveData.postValue(post));
    }

    public void uploadPost(Post post, Model.AddPostListener listener){

        Model.instance.addPost(post, listener);
    }

    public void updatePost(Post post, Model.UpdatePostListener listener){
        Model.instance.updatePost(post, listener);
    }

    public void uploadFile(Uri imageUri, Model.UploadFileListener listener){
        Model.instance.uploadFile(imageUri, listener);
    }
}
