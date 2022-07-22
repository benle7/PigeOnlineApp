package com.example.pigeonlineandroidapp.viewModels;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.pigeonlineandroidapp.Activities.AddContactActivity;
import com.example.pigeonlineandroidapp.entities.Chat;
import com.example.pigeonlineandroidapp.entities.Message;
import com.example.pigeonlineandroidapp.repos.ContactsRepository;
import java.util.List;

// View Model of Contacts (chats).
public class ContactsViewModel extends ViewModel {
    private ContactsRepository repository;
    private LiveData<List<Chat>> chats;

    public ContactsViewModel(String username, Context context, String token, String appToken, String defaultServer) {
        this.repository = new ContactsRepository(username, context, token, appToken, defaultServer);
        this.chats = repository.getAll();
    }

    public LiveData<List<Chat>> get() {
        return this.chats;
    }

    public void add(String from, String to, String server, String displayName, AddContactActivity addContactActivity) {
        this.repository.add(from, to, server, displayName, addContactActivity);
    }

    // Update chat when back from the chat to the contacts screen.
    public void updateChat(int id) {
        this.repository.update(id);
    }

    // Update the chat when get new message.
    public void updateNewMessage(Message message, String chatOwner) {
        this.repository.updateNewMessage(message, chatOwner);
    }

}
