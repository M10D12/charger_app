package com.example.charger_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText etName, etPassword;
    private Button btnLogin;
    private TextView tvSignUp;
    private UserDatabaseManager userDatabaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etName = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);

        userDatabaseManager = new UserDatabaseManager(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                Log.e(TAG, "Attempting login for: " + name);

                userDatabaseManager.checkUser(name, password, new UserDatabaseManager.CheckUserCallback() {
                    @Override
                    public void onCheckUserCompleted(User user) {
                        runOnUiThread(() -> {
                            if (user != null) {
                                Log.e(TAG, "Login successful for user: " + user.getName());
                                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                SessionManager.getInstance().setUserId(user.getId());
                                Intent intent = new Intent(LoginActivity.this, AddChargerActivity.class);
                                startActivity(intent);
                            } else {
                                Log.e(TAG, "Login failed for user: " + name);
                                Toast.makeText(LoginActivity.this, "Invalid name or password", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}