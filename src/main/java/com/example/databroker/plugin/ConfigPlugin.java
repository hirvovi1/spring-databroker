package com.example.databroker.plugin;

import com.example.databroker.dto.Message;
import com.example.databroker.service.DataBroker;
import org.springframework.stereotype.Component;

@Component
public class ConfigPlugin implements MessageProcessor {
    private final DataBroker dataBroker;

    public ConfigPlugin(DataBroker dataBroker) {
        this.dataBroker = dataBroker;
    }

    @Override
    public boolean canHandle(Message message) {
        return "config".equals(message.getType());
    }

    @Override
    public Object process(Message message) {
        String action = (String) message.getPayload("action", null);
        String key = (String) message.getPayload("key", null);
        if (action == null || key == null) return "Config requires both action and key";

        Message dbMessage = new Message();
        dbMessage.setType("db_access");
        dbMessage.addPayload("operation", action);
        dbMessage.addPayload("key", key);

        if ("write".equals(action)) {
            String value = (String) message.getPayload("value", null);
            if (value == null) return "Value is required for write action";
            dbMessage.addPayload("value", value);
        }

        Object result = dataBroker.processMessage(dbMessage);
        return result;
    }
}

