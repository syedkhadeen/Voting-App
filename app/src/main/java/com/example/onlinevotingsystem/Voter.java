package com.example.onlinevotingsystem;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Voter extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText ageEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voter);

        emailEditText = findViewById(R.id.editTextText2);
        passwordEditText = findViewById(R.id.editTextTextPassword2);
        ageEditText = findViewById(R.id.editTextNumber);

        Button loginButton = findViewById(R.id.button5);
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            int age = 0;
            try {
                age = Integer.parseInt(ageEditText.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check against admin email
            String adminEmail = CredentialManager.getAdminEmail(this);
            if (email.equals(adminEmail)) {
                Toast.makeText(this, "This email is reserved for admin use. Please use a different email.", Toast.LENGTH_LONG).show();
                return; // Prevent login if the email matches the admin's email
            }

            if (isEmailValid(email) && isPasswordValid(password) && isAgeValid(age)) {
                startActivity(new Intent(Voter.this, Voter1.class)); // Replace VoterPanel with your voter activity
                finish();
            } else {
                // Display error messages based on the validation results
                if (!isEmailValid(email)) {
                    Toast.makeText(Voter.this, "Invalid email", Toast.LENGTH_SHORT).show();
                }
                if (!isPasswordValid(password)) {
                    Toast.makeText(Voter.this, "Invalid password", Toast.LENGTH_SHORT).show();
                }
                if (!isAgeValid(age)) {
                    Toast.makeText(Voter.this, "Underage", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button backButton = findViewById(R.id.button6);
        backButton.setOnClickListener(v -> finish());
    }

    private boolean isEmailValid(String email) {
        return email.endsWith("@gmail.com") && email.length() >= 15;
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 8;
    }

    private boolean isAgeValid(int age) {
        return age >= 18;
    }
}

