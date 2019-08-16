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
import com.example.Sportify.models.Post;
import com.example.Sportify.models.PostAndUser;
import com.example.Sportify.models.User;
import com.example.Sportify.room.PostRepository;
import com.example.Sportify.room.UserDao;

import java.security.PublicKey;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class Dao {
    final public static Dao instance = new Dao();
  //  User currentUser;
    private SharedPreferences sharedPreferences;
    private static String LAST_UPDATED_KEY = "lastUpdatedTimestamp";
    //public void setCurrentUser(User user)
    //{
     //   this.currentUser=user;
    //}
    public LiveData<User> getCurrentUser()
    {
        return mPostRepository.getUserById(firebaseDao.auth.getCurrentUser().getUid());
    }

    public String getCurrentUserId()
    {
        return firebaseDao.auth.getCurrentUser().getUid();
    }
    //ModelSql modelSql;
    FirebaseDao firebaseDao;

    private PostRepository mPostRepository;

    private Dao() {
        //modelSql = new ModelSql();
        firebaseDao = new FirebaseDao();
//        sharedPreferences = .getSharedPreferences("RepositoryPrefs", Context.MODE_PRIVATE);
        firebaseDao.getAllPosts(0, firebaseListener);
        firebaseDao.getAllUsers(0,firebaseListener);
    }

    public void init(Application application) {
        mPostRepository = new PostRepository(application);
        sharedPreferences = application.getSharedPreferences("RepositoryPrefs", Context.MODE_PRIVATE);
    }
    IFirebaseListener firebaseListener=new IFirebaseListener() {
        @Override
        public void updatePosts(List<Post> posts) {

            Log.d("Tag", "########### posts num: " + posts.size());
            for (Post post:posts) {
                mPostRepository.insert(post);
            }

            Log.d("bblls","fff");
        }

        @Override
        public void updatedCommentsForPosts(int propertyId, List<Comment> commentList) {

        }

        @Override
        public void updateUsers(List<User> users) {
            Log.d("Tag", "$$$$$$$$ users num: " + users.size());
            for (User user:users) {
                mPostRepository.insert(user);
            }
        }
    };

    public void observePostsListLiveData(LifecycleOwner lifecycleOwner, Observer<List<PostAndUser>> observer) {
        mPostRepository.getAllPosts().removeObservers(lifecycleOwner);
        mPostRepository.getAllPosts().observe(lifecycleOwner, observer);
    }
    public void observePostLiveData(LifecycleOwner lifecycleOwner,String postId ,Observer<Post> observer) {
        mPostRepository.getPostById(postId).observe(lifecycleOwner, observer);
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
        firebaseDao.getAllPosts(from,listener);
    }


    public LiveData<Post> getPost(String postId) {
        return mPostRepository.getPostById(postId);
    }

    public interface AddPostListener{
        void onComplete(Post post);
    }

    public void addPost(Post post, AddPostListener listener) {
        firebaseDao.addPost(post, listener);
    }

    public interface DeletePostListener{
        void onComplete(Void avoid);
    }

    public void deletePost(String postId, DeletePostListener listener) {
        firebaseDao.deletePost(postId, listener);
        mPostRepository.deletePost(postId);
    }

    public interface  OnUpdateComleted{
        void onUpdateCompleted(boolean success);
    }
    public void singIn(String email, String password, final Dao.OnUpdateComleted listener)
    {
        firebaseDao.signIn(email,password,listener);
    }
    public void register(final User user, String password, final Uri userImageUri, final Dao.OnUpdateComleted listener)
    {
        firebaseDao.registerUser(user,password,userImageUri,listener);
    }
    public void UpdateUserProfile(User user,OnUpdateComleted listener)
    {
        firebaseDao.UpdateUserProfile(user,listener);
    }

    public void UploadUserProfileImage(String userId, Uri imageProfile, final Dao.UploadFileListener listener)
    {
        firebaseDao.uploadProfileImageFile(userId,imageProfile,listener);
    }

    public interface GetUserDetailsListener{
        void onComplete(User user);
    }

    public interface UploadFileListener{
        void onComplete(Uri imageUri);
    }

    public void uploadFile(Uri imageUri, UploadFileListener listener){ firebaseDao.uploadFile(imageUri, listener);}

    public void getUserDetails(String id,Dao.GetUserDetailsListener listener)
    {
        firebaseDao.getUser(id,listener);
    }

    public interface GetAllUsersListener{
        void onComplete(List<User> users);
    }

    public void getAllUsers(GetAllUsersListener listener){
        firebaseDao.getAllUsers(listener);
    }

    public interface GetAllCommentsListener{
        void onComplete(List<Comment> comments);
    }

    public void getAllComments(String postId, GetAllCommentsListener listener){
        firebaseDao.getAllComments(postId, listener);
    }

    public interface AddCommentListener{
        void onComplete(Comment comment);
    }

    public void addComment( Comment comment, AddCommentListener listener){
        firebaseDao.addComment(comment, listener);
    }

    public interface DeleteCommentListener{
        void onComplete(Void avoid);
    }

    public void deleteComment(String postId, String commentId, DeleteCommentListener listener){
        firebaseDao.deleteComment(postId, commentId, listener);
    }

    public interface GetCommentListener{
        void onComplete(Comment comment);
    }

    public void getComment(String postId, String commentId, GetCommentListener listener){
        firebaseDao.getComment(postId, commentId, listener);
    }

    public interface UpdateCommentListener{
        void onComplete(Comment comment);
    }

    public void updateComment(String postId, Comment comment, UpdateCommentListener listener){
        firebaseDao.updateComment(postId, comment, listener);
    }
}
