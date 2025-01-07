package com.example.onlinevotingsystem;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class Vote extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private ListView listView;
    private SharedPreferences sharedPreferences;
    private String electionName;
    private int currentVoterId;
    private int currentElectionId;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        initialize();
        loadCandidates();
    }

    private void initialize() {
        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("VotingPrefs", MODE_PRIVATE);
        electionName = getIntent().getStringExtra("electionName");
        currentVoterId = getIntent().getIntExtra("voterId", -1);
        currentElectionId = getIntent().getIntExtra("electionId", -1); // Ensure electionId is also passed and handled

        if (electionName == null) {
            Toast.makeText(this, "Election not specified!", Toast.LENGTH_LONG).show();
            finish();
        }

        listView = findViewById(R.id.listViewCandidates);
        back = findViewById(R.id.button31);
        back.setOnClickListener(v -> finish());
    }

    private void loadCandidates() {
        List<Candidate> candidates = dbHelper.getCandidatesForElection(electionName);
        if (candidates.isEmpty()) {
            Toast.makeText(this, "No candidates available for this election.", Toast.LENGTH_LONG).show();
            return;
        }

        CandidateAdapter adapter = new CandidateAdapter(this, candidates, dbHelper, currentVoterId, currentElectionId);
        listView.setAdapter(adapter);
    }

    private class CandidateAdapter extends ArrayAdapter<Candidate> {
        private List<Candidate> candidateList;
        private DatabaseHelper dbHelper;
        private int currentVoterId;
        private int currentElectionId;

        CandidateAdapter(Context context, List<Candidate> candidates, DatabaseHelper dbHelper, int currentVoterId, int currentElectionId) {
            super(context, 0, candidates);
            this.candidateList = candidates;
            this.dbHelper = dbHelper;
            this.currentVoterId = currentVoterId;
            this.currentElectionId = currentElectionId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_candidate_adapter, parent, false);
            }

            Candidate candidate = getItem(position);
            TextView name = convertView.findViewById(R.id.textViewName);
            Button voteButton = convertView.findViewById(R.id.buttonVote);

            name.setText(candidate.getName());
            updateVoteButton(voteButton, candidate);

            return convertView;
        }

        private void updateVoteButton(Button voteButton, Candidate candidate) {
            boolean alreadyVoted = sharedPreferences.getBoolean("Voted" + currentElectionId, false);
            voteButton.setEnabled(!alreadyVoted);

            voteButton.setOnClickListener(v -> {
                if (!alreadyVoted) {
                    sharedPreferences.edit().putBoolean("Voted" + currentElectionId, true).apply();
                    dbHelper.recordVote(currentVoterId, currentElectionId, candidate.getId());
                    dbHelper.incrementVoteCount(candidate.getId());
                    notifyDataSetInvalidated(); // Refresh the list to disable all vote buttons
                    Toast.makeText(getContext(), "You voted for " + candidate.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "You have already voted in this election", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
