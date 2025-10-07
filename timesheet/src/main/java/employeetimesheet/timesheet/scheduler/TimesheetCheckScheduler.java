package employeetimesheet.timesheet.scheduler;

import employeetimesheet.timesheet.entity.Timesheet;
import employeetimesheet.timesheet.entity.User;
import employeetimesheet.timesheet.repository.TimeSheetRepository;
import employeetimesheet.timesheet.repository.UserRepository;
import employeetimesheet.timesheet.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TimesheetCheckScheduler {
    private static final Logger logger = LoggerFactory.getLogger(TimesheetCheckScheduler.class);

    private final UserRepository userRepository;
    private final TimeSheetRepository timesheetRepository;
    private final EmailService emailService;

    @Value("${app.timesheet.sender}")
    private String senderName;

    @Value("${app.email.recipient}")
    private String recipientEmail;

    @Scheduled(cron = "${app.timesheet.check.cron}", zone = "Asia/Kolkata")
    public void checkMissingTimesheets() {
        logger.info("🔍 Checking for missing timesheets for date: {}", LocalDate.now());

        // Get all users (employees)
        List<User> allUsers = userRepository.findAll();
        if (allUsers.isEmpty()) {
            logger.warn("⚠️ No users found in the system");
            return;
        }

        // Get timesheets for today
        LocalDate today = LocalDate.now();
        List<Timesheet> todayTimesheets = timesheetRepository.findByWorkDate(today);

        // Find users who haven't logged timesheets
        List<User> usersWithoutTimesheets = allUsers.stream()
                .filter(user -> todayTimesheets.stream()
                        .noneMatch(ts -> ts.getUser().getUserId().equals(user.getUserId())))
                .collect(Collectors.toList());

        if (usersWithoutTimesheets.isEmpty()) {
            logger.info("✅ All users have submitted timesheets for today");
            return;
        }

        // Prepare email content with full names
        String missingUsers = usersWithoutTimesheets.stream()
                .map(user -> {
                    String fullName = String.join(" ",
                            user.getFirstName() != null ? user.getFirstName() : "",
                            user.getMiddleName() != null ? user.getMiddleName() : "",
                            user.getLastName() != null ? user.getLastName() : "").trim();
                    return "Name: " + (fullName.isEmpty() ? "Unknown" : fullName) + 
                           ", User ID: " + user.getUserId();
                })
                .collect(Collectors.joining("\n"));

        String emailSubject = "Missing Timesheets for " + today;
        String emailContent = String.format(
                "Hi,\n\nThe following employees have not submitted their timesheets for %s:\n\n%s\n\nPlease follow up.\n\nRegards,\nTimesheet Team",
                today, missingUsers);

        // Send email to the specified recipient
        try {
            emailService.sendEmail(recipientEmail, emailSubject, emailContent, senderName);
            logger.info("✅ Sent missing timesheet notification to: {}", recipientEmail);
        } catch (Exception e) {
            logger.error("❌ Failed to send email to {}: {}", recipientEmail, e.getMessage());
        }
    }
}