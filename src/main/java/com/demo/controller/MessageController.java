package com.demo.controller;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class MessageController {

    private static final Logger log =
        LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @GetMapping("/api/send")
    public String send(
        @RequestParam(defaultValue = "hello") String msg) {
        rocketMQTemplate.syncSend("devops-topic", msg);
        log.info("Message sent: {}", msg);
        return "Sent: " + msg;
    }
}
