package com.leetcode.dashboard.controller;

import com.leetcode.dashboard.entity.LeetCodeRecord;
import com.leetcode.dashboard.repository.LeetCodeRecordRepository;
import com.leetcode.dashboard.service.BatchFetchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/batch")
public class BatchController {

    @Autowired
    private BatchFetchService batchFetchService;

    @Autowired
    private LeetCodeRecordRepository leetCodeRecordRepository;

    @GetMapping("/students")
    public List<String> getStudents() {
        return batchFetchService.getStudents();
    }

    @PostMapping("/students")
    public List<String> updateStudents(@RequestBody List<String> students) {
        batchFetchService.updateStudents(students);
        return batchFetchService.getStudents();
    }

    @PostMapping("/start/{username}")
    public Map<String, Object> startBatch(@PathVariable String username) {
        batchFetchService.startBatchFetch(username);
        return batchFetchService.getBatchStatus(username);
    }

    @GetMapping("/status/{username}")
    public Map<String, Object> getStatus(@PathVariable String username) {
        return batchFetchService.getBatchStatus(username);
    }

    @GetMapping("/data")
    public List<LeetCodeRecord> getData(
            @RequestParam String username,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {
        int page = offset / limit;
        return leetCodeRecordRepository.findByUsername(username, PageRequest.of(page, limit)).getContent();
    }

}
