package com.example.influencify;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI elements
        EditText emailEditText = findViewById(R.id.et_login_email);
        EditText passwordEditText = findViewById(R.id.et_login_password);
        Button continueButton = findViewById(R.id.btn_login_continue);

        // Set click listener for Continue button
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Logging in with: " + email, Toast.LENGTH_SHORT).show();
                    // Add login logic here (e.g., authenticate with a server or proceed to a home screen)
                }
            }
        });
    }
}