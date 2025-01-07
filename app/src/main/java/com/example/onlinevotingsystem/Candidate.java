package com.example.onlinevotingsystem;

public class Candidate {
    public int getId;
    private final int id;
    private String name;
    private boolean isVoted;  // this should be the field to track if voted

    public Candidate(int id, String name) {
        this.id = id;
        this.name = name;
        this.isVoted = false;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean hasVoted() {
        return isVoted;
    }

    public void setVoted(boolean voted) {
        isVoted = voted;
    }
}
