package com.example.onlinevotingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class Admin1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin1);

        Button addCandidateButton = findViewById(R.id.button7);
        Button viewVotersButton = findViewById(R.id.button8);
        Button viewResultsButton = findViewById(R.id.button9);
        Button addElectionsButton = findViewById(R.id.button13);
        Button logoutButton = findViewById(R.id.button12);

        addCandidateButton.setOnClickListener(v -> {
            // Handle add candidate button click, perform necessary actions
            startActivity(new Intent(Admin1.this, Add.class));
        });

        viewVotersButton.setOnClickListener(v -> {
            // Handle view voters button click, perform necessary actions
        });

        viewResultsButton.setOnClickListener(v -> {
            // Handle view results button click, perform necessary actions
        });

        addElectionsButton.setOnClickListener(v -> {
            // Move to Election activity when "Add Election" button is clicked
            startActivity(new Intent(Admin1.this, Election.class));
        });

        logoutButton.setOnClickListener(v -> {
            // Move to FirstScreen activity when logout button is pressed
            startActivity(new Intent(Admin1.this, FirstScreen.class));
            finish(); // Close the current activity
        });
    }
}
