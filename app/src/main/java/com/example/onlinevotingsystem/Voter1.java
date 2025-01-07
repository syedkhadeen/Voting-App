package com.example.onlinevotingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class Voter1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voter1);

        Button viewVotersButton = findViewById(R.id.button10);
        Button logoutButton = findViewById(R.id.button11);

        viewVotersButton.setOnClickListener(v -> {
            // Handle view voters button click, perform necessary actions
            startActivity(new Intent(Voter1.this, ViewElections.class));
        });

        logoutButton.setOnClickListener(v -> {
            // Move to SecondPage activity when logout button is pressed
            startActivity(new Intent(Voter1.this, FirstScreen.class));
            finish(); // Close the current activity
        });
    }
}
