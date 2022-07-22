package com.example.pigeonlineandroidapp.repos;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.pigeonlineandroidapp.API.MessagesAPI;
import com.example.pigeonlineandroidapp.DataBase.ChatsDao;
import com.example.pigeonlineandroidapp.DataBase.LocalDatabase;
import com.example.pigeonlineandroidapp.DataBase.MessagesDao;
import com.example.pigeonlineandroidapp.entities.Chat;
import com.example.pigeonlineandroidapp.entities.Message;
import java.util.ArrayList;
import java.util.List;

// Repository of Messages.
public class MessagesRepository {
    private MessagesDao messagesDao;
    private MessagesRepository.MessageListData messageListData;
    private LocalDatabase db;
    private MessagesAPI messagesAPI;
    private String contactUsername;
    private int chatOwnerId;
    private String username;
    private String token;
    private Context context;

    public MessagesRepository(Context context, int id, String token, String contactUsername, String username, String defaultServer) {
        this.db = LocalDatabase.getInstance(context);
        this.messagesDao = db.messagesDao();
        this.messagesAPI = new MessagesAPI(context, token, defaultServer);
        this.messageListData = new MessageListData(id);
        // Take from the server all Messages of current chat.
        this.messagesAPI.get(contactUsername, this);
        this.contactUsername = contactUsername;
        this.chatOwnerId = id;
        this.token = token;
        this.username = username;
        this.context = context;
    }

    // Internal class of MutableLiveData for messages list.
    class MessageListData extends MutableLiveData<List<Message>> {
        public MessageListData(int id) {
            super();
            setValue(new ArrayList<>());
            // Take from Room all messages of the current chat.
            new Thread(()-> {
                postValue(messagesDao.index(id));
            }).start();
        }

        @Override
        protected void onActive() {
            super.onActive();
        }
    }

    public LiveData<List<Message>> getAll() {
        return this.messageListData;
    }

    // After get all messages of current chat from API.
    public void handleGetMessages(int responseNum, List<Message> messages) {
        if(responseNum == 200) {
            // Set value after change - will trigger observers.
            this.messageListData.setValue(messages);
            // Add to room.
            new Thread(() -> {
                messagesDao.insertAll(messages);
            }).start();
        }
    }

    // Add new message - send transfer request.
    public void add(Message message, String contactServer) {
        this.messagesAPI.transfer(message.getFrom(), this.contactUsername,
                message.getContent(),contactServer, this, message);
    }

    // If transfer and post message is success - add to room.
    public void handlePostMessage(int responseNum, Message message) {
        if(responseNum == 201) {
            List<Message> tempList = this.messageListData.getValue();
            tempList.add(message);
            // Set value after change - will trigger observers.
            this.messageListData.setValue(tempList);
            // Add ro room.
            new Thread(() -> {
                messagesDao.insert(message);
                // Update the chat details in the room.
                ChatsDao chatsDao = db.chatDao();
                Chat chat = chatsDao.getChat(this.chatOwnerId);
                chat.setLastMessage(message.getContent());
                chat.setDate(message.getDate());
                chatsDao.insert(chat);
            }).start();

        }
    }

    // If transfer success - call post message.
    public void afterTransfer(int responseNum, String contactUsername, String content, Message message) {
        if(responseNum == 201) {
            this.messagesAPI.postMessage(contactUsername, content,this, message);
        }
    }

    // Get new message from the current contact (chat) - firebase.
    public void addMessageFromFireBase(Message message) {
        new Thread(() -> {this.messagesDao.insert(message);}).start();
        List<Message> messages = this.messageListData.getValue();
        messages.add(message);
        this.messageListData.setValue(messages);
    }
}
