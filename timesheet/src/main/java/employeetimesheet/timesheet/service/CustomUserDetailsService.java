package employeetimesheet.timesheet.service;

import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import employeetimesheet.timesheet.entity.AppUser;
import employeetimesheet.timesheet.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private AppUserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("🔍 Loading user from DB: {}", username);

        // Fetch user from repository
        Optional<AppUser> userOptional = userRepo.findByUsername(username);
        AppUser user = userOptional.orElseThrow(() -> {
            logger.warn("❌ User not found: {}", username);
            return new UsernameNotFoundException("User not found with username: " + username);
        });

        // Validate and process role
        String role = user.getRole();
        if (role == null || role.trim().isEmpty()) {
            logger.error("❌ User {} has no role assigned", username);
            throw new UsernameNotFoundException("User " + username + " has no valid role");
        }

        // Ensure role is prefixed with "ROLE_" if not already present
        String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        logger.info("✅ User found: username={}, raw role={}, mapped authority={}", 
                    user.getUsername(), role, authority);

        // Create list of authorities (single role for now)
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(authority));

        // Return UserDetails object
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            authorities
        );
    }
}