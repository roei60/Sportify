package com.example.Sportify.viewModels;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;


import com.example.Sportify.dal.Dao;
import com.example.Sportify.models.Post;
import com.example.Sportify.models.PostAndUser;

import java.util.List;

public class PostsListViewModel extends ViewModel {

    private MutableLiveData<List<PostAndUser>> mPostsLiveData = new MutableLiveData<>();

    public PostsListViewModel() {
    }

    public void deltePost(PostAndUser post, Dao.DeletePostListener listener){
        Dao.instance.deletePost(post.getPost().getId(), listener);
    }

    public void observePostsList(LifecycleOwner lifecycleOwner, Observer<List<PostAndUser>> observer) {
        mPostsLiveData.removeObservers(lifecycleOwner);
        mPostsLiveData.observe(lifecycleOwner, observer);
    }

    public void init(LifecycleOwner lifecycleOwner) {
        Dao.instance.observePostsListLiveData(lifecycleOwner, posts -> mPostsLiveData.postValue(posts));

    }
}
