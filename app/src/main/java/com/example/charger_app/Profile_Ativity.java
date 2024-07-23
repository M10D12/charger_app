package com.example.charger_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Profile_Ativity extends AppCompatActivity {
    private TextView usernameTextView;
    private TextView emailTextView;
    private UserDatabaseManager userDatabaseManager;
    private ImageButton btnChargers, btnAdd, btnProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);
        // Obter as referências dos TextView
        usernameTextView = findViewById(R.id.username);
        emailTextView = findViewById(R.id.email);

        // Inicializar o UserDatabaseManager
        userDatabaseManager = new UserDatabaseManager(this);

        // Obter o ID do usuário logado da SessionManager
        int userId = SessionManager.getInstance().getUserId();

        // Obter os detalhes do usuário do banco de dados
        User loggedUser = userDatabaseManager.getUserDetails(userId);

        // Definir o texto dos TextView
        if (loggedUser != null) {
            usernameTextView.setText(loggedUser.getUsername());
            emailTextView.setText(loggedUser.getEmail());
        }

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
