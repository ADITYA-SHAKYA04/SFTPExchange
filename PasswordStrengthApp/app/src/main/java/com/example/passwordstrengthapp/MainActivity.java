package com.example.passwordstrengthapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText passwordInput;
    private TextView strengthText, suggestionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        passwordInput = findViewById(R.id.passwordInput);
        strengthText = findViewById(R.id.strengthText);
        suggestionText = findViewById(R.id.suggestionText);

        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateStrength(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void updateStrength(String password) {
        if (password.isEmpty()) {
            strengthText.setText("");
            suggestionText.setText("");
            return;
        }
        int score = 0;
        StringBuilder suggestion = new StringBuilder();
        if (password.length() >= 8) score++;
        else suggestion.append("Use at least 8 characters. ");
        if (password.matches(".*[A-Z].*")) score++;
        else suggestion.append("Add uppercase letters. ");
        if (password.matches(".*[a-z].*")) score++;
        else suggestion.append("Add lowercase letters. ");
        if (password.matches(".*[0-9].*")) score++;
        else suggestion.append("Add numbers. ");
        if (password.matches(".*[!@#$%^&*()_+=\-{}\[\]:;\"'<>,.?/].*")) score++;
        else suggestion.append("Add special characters. ");
        String strength;
        if (score <= 2) strength = "Weak";
        else if (score == 3 || score == 4) strength = "Medium";
        else strength = "Strong";
        strengthText.setText(strength);
        suggestionText.setText(suggestion.toString());
    }
}
