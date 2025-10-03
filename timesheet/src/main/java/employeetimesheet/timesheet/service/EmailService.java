package employeetimesheet.timesheet.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import employeetimesheet.timesheet.entity.DynamicMailSender;
import employeetimesheet.timesheet.repository.EmailSenderConfigRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final DynamicMailSender dynamicMailSender;
    private final EmailSenderConfigRepository senderRepo;

    public void sendResetEmail(String to, String token, String senderName) {
        var senderConfig = senderRepo.findBySenderName(senderName)
            .orElseThrow(() -> new RuntimeException("Sender config not found for: " + senderName));

        JavaMailSender mailSender = dynamicMailSender.getSender(
            senderConfig.getEmail(),
            senderConfig.getAppPassword()
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderConfig.getEmail());
        message.setTo(to);
        message.setSubject("🔐 Password Reset Request");
        message.setText("Hi,\n\nClick the link below to reset your password:\n\n" +
            "https://employeetimeshhet-176m.vercel.app/reset-password/?token=" + token +
          //  "http://localhost:3000/reset-password/?token=" + token +
            "\n\nThis link will expire in 30 minutes.\n\nRegards,\nTimesheet Team");

        mailSender.send(message);
        System.out.println("✅ Password reset email sent to: " + to + " using sender: " + senderName);
    }
}
