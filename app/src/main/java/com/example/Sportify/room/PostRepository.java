package com.example.Sportify.room;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.Sportify.models.Comment;
import com.example.Sportify.models.Post;
import com.example.Sportify.models.PostAndUser;
import com.example.Sportify.models.User;

import java.util.List;

public class PostRepository {

    private PostDao mPostDao;
    private UserDao mUserDao;
    private CommentDao mCommentDao;
    private LiveData<List<PostAndUser>> mAllPosts;
    private LiveData<List<User>> mAllUsers;

    public PostRepository(Application application) {
        PostRoomDatabase db = PostRoomDatabase.getDatabase(application);
        mPostDao= db.postDao();
        mCommentDao = db.commentsDao();
        mUserDao= db.userDao();
        mAllPosts = mPostDao.getAllPosts();
        mAllUsers = mUserDao.getAllUsers();

    }

    public LiveData<List<PostAndUser>> getAllPosts() {
        return mAllPosts;
    }
    public LiveData<List<User>> getAllUsers() {
        return mAllUsers;
    }

    public LiveData<List<Post>> getPostByUserId(String  userId){
        return mPostDao.getAllPostsByUserId(userId);
    }
    public LiveData<Post> getPostById(String  postId){
        return mPostDao.getPostById(postId);
    }

    public LiveData<List<Comment>> getCommentByPropertyId(String postId){
        return mCommentDao.getAllCommentByPostId(postId);
    }
    public LiveData<User> getUserById(String userId)
    {
        return  mUserDao.getUserById(userId);
    }

    public void insert(Post post) {
        new insertPostAsyncTask(mPostDao).execute(post);

    }
    public void insert(User user) {
        new insertUserAsyncTask(mUserDao).execute(user);
    }
    public void insert(Comment comment) {
        new insertAsyncTask(mCommentDao).execute(comment);
    }

    public void deletePost(String postId) {
        new deletePostAsyncTask(mPostDao).execute(postId);
    }

    private static class insertAsyncTask extends AsyncTask<Comment, Void, Void> {

        private CommentDao mAsyncTaskDao;

        insertAsyncTask(CommentDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Comment... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class insertPostAsyncTask extends AsyncTask<Post, Void, Void> {

        private PostDao mAsyncTaskDao;

        public insertPostAsyncTask(PostDao  dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Post... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
    private static class insertUserAsyncTask extends AsyncTask<User, Void, Void> {

        private UserDao mAsyncTaskDao;

        public insertUserAsyncTask (UserDao  dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final User... params) {
            mAsyncTaskDao.insertUser(params[0]);
            return null;
        }
    }

    private static class deletePostAsyncTask extends AsyncTask<String, Void, Void> {

        private PostDao mAsyncTaskDao;

        public deletePostAsyncTask(PostDao  dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final String... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }

}
