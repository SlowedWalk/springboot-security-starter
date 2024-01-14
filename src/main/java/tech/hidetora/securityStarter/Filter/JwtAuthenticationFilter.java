package tech.hidetora.securityStarter.Filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tech.hidetora.securityStarter.service.UserService;
import tech.hidetora.securityStarter.service.impl.UserServiceImpl;
import tech.hidetora.securityStarter.utils.JwtTokenUtil;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtDecoder jwtDecoder;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        if (request.getServletPath().contains("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Get authorization header and validate
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("AUTHORIZATION HEADER: {}", authHeader);
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        // Get jwt token and validate
        final String token = authHeader.split(" ")[1].trim();
        if (Objects.requireNonNull(jwtDecoder.decode(token).getExpiresAt()).isBefore(Instant.now())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Get user identity and set it on the spring security context

        UserDetails userDetails = userDetailsService.loadUserByUsername(jwtDecoder.decode(token).getSubject());
        log.info("USER DETAILS: {}", userDetails);
        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null,
                userDetails == null ?
                        Collections.emptyList() : userDetails.getAuthorities()
        );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
