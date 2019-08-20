package com.example.Sportify.dal;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.Sportify.models.Comment;
import com.example.Sportify.models.CommentAndUser;
import com.example.Sportify.models.Post;
import com.example.Sportify.models.PostAndUser;
import com.example.Sportify.models.User;
import com.example.Sportify.room.PostRepository;
import com.example.Sportify.room.TimestampConverters;

import java.util.List;

public class Model {
    final public static Model instance = new Model();
    private SharedPreferences sharedPreferences;
    private static String LAST_UPDATED_KEY = "lastUpdatedTimestamp";
    FirebaseModel firebaseModel;
    private PostRepository mPostRepository;

    public LiveData<User> getCurrentUser()
    {
        return mPostRepository.getUserById(firebaseModel.auth.getCurrentUser().getUid());
    }

    public String getCurrentUserId()
    {
        return firebaseModel.auth.getCurrentUser().getUid();
    }


    private Model() {

    }

    public void init(Application application) {
        mPostRepository = new PostRepository(application);
        sharedPreferences = application.getSharedPreferences("SportifyPrefs", Context.MODE_PRIVATE);
        firebaseModel = new FirebaseModel();
        long lastUpdatedTimestamp = getLastUpdatedTimestamp();
        firebaseModel.getAllPosts(lastUpdatedTimestamp, firebaseListener);
        firebaseModel.getAllUsers(lastUpdatedTimestamp,firebaseListener);
        firebaseModel.getAllComments(lastUpdatedTimestamp,firebaseListener);
    }

    IFirebaseListener firebaseListener=new IFirebaseListener() {
        @Override
        public void updatePosts(List<Post> posts) {
            long lastUpdatedTimestamp = getLastUpdatedTimestamp();
            Log.d("Tag", "########### posts num: " + posts.size());
            for (Post post:posts) {
                lastUpdatedTimestamp=Math.max(TimestampConverters.dateToTimestamp(post.getLastUpdate()),lastUpdatedTimestamp);
                mPostRepository.insert(post);
            }
            setLastUpdatedTimestamp(lastUpdatedTimestamp);
            firebaseModel.getAllPosts(lastUpdatedTimestamp, firebaseListener);

            Log.d("bblls", "fff");
        }

        @Override
        public void updatedCommentsForPosts(List<Comment> comments) {
            long lastUpdatedTimestamp = getLastUpdatedTimestamp();

            Log.d("Tag", "&&&&&&& comments num: " + comments.size());
            for (Comment comment:comments) {
                lastUpdatedTimestamp=Math.max(TimestampConverters.dateToTimestamp(comment.getLastUpdate()),lastUpdatedTimestamp);
                mPostRepository.insert(comment);
            }
            setLastUpdatedTimestamp(lastUpdatedTimestamp);
            firebaseModel.getAllComments(lastUpdatedTimestamp, firebaseListener);
        }

        @Override
        public void updateUsers(List<User> users) {
            long lastUpdatedTimestamp = getLastUpdatedTimestamp();

            Log.d("Tag", "$$$$$$$$ users num: " + users.size());
            for (User user:users) {
                lastUpdatedTimestamp=Math.max(TimestampConverters.dateToTimestamp(user.getLastUpdate()),lastUpdatedTimestamp);

                mPostRepository.insert(user);
            }
            setLastUpdatedTimestamp(lastUpdatedTimestamp);
            firebaseModel.getAllUsers(lastUpdatedTimestamp, firebaseListener);

        }
    };
    private long getLastUpdatedTimestamp() {
        return sharedPreferences.getLong(LAST_UPDATED_KEY, 0);
    }

    private void setLastUpdatedTimestamp(long lastUpdated) {
        sharedPreferences.edit().putLong(LAST_UPDATED_KEY, lastUpdated).apply();
    }


    public void observeCommentsListLiveData(LifecycleOwner lifecycleOwner, String postId, Observer<List<CommentAndUser>> observer) {
        mPostRepository.getAllComments(postId).observe(lifecycleOwner, observer);
    }

    public void observePostsListLiveData(LifecycleOwner lifecycleOwner, Observer<List<PostAndUser>> observer) {
        mPostRepository.getAllPosts().observe(lifecycleOwner, observer);
    }
    public void observePostLiveData(LifecycleOwner lifecycleOwner,String postId ,Observer<Post> observer) {
        mPostRepository.getPostById(postId).observe(lifecycleOwner, observer);
    }

