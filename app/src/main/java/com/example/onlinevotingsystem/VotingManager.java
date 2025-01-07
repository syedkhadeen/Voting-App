package com.example.onlinevotingsystem;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class VotingManager {

    private final DatabaseHelper dbHelper;
    private final SharedPreferences sharedPreferences;
    private final Context context;

    public VotingManager(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
        this.sharedPreferences = context.getSharedPreferences("VotingPrefs", Context.MODE_PRIVATE);
    }

    public boolean hasVoted(int voterId, int electionId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM voter_elections WHERE voter_id = ? AND election_id = ?", new String[]{String.valueOf(voterId), String.valueOf(electionId)});
        boolean hasVoted = cursor.moveToFirst();
        cursor.close();
        db.close();
        return hasVoted;
    }

    public void recordVote(int voterId, int electionId, int candidateId) {
        if (!hasVoted(voterId, electionId)) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL("INSERT INTO voter_elections (voter_id, election_id, candidate_id, has_voted) VALUES (?, ?, ?, 1)", new Object[]{voterId, electionId, candidateId});
            dbHelper.incrementVoteCountAsync(candidateId); // Asynchronously increment votes count
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("Voted_" + electionId, true);
            editor.apply();
            db.close();
        }
    }

    public int getVoterIdByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM voters WHERE email = ?", new String[]{email});
        int voterId = -1;
        if (cursor.moveToFirst()) {
            voterId = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return voterId;
    }
}
