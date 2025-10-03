package employeetimesheet.timesheet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import employeetimesheet.timesheet.dto.TimeSheetDTO;
import employeetimesheet.timesheet.entity.AppUser;
import employeetimesheet.timesheet.entity.Timesheet;
import employeetimesheet.timesheet.entity.User;
import employeetimesheet.timesheet.repository.AppUserRepository;
import employeetimesheet.timesheet.repository.UserRepository;
import employeetimesheet.timesheet.service.TimesheetService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/timesheets")
@RequiredArgsConstructor
public class TimesheetController {
    private final TimesheetService service;
    private final AppUserRepository appUserRepository;
    private final UserRepository userRepository; // Add UserRepository
    private static final Logger logger = LoggerFactory.getLogger(TimesheetController.class);

   @GetMapping
public List<TimeSheetDTO> getAll() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    var authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    logger.info("🔍 Fetching timesheets for user: {}", username);

    AppUser appUser = appUserRepository.findByUsername(username)
            .orElseThrow(() -> {
                logger.error("❌ AppUser not found for username: {}", username);
                return new IllegalArgumentException("User not found");
            });

    boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    logger.info("User role: {}", isAdmin ? "ROLE_ADMIN" : "ROLE_USER");

    if (isAdmin) {
        logger.info("✅ Admin user: Returning all timesheets");
        return service.findAllAsDto();
    } else {
        User authenticatedUser = userRepository.findByAppUserId(appUser.getId())
                .orElseThrow(() -> {
                    logger.error("❌ User profile not found for AppUser id: {}", appUser.getId());
                    return new IllegalArgumentException("User profile not found");
                });
        Integer userId = authenticatedUser.getUserId();
        logger.info("✅ Regular user: AppUser.id={}, User.userId={}, Returning timesheets for userId: {}", appUser.getId(), userId, userId);
        if (userId == null) {
            logger.warn("⚠️ User.userId is null for AppUser.id: {}, returning empty list", appUser.getId());
            return Collections.emptyList();
        }
        List<TimeSheetDTO> timesheets = service.findByUserId(userId);
        logger.info("✅ Fetched timesheets count: {}", timesheets.size());
        return timesheets;
    }
}
    @GetMapping("/{id}")
    public Timesheet getById(@PathVariable Integer id) {
        return service.findById(id);
    }

    @PostMapping("/posttimesheet")
    public ResponseEntity<TimeSheetDTO> create(@RequestBody TimeSheetDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        logger.info("✅ Attempting to create timesheet for userId: {}", dto.getUserId());
        // Fetch the User profile for the authenticated AppUser
        User authenticatedUser = userRepository.findByAppUserId(appUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("User profile not found for " + username));

        // Validate userId matches the authenticated user's user_id
        if (!isAdmin && !authenticatedUser.getUserId().equals(Integer.valueOf(dto.getUserId()))) {
            logger.error("❌ Access denied: User {} (user_id: {}) cannot create timesheet for userId {}", 
                username, authenticatedUser.getUserId(), dto.getUserId());
            return ResponseEntity.status(403).body(null);
        }

        try {
            TimeSheetDTO savedDto = service.create(dto);
            logger.info("✅ Timesheet created successfully for userId: {}", dto.getUserId());
            return ResponseEntity.ok(savedDto);
        } catch (IllegalArgumentException e) {
            logger.error("❌ Failed to create timesheet: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TimeSheetDTO> update(@PathVariable Integer id, @RequestBody TimeSheetDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}