package com.example.pigeonlineandroidapp.DataBase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.pigeonlineandroidapp.entities.Chat;
import com.example.pigeonlineandroidapp.entities.Message;
import java.util.List;

@Dao
public interface MessagesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Message...messages);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Message> messages);

    @Query("SELECT * FROM message WHERE chatOwnerId=:id")
    List<Message> index(int id);

}
