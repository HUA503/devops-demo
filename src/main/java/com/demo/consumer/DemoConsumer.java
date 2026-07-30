package com.demo.consumer;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(
    topic = "devops-topic",
    consumerGroup = "devops-consumer-group")
public class DemoConsumer implements RocketMQListener<String> {

    private static final Logger log =
        LoggerFactory.getLogger(DemoConsumer.class);

    @Override
    public void onMessage(String message) {
        log.info("Received message: {}", message);
    }
}
