package com.leetcode.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class TargetService {

    private static final String TARGETS_FILE = "targets.json";
    private Map<String, Integer> targets = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        loadTargets();
        if (targets.isEmpty()) {
            targets.put("Java", 40);
            targets.put("C++", 50);
            targets.put("Python", 30);
            targets.put("Python3", 30);
            targets.put("JavaScript", 30);
            
            targets.put("Target_Total", 500);
            targets.put("Target_Easy", 200);
            targets.put("Target_Medium", 200);
            targets.put("Target_Hard", 100);
            
            saveTargets();
        }
    }

    public int getTarget(String key, int defaultValue) {
        return targets.getOrDefault(key, defaultValue);
    }

    private void loadTargets() {
        File file = new File(TARGETS_FILE);
        if (file.exists()) {
            try {
                targets = objectMapper.readValue(file, new TypeReference<Map<String, Integer>>() {});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveTargets() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(TARGETS_FILE), targets);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
