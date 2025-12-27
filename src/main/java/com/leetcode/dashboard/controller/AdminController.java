package com.leetcode.dashboard.controller;

import com.leetcode.dashboard.service.TargetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class AdminController {

    private final TargetService targetService;

    public AdminController(TargetService targetService) {
        this.targetService = targetService;
    }

    @GetMapping("/admin")
    public String adminPage(Model model) {
        model.addAttribute("targets", targetService.getAllTargets());
        return "admin";
    }

    @PostMapping("/admin/targets")
    public String updateTargets(@RequestParam Map<String, String> allParams) {
        // Filter and convert params to Map<String, Integer>
        Map<String, Integer> newTargets = new java.util.HashMap<>();
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            try {
                int val = Integer.parseInt(entry.getValue());
                newTargets.put(entry.getKey(), val);
            } catch (NumberFormatException e) {
                // Ignore non-integer params
            }
        }
        targetService.updateTargets(newTargets);
        return "redirect:/admin?success";
    }
}
