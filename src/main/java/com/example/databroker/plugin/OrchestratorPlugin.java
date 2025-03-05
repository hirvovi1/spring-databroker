package com.example.databroker.plugin;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.databroker.service.DataBroker;

@Component
public class OrchestratorPlugin implements MessageProcessor {
    private final DataBroker dataBroker;

    public OrchestratorPlugin(DataBroker dataBroker) {
        this.dataBroker = dataBroker;
    }

    @Override
    public boolean canHandle(Map<String, Object> message) {
        return "chain".equals(message.get("type"));
    }

    @Override
    public Object process(Map<String, Object> message) {
        List<Map<String, Object>> steps = (List<Map<String, Object>>) message.get("steps");
        if (steps == null) return "No steps, dumbass";
        Object result = null;
        for (Map<String, Object> step : steps) {
            result = dataBroker.processMessage(step);
            if (result instanceof String && ((String) result).startsWith("No processor")) break;
            step.put("input", result);
            }
        return result != null ? result : "Chain’s empty, shithead";
    }
}