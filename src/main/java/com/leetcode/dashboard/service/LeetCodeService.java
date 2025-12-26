package com.leetcode.dashboard.service;

import com.leetcode.dashboard.model.LeetCodeStats;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class LeetCodeService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String LEETCODE_API_URL = "https://leetcode.com/graphql";

    public LeetCodeStats getStats(String username) {
        String query = """
            query getUserProfile($username: String!) {
                matchedUser(username: $username) {
                    submitStats: submitStatsGlobal {
                        acSubmissionNum {
                            difficulty
                            count
                        }
                    }
                    languageProblemCount {
                        languageName
                        problemsSolved
                    }
                }
            }
        """;

        Map<String, Object> variables = new HashMap<>();
        variables.put("username", username);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);
        requestBody.put("variables", variables);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Referer", "https://leetcode.com"); // Sometimes needed

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(LEETCODE_API_URL, entity, Map.class);
            return parseResponse(response.getBody());
        } catch (Exception e) {
            LeetCodeStats stats = new LeetCodeStats();
            stats.setStatus("error");
            stats.setMessage("Failed to fetch data for user: " + username + ". Error: " + e.getMessage());
            return stats;
        }
    }

    private LeetCodeStats parseResponse(Map body) {
        LeetCodeStats stats = new LeetCodeStats();
        try {
            if (body == null || body.containsKey("errors")) {
                stats.setStatus("error");
                stats.setMessage("User not found or API error.");
                return stats;
            }

            Map data = (Map) body.get("data");
            Map matchedUser = (Map) data.get("matchedUser");
            
            if (matchedUser == null) {
                stats.setStatus("error");
                stats.setMessage("User not found.");
                return stats;
            }

            Map submitStats = (Map) matchedUser.get("submitStats");
            List<Map<String, Object>> acSubmissionNum = (List<Map<String, Object>>) submitStats.get("acSubmissionNum");

            for (Map<String, Object> entry : acSubmissionNum) {
                String difficulty = (String) entry.get("difficulty");
                int count = (int) entry.get("count");

                switch (difficulty) {
                    case "All" -> stats.setTotalSolved(count);
                    case "Easy" -> stats.setEasySolved(count);
                    case "Medium" -> stats.setMediumSolved(count);
                    case "Hard" -> stats.setHardSolved(count);
                }
            }

            List<Map<String, Object>> languageProblemCount = (List<Map<String, Object>>) matchedUser.get("languageProblemCount");
            if (languageProblemCount != null) {
                java.util.List<LeetCodeStats.LanguageStat> langStats = new java.util.ArrayList<>();
                for (Map<String, Object> entry : languageProblemCount) {
                    LeetCodeStats.LanguageStat stat = new LeetCodeStats.LanguageStat();
                    stat.setLanguageName((String) entry.get("languageName"));
                    stat.setProblemsSolved((int) entry.get("problemsSolved"));
                    langStats.add(stat);
                }
                // Sort by problems solved descending
                langStats.sort((a, b) -> b.getProblemsSolved() - a.getProblemsSolved());
                stats.setLanguageStats(langStats);
            }

            stats.setStatus("success");
        } catch (Exception e) {
            stats.setStatus("error");
            stats.setMessage("Error parsing response: " + e.getMessage());
        }
        return stats;
    }
}
