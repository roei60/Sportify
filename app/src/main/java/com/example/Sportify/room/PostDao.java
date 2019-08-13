package com.example.Sportify.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.Sportify.models.Post;

import java.util.List;

@Dao
public interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Post post);

    @Query("DELETE FROM post_table")
    void deleteAll();

    @Query("SELECT * from post_table")
    LiveData<List<Post>> getAllPosts();

    @Query("SELECT * from post_table WHERE post_id=:postId")
    LiveData<Post> getPostById(String postId);

    @Query("SELECT * from post_table WHERE mAuthorId =:authorId ")
    LiveData<List<Post>> getAllPostsByUserId(String authorId);

}