    public void observeCommentLiveData(LifecycleOwner lifecycleOwner,String commentId ,Observer<CommentAndUser> observer) {
        mPostRepository.getCommentById(commentId).observe(lifecycleOwner, observer);
    }

    public void observeUserPostsListLiveData(LifecycleOwner lifecycleOwner, String userId, Observer<List<PostAndUser>> observer) {
        mPostRepository.getAllPostsByUserId(userId).observe(lifecycleOwner, observer);
    }

    public void observeUsersLiveData(LifecycleOwner lifecycleOwner, Observer<List<User>> observer) {
        mPostRepository.getAllUsers().observe(lifecycleOwner, observer);
    }
    public void observeUserByIdLiveData(String userId,LifecycleOwner lifecycleOwner, Observer<User> observer) {
        mPostRepository.getUserById(userId).observe(lifecycleOwner, observer);
    }


    public interface GetAllPostsListener {
        void onComplete(List<Post> data);
    }
    public void getAllPosts(long from,IFirebaseListener listener) {
        firebaseModel.getAllPosts(from,listener);
    }


    public LiveData<Post> getPost(String postId) {
        return mPostRepository.getPostById(postId);
    }

    public interface UpdatePostListener{
        void onComplete(Post post);
    }

    public void updatePost(Post post, UpdatePostListener listener){
        firebaseModel.updatePost(post, listener);
    }

    public interface AddPostListener{
        void onComplete(Post post);
    }

    public void addPost(Post post, AddPostListener listener) {
        firebaseModel.addPost(post, listener);
    }

    public interface DeletePostListener{
        void onComplete(Void avoid);
    }

    public void deletePost(Post post, DeletePostListener listener) {
        firebaseModel.deletePost(post, listener);
     //   mPostRepository.deletePost(postId);
    }

    public interface  OnUpdateComleted{
        void onUpdateCompleted(boolean success);
    }
    public void signIn(String email, String password, final Model.OnUpdateComleted listener)
    {
        firebaseModel.signIn(email,password,listener);
    }
    public void register(final User user, String password, final Uri userImageUri, final Model.OnUpdateComleted listener)
    {
        firebaseModel.registerUser(user,password,userImageUri,listener);
    }
    public void UpdateUserProfile(User user,OnUpdateComleted listener)
    {
        firebaseModel.UpdateUserProfile(user,listener);
    }

    public void UploadUserProfileImage(String userId, Uri imageProfile, final Model.UploadFileListener listener)
    {
        firebaseModel.uploadProfileImageFile(userId,imageProfile,listener);
    }

    public interface GetUserDetailsListener{
        void onComplete(User user);
    }

    public interface UploadFileListener{
        void onComplete(Uri imageUri);
    }

    public void uploadFile(Uri imageUri, UploadFileListener listener){ firebaseModel.uploadFile(imageUri, listener);}

    public void getUserDetails(String id, Model.GetUserDetailsListener listener)
    {
        firebaseModel.getUser(id,listener);
    }

    public interface GetAllUsersListener{
        void onComplete(List<User> users);
    }

    public void getAllUsers(GetAllUsersListener listener){
        firebaseModel.getAllUsers(listener);
    }

//    public interface GetAllCommentsListener{
//        void onComplete(List<Comment> comments);
//    }
//
//    public void getAllComments(String postId, GetAllCommentsListener listener){
//        firebaseModel.getAllComments(postId, listener);
//    }

    public interface AddCommentListener{
        void onComplete(Comment comment);
    }

    public void addComment( Comment comment, AddCommentListener listener){
        firebaseModel.addComment(comment, listener);
    }

    public interface DeleteCommentListener{
        void onComplete(Void avoid);
    }

    public void deleteComment(Comment comment, DeleteCommentListener listener){
        firebaseModel.deleteComment(comment, listener);
     //   mPostRepository.deleteComment(commentId);
    }

    public interface GetCommentListener{
        void onComplete(Comment comment);
    }

    public void getComment(String postId, String commentId, GetCommentListener listener){
        firebaseModel.getComment(postId, commentId, listener);
    }

    public interface UpdateCommentListener{
        void onComplete(Comment comment);
    }

    public void updateComment(Comment comment, UpdateCommentListener listener){
        firebaseModel.updateComment(comment, listener);
    }
}
