package employeetimesheet.timesheet.service;

import org.springframework.stereotype.Service;

import employeetimesheet.timesheet.entity.AppUser;
import employeetimesheet.timesheet.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private AppUserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("🔍 Loading user from DB: {}", username);

        AppUser user = userRepo.findByUsername(username)
            .orElseThrow(() -> {
                logger.warn("❌ User not found: {}", username);
                return new UsernameNotFoundException("User not found");
            });

        logger.info("✅ User found: {}, Role: {}", user.getUsername(), user.getRole());

        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            List.of(new SimpleGrantedAuthority(user.getRole()))
        );
    }
}