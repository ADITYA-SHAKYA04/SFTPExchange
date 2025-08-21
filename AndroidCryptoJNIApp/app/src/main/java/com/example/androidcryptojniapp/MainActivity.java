package com.example.androidcryptojniapp;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText inputText;
    private Spinner algorithmSpinner;
    private Button encryptButton, decryptButton;
    private TextView resultText;
    private String lastCiphertext = null;
    private String lastAlgorithm = null;

    static {
        System.loadLibrary("crypto_jni");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputText = findViewById(R.id.inputText);
        algorithmSpinner = findViewById(R.id.algorithmSpinner);
        encryptButton = findViewById(R.id.encryptButton);
        decryptButton = findViewById(R.id.decryptButton);
        resultText = findViewById(R.id.resultText);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.algorithms_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        algorithmSpinner.setAdapter(adapter);

        encryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String plain = inputText.getText().toString();
                String algo = algorithmSpinner.getSelectedItem().toString();
                try {
                    String cipher = encryptNative(algo, plain);
                    lastCiphertext = cipher;
                    lastAlgorithm = algo;
                    resultText.setText(cipher);
                } catch (Exception e) {
                    resultText.setText("Error: " + e.getMessage());
                }
            }
        });
        decryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastCiphertext == null || lastAlgorithm == null) {
                    resultText.setText("No ciphertext to decrypt");
                    return;
                }
                try {
                    String plain = decryptNative(lastAlgorithm, lastCiphertext);
                    resultText.setText(plain);
                } catch (Exception e) {
                    resultText.setText("Error: " + e.getMessage());
                }
            }
        });
    }

    public native String encryptNative(String algorithm, String plainText);
    public native String decryptNative(String algorithm, String cipherText);
}
