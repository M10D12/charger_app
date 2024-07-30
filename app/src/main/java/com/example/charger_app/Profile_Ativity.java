package com.example.charger_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Profile_Ativity extends AppCompatActivity {
    private TextView nameTextView;
    private TextView emailTextView;
    private UserDatabaseManager userDatabaseManager;
    private ImageButton btnChargers, btnAdd, btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        nameTextView = findViewById(R.id.username);
        emailTextView = findViewById(R.id.email);

        userDatabaseManager = new UserDatabaseManager(this);

        int userId = SessionManager.getInstance().getUserId();

        userDatabaseManager.getUserDetails(userId, new UserDatabaseManager.GetUserDetailsCallback() {
            @Override
            public void onGetUserDetailsCompleted(User loggedUser) {
                if (loggedUser != null) {
                    nameTextView.setText(loggedUser.getName());
                    emailTextView.setText(loggedUser.getEmail());
                }
            }
        });

        btnChargers = findViewById(R.id.btnChargers);
        btnAdd = findViewById(R.id.btnAdd);
        btnProfile = findViewById(R.id.btnProfile);

        btnChargers.setOnClickListener(v -> {
            Intent intent = new Intent(Profile_Ativity.this, chargesPage.class);
            startActivity(intent);
        });

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(Profile_Ativity.this, AddChargerActivity.class);
            startActivity(intent);
        });

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Profile_Ativity.this, Profile_Ativity.class);
            startActivity(intent);
        });
    }
}