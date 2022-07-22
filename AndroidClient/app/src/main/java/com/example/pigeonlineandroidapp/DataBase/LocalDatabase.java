package com.example.pigeonlineandroidapp.DataBase;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.pigeonlineandroidapp.entities.Chat;
import com.example.pigeonlineandroidapp.entities.Message;

@Database(entities = {Chat.class, Message.class}, version = 1)
public abstract class LocalDatabase extends RoomDatabase {
    private static LocalDatabase localdb = null;

    public abstract ChatsDao chatDao();
    public abstract MessagesDao messagesDao();

    public static LocalDatabase getInstance(Context context) {
        if(localdb == null) {
            localdb = Room.databaseBuilder(context, LocalDatabase.class,
                    "PigeOnlineDB").allowMainThreadQueries().build();
        }
        return localdb;
    }

}
