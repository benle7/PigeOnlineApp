package com.example.pigeonlineandroidapp.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.pigeonlineandroidapp.API.UserAPI;
import com.example.pigeonlineandroidapp.R;
import com.example.pigeonlineandroidapp.entities.User;
import com.google.firebase.iid.FirebaseInstanceId;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

/*
 * Register Activity - for register with new user.
 */
public class RegisterActivity extends AppCompatActivity {
    private UserAPI userAPI;
    private String appToken;
    private String defaultServer;
    private ActivityResultLauncher<Intent> resultLauncher;
    private String image = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.defaultServer = this.getString(R.string.BaseUrl);
        Intent intentSrv = getIntent();
        Bundle extras = intentSrv.getExtras();
        // Take default server from Login (Main) activity.
        if (extras != null) {
            if (extras.containsKey("defaultServer")) {
                this.defaultServer = intentSrv.getExtras().getString("defaultServer");
            }
        }

        this.userAPI = new UserAPI(this.getApplicationContext(), this.defaultServer);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(
                RegisterActivity.this, instanceIdResult -> {
                    this.appToken = instanceIdResult.getToken();
                });

        Button loginBtn = findViewById(R.id.register_login_btn);
        loginBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        this.resultLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(), result -> {
                            if (result.getResultCode() == RESULT_OK && result.getData()!= null) {
                                Intent data = result.getData();
                                if(data.getData() != null) {
                                    Uri uri = (Uri) data.getData();
                                    Bitmap bitmap;
                                    try {
                                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                                        byte[] b = byteArrayOutputStream.toByteArray();
                                        this.image = Base64.encodeToString(b, Base64.DEFAULT);
                                    }
                                    catch(IOException e) {
                                        Toast.makeText(this,"Unable to upload image", Toast.LENGTH_LONG).show();
                                    }

                                }
                            }
                        });

        Button addImageBtn = findViewById(R.id.register_addImage);
        addImageBtn.setOnClickListener( view -> {
                    Intent imagePickerIntent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    imagePickerIntent.setType("image/*");
                    resultLauncher.launch(imagePickerIntent);
                }
                );

        Button registerBtn = findViewById(R.id.register_register_btn);
        registerBtn.setOnClickListener(view -> {
            TextView warningMessage = findViewById(R.id.register_warning_message);
            EditText userName = findViewById(R.id.register_username);
            ViewGroup.LayoutParams params = warningMessage.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            if(userName.getText().toString().equals("")) {
                warningMessage.setLayoutParams(params);
                warningMessage.setVisibility(View.VISIBLE);
                warningMessage.setText("The username field cannot be empty.");
                return;
            }
            EditText password = findViewById(R.id.register_password);
            if(password.getText().toString().equals("") ||
                            !Pattern.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,20}$",
                            password.getText().toString())) {
                warningMessage.setLayoutParams(params);
                warningMessage.setVisibility(View.VISIBLE);
                warningMessage.setText("Choose at least 8 characters- one numeric digit, one uppercase and one lowercase letter.");
                return;
            }
            EditText validatePassword = findViewById(R.id.register_validatePass);
            if(!password.getText().toString().equals(validatePassword.getText().toString())) {
                warningMessage.setLayoutParams(params);
                warningMessage.setVisibility(View.VISIBLE);
                warningMessage.setText("The validate password field must be equal to the password field.");
                return;
            }
            EditText displayName = findViewById(R.id.register_displayName);
            if(displayName.getText().toString().equals("")) {
                warningMessage.setLayoutParams(params);
                warningMessage.setVisibility(View.VISIBLE);
                warningMessage.setText("The displayName field cannot be empty.");
                return;
            }
            User user = new User();
            user.setUsername(userName.getText().toString());
            user.setPassword(password.getText().toString());
            user.setDisplayName(displayName.getText().toString());
            if(this.image != null) {
                user.setImage(this.image);
            }
            else {
                user.setImage("im3.jpg");
            }
            this.userAPI.postUser(user, this);
        });

    }

    // Handle success of postUser in userAPI.
    public void handleRegisterResponse(int responseCode, String token, String username) {
        if(responseCode == 200) {
            EditText userNameET = findViewById(R.id.register_username);
            EditText passwordET = findViewById(R.id.register_password);
            EditText valET = findViewById(R.id.register_validatePass);
            EditText displayET = findViewById(R.id.register_displayName);
            userNameET.setText("");
            passwordET.setText("");
            valET.setText("");
            displayET.setText("");
            TextView warningMessage = findViewById(R.id.register_warning_message);
            warningMessage.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(this, ContactsActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("token", "Bearer " + token);
            intent.putExtra("appToken", appToken);
            intent.putExtra("defaultServer", this.defaultServer);
            startActivity(intent);
            finish();
        }
        else {
            TextView warningMessage = findViewById(R.id.register_warning_message);
            ViewGroup.LayoutParams params = warningMessage.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            warningMessage.setLayoutParams(params);
            warningMessage.setVisibility(View.VISIBLE);
            warningMessage.setText("Sorry, this username is already taken.");
        }
    }

}