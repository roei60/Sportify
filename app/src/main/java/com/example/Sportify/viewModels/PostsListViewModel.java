package com.example.Sportify.viewModels;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;


import com.example.Sportify.dal.Model;
import com.example.Sportify.models.PostAndUser;

import java.util.List;

public class PostsListViewModel extends ViewModel {

    private MutableLiveData<List<PostAndUser>> mPostsLiveData = new MutableLiveData<>();

    public PostsListViewModel() {
    }

    public void deltePost(PostAndUser post, Model.DeletePostListener listener){
        Model.instance.deletePost(post.getPost(), listener);
    }

    public void observePostsList(LifecycleOwner lifecycleOwner, Observer<List<PostAndUser>> observer) {
        mPostsLiveData.observe(lifecycleOwner, observer);
    }

    public void init(LifecycleOwner lifecycleOwner) {
        Model.instance.observePostsListLiveData(lifecycleOwner, posts -> mPostsLiveData.postValue(posts));

    }
}
