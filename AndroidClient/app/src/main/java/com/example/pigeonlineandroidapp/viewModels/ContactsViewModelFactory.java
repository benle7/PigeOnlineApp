package com.example.pigeonlineandroidapp.viewModels;

import android.content.Context;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.pigeonlineandroidapp.repos.ContactsRepository;

// Factory for create Contacts View Model (by singleton)
public class ContactsViewModelFactory implements ViewModelProvider.Factory {
    private String username;
    private Context context;
    private String token;
    private String appToken;
    private String defaultServer;

    public ContactsViewModelFactory(String username, Context context, String token, String appToken, String defaultServer) {
        this.username = username;
        this.context = context;
        this.token = token;
        this.appToken = appToken;
        this.defaultServer = defaultServer;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new ContactsViewModel(username, context, token, appToken, defaultServer);
    }
}
