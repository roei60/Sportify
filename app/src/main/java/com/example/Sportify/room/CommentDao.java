package com.example.Sportify.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.Sportify.models.Comment;
import com.example.Sportify.models.CommentAndUser;
import com.example.Sportify.models.Post;

import java.util.List;

@Dao
public interface CommentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Comment comment);

    @Query("DELETE FROM comment_table")
    void deleteAll();

    @Query("SELECT * from comment_table " +
                " INNER JOIN user_table ON mUserId=user_id WHERE mPostId=:postId")
    LiveData<List<CommentAndUser>> getAllCommentByPostId(String postId);

}
