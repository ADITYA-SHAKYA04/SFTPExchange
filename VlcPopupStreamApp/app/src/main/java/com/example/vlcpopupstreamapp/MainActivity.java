package com.example.vlcpopupstreamapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.io.*;
import java.net.*;

public class MainActivity extends AppCompatActivity {
    private EditText urlInput;
    private Button streamButton, downloadButton;
    private String selectedUrl = null;
    private ActivityResultLauncher<Intent> folderPickerLauncher;
    private Uri downloadUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        urlInput = findViewById(R.id.urlInput);
        streamButton = findViewById(R.id.streamButton);
        downloadButton = findViewById(R.id.downloadButton);
        streamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedUrl = urlInput.getText().toString();
                if (selectedUrl != null && !selectedUrl.isEmpty()) {
                    playStreamPopup(selectedUrl);
                } else {
                    Toast.makeText(MainActivity.this, "Enter a stream URL", Toast.LENGTH_SHORT).show();
                }
            }
        });
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedUrl = urlInput.getText().toString();
                if (selectedUrl != null && !selectedUrl.isEmpty()) {
                    pickDownloadLocation();
                } else {
                    Toast.makeText(MainActivity.this, "Enter a stream URL", Toast.LENGTH_SHORT).show();
                }
            }
        });
        folderPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        downloadUri = result.getData().getData();
                        if (downloadUri != null && selectedUrl != null) {
                            downloadFile(selectedUrl, downloadUri);
                        }
                    }
                });
    }

    private void playStreamPopup(String url) {
        VlcPopupPlayer popup = new VlcPopupPlayer(this, url);
        popup.show();
    }

    private void pickDownloadLocation() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        folderPickerLauncher.launch(intent);
    }

    private void downloadFile(String url, Uri folderUri) {
        new Thread(() -> {
            try {
                URL u = new URL(url);
                InputStream in = u.openStream();
                String fileName = url.substring(url.lastIndexOf('/') + 1);
                File dir = new File(Environment.getExternalStorageDirectory(), "VlcPopupDownloads");
                if (!dir.exists()) dir.mkdirs();
                File out = new File(dir, fileName);
                FileOutputStream fos = new FileOutputStream(out);
                byte[] buf = new byte[4096];
                int len;
                while ((len = in.read(buf)) > 0) fos.write(buf, 0, len);
                fos.close();
                in.close();
                runOnUiThread(() -> Toast.makeText(this, "Downloaded: " + out.getAbsolutePath(), Toast.LENGTH_LONG).show());
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Download failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}
