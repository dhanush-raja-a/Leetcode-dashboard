package com.leetcode.dashboard.controller;

import com.leetcode.dashboard.model.LeetCodeStats;
import com.leetcode.dashboard.service.LeetCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DashboardController {

    @Autowired
    private LeetCodeService leetCodeService;

    @GetMapping("/")
    public String dashboard(@RequestParam(name = "username", required = false, defaultValue = "dhanushrajaa") String username, Model model) {
        LeetCodeStats stats = leetCodeService.getStats(username);
        model.addAttribute("stats", stats);
        model.addAttribute("username", username);
        return "dashboard";
    }
    
    @GetMapping("/api/stats")
    @ResponseBody
    public LeetCodeStats getStatsApi(@RequestParam(name = "username") String username) {
        return leetCodeService.getStats(username);
    }
}
