package employeetimesheet.timesheet.service;

import employeetimesheet.timesheet.entity.DynamicMailSender;
import employeetimesheet.timesheet.repository.EmailSenderConfigRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final DynamicMailSender dynamicMailSender;
    private final EmailSenderConfigRepository senderRepo;

    /**
     * Sends a password reset email with a token.
     *
     * @param to         Recipient email address
     * @param token      Password reset token
     * @param senderName Name of the sender configuration
     */
    public void sendResetEmail(String to, String token, String senderName) {
        sendEmail(to, "🔐 Password Reset Request",
                "Hi,\n\nClick the link below to reset your password:\n\n" +
                        "https://timesheetapplication-v2.vercel.app/reset-password/?token=" + token +
                        "\n\nThis link will expire in 30 minutes.\n\nRegards,\nTimesheet Team",
                senderName);
    }

    /**
     * Sends a generic email with the specified subject and content.
     *
     * @param to         Recipient email address
     * @param subject    Email subject
     * @param content    Email body content
     * @param senderName Name of the sender configuration
     */
    public void sendEmail(String to, String subject, String content, String senderName) {
        var senderConfig = senderRepo.findBySenderName(senderName)
                .orElseThrow(() -> {
                    logger.error("❌ Sender config not found for: {}", senderName);
                    return new RuntimeException("Sender config not found for: " + senderName);
                });

        JavaMailSender mailSender = dynamicMailSender.getSender(
                senderConfig.getEmail(),
                senderConfig.getAppPassword()
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderConfig.getEmail());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        try {
            mailSender.send(message);
            logger.info("✅ Email sent to: {} using sender: {}", to, senderName);
        } catch (MailException e) {
            logger.error("❌ Failed to send email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}