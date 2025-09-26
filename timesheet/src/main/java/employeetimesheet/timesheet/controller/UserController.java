package employeetimesheet.timesheet.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
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


    // 🔍 Get all users (admin only ideally)
    @GetMapping
    public List<User> getAll() {
        return userService.findAll();
    }

    // 🔍 Get user by ID
  @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        AppUser appUser = appUserRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        User userProfile = userRepo.findByAppUserId(id)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for user id: " + id));
        UserInfoDTO userInfo = new UserInfoDTO(appUser.getId(), appUser.getEmail(), userProfile);
        return ResponseEntity.ok(userInfo);
    }

    // 🔐 Secure profile creation — only for logged-in user
   @PostMapping("/postuser/{id}")
public ResponseEntity<?> createProfile(@PathVariable Long id, @RequestBody UserDTO dto) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    System.out.println("🔐 Token username: " + username);

    AppUser appUser = appUserRepo.findByUsername(username).orElseThrow(() -> {
        System.out.println("❌ AppUser not found for username: " + username);
        return new IllegalArgumentException("User not found");
    });

    System.out.println("✅ AppUser ID from DB: " + appUser.getId());
    System.out.println("✅ ID from URL: " + id);

    if (!appUser.getId().equals(id)) {
        System.out.println("❌ ID mismatch — cannot post for another user");
        return ResponseEntity.status(403).body("You can only post your own profile");
    }

    System.out.println("✅ Email in DB: " + appUser.getEmail());
    System.out.println("✅ Email in payload: " + dto.getEmail());

    if (!appUser.getEmail().equalsIgnoreCase(dto.getEmail())) {
        System.out.println("❌ Email mismatch — payload email doesn't match registered email");
        return ResponseEntity.status(403).body("Email mismatch — profile not allowed");
    }

    boolean profileExists = userRepo.existsByAppUser(appUser);
    System.out.println("🔍 Profile already exists: " + profileExists);

    if (profileExists) {
        System.out.println("❌ Profile already exists for this user");
        return ResponseEntity.status(403).body("Profile already exists for this user");
    }

    User user = userService.create(dto, appUser);
    System.out.println("✅ Profile created successfully for: " + username);

    return ResponseEntity.ok(user);
}

@GetMapping("/me")
public ResponseEntity<?> getCurrentUser() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    logger.info("Authenticated username: {}", username);
    AppUser appUser = appUserRepo.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    logger.info("User ID: {}", appUser.getId());

    User userProfile = userRepo.findByAppUserId(appUser.getId()).orElse(null);
    if (userProfile != null) {
        UserInfoDTO userInfo = new UserInfoDTO(appUser.getId(), appUser.getEmail(), userProfile);
        return ResponseEntity.ok(userInfo);
    } else {
        return ResponseEntity.ok(new UserInfoDTO(appUser.getId(), appUser.getEmail()));
    }
}

    // 🔄 Update profile — only if ID matches logged-in user
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody UserDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser appUser = appUserRepo.findByUsername(username).orElseThrow();

        User existing = userRepo.findById(id).orElse(null);
        if (existing == null || !existing.getAppUser().getId().equals(appUser.getId())) {
            return ResponseEntity.status(403).body("❌ You can only update your own profile");
        }

        User updated = userService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    // ❌ Delete — only if user owns the profile
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser appUser = appUserRepo.findByUsername(username).orElseThrow();

        User existing = userRepo.findById(id).orElse(null);
        if (existing == null || !existing.getAppUser().getId().equals(appUser.getId())) {
            return ResponseEntity.status(403).body("❌ You can only delete your own profile");
        }

        userService.delete(id);
        return ResponseEntity.ok("✅ Profile deleted");
    }

    // 🔍 Search endpoints (optional)
    @GetMapping("/searchbyfirstname")
    public List<User> searchByFirstName(@RequestParam String firstname) {
        return userService.searchByFirstName(firstname);
    }

    @GetMapping("/filterbyfirstname")
    public List<User> filterByFirstName(@RequestParam String keyword) {
        return userService.filterByFirstName(keyword);
    }

    @GetMapping("/searchbygender")
    public List<User> searchByGender(@RequestParam String gender) {
        return userService.searchByGender(gender);
    }

    @GetMapping("/filterbygender")
    public List<User> filterByGender(@RequestParam String keyword) {
        return userService.filterByGender(keyword);
    }
}
