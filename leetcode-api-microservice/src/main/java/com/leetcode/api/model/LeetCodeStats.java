package com.leetcode.api.model;

import lombok.Data;
import java.util.List;

@Data
public class LeetCodeStats {
    private String status;
    private String message;
    private int totalSolved;
    private int easySolved;
    private int mediumSolved;
    private int hardSolved;
    
    private int totalTarget;
    private int easyTarget;
    private int mediumTarget;
    private int hardTarget;
    
    private int totalPercentage;
    private int easyPercentage;
    private int mediumPercentage;
    private int hardPercentage;
    
    private List<LanguageProgress> languageStats;
}
