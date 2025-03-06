package com.example.databroker.controller;

import com.example.databroker.dto.Message;
import com.example.databroker.service.DataBroker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/process")
public class MessageController {
    private final DataBroker dataBroker;

    @Autowired
    public MessageController(DataBroker dataBroker) {
        this.dataBroker = dataBroker;
    }

    @PostMapping
    public ResponseEntity<String> processMessage(@RequestBody Map<String, Object> rawMessage) {
        String type = (String) rawMessage.get("type");
        if (type == null) {
            return ResponseEntity.badRequest().body("Message type is required");
        }
        rawMessage.remove("type");
        Message message = new Message(type, rawMessage);
        Object result = dataBroker.processMessage(message);
        return ResponseEntity.ok(result.toString());
    }
}

