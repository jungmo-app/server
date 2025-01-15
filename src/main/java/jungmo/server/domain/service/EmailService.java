package jungmo.server.domain.service;

import jungmo.server.domain.dto.request.PasswordResetRequest;
import jungmo.server.domain.entity.User;
import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}") // SMTP 설정에서 발신자 이메일 가져오기
    private String fromEmail;



    public void sendEmail(String email, String resetLink) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("jungmo 비밀번호 재설정 요청");
            message.setText("아래 링크를 클릭하여 비밀번호를 재설정하세요:\n" + resetLink);
            message.setFrom(fromEmail);
            mailSender.send(message);
        } catch (MailException e) {
            throw new BusinessException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

}
