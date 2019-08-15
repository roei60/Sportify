package com.example.Sportify.viewModels;

import android.net.Uri;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.navigation.Navigation;

import com.example.Sportify.R;
import com.example.Sportify.dal.Dao;
import com.example.Sportify.models.Post;
import com.example.Sportify.utils.Consts;
import com.example.Sportify.utils.DateTimeUtils;

import java.util.Calendar;
import java.util.Date;

public class PostViewModel extends ViewModel {

    private MutableLiveData<Post> mPostLiveData = new MutableLiveData<>();
    public PostViewModel() {
    }


    public void observePost(LifecycleOwner lifecycleOwner, Observer<Post> observer) {
        mPostLiveData.observe(lifecycleOwner, observer);
    }

    public void SetPostId(String postId,LifecycleOwner lifecycleOwner, Observer<Post> observer) {
        mPostLiveData.observe(lifecycleOwner,observer);
        Dao.instance.observePostLiveData(lifecycleOwner,postId, posts -> mPostLiveData.postValue(posts));
    }

    public void uploadPost(Post post, Uri imageUri, Dao.AddPostListener listener){

        Dao.instance.addPost(post, listener);
    }

    public void updatePost(Post post, Uri imageUri,    Dao.AddPostListener listener){
        Dao.instance.addPost(post, listener);
    }

    public void uploadFile(Uri imageUri, Dao.UploadFileListener listener){
        Dao.instance.uploadFile(imageUri, listener);
    }
}
