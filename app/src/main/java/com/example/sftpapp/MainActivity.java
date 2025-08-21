
package com.example.sftpapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sftpapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {
    private TextInputEditText hostEditText, portEditText, usernameEditText, passwordEditText;
    private MaterialButton connectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // // For Android 11+ (API 30+), guide user to grant MANAGE_EXTERNAL_STORAGE
        // if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
        //     if (!android.os.Environment.isExternalStorageManager()) {
        //         new android.app.AlertDialog.Builder(this)
        //             .setTitle("Permission Required")
        //             .setMessage("To access all files, please grant 'All files access' in app settings. Tap 'Grant' to open settings.")
        //             .setPositiveButton("Grant", (dialog, which) -> {
        //                 try {
        //                     android.net.Uri uri = android.net.Uri.parse("package:" + getPackageName());
        //                     Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
        //                     startActivity(intent);
        //                 } catch (android.content.ActivityNotFoundException e) {
        //                     Toast.makeText(this, "Please grant 'All files access' manually in system settings > Apps > sftpapp.", Toast.LENGTH_LONG).show();
        //                 }
        //             })
        //             .setNegativeButton("Continue", null)
        //             .setCancelable(true)
        //             .show();
        //     }
        // }
        // // For Android 10 and below, request WRITE_EXTERNAL_STORAGE at runtime
        // if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.R) {
        //     String[] permissions = {
        //         android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        //     };
        //     for (String perm : permissions) {
        //         if (checkSelfPermission(perm) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
        //             requestPermissions(permissions, 201);
        //             break;
        //         }
        //     }
        // }

        hostEditText = findViewById(R.id.hostEditText);
        portEditText = findViewById(R.id.portEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        connectButton = findViewById(R.id.connectButton);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get SFTP credentials from input fields
                final String host = hostEditText.getText() != null ? hostEditText.getText().toString().trim() : "";
                int parsedPort = 22;
                try {
                    parsedPort = Integer.parseInt(portEditText.getText() != null ? portEditText.getText().toString().trim() : "22");
                } catch (NumberFormatException ignored) {}
                final int port = parsedPort;
                final String username = usernameEditText.getText() != null ? usernameEditText.getText().toString().trim() : "";
                final String password = passwordEditText.getText() != null ? passwordEditText.getText().toString() : "";

                connectButton.setEnabled(false);
                connectButton.setText("Connecting...");

                new Thread(() -> {
                    try {
                        com.jcraft.jsch.JSch jsch = new com.jcraft.jsch.JSch();
                        com.jcraft.jsch.Session session = jsch.getSession(username, host, port);
                        session.setPassword(password);
                        java.util.Properties config = new java.util.Properties();
                        config.put("StrictHostKeyChecking", "no");
                        session.setConfig(config);
                        session.connect(5000); // 5 seconds timeout
                        session.disconnect();
                        runOnUiThread(() -> {
                            connectButton.setEnabled(true);
                            connectButton.setText("Connect");
                            Intent intent = new Intent(MainActivity.this, BrowseActivity.class);
                            intent.putExtra("host", host);
                            intent.putExtra("port", port);
                            intent.putExtra("username", username);
                            intent.putExtra("password", password);
                            startActivity(intent);
                        });
                    } catch (Exception e) {
                        final StringBuilder errorBuilder = new StringBuilder();
                        errorBuilder.append(e.toString()).append("\n");
                        for (StackTraceElement el : e.getStackTrace()) {
                            errorBuilder.append(el.toString()).append("\n");
                        }
                        runOnUiThread(() -> {
                            connectButton.setEnabled(true);
                            connectButton.setText("Connect");
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Connection failed")
                                   .setPositiveButton("OK", null)
                                   .show();
                        });
                    }
                }).start();
            }
        });
    }
}
