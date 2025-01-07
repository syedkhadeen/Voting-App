package com.example.onlinevotingsystem;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CandidateAdapter extends ArrayAdapter<Candidate> {
    private List<Candidate> candidateList;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private int currentVoterId;
    private int currentElectionId;

    public CandidateAdapter(Context context, List<Candidate> candidates, DatabaseHelper dbHelper, int currentVoterId, int currentElectionId, SharedPreferences sharedPreferences) {
        super(context, 0, candidates);
        this.candidateList = candidates;
        this.dbHelper = dbHelper;
        this.currentVoterId = currentVoterId;
        this.currentElectionId = currentElectionId;
        this.sharedPreferences = sharedPreferences;
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
        String voteKey = "Voted_" + currentVoterId + "_" + currentElectionId;
        boolean alreadyVoted = sharedPreferences.getBoolean(voteKey, false);
        voteButton.setEnabled(!alreadyVoted);

        voteButton.setOnClickListener(v -> {
            if (!alreadyVoted) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(voteKey, true);
                editor.apply();
                dbHelper.recordVote(currentVoterId, currentElectionId, candidate.getId());
                dbHelper.incrementVoteCount(candidate.getId());
                notifyDataSetChanged(); // Refresh the list to disable all vote buttons
                Toast.makeText(getContext(), "You voted for " + candidate.getName(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "You have already voted in this election", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
