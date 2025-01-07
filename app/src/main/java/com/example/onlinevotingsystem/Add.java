package com.example.onlinevotingsystem;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Add extends AppCompatActivity {

    private EditText editTextCandidateName;
    private SharedPreferences sharedPreferences;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // Initialize views
        editTextCandidateName = findViewById(R.id.editTextCandidateName);
        Button buttonAddCandidate = findViewById(R.id.buttonAddCandidate);

        // Initialize Database Helper
        databaseHelper = new DatabaseHelper(this);

        // Access shared preferences to check for the active election
        sharedPreferences = getSharedPreferences("ElectionPrefs", Context.MODE_PRIVATE);

        // Set click listener for the Add Candidate button
        buttonAddCandidate.setOnClickListener(v -> addCandidate());

        // Set click listener for the Back button to return to previous activity
        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());
    }

    // In your Add activity
    private void addCandidate() {
        String candidateName = editTextCandidateName.getText().toString().trim();
        int currentElectionId = sharedPreferences.getInt("CurrentElectionId", -1);

        if (candidateName.isEmpty()) {
            Toast.makeText(this, "Please enter candidate name", Toast.LENGTH_SHORT).show();
        } else if (currentElectionId == -1) {
            Toast.makeText(this, "No active election. Please add an election first.", Toast.LENGTH_LONG).show();
        } else {
            databaseHelper.addCandidate(candidateName, currentElectionId);
            Toast.makeText(this, "Candidate added: " + candidateName, Toast.LENGTH_SHORT).show();
            editTextCandidateName.setText(""); // Clear the EditText after adding candidate
        }
    }

}