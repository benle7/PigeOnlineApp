package com.example.pigeonlineandroidapp.DataBase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.pigeonlineandroidapp.entities.Chat;
import java.util.List;

@Dao
public interface ChatsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Chat...chats);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Chat> chats);

    @Query("SELECT * FROM chat WHERE chatOwner=:id")
    List<Chat> index(String id);

    @Query("SELECT * FROM chat WHERE id=:id")
    Chat getChat(int id);

    @Query("SELECT * FROM chat where chatOwner=:chatOwner and chatWith=:chatWith")
    Chat getChatByStrings(String chatOwner, String chatWith);
}
