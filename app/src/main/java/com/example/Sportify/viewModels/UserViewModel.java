package com.example.Sportify.viewModels;

import android.net.Uri;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.Sportify.dal.Model;
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
        String userId = Model.instance.getCurrentUserId();
        Model.instance.observeUserPostsListLiveData(lifecycleOwner, userId, posts -> mUserPostsListLiveData.postValue(posts));

    }

    public void setUserId(String userId, LifecycleOwner lifecycleOwner, Observer<User> observer) {
        mUserLiveData.observe(lifecycleOwner,observer);
        Model.instance.observeUserByIdLiveData(userId,lifecycleOwner, user -> mUserLiveData.postValue(user));
    }

    public void register(User user, String password, Uri imageUri, Model.OnUpdateComleted listener){
        Model.instance.register(user,password,imageUri,listener);
    }

    public void updateUser(User user, Model.OnUpdateComleted listener){
        Model.instance.UpdateUserProfile(user, listener);
    }

    public void uploadFile(String userId,Uri imageUri, Model.UploadFileListener listener){
        Model.instance.UploadUserProfileImage(userId,imageUri,listener);
    }

    public void signIn(String mEmail, String mPassword, Model.OnUpdateComleted listener) {
        Model.instance.signIn(mEmail,mPassword,listener);
    }
}
