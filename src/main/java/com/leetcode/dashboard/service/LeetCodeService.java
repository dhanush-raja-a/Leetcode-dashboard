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
    private final TargetService targetService;

    public LeetCodeService(TargetService targetService) {
        this.targetService = targetService;
    }


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
                recentAcSubmissionList(username: $username, limit: 100) {
                    titleSlug
                    lang
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
        headers.set("Referer", "https://leetcode.com");
        headers.set("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(LEETCODE_API_URL, entity, Map.class);
            return parseResponse(response.getBody(), username);
        } catch (Exception e) {
            LeetCodeStats stats = new LeetCodeStats();
            stats.setStatus("error");
            stats.setMessage("Failed to fetch data for user: " + username + ". Error: " + e.getMessage());
            return stats;
        }
    }

    private LeetCodeStats parseResponse(Map body, String username) {
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

            // Parse Global Stats
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

            // Reconstruct Per-Language Difficulty Breakdown
            List<Map<String, Object>> recentSubmissions = (List<Map<String, Object>>) data.get("recentAcSubmissionList");
            Map<String, String> slugToDifficulty = new HashMap<>();
            Map<String, String> slugToLang = new HashMap<>();

            if (recentSubmissions != null) {
                java.util.List<String> uniqueSlugs = new java.util.ArrayList<>();
                for (Map<String, Object> sub : recentSubmissions) {
                    String slug = (String) sub.get("titleSlug");
                    String lang = (String) sub.get("lang");
                    if (slug != null && !slugToDifficulty.containsKey(slug)) {
                        uniqueSlugs.add(slug);
                        slugToDifficulty.put(slug, "Unknown");
                        slugToLang.put(slug, lang);
                    }
                }

                // Batch fetch difficulties
                if (!uniqueSlugs.isEmpty()) {
                    slugToDifficulty.putAll(fetchDifficulties(uniqueSlugs));
                }
            }

            // Count unique problems per lang/difficulty
            Map<String, java.util.Set<String>> seenEasy = new HashMap<>();
            Map<String, java.util.Set<String>> seenMedium = new HashMap<>();
            Map<String, java.util.Set<String>> seenHard = new HashMap<>();

            for (Map.Entry<String, String> entry : slugToDifficulty.entrySet()) {
                String slug = entry.getKey();
                String diff = entry.getValue();
                String lang = slugToLang.get(slug);

                if ("Easy".equals(diff)) seenEasy.computeIfAbsent(lang, k -> new java.util.HashSet<>()).add(slug);
                else if ("Medium".equals(diff)) seenMedium.computeIfAbsent(lang, k -> new java.util.HashSet<>()).add(slug);
                else if ("Hard".equals(diff)) seenHard.computeIfAbsent(lang, k -> new java.util.HashSet<>()).add(slug);
            }

            // Calculate Difficulty Progress
            int totalTarget = targetService.getTarget("Target_Total", 500);
            int easyTarget = targetService.getTarget("Target_Easy", 200);
            int mediumTarget = targetService.getTarget("Target_Medium", 200);
            int hardTarget = targetService.getTarget("Target_Hard", 100);

            stats.setTotalTarget(totalTarget);
            stats.setEasyTarget(easyTarget);
            stats.setMediumTarget(mediumTarget);
            stats.setHardTarget(hardTarget);

            stats.setTotalPercentage(calculatePercentage(stats.getTotalSolved(), totalTarget));
            stats.setEasyPercentage(calculatePercentage(stats.getEasySolved(), easyTarget));
            stats.setMediumPercentage(calculatePercentage(stats.getMediumSolved(), mediumTarget));
            stats.setHardPercentage(calculatePercentage(stats.getHardSolved(), hardTarget));

            List<Map<String, Object>> languageProblemCount = (List<Map<String, Object>>) matchedUser.get("languageProblemCount");
            if (languageProblemCount != null) {
                Map<String, Integer> solvedMap = new HashMap<>();
                for (Map<String, Object> entry : languageProblemCount) {
                    solvedMap.put((String) entry.get("languageName"), (Integer) entry.get("problemsSolved"));
                }

                java.util.List<com.leetcode.dashboard.model.LanguageProgress> langStats = new java.util.ArrayList<>();
                java.util.Set<String> knownLanguages = new java.util.HashSet<>(List.of("Java", "C++", "Python", "Python3", "JavaScript", "MySQL"));
                knownLanguages.addAll(solvedMap.keySet());

                for (String name : knownLanguages) {
                    int solved = solvedMap.getOrDefault(name, 0);
                    int totalT = targetService.getTarget(name, 20); 
                    int easyT = targetService.getTarget(name + "_Easy", 10);
                    int mediumT = targetService.getTarget(name + "_Medium", 10);
                    int hardT = targetService.getTarget(name + "_Hard", 5);
                    
                    String langKey = name.toLowerCase();
                    if ("c++".equals(langKey)) langKey = "cpp";
                    
                    int easyS = Math.max(
                        seenEasy.getOrDefault(name, java.util.Collections.emptySet()).size(),
                        seenEasy.getOrDefault(langKey, java.util.Collections.emptySet()).size()
                    );
                    int mediumS = Math.max(
                        seenMedium.getOrDefault(name, java.util.Collections.emptySet()).size(),
                        seenMedium.getOrDefault(langKey, java.util.Collections.emptySet()).size()
                    );
                    int hardS = Math.max(
                        seenHard.getOrDefault(name, java.util.Collections.emptySet()).size(),
                        seenHard.getOrDefault(langKey, java.util.Collections.emptySet()).size()
                    );

                    com.leetcode.dashboard.model.LanguageProgress stat = new com.leetcode.dashboard.model.LanguageProgress(
                        name, solved, totalT,
                        easyS, mediumS, hardS,
                        easyT, mediumT, hardT
                    );
                    langStats.add(stat);
                }
                
                langStats.sort((a, b) -> b.getProblemsSolved() - a.getProblemsSolved());
                stats.setLanguageStats(langStats);
            }

            stats.setStatus("success");
        } catch (Exception e) {
            stats.setStatus("error");
            stats.setMessage("Error parsing response: " + e.getMessage());
            e.printStackTrace();
        }
        return stats;
    }

    private Map<String, String> fetchDifficulties(java.util.List<String> slugs) {
        Map<String, String> slugToDifficulty = new HashMap<>();
        if (slugs == null || slugs.isEmpty()) return slugToDifficulty;

        // Batch into groups of 20 to avoid URL/body length limits
        for (int i = 0; i < slugs.size(); i += 20) {
            int end = Math.min(i + 20, slugs.size());
            java.util.List<String> chunk = slugs.subList(i, end);

            StringBuilder query = new StringBuilder("query {");
            for (int j = 0; j < chunk.size(); j++) {
                query.append("q").append(j).append(": question(titleSlug: \"").append(chunk.get(j)).append("\") { difficulty } ");
            }
            query.append("}");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("query", query.toString());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Referer", "https://leetcode.com");
            headers.set("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            try {
                ResponseEntity<Map> response = restTemplate.postForEntity(LEETCODE_API_URL, entity, Map.class);
                Map body = response.getBody();
                if (body != null && body.containsKey("data")) {
                    Map data = (Map) body.get("data");
                    for (int j = 0; j < chunk.size(); j++) {
                        Map question = (Map) data.get("q" + j);
                        if (question != null) {
                            slugToDifficulty.put(chunk.get(j), (String) question.get("difficulty"));
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error fetching batch difficulties: " + e.getMessage());
            }
        }
        return slugToDifficulty;
    }

    private int calculatePercentage(int solved, int target) {
        if (target <= 0) return 100;
        int p = (int) ((double) solved / target * 100);
        return Math.min(p, 100);
    }
}
