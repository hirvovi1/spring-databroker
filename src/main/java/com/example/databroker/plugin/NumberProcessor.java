package com.example.databroker.plugin;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NumberProcessor implements MessageProcessor {
    @Override
    public boolean canHandle(Map<String, Object> message) {
        return "number".equals(message.get("type"));
    }

    @Override
    public Object process(Map<String, Object> message) {
        Number content = (Number) message.getOrDefault("content", 0);
        return "Processed number: " + (content.doubleValue() * 2);
    }
}