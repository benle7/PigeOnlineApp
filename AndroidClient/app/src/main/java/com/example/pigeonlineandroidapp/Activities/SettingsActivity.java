package com.example.pigeonlineandroidapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import com.example.pigeonlineandroidapp.R;

/*
* Settings Activity - for change default server or change theme.
*/
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        EditText defaultServer = findViewById(R.id.settings_server);
        Button applyBtn = findViewById(R.id.settings_apply_btn);
        Button lightBtn = findViewById(R.id.settings_light_btn);
        Button nightBtn = findViewById(R.id.settings_night_btn);

        // Change theme by push light / night button.
        lightBtn.setOnClickListener(view -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        });
        nightBtn.setOnClickListener(view -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        });

        // Set the server and back to login screen.
        applyBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            if(!defaultServer.getText().toString().equals("")) {
                intent.putExtra("defaultServer", defaultServer.getText().toString());
                defaultServer.setText("");
            }
            startActivity(intent);
        });
    }
}