package com.example.databroker.plugin;

import com.example.databroker.dto.Message;

public interface MessageProcessor {
    boolean canHandle(Message message);
    Object process(Message message);
}

