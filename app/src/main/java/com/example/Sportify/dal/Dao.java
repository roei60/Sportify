package com.example.Sportify.dal;

import android.net.Uri;

import com.example.Sportify.models.Comment;
import com.example.Sportify.models.Post;
import com.example.Sportify.models.User;

import java.security.PublicKey;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class Dao {
    final public static Dao instance = new Dao();
    User currentUser;

    public void setCurrentUser(User user)
    {
        this.currentUser=user;
    }
    public User getCurrentUser()
    {
        return this.currentUser;
    }
    //ModelSql modelSql;
    FirebaseDao firebaseDao;
    private Dao() {
        //modelSql = new ModelSql();
        firebaseDao = new FirebaseDao();
    }


    public interface GetAllPostsListener {
        void onComplete(List<Post> data);
    }
    public void getAllPosts(GetAllPostsListener listener) {
        firebaseDao.getAllPosts(listener);
    }

    public interface GetPostListener {
        void onComplete(Post post);
    }
    public void getPost(String postId, GetPostListener listener) {
        firebaseDao.getPost(postId, listener);
    }

    public interface AddPostListener{
        void onComplete(Post post);
    }

    public void addPost(Post post, AddPostListener listener) {
        firebaseDao.addPost(post, listener);
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

    public void addComment(String postId, Comment comment, AddCommentListener listener){
        firebaseDao.addComment(postId, comment, listener);
    }
}
