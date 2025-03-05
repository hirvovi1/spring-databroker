package com.example.databroker.plugin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ConfigPlugin implements MessageProcessor {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ConfigPlugin(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean canHandle(Map<String, Object> message) {
        return "config".equals(message.get("type"));
    }

    @Override
    public Object process(Map<String, Object> message) {
        String action = (String) message.get("action");
        String key = (String) message.get("key");
        if (action == null || key == null) return "Config’s fucked—need action and key";
        try {
            if ("read".equals(action)) {
                String sql = "SELECT value FROM config WHERE key = ?";
                return jdbcTemplate.queryForObject(sql, String.class, key);
            } else if ("write".equals(action)) {
                String value = (String) message.get("value");
                String sql = "INSERT INTO config (key, value) VALUES (?, ?) ON DUPLICATE KEY UPDATE value = ?";
                jdbcTemplate.update(sql, key, value, value);
                return "Wrote " + key + "=" + value + ", prick";
            }
            return "Bad action, asshole: " + action;
        } catch (Exception e) {
            return "DB config’s a bitch: " + e.getMessage();
        }
    }
}