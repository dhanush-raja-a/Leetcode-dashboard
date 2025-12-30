package com.leetcode.dashboard.repository;

import com.leetcode.dashboard.entity.LeetCodeRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeetCodeRecordRepository extends JpaRepository<LeetCodeRecord, Long> {
    Page<LeetCodeRecord> findByUsername(String username, Pageable pageable);
}
