package com.nanokulon.nanotracker.security;

import com.nanokulon.nanotracker.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtTokenUtils;
    private final UserDetailsService userDetailsService;
    private final MessageSource messageSource;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String jwtToken = this.extractBearerToken(request);
        if (jwtToken != null) {
            try {
                String username = this.jwtTokenUtils.getUsername(jwtToken);
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    Authentication token =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(token);
                }
            } catch (ExpiredJwtException exception) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                        this.getLocaleMessage("jwt.errors.expire", request.getLocale()));
                return;
            } catch (JwtException exception) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                        this.getLocaleMessage("jwt.errors.invalid", request.getLocale()));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractBearerToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        return null;
    }

    private String getLocaleMessage(String message, Locale locale) {
        return this.messageSource.getMessage(message, new Object[0], message, locale);
    }
}
