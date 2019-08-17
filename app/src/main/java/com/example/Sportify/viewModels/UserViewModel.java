package com.example.Sportify.viewModels;

import android.net.Uri;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.Sportify.dal.Dao;
import com.example.Sportify.models.PostAndUser;
import com.example.Sportify.models.User;

import java.util.List;

public class UserViewModel extends ViewModel {
    private MutableLiveData<User> mUserLiveData= new MutableLiveData<>();
    private MutableLiveData<List<PostAndUser>> mUserPostsListLiveData= new MutableLiveData<>();
    public UserViewModel() {

    }

    public void observeUserPostsList(LifecycleOwner lifecycleOwner, Observer<List<PostAndUser>> observer) {
        mUserPostsListLiveData.observe(lifecycleOwner, observer);
    }

    public void init(LifecycleOwner lifecycleOwner) {
        String userId = Dao.instance.getCurrentUserId();
        Dao.instance.observeUserPostsListLiveData(lifecycleOwner, userId, posts -> mUserPostsListLiveData.postValue(posts));

    }

    public void setUserId(String userId, LifecycleOwner lifecycleOwner, Observer<User> observer) {
        mUserLiveData.observe(lifecycleOwner,observer);
        Dao.instance.observeUserByIdLiveData(userId,lifecycleOwner, user -> mUserLiveData.postValue(user));
    }

    public void register(User user,String password,Uri imageUri,Dao.OnUpdateComleted listener){
        Dao.instance.register(user,password,imageUri,listener);
    }

    public void updateUser(User user,Dao.OnUpdateComleted listener){
        Dao.instance.UpdateUserProfile(user, listener);
    }

    public void uploadFile(String userId,Uri imageUri, Dao.UploadFileListener listener){
        Dao.instance.UploadUserProfileImage(userId,imageUri,listener);
    }

    public void signIn(String mEmail, String mPassword, Dao.OnUpdateComleted listener) {
        Dao.instance.signIn(mEmail,mPassword,listener);
    }
}
