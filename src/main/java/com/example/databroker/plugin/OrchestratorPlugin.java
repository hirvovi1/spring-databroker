package com.example.databroker.plugin;

import com.example.databroker.dto.Message;
import com.example.databroker.service.DataBroker;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrchestratorPlugin implements MessageProcessor {
    private final DataBroker dataBroker;

    public OrchestratorPlugin(@Lazy DataBroker dataBroker) {
        this.dataBroker = dataBroker;
    }

    @Override
    public boolean canHandle(Message message) {
        return "chain".equals(message.getType());
    }

    @Override
    public Object process(Message message) {
        List<Message> steps = (List<Message>) message.getPayload("steps", null);
        if (steps == null) return "No steps provided in the chain";
        Object result = null;
        for (Message step : steps) {
            result = dataBroker.processMessage(step);
            if (result instanceof String && ((String) result).startsWith("No processor")) break;
            step.addPayload("input", result);
        }
        return result != null ? result : "Chain completed with no result";
    }
}
