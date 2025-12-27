package com.leetcode.api.model;

public class LanguageProgress {
    private String languageName;
    private int problemsSolved;
    private int target;
    private int percentage;
    
    private int easySolved;
    private int mediumSolved;
    private int hardSolved;
    
    private int easyTarget;
    private int mediumTarget;
    private int hardTarget;
    
    private int easyPercentage;
    private int mediumPercentage;
    private int hardPercentage;

    public LanguageProgress() {
    }

    public LanguageProgress(String languageName, int problemsSolved, int target, 
                          int easySolved, int mediumSolved, int hardSolved,
                          int easyTarget, int mediumTarget, int hardTarget) {
        this.languageName = languageName;
        this.problemsSolved = problemsSolved;
        this.target = target;
        this.percentage = calculatePercentage(problemsSolved, target);
        
        this.easySolved = easySolved;
        this.mediumSolved = mediumSolved;
        this.hardSolved = hardSolved;
        
        this.easyTarget = easyTarget;
        this.mediumTarget = mediumTarget;
        this.hardTarget = hardTarget;
        
        this.easyPercentage = calculatePercentage(easySolved, easyTarget);
        this.mediumPercentage = calculatePercentage(mediumSolved, mediumTarget);
        this.hardPercentage = calculatePercentage(hardSolved, hardTarget);
    }

    private int calculatePercentage(int solved, int target) {
        if (target <= 0) return 100;
        int p = (int) ((double) solved / target * 100);
        return Math.min(p, 100);
    }

    public String getLanguageName() { return languageName; }
    public void setLanguageName(String languageName) { this.languageName = languageName; }
    public int getProblemsSolved() { return problemsSolved; }
    public void setProblemsSolved(int problemsSolved) { this.problemsSolved = problemsSolved; }
    public int getTarget() { return target; }
    public void setTarget(int target) { 
        this.target = target; 
        this.percentage = calculatePercentage(this.problemsSolved, target);
    }
    public int getPercentage() { return percentage; }
    public void setPercentage(int percentage) { this.percentage = percentage; }
    public int getEasySolved() { return easySolved; }
    public void setEasySolved(int easySolved) { this.easySolved = easySolved; }
    public int getMediumSolved() { return mediumSolved; }
    public void setMediumSolved(int mediumSolved) { this.mediumSolved = mediumSolved; }
    public int getHardSolved() { return hardSolved; }
    public void setHardSolved(int hardSolved) { this.hardSolved = hardSolved; }
    public int getEasyTarget() { return easyTarget; }
    public void setEasyTarget(int easyTarget) { 
        this.easyTarget = easyTarget;
        this.easyPercentage = calculatePercentage(this.easySolved, easyTarget);
    }
    public int getMediumTarget() { return mediumTarget; }
    public void setMediumTarget(int mediumTarget) { 
        this.mediumTarget = mediumTarget;
        this.mediumPercentage = calculatePercentage(this.mediumSolved, mediumTarget);
    }
    public int getHardTarget() { return hardTarget; }
    public void setHardTarget(int hardTarget) { 
        this.hardTarget = hardTarget;
        this.hardPercentage = calculatePercentage(this.hardSolved, hardTarget);
    }
    public int getEasyPercentage() { return easyPercentage; }
    public int getMediumPercentage() { return mediumPercentage; }
    public int getHardPercentage() { return hardPercentage; }
}
