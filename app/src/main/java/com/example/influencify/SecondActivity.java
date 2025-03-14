package com.example.influencify;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // Initialize UI elements
        EditText passwordEditText = findViewById(R.id.et_password);
        EditText confirmPasswordEditText = findViewById(R.id.et_confirm_password);
        Button continueButton = findViewById(R.id.btn_continue_second);

        // Set click listener for Continue button
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                if (password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(SecondActivity.this, "Please fill in both password fields", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(SecondActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SecondActivity.this, "Passwords match! Proceeding...", Toast.LENGTH_SHORT).show();
                    // Add logic here to proceed (e.g., save password and navigate to another screen)
                }
            }
        });
    }
}