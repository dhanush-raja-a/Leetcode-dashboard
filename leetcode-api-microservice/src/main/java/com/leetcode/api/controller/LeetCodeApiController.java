package com.leetcode.api.controller;

import com.leetcode.api.model.LanguageProgress;
import com.leetcode.api.model.LeetCodeStats;
import com.leetcode.api.service.LeetCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LeetCodeApiController {

    private final LeetCodeService leetCodeService;

    @Autowired
    public LeetCodeApiController(LeetCodeService leetCodeService) {
        this.leetCodeService = leetCodeService;
    }

    @GetMapping("/stats")
    public LeetCodeStats getStats(@RequestParam(name = "username") String username) {
        return leetCodeService.getStats(username);
    }

    @GetMapping("/language-stats")
    public List<LanguageProgress> getLanguageStats(@RequestParam(name = "username") String username) {
        LeetCodeStats stats = leetCodeService.getStats(username);
        return stats.getLanguageStats();
    }
}
