package com.example.databroker.plugin;

import com.example.databroker.dto.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ConfigPlugin implements MessageProcessor {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ConfigPlugin(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean canHandle(Message message) {
        return "config".equals(message.getType());
    }

    @Override
    public Object process(Message message) {
        String action = (String) message.getPayload("action", null);
        String key = (String) message.getPayload("key", null);
        if (action == null || key == null) return "Config requires both action and key";
        try {
            if ("read".equals(action)) {
                String sql = "SELECT value FROM config WHERE key = ?";
                return jdbcTemplate.queryForObject(sql, String.class, key);
            } else if ("write".equals(action)) {
                String value = (String) message.getPayload("value", null);
                String sql = "INSERT INTO config (key, value) VALUES (?, ?) ON DUPLICATE KEY UPDATE value = ?";
                jdbcTemplate.update(sql, key, value, value);
                return "Wrote " + key + "=" + value + " to config";
            }
            return "Unsupported action: " + action;
        } catch (Exception e) {
            return "Failed to process config: " + e.getMessage();
        }
    }
}

