package com.example.pigeonlineandroidapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import com.example.pigeonlineandroidapp.R;
import com.example.pigeonlineandroidapp.entities.Chat;
import com.example.pigeonlineandroidapp.entities.Message;
import java.util.List;

// Adapter for messages listView.
public class MessagesAdapter extends ArrayAdapter<Message> {
    LayoutInflater inflater;
    String currentUsr;

    public MessagesAdapter(Context context, List<Message> messages, String usr) {
        super(context, R.layout.lmessage_item, messages);
        this.inflater = LayoutInflater.from(context);
        this.currentUsr = usr;
    }

    public void setData(List<Message> messages) {
        super.clear();
        super.addAll(messages);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Message message = getItem(position);
        // Set the layout by the sender.
        if(message.getFrom().equals(this.currentUsr)) {
            convertView = inflater.inflate(R.layout.rmessage_item, parent, false);
        } else {
            convertView = inflater.inflate(R.layout.lmessage_item, parent, false);
        }
        TextView content = convertView.findViewById(R.id.message_content);
        content.setText(message.getContent());
        return convertView;
    }
}
