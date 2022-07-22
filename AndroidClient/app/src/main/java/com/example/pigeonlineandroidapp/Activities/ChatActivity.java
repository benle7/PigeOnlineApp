package com.example.pigeonlineandroidapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.example.pigeonlineandroidapp.Adapters.MessagesAdapter;
import com.example.pigeonlineandroidapp.R;
import com.example.pigeonlineandroidapp.entities.Message;
import com.example.pigeonlineandroidapp.viewModels.MessagesViewModel;
import com.example.pigeonlineandroidapp.viewModels.MessagesViewModelFactory;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/*
 * Chat Activity - Specific chat screen.
 */
public class ChatActivity extends AppCompatActivity {
    private MessagesViewModel messagesViewModel;
    private ListView messagesLV;
    private String token;
    private String username;
    private String contactServer;
    private String defaultServer;
    private String contactUsername;

    private class BroadcastInChat extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if(extras.getString("from").equals(contactUsername)) {
                Message message = new Message();
                message.setFrom(extras.getString("from"));
                message.setContent(extras.getString("content"));
                message.setDate(extras.getString("date"));
                messagesViewModel.addMessageFromFireBase(message);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        this.defaultServer = intent.getExtras().getString("defaultServer");
        this.username = intent.getExtras().getString("currentUsername");
        this.contactUsername = intent.getExtras().getString("contactUsername");
        this.token = intent.getExtras().getString("token");
        this.contactServer = intent.getExtras().getString("server");
        String contactDisplayName = intent.getExtras().getString("contactDisplayName");
        int chatID = intent.getExtras().getInt("chatId");

        // Set display name and image of the contact (in the current chat).
        TextView displayNameTV = findViewById(R.id.chat_displayName);
        displayNameTV.setText(contactDisplayName);
        String imgStr = intent.getExtras().getString("chatImg");
        ImageView imgChat = findViewById(R.id.chat_image);
        if (!imgStr.equals("im3.jpg")) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            imageBytes = Base64.decode(imgStr, Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imgChat.setImageBitmap(decodedImage);
        }

        this.messagesViewModel = new ViewModelProvider(this, new MessagesViewModelFactory
                (chatID, getApplicationContext(), token, contactUsername, this.username, this.defaultServer)).get(MessagesViewModel.class);

        this.messagesLV = findViewById(R.id.chat_messagesList);
        List<Message> messages = this.messagesViewModel.get().getValue();
        final MessagesAdapter messagesAdapter = new MessagesAdapter(this,
                messages, this.username);
        this.messagesLV.setAdapter(messagesAdapter);
        // Scroll to bottom.
        this.messagesLV.setSelection(messagesAdapter.getCount() - 1);

        Button sendBtn = findViewById(R.id.send_button);
        EditText messageEt = findViewById(R.id.message_input);
        sendBtn.setOnClickListener(item -> {
            if (messageEt.getText().toString().equals("")) {
                return;
            }
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            this.messagesViewModel.add(new Message(this.username,
                    messageEt.getText().toString(),
                    formatter.format(date), chatID, "text"), this.contactServer);
            messageEt.setText("");
            // Scroll to bottom.
            this.messagesLV.setSelection(messagesAdapter.getCount() - 1);
        });

        this.messagesViewModel.get().observe(this, allMessages -> {
            messagesAdapter.setData(allMessages);
            // Scroll to bottom.
            this.messagesLV.setSelection(messagesAdapter.getCount() - 1);
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("onMessageReceived");
        BroadcastInChat receiver = new BroadcastInChat();
        registerReceiver(receiver, intentFilter);
    }
}