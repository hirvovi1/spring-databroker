package com.example.databroker.plugin;

import java.util.Map;

public interface MessageProcessor {
    boolean canHandle(Map<String, Object> message);
    Object process(Map<String, Object> message);
}