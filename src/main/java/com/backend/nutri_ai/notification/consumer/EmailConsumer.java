package com.backend.nutri_ai.notification.consumer;

import com.backend.nutri_ai.auth.Mail.service.MailService;
import com.backend.nutri_ai.notification.config.EmailQueueMessage;
import com.backend.nutri_ai.notification.config.RabbitConfig;
import com.backend.nutri_ai.notification.entity.NotificationSendLog;
import com.backend.nutri_ai.notification.repository.NotificationSendLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailConsumer {

    private final MailService mailService;
    private final NotificationSendLogRepository logRepo;

    @RabbitListener(queues = RabbitConfig.EMAIL_QUEUE)
    public void consume(EmailQueueMessage msg) {

        log.info(" Sending mail to {}", msg.getTo());

        mailService.sendHtmlMail(
                msg.getTo(),
                msg.getSubject(),
                msg.getHtml()
        );

        NotificationSendLog log = new NotificationSendLog();
        log.setNotificationId(msg.getNotificationId());
        log.setUserId(msg.getUserId());
        log.setSentAt(Instant.now());

        logRepo.save(log);
    }
}
