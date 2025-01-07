package com.example.onlinevotingsystem;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Election extends AppCompatActivity {

    private EditText electionNameEditText, endDateEditText;
    private Button addElectionButton, backButton;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avtivity_elections);  // Make sure the layout name is correctly spelled.

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("ElectionPrefs", Context.MODE_PRIVATE);

        electionNameEditText = findViewById(R.id.editTextName);
        endDateEditText = findViewById(R.id.editTextDate);
        addElectionButton = findViewById(R.id.buttonSubmit);
        backButton = findViewById(R.id.buttonBack);

        addElectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String electionName = electionNameEditText.getText().toString().trim();
                String endDate = endDateEditText.getText().toString().trim();
                if (!electionName.isEmpty() && !endDate.isEmpty()) {
                    addElection(electionName, endDate);
                } else {
                    Toast.makeText(Election.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void addElection(String name, String endDate) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("end_date", endDate);
        long result = db.insert("elections", null, values);
        db.close();
        if (result == -1) {
            Toast.makeText(this, "Failed to add election", Toast.LENGTH_LONG).show();
        } else {
            // Store the current election ID in SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("CurrentElectionId", (int) result);
            editor.apply();

            Toast.makeText(this, "Election added successfully", Toast.LENGTH_SHORT).show();
            electionNameEditText.setText("");
            endDateEditText.setText("");
        }
    }
}
