package com.example.charger_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent; // Certifique-se de importar Intent
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class AddChargerActivity extends AppCompatActivity {
    private static final String TAG = "AddChargerActivity";
    private static final int REQUEST_BLUETOOTH_PERMISSION = 1;
    private static final int REQUEST_WIFI_PERMISSION = 2;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private ImageButton btnConnectBluetooth, btnConnectWifi, btnChargers, btnAdd, btnProfile;
    private ArrayList<BluetoothDevice> pairedDevicesList;
    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_charger);

        btnConnectBluetooth = findViewById(R.id.btnConnectBluetooth);
        btnConnectWifi = findViewById(R.id.btnConnectWifi);
        btnChargers = findViewById(R.id.btnChargers);
        btnAdd = findViewById(R.id.btnAdd);
        btnProfile = findViewById(R.id.btnProfile);

        pairedDevicesList = new ArrayList<>();

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        btnConnectBluetooth.setOnClickListener(v -> checkBluetoothPermission());
        btnConnectWifi.setOnClickListener(v -> checkWifiPermission());

        btnChargers.setOnClickListener(v -> {
            Intent intent = new Intent(AddChargerActivity.this, chargesPage.class);
            startActivity(intent);
        });

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(AddChargerActivity.this, AddChargerActivity.class);
            startActivity(intent);
        });

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(AddChargerActivity.this, Profile_Ativity.class);
            startActivity(intent);
        });
    }

    private String getDeviceName(BluetoothDevice device) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            return device.getName();
        } else {
            return "Dispositivo";
        }
    }

    private void checkBluetoothPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSION);
        } else {
            listPairedDevices();
        }
    }

    private void checkWifiPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_WIFI_PERMISSION);
        } else {
            listAvailableChargersOnWifi();
        }
    }

    @SuppressLint("MissingPermission")
    private void listPairedDevices() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth não suportado", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Ative o Bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            pairedDevicesList.clear();
            ArrayList<String> deviceNamesList = new ArrayList<>();
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesList.add(device);
                deviceNamesList.add(device.getName() + "\n" + device.getAddress());
            }
            showPairedDevicesDialog(deviceNamesList);
        } else {
            Toast.makeText(this, "Nenhum dispositivo emparelhado encontrado", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void listAvailableChargersOnWifi() {
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "Ative o Wi-Fi", Toast.LENGTH_SHORT).show();
            return;
        }

        // Enviar uma mensagem de broadcast para descobrir carregadores na rede atual
        new Thread(() -> {
            try {
                DatagramSocket socket = new DatagramSocket();
                socket.setBroadcast(true);
                byte[] sendData = "DISCOVER_CHARGER_REQUEST".getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, getBroadcastAddress(), 8888);
                socket.send(sendPacket);

                // Esperar pela resposta dos carregadores
                byte[] recvBuf = new byte[15000];
                DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(receivePacket);

                String response = new String(receivePacket.getData()).trim();
                if (response.equals("DISCOVER_CHARGER_RESPONSE")) {
                    String chargerIp = receivePacket.getAddress().getHostAddress();
                    runOnUiThread(() -> showWifiDialog(chargerIp));
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Nenhum carregador encontrado na rede", Toast.LENGTH_SHORT).show());
                }

                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "Erro ao procurar carregadores via Wi-Fi", e);
                runOnUiThread(() -> Toast.makeText(this, "Erro ao procurar carregadores via Wi-Fi", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private InetAddress getBroadcastAddress() throws IOException {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String broadcastAddress = (ipAddress & 0xff) + "." + ((ipAddress >> 8) & 0xff) + "." + ((ipAddress >> 16) & 0xff) + ".255";
        return InetAddress.getByName(broadcastAddress);
    }

    private void showPairedDevicesDialog(ArrayList<String> deviceNamesList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Dispositivos Emparelhados");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceNamesList);
        builder.setAdapter(adapter, (dialog, which) -> {
            BluetoothDevice device = pairedDevicesList.get(which);
            showBluetoothDialog(device);
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void showBluetoothDialog(BluetoothDevice device) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Conectar via Bluetooth")
                .setMessage("Deseja conectar-se ao carregador " + getDeviceName(device) + " via Bluetooth?")
                .setPositiveButton("Conectar", (dialog, which) -> connectToChargerBluetooth(device))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showWifiDialog(String chargerIp) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Conectar via Wi-Fi")
                .setMessage("Deseja conectar-se ao carregador com IP " + chargerIp + "?")
                .setPositiveButton("Conectar", (dialog, which) -> connectToChargerWifi(chargerIp, 12345))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @SuppressLint("MissingPermission")
    private void connectToChargerBluetooth(BluetoothDevice device) {
        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            bluetoothSocket.connect();
            inputStream = bluetoothSocket.getInputStream();
            outputStream = bluetoothSocket.getOutputStream();
            Toast.makeText(this, "Conectado ao carregador via Bluetooth", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e(TAG, "Erro ao conectar", e);
            Toast.makeText(this, "Erro ao conectar", Toast.LENGTH_SHORT).show();
        }
    }

    private void connectToChargerWifi(String ipAddress, int port) {
        new Thread(() -> {
            try {
                Socket socket = new Socket(ipAddress, port);
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                runOnUiThread(() -> Toast.makeText(AddChargerActivity.this, "Conectado ao carregador via Wi-Fi", Toast.LENGTH_SHORT).show());
                // Salvar os streams para uso posterior
            } catch (IOException e) {
                Log.e(TAG, "Erro ao conectar via Wi-Fi", e);
                runOnUiThread(() -> Toast.makeText(AddChargerActivity.this, "Erro ao conectar via Wi-Fi", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                listPairedDevices();
            } else {
                Toast.makeText(this, "Permissão de Bluetooth negada", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_WIFI_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                listAvailableChargersOnWifi();
            } else {
                Toast.makeText(this, "Permissão de Wi-Fi negada", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
