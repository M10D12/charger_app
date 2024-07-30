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

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private EditText etEmail, etName, etPassword;
    private Button btnRegister;
    private TextView tvLogin;
    private UserDatabaseManager userDatabaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.etEmail);
        etName = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        userDatabaseManager = new UserDatabaseManager(this);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String email = etEmail.getText().toString().trim();
                    String name = etName.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();
                    Log.d(TAG, "Register button clicked");

                    if (validateInputs(email, name, password)) {
                        Log.d(TAG, "Inputs validated: " + email + ", " + name);
                        userDatabaseManager.registerUser(email, name, password, new UserDatabaseManager.RegisterUserCallback() {
                            @Override
                            public void onRegisterUserCompleted(User user) {
                                runOnUiThread(() -> {
                                    if (user != null) {
                                        Log.d(TAG, "Registration successful for user: " + user.getName());
                                        Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Log.d(TAG, "Registration failed");
                                        Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error during registration", e);
                    Toast.makeText(RegisterActivity.this, "An error occurred during registration", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean validateInputs(String email, String name, String password) {
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
            return false;
        }

        if (name.isEmpty()) {
            etName.setError("Name is required");
            etName.requestFocus();
            return false;
        }

        if (password.isEmpty() || password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters long");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }
}