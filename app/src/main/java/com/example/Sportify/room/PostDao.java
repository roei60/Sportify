package com.example.Sportify.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.Sportify.models.Post;
import com.example.Sportify.models.PostAndUser;

import java.util.List;

@Dao
public interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Post post);

    @Query("DELETE FROM post_table")
    void deleteAll();

    @Query("SELECT * from post_table" +
            " INNER JOIN user_table ON mAuthorId=user_id  and mIsDeleted=0 order by mCreationDate desc")
    LiveData<List<PostAndUser>> getAllPosts();

    @Query("SELECT * from post_table WHERE post_id=:postId  and mIsDeleted=0 order by mCreationDate desc ")
    LiveData<Post> getPostById(String postId);

    @Query("SELECT * from post_table " +
            "INNER JOIN user_table ON mAuthorId=user_id WHERE mAuthorId =:authorId and mIsDeleted=0 order by mCreationDate desc ")
    LiveData<List<PostAndUser>> getAllPostsByUserId(String authorId);

    @Query("DELETE FROM post_table WHERE post_id=:postId")
    void delete(String postId);

}
