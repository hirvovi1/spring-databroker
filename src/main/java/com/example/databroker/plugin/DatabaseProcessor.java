package com.example.databroker.plugin;

import com.example.databroker.dto.Message;
import com.example.databroker.service.DataBroker;
import org.springframework.stereotype.Component;

@Component
public class DatabaseProcessor implements MessageProcessor {
    private final DataBroker dataBroker;

    public DatabaseProcessor(DataBroker dataBroker) {
        this.dataBroker = dataBroker;
    }

    @Override
    public boolean canHandle(Message message) {
        return "db".equals(message.getType());
    }

    @Override
    public Object process(Message message) {
        String query = (String) message.getPayload("query", "SELECT 'No query provided' as result");
        Message dbMessage = new Message();
        dbMessage.setType("db_access");
        dbMessage.addPayload("operation", "query");
        dbMessage.addPayload("query", query);
        return dataBroker.processMessage(dbMessage);
    }
}
