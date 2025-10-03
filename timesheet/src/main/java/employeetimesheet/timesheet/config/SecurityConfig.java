package employeetimesheet.timesheet.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    public SecurityConfig() {
        logger.info("🔐 SecurityConfig initialized at {}", java.time.LocalDateTime.now());
    }

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        logger.info("🔐 Configuring CORS settings");
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:3001", "http://localhost:8080","https://employeetimeshhet-176m.vercel.app"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        logger.info("🔐 Configuring SecurityFilterChain at {}", java.time.LocalDateTime.now());

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints (no authentication required)
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").hasRole("ADMIN")

                // User and Admin accessible endpoints
                .requestMatchers("/api/users").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/users/postuser/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/users/me").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/users/{id}").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/timesheets/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/task-categories").hasAnyRole("USER", "ADMIN") // Added for task categories
                .requestMatchers("/api/shifts").hasAnyRole("USER", "ADMIN") // Added for shifts
                .requestMatchers("/api/user-positions/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/getallteams/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/roles/getallroles/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/users/all/**").hasAnyRole("USER", "ADMIN")
                // Admin-only endpoints
                .requestMatchers("/api/roles/postrole").hasRole("ADMIN")
                .requestMatchers("/api/roles/{id}").hasRole("ADMIN")
                .requestMatchers("/api/positions").hasRole("ADMIN")
                .requestMatchers("/api/positions/postpositions").hasRole("ADMIN")
                .requestMatchers("/api/positions/{id}").hasRole("ADMIN")

                // Catch-all for other API endpoints (admin-only unless specified)
                .requestMatchers("/api/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("🔐 Initializing PasswordEncoder (BCrypt) at {}", java.time.LocalDateTime.now());
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        logger.info("🔐 Creating AuthenticationManager bean at {}", java.time.LocalDateTime.now());
        return config.getAuthenticationManager();
    }
}