package com.example.onlinevotingsystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "votingSystem.db";
    private static final int DATABASE_VERSION = 2;

    // Table names
    private static final String TABLE_ELECTIONS = "elections";
    private static final String TABLE_CANDIDATES = "candidates";
    private static final String TABLE_VOTERS = "voters";
    private static final String TABLE_VOTES = "voter_elections";  // Corrected table name

    // Common columns
    private static final String COLUMN_ID = "id";

    // Elections columns
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_END_DATE = "end_date";

    // Candidates columns
    private static final String COLUMN_CANDIDATE_ID = "candidate_id";
    private static final String COLUMN_CANDIDATE_NAME = "candidate_name";
    private static final String COLUMN_ELECTION_ID = "election_id";
    private static final String COLUMN_VOTES_COUNT = "votes_count";  // To be consistent with its usage

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ELECTIONS_TABLE = "CREATE TABLE " + TABLE_ELECTIONS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_END_DATE + " TEXT)";

        String CREATE_CANDIDATES_TABLE = "CREATE TABLE " + TABLE_CANDIDATES + " (" +
                COLUMN_CANDIDATE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CANDIDATE_NAME + " TEXT, " +
                COLUMN_ELECTION_ID + " INTEGER, " +
                COLUMN_VOTES_COUNT + " INTEGER DEFAULT 0, " +
                "FOREIGN KEY(" + COLUMN_ELECTION_ID + ") REFERENCES " + TABLE_ELECTIONS + "(" + COLUMN_ID + "))";

        String CREATE_VOTERS_TABLE = "CREATE TABLE " + TABLE_VOTERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT UNIQUE, " +
                "password TEXT)";

        String CREATE_VOTES_TABLE = "CREATE TABLE " + TABLE_VOTES + " (" +
                "voter_id INTEGER, " +
                "election_id INTEGER, " +
                "candidate_id INTEGER, " +
                "has_voted BOOLEAN DEFAULT 0, " +
                "PRIMARY KEY (voter_id, election_id), " +
                "FOREIGN KEY (voter_id) REFERENCES " + TABLE_VOTERS + "(id), " +
                "FOREIGN KEY (election_id) REFERENCES " + TABLE_ELECTIONS + "(id), " +
                "FOREIGN KEY (candidate_id) REFERENCES " + TABLE_CANDIDATES + "(id))";

        db.execSQL(CREATE_ELECTIONS_TABLE);
        db.execSQL(CREATE_CANDIDATES_TABLE);
        db.execSQL(CREATE_VOTERS_TABLE);
        db.execSQL(CREATE_VOTES_TABLE);
    }
    public void castVote(int voterId, int electionId, int candidateId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("voter_id", voterId);
        values.put("election_id", electionId);
        values.put("candidate_id", candidateId);
        db.insert(TABLE_VOTES, null, values);
        incrementCandidateVotes(candidateId);
        db.close();
    }
    public void incrementVoteCountAsync(int candidateId) {
        new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... params) {
                incrementVoteCount(params[0]);
                return null;
            }
        }.execute(candidateId);
    }
    private void incrementCandidateVotes(int candidateId) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_CANDIDATES + " SET votes_count = votes_count + 1 WHERE id = ?", new Object[]{candidateId});
        db.close();
    }
    void incrementVoteCount(int candidateId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_CANDIDATES + " SET votes_count = votes_count + 1 WHERE candidate_id = ?", new Object[]{candidateId});
        db.close();
    }
    private int getVoteCount(int candidateId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("candidates", new String[]{"votes_count"}, "candidate_id = ?", new String[]{String.valueOf(candidateId)}, null, null, null);
        if (cursor.moveToFirst()) {
            int votes = cursor.getInt(0);
            cursor.close();
            db.close();
            return votes;
        }
        cursor.close();
        db.close();
        return 0;  // Default to zero if not found
    }

    public void registerVoter(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("password", password);
        db.insert("voters", null, values);
        db.close();
    }public boolean checkVoterCredentials(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT id FROM voters WHERE email = ? AND password = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});
        boolean isLoggedIn = cursor.moveToFirst();
        cursor.close();
        db.close();
        return isLoggedIn;
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Create voter_elections table
            String CREATE_VOTES_TABLE = "CREATE TABLE voter_elections (" +
                    "voter_id INTEGER," +
                    "election_id INTEGER," +
                    "candidate_id INTEGER," +
                    "has_voted BOOLEAN DEFAULT 0," +
                    "PRIMARY KEY (voter_id, election_id)," +
                    "FOREIGN KEY (voter_id) REFERENCES voters(id)," +
                    "FOREIGN KEY (election_id) REFERENCES elections(id)," +
                    "FOREIGN KEY (candidate_id) REFERENCES candidates(id))";
            db.execSQL(CREATE_VOTES_TABLE);
        }
    }

    public long addElection(String name, String endDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_END_DATE, endDate);
        long result = db.insert(TABLE_ELECTIONS, null, values);
        db.close();
        return result;
    }
    public List<Candidate> getCandidatesForElection(String electionName) {
        List<Candidate> candidates = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT c.candidate_id, c.candidate_name FROM candidates c " +
                "JOIN elections e ON c.election_id = e.id " +
                "WHERE e.name = ?";

        Cursor cursor = db.rawQuery(query, new String[]{electionName});

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("candidate_id");
            int nameIndex = cursor.getColumnIndex("candidate_name");

            if (idIndex == -1 || nameIndex == -1) {
                // Log an error or handle the case where column indices are not found
                Log.e("DatabaseHelper", "Column not found in cursor");
                return candidates;  // Return empty or handle appropriately
            }

            do {
                int id = cursor.getInt(idIndex);
                String name = cursor.getString(nameIndex);
                candidates.add(new Candidate(id, name)); // Ensure you have a suitable constructor in Candidate
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return candidates;
    }


    public void addCandidate(String candidateName, int electionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CANDIDATE_NAME, candidateName);
        values.put(COLUMN_ELECTION_ID, electionId);
        long result = db.insert(TABLE_CANDIDATES, null, values);
        if (result == -1) {
            Log.e("DBHelper", "Failed to insert candidate");
        } else {
            Log.d("DBHelper", "Candidate inserted successfully");
        }
        db.close();
    }


    public List<String> getActiveElections() {
        List<String> elections = new ArrayList<>();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ELECTIONS, new String[]{COLUMN_NAME},
                COLUMN_END_DATE + " > ?", new String[]{currentDate}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                elections.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return elections;
    }public boolean hasVoted(int voterId, int electionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean hasVoted = false;
        try {
            Cursor cursor = db.query(
                    TABLE_VOTES, // The table to query
                    new String[] {"has_voted"}, // The columns to return
                    "voter_id = ? AND election_id = ?", // The columns for the WHERE clause
                    new String[] {String.valueOf(voterId), String.valueOf(electionId)}, // The values for the WHERE clause
                    null, // group by
                    null, // having
                    null  // order by
            );
            if (cursor.moveToFirst()) {
                hasVoted = cursor.getInt(cursor.getColumnIndexOrThrow("has_voted")) == 1;
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error checking if voted", e);
        } finally {
            db.close();
        }
        return hasVoted;
    }

    public void recordVote(int voterId, int electionId, int candidateId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("voter_id", voterId);
        values.put("election_id", electionId);
        values.put("candidate_id", candidateId);
        values.put("has_voted", 1);
        db.insert("voter_elections", null, values);
        db.close();
    }

    public boolean loginVoter(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT id FROM voters WHERE email =? AND password =?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});
        boolean isLoggedIn = cursor.moveToFirst();
        cursor.close();
        db.close();
        return isLoggedIn;
    }

    public void deleteExpiredElections() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(TABLE_ELECTIONS, COLUMN_END_DATE + " <= ?", new String[]{currentDate});
        Log.d("DBHelper", "Expired elections deleted: " + deletedRows);
        db.close();
    }
}