package com.leetcode.dashboard.model;

public class LanguageProgress {
    private String languageName;
    private int problemsSolved;
    private int target;
    private int percentage;

    public LanguageProgress() {
    }

    public LanguageProgress(String languageName, int problemsSolved, int target) {
        this.languageName = languageName;
        this.problemsSolved = problemsSolved;
        this.target = target;
        this.percentage = calculatePercentage(problemsSolved, target);
    }

    private int calculatePercentage(int solved, int target) {
        if (target <= 0) return 100;
        int p = (int) ((double) solved / target * 100);
        return Math.min(p, 100); // Cap at 100%
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public int getProblemsSolved() {
        return problemsSolved;
    }

    public void setProblemsSolved(int problemsSolved) {
        this.problemsSolved = problemsSolved;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
        this.percentage = calculatePercentage(this.problemsSolved, target);
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }
}
