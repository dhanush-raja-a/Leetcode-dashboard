package com.leetcode.dashboard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leetcode.dashboard.entity.LeetCodeRecord;
import com.leetcode.dashboard.model.LeetCodeStats;
import com.leetcode.dashboard.repository.LeetCodeRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class BatchFetchService {

    private static final Logger logger = LoggerFactory.getLogger(BatchFetchService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    @Autowired
    private LeetCodeService leetCodeService;

    @Autowired
    private LeetCodeRecordRepository leetCodeRecordRepository;

    private List<String> students = new ArrayList<>(Arrays.asList(
            "Dhanush_Raja_A", "tourist", "benq", "neal_wu", "ksun48"
    ));

    private Map<String, Integer> progressMap = new ConcurrentHashMap<>();
    private Map<String, String> statusMap = new ConcurrentHashMap<>();

    public List<String> getStudents() {
        return students;
    }

    public void updateStudents(List<String> newStudents) {
        if (newStudents != null && !newStudents.isEmpty()) {
            this.students = new ArrayList<>(newStudents);
        }
    }

    public void startBatchFetch(String username) {
        progressMap.put(username, 0);
        statusMap.put(username, "Running");

        executor.submit(() -> {
            try {
                for (int i = 1; i <= 100; i++) {
                    long startTime = System.currentTimeMillis();
                    LeetCodeStats stats = leetCodeService.getStats(username);
                    long duration = System.currentTimeMillis() - startTime;

                    LeetCodeRecord record = new LeetCodeRecord();
                    record.setUsername(username);
                    record.setTimestamp(LocalDateTime.now());
                    record.setDurationMs(duration);

                    if (stats != null && !"error".equals(stats.getStatus())) {
                        record.setTotalSolved(stats.getTotalSolved());
                        record.setEasySolved(stats.getEasySolved());
                        record.setMediumSolved(stats.getMediumSolved());
                        record.setHardSolved(stats.getHardSolved());
                        record.setStatus("success");
                        try {
                            record.setLanguageStatsJson(objectMapper.writeValueAsString(stats.getLanguageStats()));
                        } catch (Exception e) {
                            logger.error("Failed to serialize language stats", e);
                        }
                    } else {
                        record.setStatus("error");
                        record.setErrorMessage(stats != null ? stats.getMessage() : "Null response");
                    }

                    leetCodeRecordRepository.save(record);
                    progressMap.put(username, i);

                    if (i % 10 == 0) {
                        logger.info("Batch progress for {}: {}/100", username, i);
                    }
                }
                statusMap.put(username, "Completed");
            } catch (Exception e) {
                logger.error("Batch fetch failed for {}", username, e);
                statusMap.put(username, "Failed: " + e.getMessage());
            }
        });
    }

    public Map<String, Object> getBatchStatus(String username) {
        Map<String, Object> res = new HashMap<>();
        res.put("username", username);
        res.put("progress", progressMap.getOrDefault(username, 0));
        res.put("status", statusMap.getOrDefault(username, "Idle"));
        return res;
    }
}
