package com.example.databroker.service;

import com.example.databroker.dto.Message;
import com.example.databroker.plugin.MessageProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DataBroker {
    private final List<MessageProcessor> processors = new ArrayList<>();

    @Autowired
    public DataBroker(List<MessageProcessor> initialProcessors) {
        this.processors.addAll(initialProcessors);
    }

    public void registerPlugin(MessageProcessor plugin) {
        processors.add(plugin);
    }

    public Object processMessage(Message message) {
        for (MessageProcessor processor : processors) {
            if (processor.canHandle(message)) {
                return processor.process(message);
            }
        }
        return "No processor found to handle this message";
    }
}

