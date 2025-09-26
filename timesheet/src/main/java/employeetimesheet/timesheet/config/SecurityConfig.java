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
        logger.info("🔐 SecurityConfig initialized");
    }

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    // config.setAllowedOrigins(List.of("https://timesheet-frontend-forgetpassword-r-five.vercel.app")); // ✅ Frontend origin
     config.setAllowedOrigins(List.of("http://localhost:3000","http://localhost:8080")); 
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // ✅ Include OPTIONS
    config.setAllowedHeaders(List.of("*")); // ✅ Allow all headers
    config.setAllowCredentials(true); // ✅ If you're using cookies or auth headers

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config); // ✅ Apply to all endpoints
    return source;
}

    
   @Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    logger.info("🔐 Configuring SecurityFilterChain");

    return http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/**").permitAll() // 🔓 Open to all
            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").hasRole("ADMIN") // 🔐 Swagger only for admins

            // ✅ Role-based access control
            .requestMatchers("/api/users").hasRole("USER")// 🔓 Only this endpoint for USER
            .requestMatchers("/api/users/postuser/**").hasRole("USER") 
            .requestMatchers("/api/users/me").hasRole("USER")
            .requestMatchers("/api/users/{id}").hasRole("USER")
            .requestMatchers("/api/**").hasRole("ADMIN")   // 🔐 All other /api/** endpoints for ADMIN only

            .anyRequest().authenticated() // 🔒 Everything else needs authentication
        )
        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
}



    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("🔐 Initializing PasswordEncoder (BCrypt)");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        logger.info("🔐 Creating AuthenticationManager bean");
        return config.getAuthenticationManager();
    }
}