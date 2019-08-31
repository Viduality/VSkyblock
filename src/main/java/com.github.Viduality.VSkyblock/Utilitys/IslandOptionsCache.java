package com.github.Viduality.VSkyblock.Utilitys;

public class IslandOptionsCache {


    private boolean visit = true;
    private String difficulty = "normal";

    public void setVisit(boolean visit) {
        this.visit = visit;
    }

    public boolean getVisit() {
        return visit;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getDifficulty() {
        return difficulty;
    }
}
