package com.ngocduy.fap.swp391.service;

import com.ngocduy.fap.swp391.model.response.EmailDetail;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public boolean sendMailTemplate(EmailDetail emailDetail){
        try {
            // Validate input
            if (emailDetail == null || emailDetail.getRecipient() == null || emailDetail.getRecipient().isEmpty()) {
                logger.error("Email recipient is null or empty");
                return false;
            }

            logger.info("Preparing to send email to: {}", emailDetail.getRecipient());

            // Prepare Thymeleaf context
            Context context = new Context();
            context.setVariable("name", emailDetail.getFullName() != null ? emailDetail.getFullName() : "Người dùng");

            // Process template
            String text = templateEngine.process("order-confirm", context);
            logger.debug("Email template processed successfully");

            // Create MIME message
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // Set email details
            mimeMessageHelper.setFrom(fromEmail != null ? fromEmail : "admin@gmail.com");
            mimeMessageHelper.setTo(emailDetail.getRecipient());
            mimeMessageHelper.setText(text, true); // true = HTML content
            mimeMessageHelper.setSubject(emailDetail.getSubject() != null ? emailDetail.getSubject() : "Xác nhận đơn hàng");

            // Send email
            mailSender.send(mimeMessage);
            logger.info("Email sent successfully to: {}", emailDetail.getRecipient());
            return true;

        } catch (MessagingException e) {
            logger.error("MessagingException while sending email to {}: {}", emailDetail.getRecipient(), e.getMessage(), e);
            return false;
        } catch (MailException e) {
            logger.error("MailException while sending email to {}: {}", emailDetail.getRecipient(), e.getMessage(), e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error while sending email to {}: {}", emailDetail.getRecipient(), e.getMessage(), e);
            return false;
        }
    }

    public boolean sendPasswordResetEmail(String recipient, String fullName, String resetLink) {
        try {
            // Validate input
            if (recipient == null || recipient.isEmpty()) {
                logger.error("Email recipient is null or empty");
                return false;
            }

            logger.info("Preparing to send password reset email to: {}", recipient);

            // Prepare Thymeleaf context
            Context context = new Context();
            context.setVariable("name", fullName != null ? fullName : "Người dùng");
            context.setVariable("resetLink", resetLink);

            // Process template
            String text = templateEngine.process("password-reset", context);
            logger.debug("Password reset email template processed successfully");

            // Create MIME message
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // Set email details
            mimeMessageHelper.setFrom(fromEmail != null ? fromEmail : "admin@gmail.com");
            mimeMessageHelper.setTo(recipient);
            mimeMessageHelper.setText(text, true); // true = HTML content
            mimeMessageHelper.setSubject("Đặt lại mật khẩu - EV Trading Platform");

            // Send email
            mailSender.send(mimeMessage);
            logger.info("Password reset email sent successfully to: {}", recipient);
            return true;

        } catch (MessagingException e) {
            logger.error("MessagingException while sending password reset email to {}: {}", recipient, e.getMessage(), e);
            return false;
        } catch (MailException e) {
            logger.error("MailException while sending password reset email to {}: {}", recipient, e.getMessage(), e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error while sending password reset email to {}: {}", recipient, e.getMessage(), e);
            return false;
        }
    }

    public boolean sendArticleRejectionEmail(String recipient, String fullName, String articleTitle, String rejectionReason) {
        try {
            // Validate input
            if (recipient == null || recipient.isEmpty()) {
                logger.error("Email recipient is null or empty");
                return false;
            }

            logger.info("Preparing to send article rejection email to: {}", recipient);

            // Prepare Thymeleaf context
            Context context = new Context();
            context.setVariable("name", fullName != null ? fullName : "Người dùng");
            context.setVariable("articleTitle", articleTitle != null ? articleTitle : "Bài đăng của bạn");
            context.setVariable("rejectionReason", rejectionReason != null ? rejectionReason : "Không đáp ứng yêu cầu");

            // Process template
            String text = templateEngine.process("article-rejection", context);
            logger.debug("Article rejection email template processed successfully");

            // Create MIME message
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // Set email details
            mimeMessageHelper.setFrom(fromEmail != null ? fromEmail : "admin@gmail.com");
            mimeMessageHelper.setTo(recipient);
            mimeMessageHelper.setText(text, true); // true = HTML content
            mimeMessageHelper.setSubject("Thông báo: Bài đăng của bạn đã bị từ chối - EV Trading Platform");

            // Send email
            mailSender.send(mimeMessage);
            logger.info("Article rejection email sent successfully to: {}", recipient);
            return true;

        } catch (MessagingException e) {
            logger.error("MessagingException while sending article rejection email to {}: {}", recipient, e.getMessage(), e);
            return false;
        } catch (MailException e) {
            logger.error("MailException while sending article rejection email to {}: {}", recipient, e.getMessage(), e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error while sending article rejection email to {}: {}", recipient, e.getMessage(), e);
            return false;
        }
    }
}
