package com.example.pigeonlineandroidapp.repos;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.pigeonlineandroidapp.API.ChatsAPI;
import com.example.pigeonlineandroidapp.API.InvitationParams;
import com.example.pigeonlineandroidapp.API.PostContactParams;
import com.example.pigeonlineandroidapp.Activities.AddContactActivity;
import com.example.pigeonlineandroidapp.DataBase.ChatsDao;
import com.example.pigeonlineandroidapp.DataBase.LocalDatabase;
import com.example.pigeonlineandroidapp.DataBase.MessagesDao;
import com.example.pigeonlineandroidapp.R;
import com.example.pigeonlineandroidapp.entities.Chat;
import com.example.pigeonlineandroidapp.entities.Message;
import java.util.ArrayList;
import java.util.List;

// Repository of contacts (chats).
public class ContactsRepository {
    private ChatsDao chatsDao;
    private ChatListData chatListData;
    private String username;
    private ChatsAPI chatsAPI;
    private LocalDatabase db;
    private Context context;
    private String defaultServer;

    public ContactsRepository(String username, Context context, String token, String appToken, String defaultServer) {
        this.username = username;
        this.db = LocalDatabase.getInstance(context);
        this.chatsDao = db.chatDao();
        this.chatsAPI = new ChatsAPI(context, this.chatsDao, token, defaultServer);
        this.chatListData = new ChatListData();
        this.chatsAPI.declareOnline(appToken);
        // Take from the server all chats of current user.
        this.chatsAPI.get(this);
        this.context = context;
        this.defaultServer = defaultServer;
    }

    // Internal class of MutableLiveData for chats list.
    class ChatListData extends MutableLiveData<List<Chat>> {
        public ChatListData() {
            super();
            setValue(new ArrayList<>());
            // Take from Room all chats of the current user.
            new Thread(()-> {
                postValue(chatsDao.index(username));
            }).start();
        }
    }

    public LiveData<List<Chat>> getAll() {
        return chatListData;
    }

    // Add new contact - send invitation request.
    public void add(String from, String to, String server, String displayName, AddContactActivity addContactActivity) {
        InvitationParams invitationParams = new InvitationParams();
        invitationParams.setTo(to);
        invitationParams.setFrom(from);
        if(this.defaultServer.equals("http://10.0.2.2:5010")) {
            invitationParams.setServer("http://127.0.0.1:5010");
        }
        else {
            invitationParams.setServer(this.defaultServer);
        }
        if(server.equals("http://127.0.0.1:5010") || server.equals("http://localhost:5010")) {
            server = "http://10.0.2.2:5010";
        }
        chatsAPI.sendInvitation(invitationParams, server, displayName, this, addContactActivity);
    }

    // If invitation and post contact is success - add to room.
    public void postChatHandler(int responseCode, String to, String name, String server, String from, AddContactActivity addContactActivity, String image) {
        if(responseCode == 201) {
            Chat chat = new Chat(to, name, server, from);
            chat.setImage(image);
            List<Chat> tempList = this.chatListData.getValue();
            tempList.add(chat);
            // Set value after change - will trigger observers.
            this.chatListData.setValue(tempList);
            // Add to room.
            new Thread(() -> {chatsDao.insert(chat);}).start();
            addContactActivity.handleSuccess();
        }
        else {
            addContactActivity.handleFailure();
        }
    }

    // If invitation is success - send post contact.
    public void afterInvitationHandler(int responseCode, String to, String name, String server, AddContactActivity addContactActivity) {
        if(responseCode == 201) {
            PostContactParams postContactParams = new PostContactParams();
            postContactParams.setId(to);
            postContactParams.setName(name);
            if(server.equals("http://10.0.2.2:5010")) {
                server = "http://127.0.0.1:5010";
            }
            postContactParams.setServer(server);
            chatsAPI.post(postContactParams, this, this.username, addContactActivity);
        }
        else {
            addContactActivity.handleFailure();
        }
    }

    // After get all chats of current user from API.
    public void handleGetChats(int responseNum, List<Chat> chatsList) {
        if(responseNum == 200) {
            // Set current user to be the owner.
            for(Chat chat : chatsList) {
                chat.setChatOwner(username);
            }
            // Set value after change - will trigger observers.
            this.chatListData.setValue(chatsList);
            // Add to room.
            new Thread(() -> {
                chatsDao.insertAll(chatsList);
            }).start();
        }
    }

    // Update chat details in the live data list.
    public void update(int id) {
        Chat chat = this.chatsDao.getChat(id);
        List<Chat> chats = this.chatListData.getValue();
        for(Chat c : chats) {
            if(c.getId() == id) {
                c.setDate(chat.getDate());
                c.setLastMessage(chat.getLastMessage());
            }
        }
        this.chatListData.setValue(chats);
    }

    // Update chat details and add new message after get new message.
    public void updateNewMessage(Message message, String chatOwner) {
        String chatWith = message.getFrom();
        Chat chat = this.chatsDao.getChatByStrings(chatOwner, chatWith);
        chat.setLastMessage(message.getContent());
        chat.setDate(message.getDate());
        // Room update - (replace).
        new Thread(() -> {this.chatsDao.insert(chat);}).start();
        List<Chat> chats = this.chatListData.getValue();
        for(Chat c : chats) {
            if(c.getChatWith().equals(chat.getChatWith())) {
                c.setDate(chat.getDate());
                c.setLastMessage(chat.getLastMessage());
            }
        }
        // Live data list update.
        this.chatListData.setValue(chats);
        MessagesDao messagesDao = LocalDatabase.getInstance(this.context).messagesDao();
        new Thread(() -> {messagesDao.insert(message);}).start();
    }
}
