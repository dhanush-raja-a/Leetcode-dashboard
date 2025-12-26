package com.leetcode.dashboard.model;

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
    private List<LanguageStat> languageStats;

    @Data
    public static class LanguageStat {
        private String languageName;
        private int problemsSolved;
    }
    
    // Helper method to populate from GraphQL response if needed, 
    // but we might just map it directly in the service.
}
