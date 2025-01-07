package com.example.onlinevotingsystem;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Admin extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        emailEditText = findViewById(R.id.editTextText);
        passwordEditText = findViewById(R.id.editTextTextPassword);

        Button loginButton = findViewById(R.id.button4);
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String savedEmail = CredentialManager.getAdminEmail(this);

            if (savedEmail != null && !savedEmail.equals(email)) {
                Toast.makeText(this, "Incorrect email. Please enter the original admin email.", Toast.LENGTH_LONG).show();
            } else {
                // Save the email if it's not already set
                if (savedEmail == null) {
                    CredentialManager.saveAdminEmail(this, email);
                }
                // Proceed with any other checks or the admin panel
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Admin.this, Admin1.class));  // Assuming there's a class named AdminPanel
                finish();
            }
        });

        Button backButton = findViewById(R.id.button6);
        backButton.setOnClickListener(v -> finish());
    }
}


