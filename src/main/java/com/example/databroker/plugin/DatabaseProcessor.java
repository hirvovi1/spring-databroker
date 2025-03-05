package com.example.databroker.plugin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DatabaseProcessor implements MessageProcessor {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseProcessor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean canHandle(Map<String, Object> message) {
        return "db".equals(message.get("type"));
    }

    @Override
    public Object process(Map<String, Object> message) {
        String query = (String) message.getOrDefault("query", "SELECT 'fuck all' as result");
        try {
            return jdbcTemplate.queryForObject(query, String.class);
        } catch (Exception e) {
            return "Screwed up: " + e.getMessage();
        }
    }
}