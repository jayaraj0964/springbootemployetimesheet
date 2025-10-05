package employeetimesheet.timesheet.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import employeetimesheet.timesheet.dto.UserDTO;
import employeetimesheet.timesheet.dto.UserInfoDTO;
import employeetimesheet.timesheet.entity.AppUser;
import employeetimesheet.timesheet.entity.User;
import employeetimesheet.timesheet.repository.AppUserRepository;
import employeetimesheet.timesheet.repository.UserRepository;
import employeetimesheet.timesheet.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AppUserRepository appUserRepo;
    private final UserRepository userRepo;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    // 🔍 Get all users (admin only)
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAll() {
        List<User> users = userService.findAll();
        logger.info("🔍 Returning {} users from /api/users/all", users.size());
        if (users.isEmpty()) {
            logger.warn("⚠️ No users found in the database");
        }
        return users;
    }

    // 🔍 Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("🔍 Fetching user ID: {} by authenticated user: {}", id, username);

        AppUser appUser = appUserRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        if (!appUser.getUsername().equals(username) && !appUser.getRole().equals("ROLE_ADMIN")) {
            logger.warn("❌ Unauthorized access to user ID: {} by user: {}", id, username);
            return ResponseEntity.status(403).body("Unauthorized to access this user");
        }

        User userProfile = userRepo.findByAppUserId(id).orElse(null);
        UserInfoDTO userInfo = new UserInfoDTO(appUser.getId(), appUser.getEmail(), appUser.getRole(), userProfile);
        logger.info("✅ Returning user info for ID: {}", id);
        return ResponseEntity.ok(userInfo);
    }

    // 🔐 Secure profile creation — only for logged-in user
    @PostMapping("/postuser/{id}")
    public ResponseEntity<?> createProfile(@PathVariable Long id, @RequestBody UserDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("📝 Creating profile for user ID: {}, authenticated user: {}", id, username);

        AppUser appUser = appUserRepo.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("❌ AppUser not found for username: {}", username);
                    return new IllegalArgumentException("User not found");
                });

        if (!appUser.getId().equals(id)) {
            logger.warn("❌ ID mismatch — cannot post for another user, ID: {}, username: {}", id, username);
            return ResponseEntity.status(403).body("You can only post your own profile");
        }

        if (!appUser.getEmail().equalsIgnoreCase(dto.getEmail())) {
            logger.warn("❌ Email mismatch — payload email: {} doesn't match registered email: {}", dto.getEmail(), appUser.getEmail());
            return ResponseEntity.status(403).body("Email mismatch — profile not allowed");
        }

        boolean profileExists = userRepo.existsByAppUser(appUser);
        if (profileExists) {
            logger.warn("❌ Profile already exists for user ID: {}", id);
            return ResponseEntity.status(400).body("Profile already exists for this user");
        }

        try {
            User user = userService.create(dto, appUser);
            logger.info("✅ Profile created successfully for user: {}", username);
            return ResponseEntity.ok(new UserInfoDTO(appUser.getId(), appUser.getEmail(), appUser.getRole(), user));
        } catch (IllegalArgumentException e) {
            logger.error("❌ Error creating profile: {}", e.getMessage());
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            logger.error("❌ Unexpected error creating profile", e);
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    // 🔍 Get current user
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("🔍 Fetching current user: {}", username);

        AppUser appUser = appUserRepo.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("❌ User not found: {}", username);
                    return new IllegalArgumentException("User not found");
                });

        User userProfile = userRepo.findByAppUserId(appUser.getId()).orElse(null);
        UserInfoDTO userInfo = new UserInfoDTO(appUser.getId(), appUser.getEmail(), appUser.getRole(), userProfile);
        logger.info("✅ Returning user info for: {}, role: {}", username, appUser.getRole());
        return ResponseEntity.ok(userInfo);
    }

    // 🔄 Update profile — only if ID matches logged-in user
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody UserDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("📝 Updating profile for user ID: {}, authenticated user: {}", id, username);

        AppUser appUser = appUserRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        User existing = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        if (!existing.getAppUser().getId().equals(appUser.getId())) {
            logger.warn("❌ Unauthorized update attempt for ID: {} by user: {}", id, username);
            return ResponseEntity.status(403).body("You can only update your own profile");
        }

        try {
            User updated = userService.update(id, dto);
            logger.info("✅ Profile updated successfully for ID: {}", id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            logger.error("❌ Error updating profile: {}", e.getMessage());
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            logger.error("❌ Unexpected error updating profile", e);
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    // ❌ Delete — only if user owns the profile
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("🗑️ Deleting profile for user ID: {}, authenticated user: {}", id, username);

        AppUser appUser = appUserRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        User existing = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        if (!existing.getAppUser().getId().equals(appUser.getId())) {
            logger.warn("❌ Unauthorized delete attempt for ID: {} by user: {}", id, username);
            return ResponseEntity.status(403).body("You can only delete your own profile");
        }

        userService.delete(id);
        logger.info("✅ Profile deleted successfully for ID: {}", id);
        return ResponseEntity.ok("Profile deleted");
    }

    // 🔍 Search endpoints
    @GetMapping("/searchbyfirstname")
    public List<User> searchByFirstName(@RequestParam String firstname) {
        logger.info("🔍 Searching users by firstName: {}", firstname);
        return userService.searchByFirstName(firstname);
    }

    @GetMapping("/filterbyfirstname")
    public List<User> filterByFirstName(@RequestParam String keyword) {
        logger.info("🔍 Filtering users by firstName containing: {}", keyword);
        return userService.filterByFirstName(keyword);
    }

    @GetMapping("/searchbygender")
    public List<User> searchByGender(@RequestParam String gender) {
        logger.info("🔍 Searching users by gender: {}", gender);
        return userService.searchByGender(gender);
    }

    @GetMapping("/filterbygender")
    public List<User> filterByGender(@RequestParam String keyword) {
        logger.info("🔍 Filtering users by gender containing: {}", keyword);
        return userService.filterByGender(keyword);
    }
}