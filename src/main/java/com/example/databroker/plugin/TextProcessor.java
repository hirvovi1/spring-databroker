package com.example.databroker.plugin;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TextProcessor implements MessageProcessor {
    @Override
    public boolean canHandle(Map<String, Object> message) {
        return "text".equals(message.get("type"));
    }

    @Override
    public Object process(Map<String, Object> message) {
        String content = (String) message.getOrDefault("content", "");
        return "Processed text: " + content.toUpperCase();
    }
}