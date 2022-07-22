package com.example.pigeonlineandroidapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.pigeonlineandroidapp.R;
import com.example.pigeonlineandroidapp.viewModels.ContactsViewModel;
import com.example.pigeonlineandroidapp.viewModels.ContactsViewModelFactory;
import java.util.ArrayList;

/*
 * Add Contact Activity - Specific chat screen.
 */
public class AddContactActivity extends AppCompatActivity {
    private ContactsViewModel contactsViewModel;
    private String username;
    private String token;
    private String defaultServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        Intent intentParams = getIntent();
        String appToken = intentParams.getExtras().getString("appToken");
        this.username = intentParams.getExtras().getString("username");
        this.token = intentParams.getExtras().getString("token");
        this.defaultServer = intentParams.getExtras().getString("defaultServer");
        this.contactsViewModel = new ViewModelProvider(this, new ContactsViewModelFactory
                (username, getApplicationContext(), token, appToken, this.defaultServer)).get(ContactsViewModel.class);

        Button addBtn = findViewById(R.id.contact_add_btn);
        addBtn.setOnClickListener(item -> {
            TextView warningMessage = findViewById(R.id.contact_warning_message);
            EditText identifier = findViewById(R.id.contact_identifier);
            EditText displayName = findViewById(R.id.contact_display);
            EditText server = findViewById(R.id.contact_server);
            if((identifier.getText().toString().equals("")) ||
                    (displayName.getText().toString().equals("")) ||
                    (server.getText().toString().equals(""))) {
                warningMessage.setText("All fields are required !");
                warningMessage.setVisibility(View.VISIBLE);
                return;
            }
            if (username.equals(identifier.getText().toString())) {
                warningMessage.setText("You can't add yourself !");
                warningMessage.setVisibility(View.VISIBLE);
                return;
            }
            Intent intentLst = getIntent();
            ArrayList<String> identifiersLst =  (ArrayList<String>) intentLst.
                    getSerializableExtra("identifiers_list");
            if(identifiersLst != null) {
                for (String contact_usr : identifiersLst) {
                    if (contact_usr.equals(identifier.getText().toString())) {
                        warningMessage.setText("You already have this contact !");
                        warningMessage.setVisibility(View.VISIBLE);
                        return;
                    }
                }
            }

            this.contactsViewModel.add(username, identifier.getText().toString(),
                    server.getText().toString(),
                    displayName.getText().toString(), this);
        });
    }

    // If success to add the contact - return to contacts activity.
    public void handleSuccess() {
        EditText identifierET = findViewById(R.id.contact_identifier);
        EditText displayET = findViewById(R.id.contact_display);
        EditText serverET = findViewById(R.id.contact_server);
        identifierET.setText("");
        displayET.setText("");
        serverET.setText("");
        Intent intent = new Intent(getApplicationContext(), ContactsActivity.class);
        intent.putExtra("username", this.username);
        intent.putExtra("token", this.token);
        startActivity(intent);
    }

    public void handleFailure() {
        TextView warningMessage = findViewById(R.id.contact_warning_message);
        warningMessage.setText("User not exist !");
        warningMessage.setVisibility(View.VISIBLE);
    }

}