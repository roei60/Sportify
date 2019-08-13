package com.example.Sportify.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.Sportify.models.User;

import java.util.List;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Query("DELETE FROM user_table")
    void deleteAll();

    @Query("SELECT * from user_table")
    LiveData<List<User>> getAllUsers();

    @Query("SELECT * FROM user_table WHERE user_id= :userId")
    LiveData<User> getUserById(String userId);
}
