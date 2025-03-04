package com.example.databroker.controller;

import com.example.databroker.service.DataBroker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MessageController {
    private final DataBroker dataBroker;

    @Autowired
    public MessageController(DataBroker dataBroker) {
        this.dataBroker = dataBroker;
    }

    @PostMapping("/process")
    public Object process(@RequestBody Map<String, Object> message) {
        return dataBroker.processMessage(message);
    }
}