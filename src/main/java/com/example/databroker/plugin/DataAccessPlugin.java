package com.example.databroker.plugin;

import com.example.databroker.dto.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DataAccessPlugin implements MessageProcessor {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DataAccessPlugin(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean canHandle(Message message) {
        return "db_access".equals(message.getType());
    }

    @Override
    public Object process(Message message) {
        String operation = (String) message.getPayload("operation", null);
        if (operation == null) {
            return "Operation is required for db_access";
        }

        try {
            switch (operation.toLowerCase()) {
                case "read":
                    return handleRead(message);
                case "write":
                    return handleWrite(message);
                case "update":
                    return handleUpdate(message);
                case "delete":
                    return handleDelete(message);
                case "query":
                    return handleQuery(message);
                default:
                    return "Unsupported operation: " + operation;
            }
        } catch (Exception e) {
            return "Failed to execute db_access operation: " + e.getMessage();
        }
    }

    private Object handleRead(Message message) {
        String key = (String) message.getPayload("key", null);
        if (key == null) return "Key is required for read operation";
        String table = (String) message.getPayload("table", "config");
        String sql = "SELECT value FROM " + table + " WHERE key = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, key);
        } catch (Exception e) {
            return "Failed to read from " + table + ": " + e.getMessage();
        }
    }

    private Object handleWrite(Message message) {
        String key = (String) message.getPayload("key", null);
        String value = (String) message.getPayload("value", null);
        if (key == null || value == null) return "Key and value are required for write operation";
        String table = (String) message.getPayload("table", "config");
        String sql = "INSERT INTO " + table + " (key, value) VALUES (?, ?) ON DUPLICATE KEY UPDATE value = ?";
        try {
            jdbcTemplate.update(sql, key, value, value);
            return "Successfully wrote " + key + "=" + value + " to " + table;
        } catch (Exception e) {
            return "Failed to write to " + table + ": " + e.getMessage();
        }
    }

    private Object handleUpdate(Message message) {
        String key = (String) message.getPayload("key", null);
        String value = (String) message.getPayload("value", null);
        if (key == null || value == null) return "Key and value are required for update operation";
        String table = (String) message.getPayload("table", "config");
        String sql = "UPDATE " + table + " SET value = ? WHERE key = ?";
        try {
            int rows = jdbcTemplate.update(sql, value, key);
            return rows > 0 ? "Successfully updated " + key + "=" + value + " in " + table : "Key not found: " + key;
        } catch (Exception e) {
            return "Failed to update in " + table + ": " + e.getMessage();
        }
    }

    private Object handleDelete(Message message) {
        String key = (String) message.getPayload("key", null);
        if (key == null) return "Key is required for delete operation";
        String table = (String) message.getPayload("table", "config");
        String sql = "DELETE FROM " + table + " WHERE key = ?";
        try {
            int rows = jdbcTemplate.update(sql, key);
            return rows > 0 ? "Successfully deleted key " + key + " from " + table : "Key not found: " + key;
        } catch (Exception e) {
            return "Failed to delete from " + table + ": " + e.getMessage();
        }
    }

    private Object handleQuery(Message message) {
        String query = (String) message.getPayload("query", null);
        if (query == null) return "Query is required for query operation";
        try {
            return jdbcTemplate.queryForObject(query, String.class);
        } catch (Exception e) {
            return "Failed to execute query: " + e.getMessage();
        }
    }
}

