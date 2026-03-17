package com.backend.nutri_ai.notification.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailProducer {

    private final RabbitTemplate rabbitTemplate;

    public void send(EmailQueueMessage msg) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EMAIL_QUEUE,
                msg
        );
    }
}
