package com.example.databroker.service;

import com.example.databroker.plugin.MessageProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DataBroker {
    private final List<MessageProcessor> processors;

    @Autowired
    public DataBroker(List<MessageProcessor> processors) {
        this.processors = processors;
    }

    public Object processMessage(Map<String, Object> message) {
        for (MessageProcessor processor : processors) {
            if (processor.canHandle(message)) {
                return processor.process(message);
            }
        }
        return "No processor found to handle this message";
    }
}