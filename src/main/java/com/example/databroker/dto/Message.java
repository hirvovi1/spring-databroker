package com.example.databroker.dto;

import java.util.HashMap;
import java.util.Map;

public class Message {
    private String type;
    private Map<String, Object> payload;

    public Message() {
        this.payload = new HashMap<>();
    }

    public Message(String type, Map<String, Object> payload) {
        this.type = type;
        this.payload = payload != null ? payload : new HashMap<>();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload != null ? payload : new HashMap<>();
    }

    public void addPayload(String key, Object value) {
        this.payload.put(key, value);
    }

    public Object getPayload(String key, Object defaultValue) {
        return this.payload.getOrDefault(key, defaultValue);
    }

    @Override
    public String toString() {
        return "Message{type='" + type + "', payload=" + payload + "}";
    }
}

