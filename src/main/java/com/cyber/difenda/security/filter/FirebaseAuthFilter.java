package com.cyber.difenda.security.filter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class FirebaseAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            try {
                String token = header.replace("Bearer ", "");
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);

                // Extract roles from custom claims
                //Map<String, Object> claims = decodedToken.getClaims();
                //TODO: Implement Roles functionality
                List<String> roles = Collections.emptyList();//(List<String>) claims.getOrDefault("roles", Collections.emptyList());

                // Set Authentication in Spring context
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        decodedToken.getUid(),
                        null,
                        roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid Firebase token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}

