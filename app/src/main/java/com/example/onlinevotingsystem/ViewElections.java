package com.example.onlinevotingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ViewElections extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        dbHelper = new DatabaseHelper(this);

        // Fetch the list of active elections
        List<String> activeElections = dbHelper.getActiveElections();

        // Setup the ListView to display the active elections
        ListView listView = findViewById(R.id.list_view_elections);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, activeElections);
        listView.setAdapter(adapter);

        // Set an item click listener on the ListView to handle selection
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedElection = adapter.getItem(position);
            // Start VoteActivity and pass the selected election name to it
            Intent intent = new Intent(ViewElections.this, Vote.class);
            intent.putExtra("electionName", selectedElection);
            // If you need to pass the election ID or other details, make sure to modify your model and fetch them here
            // Example: intent.putExtra("electionId", selectedElectionId);
            startActivity(intent);
        });

        // Setup the back button to close the current activity
        Button backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> finish());
    }
}
