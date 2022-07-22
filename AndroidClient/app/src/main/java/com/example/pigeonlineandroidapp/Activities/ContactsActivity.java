package com.example.pigeonlineandroidapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import com.example.pigeonlineandroidapp.API.ServiceAPI;
import com.example.pigeonlineandroidapp.API.UserAPI;
import com.example.pigeonlineandroidapp.Adapters.ContactsAdapter;
import com.example.pigeonlineandroidapp.R;
import com.example.pigeonlineandroidapp.entities.Chat;
import com.example.pigeonlineandroidapp.entities.Message;
import com.example.pigeonlineandroidapp.viewModels.ContactsViewModel;
import com.example.pigeonlineandroidapp.viewModels.ContactsViewModelFactory;
import java.util.ArrayList;
import java.util.List;

/*
 * Contacts Activity - chats screen.
 */
public class ContactsActivity extends AppCompatActivity {
    private ContactsViewModel contactsViewModel;
    private String username;
    private ListView contactsListView;
    private String token;
    private int lastPressedChat;
    private String defaultServer;

    private class BroadcastInContacts extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Message message = new Message();
            Bundle extras = intent.getExtras();
            message.setFrom(extras.getString("from"));
            message.setContent(extras.getString("content"));
            message.setDate(extras.getString("date"));
            contactsViewModel.updateNewMessage(message, username);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Intent intent = getIntent();
        this.lastPressedChat = -1;
        this.defaultServer = intent.getExtras().getString("defaultServer");
        this.username = intent.getExtras().getString("username");
        this.token = intent.getExtras().getString("token");
        String appToken = intent.getExtras().getString("appToken");
        this.contactsViewModel = new ViewModelProvider(this, new ContactsViewModelFactory
                (this.username, this, this.token, appToken, this.defaultServer)).get(ContactsViewModel.class);
        this.contactsListView = findViewById(R.id.contacts_chatsList);

        List<Chat> chats = this.contactsViewModel.get().getValue();
        final ContactsAdapter contactsAdapter = new ContactsAdapter(this, chats);
        this.contactsListView.setAdapter(contactsAdapter);

        Button addBtn = findViewById(R.id.contacts_btn);
        addBtn.setOnClickListener(item -> {
            Intent intentAdd = new Intent(getApplicationContext(), AddContactActivity.class);
            ArrayList<String> identifiersLst = new ArrayList<>();
            if(chats != null) {
                for (Chat chat : chats) {
                    identifiersLst.add(chat.getChatWith());
                }
            }
            // Put the identifiers of the contacts - for check if the contact already exist.
            intentAdd.putExtra("identifiers_list", identifiersLst);
            intentAdd.putExtra("username", this.username);
            intentAdd.putExtra("token", this.token);
            intentAdd.putExtra("appToken", appToken);
            intentAdd.putExtra("defaultServer", this.defaultServer);
            startActivity(intentAdd);
        });

        contactsListView.setClickable(true);
        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("currentUsername", username);
                intent.putExtra("contactUsername", contactsAdapter.getItem(position).getChatWith());
                intent.putExtra("contactDisplayName", contactsAdapter.getItem(position).getDisplayName());
                intent.putExtra("chatId", contactsAdapter.getItem(position).getId());
                intent.putExtra("server", contactsAdapter.getItem(position).getServerURL());
                intent.putExtra("chatImg", contactsAdapter.getItem(position).getImage());
                intent.putExtra("token", token);
                intent.putExtra("defaultServer", defaultServer);
                lastPressedChat = contactsAdapter.getItem(position).getId();
                startActivity(intent);
            }
        });

        this.contactsViewModel.get().observe(this, contacts -> {
            contactsAdapter.setData(contacts);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // On back from chat - update details in contacts screen.
        if(this.lastPressedChat != -1) {
            super.onResume();
            this.contactsViewModel.updateChat(this.lastPressedChat);
            this.lastPressedChat = -1;
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("onMessageReceived");
        BroadcastInContacts receiver = new BroadcastInContacts();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        UserAPI api = new UserAPI(this.getApplicationContext(), this.defaultServer);
        // Logout.
        api.declareOffline(this.username);
        finish();
    }
}