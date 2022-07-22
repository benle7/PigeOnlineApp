package com.example.pigeonlineandroidapp.viewModels;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.pigeonlineandroidapp.entities.Message;
import com.example.pigeonlineandroidapp.repos.MessagesRepository;
import java.util.List;

// View Model of Messages.
public class MessagesViewModel extends ViewModel {
    private MessagesRepository repository;
    private LiveData<List<Message>> messages;

    public MessagesViewModel(Context context, int id, String token, String contactUsername, String username, String defaultServer) {
        this.repository = new MessagesRepository(context, id, token, contactUsername, username, defaultServer);
        this.messages = repository.getAll();
    }

    public LiveData<List<Message>> get() {
        return this.messages;
    }

    public void add(Message message, String contactServer) {
        this.repository.add(message, contactServer);
    }

    // Add message when get from current chat (firebase).
    public void addMessageFromFireBase(Message message) {
        this.repository.addMessageFromFireBase(message);
    }
}
