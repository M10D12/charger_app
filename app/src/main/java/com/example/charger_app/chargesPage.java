// chargesPage.java
package com.example.charger_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;


import androidx.appcompat.app.AppCompatActivity;

public class chargesPage extends AppCompatActivity {
    private ImageButton btnChargers, btnAdd, btnProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chargers_page);

        btnChargers.setOnClickListener(v -> {
            Intent intent = new Intent(chargesPage.this, chargesPage.class);
            startActivity(intent);
        });

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(chargesPage.this, AddChargerActivity.class);
            startActivity(intent);
        });

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(chargesPage.this, Profile_Ativity.class);
            startActivity(intent);
        });
    }
}
