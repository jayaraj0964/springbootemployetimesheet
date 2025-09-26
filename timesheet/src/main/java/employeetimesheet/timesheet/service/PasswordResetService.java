package employeetimesheet.timesheet.service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import employeetimesheet.timesheet.entity.AppUser;
import employeetimesheet.timesheet.entity.PasswordResetToken;
import employeetimesheet.timesheet.repository.AppUserRepository;
import employeetimesheet.timesheet.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final AppUserRepository userRepo;
    private final PasswordResetTokenRepository tokenRepo;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ResponseEntity<String> initiateReset(String email) {
        Optional<AppUser> userOpt = userRepo.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Email not registered");
        }

        // 🧪 Generate token and expiry (30 mins)
        String token = UUID.randomUUID().toString();
        Date expiry = new Date(System.currentTimeMillis() + 30 * 60 * 1000);

        // 🧾 Save token
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setEmail(email);
        resetToken.setExpiryDate(expiry);

        tokenRepo.deleteByEmail(email); // 🧹 Clean old tokens
        tokenRepo.save(resetToken);

        // 📤 Send email
        emailService.sendResetEmail(email, token, "admin");

        return ResponseEntity.ok("Reset link sent to your email");
    }

    public ResponseEntity<String> resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> tokenOpt = tokenRepo.findByToken(token);
        if (tokenOpt.isEmpty()) {
            return ResponseEntity.status(400).body("Invalid token");
        }

        PasswordResetToken resetToken = tokenOpt.get();

        // 🕒 Compare with current time using java.util.Date
        if (resetToken.getExpiryDate().before(new Date())) {
            return ResponseEntity.status(400).body("Token expired");
        }

        AppUser user = userRepo.findByEmail(resetToken.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        tokenRepo.delete(resetToken); // 🧹 Remove used token

        return ResponseEntity.ok("Password reset successful");
    }
}
