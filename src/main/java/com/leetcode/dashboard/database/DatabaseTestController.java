package com.leetcode.dashboard.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/db")
public class DatabaseTestController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/check")
    public Map<String, Object> checkConnection() {
        Map<String, Object> response = new HashMap<>();
        try (Connection connection = dataSource.getConnection()) {
            response.put("status", "success");
            response.put("message", "Successfully connected to MySQL database!");
            response.put("database", connection.getCatalog());
            response.put("driver", connection.getMetaData().getDriverName());
        } catch (SQLException e) {
            response.put("status", "error");
            response.put("message", "Failed to connect to MySQL: " + e.getMessage());
        }
        return response;
    }
}
