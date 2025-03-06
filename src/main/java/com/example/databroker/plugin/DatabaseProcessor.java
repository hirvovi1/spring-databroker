package com.example.databroker.plugin;

import com.example.databroker.dto.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseProcessor implements MessageProcessor {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseProcessor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean canHandle(Message message) {
        return "db".equals(message.getType());
    }

    @Override
    public Object process(Message message) {
        String query = (String) message.getPayload("query", "SELECT 'No query provided' as result");
        try {
            return jdbcTemplate.queryForObject(query, String.class);
        } catch (Exception e) {
            return "Failed to execute query: " + e.getMessage();
        }
    }
}
