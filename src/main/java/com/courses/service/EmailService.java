package com.courses.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPasswordResetEmail(String to, String resetLink) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Восстановление пароля — VioletBloom");
            message.setText("Для восстановления пароля перейдите по ссылке:\n\n" + resetLink
                    + "\n\nСсылка действительна 24 часа.");
            mailSender.send(message);
        } catch (Exception e) {
            log.warn("Не удалось отправить email на {}: {}. Ссылка: {}", to, e.getMessage(), resetLink);
        }
    }
}