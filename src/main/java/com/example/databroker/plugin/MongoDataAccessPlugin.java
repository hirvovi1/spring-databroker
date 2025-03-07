package com.example.databroker.plugin;

import com.example.databroker.dto.Message;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MongoDataAccessPlugin implements MessageProcessor {
    // In-memory mock storage simulating MongoDB's config collection
    private final Map<String, String> mockConfigStore = new HashMap<>();

    @Override
    public boolean canHandle(Message message) {
        return "mongo_access".equals(message.getType());
    }

    @Override
    public Object process(Message message) {
        String operation = (String) message.getPayload("operation", null);
        if (operation == null) {
            return "Operation is required for mongo_access";
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
                default:
                    return "Unsupported operation: " + operation + " (query not supported in mock MongoDB)";
            }
        } catch (Exception e) {
            return "Failed to execute mongo_access operation: " + e.getMessage();
        }
    }

    private Object handleRead(Message message) {
        String key = (String) message.getPayload("key", null);
        if (key == null) return "Key is required for read operation";
        String value = mockConfigStore.get(key);
        return value != null ? value : "Key not found: " + key;
    }

    private Object handleWrite(Message message) {
        String key = (String) message.getPayload("key", null);
        String value = (String) message.getPayload("value", null);
        if (key == null || value == null) return "Key and value are required for write operation";
        mockConfigStore.put(key, value);
        return "Successfully wrote " + key + "=" + value + " to mock MongoDB";
    }

    private Object handleUpdate(Message message) {
        String key = (String) message.getPayload("key", null);
        String value = (String) message.getPayload("value", null);
        if (key == null || value == null) return "Key and value are required for update operation";
        if (!mockConfigStore.containsKey(key)) return "Key not found: " + key;
        mockConfigStore.put(key, value);
        return "Successfully updated " + key + "=" + value + " in mock MongoDB";
    }

    private Object handleDelete(Message message) {
        String key = (String) message.getPayload("key", null);
        if (key == null) return "Key is required for delete operation";
        String value = mockConfigStore.remove(key);
        return value != null ? "Successfully deleted key " + key + " from mock MongoDB" : "Key not found: " + key;
    }
}

