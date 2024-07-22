// AddChargerActivity.java
package com.example.charger_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddChargerActivity extends AppCompatActivity {
    private EditText etDeviceName, etMacAddress;
    private Button btnAddDevice;
    private UserDatabaseManager userDatabaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_charger);

        etDeviceName = findViewById(R.id.etDeviceName);
        etMacAddress = findViewById(R.id.etMacAddress);
        btnAddDevice = findViewById(R.id.btnAddDevice);
        userDatabaseManager = new UserDatabaseManager(this);

        btnAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deviceName = etDeviceName.getText().toString().trim();
                String macAddress = etMacAddress.getText().toString().trim();
                int userId = SessionManager.getInstance().getUserId();

                if (userId > 0 && !deviceName.isEmpty() && !macAddress.isEmpty()) {
                    long result = userDatabaseManager.addDevice(userId, deviceName, macAddress);
                    if (result > 0) {
                        Toast.makeText(AddChargerActivity.this, "Dispositivo adicionado com sucesso", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddChargerActivity.this, "Falha ao adicionar dispositivo", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddChargerActivity.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
