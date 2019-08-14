package com.example.Sportify.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;


import com.example.Sportify.dal.Dao;
import com.example.Sportify.models.Post;
import com.example.Sportify.models.PostAndUser;
import com.example.Sportify.models.User;

import java.util.List;

public class PostsListViewModel extends ViewModel {

    private MutableLiveData<List<PostAndUser>> mPostsLiveData = new MutableLiveData<>();

    public PostsListViewModel() {
    }


    public void observePostsList(LifecycleOwner lifecycleOwner, Observer<List<PostAndUser>> observer) {
        mPostsLiveData.observe(lifecycleOwner, observer);
    }

    public void init(LifecycleOwner lifecycleOwner) {
        Dao.instance.observePostsLiveData(lifecycleOwner, posts -> mPostsLiveData.postValue(posts));

    }
}
