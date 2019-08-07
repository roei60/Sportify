package com.example.Sportify.dal;

import android.net.Uri;

import com.example.Sportify.models.Comment;
import com.example.Sportify.models.Post;
import com.example.Sportify.models.User;

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

//        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        Date date = new Date();
//        System.out.println(dateFormat.format(date));
//        Post p1 = new Post("Avi Levi", "my first post!", date);
//        Post p2 = new Post("Avi Levi", "my second post!", date);
//        addPost(p1);
//        addPost(p2);

//        for (int i = 0; i < 10; i++) {
//            Student st = new Student("" + i, "demo " + i, "image url");
//            addStudent(st);
//        }
    }


    public interface GetAllPostsListener {
        void onComplete(List<Post> data);
    }
    public void getAllPosts(GetAllPostsListener listener) {
        firebaseDao.getAllPosts(listener);
    }

    public interface AddPostListener{
        void onComplete(Post post);
    }

    public void addPost(Post post, AddPostListener listener) {
        firebaseDao.addPost(post, listener);
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
