package com.leetcode.dashboard.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "leetcode_records")
public class LeetCodeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    
    private LocalDateTime timestamp;

    private int totalSolved;
    private int easySolved;
    private int mediumSolved;
    private int hardSolved;

    @Column(columnDefinition = "TEXT")
    private String languageStatsJson;

    private long durationMs;
    private String status; // success or error
    private String errorMessage;
}
