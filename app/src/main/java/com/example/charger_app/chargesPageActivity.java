package com.example.charger_app;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class chargesPageActivity extends AppCompatActivity {
    private TextView usernameTextView;
    private TextView emailTextView;
    private UserDatabaseManager userDatabaseManager;

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
    }
}
