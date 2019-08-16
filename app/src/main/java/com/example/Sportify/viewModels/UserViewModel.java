package com.example.Sportify.viewModels;

import android.net.Uri;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.Sportify.dal.Dao;
import com.example.Sportify.models.Post;
import com.example.Sportify.models.User;

public class UserViewModel extends ViewModel {
    private MutableLiveData<User> mUserLiveData= new MutableLiveData<>();
    public UserViewModel() {

    }



    public void SetUserId(String userId,LifecycleOwner lifecycleOwner, Observer<User> observer) {
        mUserLiveData.observe(lifecycleOwner,observer);
        Dao.instance.observeUserByIdLiveData(userId,lifecycleOwner, user -> mUserLiveData.postValue(user));
    }

    public void UpdateUser(User user, Uri imageUri, Dao.OnUpdateComleted listener){

        Dao.instance.UpdateUserProfile(user, listener);
    }

    public void updatePost(Post post, Uri imageUri,    Dao.AddPostListener listener){
        Dao.instance.addPost(post, listener);
    }

    public void uploadFile(Uri imageUri, Dao.UploadFileListener listener){
        Dao.instance.uploadFile(imageUri, listener);
    }

}
