package employeetimesheet.timesheet.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import employeetimesheet.timesheet.service.CustomUserDetailsService;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        logger.debug("📥 Incoming request URI: {}", request.getRequestURI());

        // Skip JWT validation for auth endpoints
        if (request.getRequestURI().startsWith("/auth/")) {
            logger.info("🔄 Skipping JWT validation for auth endpoint: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                String tokenType = jwtUtil.extractTokenType(jwt);
                if (!"access".equals(tokenType)) {
                    logger.warn("❌ Token is not an access token. Type: {}. URI: {}", tokenType, request.getRequestURI());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Invalid token type");
                    return;
                }

                username = jwtUtil.extractUsername(jwt);
                logger.info("✅ JWT extracted: username={}, tokenType={}, URI={}", username, tokenType, request.getRequestURI());
            } catch (Exception e) {
                logger.error("❌ JWT extraction failed for URI: {}. Error: {}", request.getRequestURI(), e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid JWT token");
                return;
            }
        } else {
            logger.warn("❌ Authorization header missing or invalid format for URI: {}", request.getRequestURI());
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                boolean isTokenValid = jwtUtil.validateToken(jwt, userDetails);
                logger.debug("✅ Token validation result for user: {}, valid={}, authorities={}", 
                             username, isTokenValid, userDetails.getAuthorities());
                if (isTokenValid) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.info("✅ Authentication set for user: {}, URI: {}", username, request.getRequestURI());
                } else {
                    logger.warn("❌ Invalid JWT token for user: {}, URI: {}", username, request.getRequestURI());
                }
            } catch (Exception e) {
                logger.error("❌ Failed to load user details for username: {}, URI: {}. Error: {}", 
                             username, request.getRequestURI(), e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("User authentication failed");
                return;
            }
        } else if (username == null) {
            logger.warn("❌ No username extracted from JWT for URI: {}", request.getRequestURI());
        } else {
            logger.debug("✅ Authentication already exists for URI: {}, skipping JWT validation", request.getRequestURI());
        }

        filterChain.doFilter(request, response);
    }
}