package _LicHub.Backend.playerService.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


@Service
public class EmailService {

    @Value("${mail.username}")
    private String senderMail;

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine){
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }


    @Async
    public void sendAuthEmail(String to, String userName, String otp) throws MessagingException {

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("userName", userName);
        templateModel.put("otpCode", otp);

        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);

        String htmlBody = templateEngine.process("authEmail", thymeleafContext);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(senderMail);
        helper.setTo(to);
        helper.setSubject("4LicHub Verification Code");
        helper.setText(htmlBody, true);

        mailSender.send(message);
    }
}