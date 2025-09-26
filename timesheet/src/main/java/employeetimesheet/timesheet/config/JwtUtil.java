package employeetimesheet.timesheet.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.nio.charset.StandardCharsets;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    // Use a secure, randomly generated key (at least 32 bytes for HS256)
    private final String SECRET_KEY = "8Y2jX5kP9mW3qZ7tR4vN6bL2hF8gD1cJ3xK9pQ5wT2rY6mB";

   // 🔐 Access Token (short-lived)
public String generateToken(String username) {
    logger.info("🔐 Generating access token for user: {}", username);
    return Jwts.builder()
        .setSubject(username)
        .claim("type", "access")
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // 5 mins
        .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes(StandardCharsets.UTF_8))
        .compact();
}

// 🔄 Refresh Token (long-lived)
public String generateRefreshToken(String username) {
    logger.info("🔄 Generating refresh token for user: {}", username);
    return Jwts.builder()
        .setSubject(username)
        .claim("type", "refresh")
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)) // 7 days
        .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes(StandardCharsets.UTF_8))
        .compact();
}

// 🆕 Add this method to extract token type
public String extractTokenType(String token) {
    try {
        return getClaims(token).get("type", String.class);
    } catch (Exception e) {
        logger.error("❌ Failed to extract token type", e);
        return null;
    }
}


    // 🔍 Extract username from token
    public String extractUsername(String token) {
        try {
            String username = getClaims(token).getSubject();
            logger.debug("✅ Extracted username from token: {}", username);
            return username;
        } catch (Exception e) {
            logger.error("❌ Failed to extract username from token", e);
            return null;
        }
    }

    // 📅 Optional: Extract expiration date
    public Date extractExpiration(String token) {
        try {
            return getClaims(token).getExpiration();
        } catch (Exception e) {
            logger.error("❌ Failed to extract expiration from token", e);
            return null;
        }
    }

    // ✅ Validate token
    public boolean validateToken(String token, UserDetails userDetails) {
        String extracted = extractUsername(token);
        boolean isValid = extracted != null && extracted.equals(userDetails.getUsername());
        // logger.info("🔍 Token validation for {}: {}", userDetails.getUsername(), isValid ? "VALID" : "INVALID");
        return isValid;
    }

    // 🔧 Internal helper
    private Claims getClaims(String token) {
        return Jwts.parser()
            .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
            .parseClaimsJws(token)
            .getBody();
    }
}
