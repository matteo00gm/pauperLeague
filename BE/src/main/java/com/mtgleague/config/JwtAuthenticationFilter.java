package com.mtgleague.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mtgleague.service.PlayersService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final PlayersService playersService;

    private static final List<Pattern> ADMIN_ROUTES = Arrays.asList(
            Pattern.compile("^/events/new$"),
            Pattern.compile("^/events/delete$"),
            Pattern.compile("^/events/[0-9a-fA-F]{1,}/kick$"),
            Pattern.compile("^/events/[0-9a-fA-F]{1,}/start$"),
            Pattern.compile("^/events/[0-9a-fA-F]{1,}/startTimer$"),
            Pattern.compile("^/events/edit$"),
            Pattern.compile("^/leagues/removeMember$"),
            Pattern.compile("^/subs/[0-9a-fA-F]{1,}$"),
            Pattern.compile("^/subs/event/accept$"),
            Pattern.compile("^/subs/event/deny$"),
            Pattern.compile("^/subs/league/deny$"),
            Pattern.compile("^/subs/league/accept$")
    );

    private static final List<Pattern> SELF_ROUTES = Arrays.asList(
            Pattern.compile("^/events/[0-9a-fA-F]{1,}/register$"),
            Pattern.compile("^/events/[0-9a-fA-F]{1,}/quit$"),
            Pattern.compile("^/leagues/[0-9a-fA-F]{1,}/leave$"),
            Pattern.compile("^/leagues/my-subs$"),
            Pattern.compile("^/events/my-events$"),
            Pattern.compile("^/subs/my-subs$"),
            Pattern.compile("^/rounds/save$"),
            Pattern.compile("^/events/drop$")
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        final String userEmail;
        String playerIdFromBody = null; // To hold player ID from the request body
        String leagueIdFromBody = null; // To hold league ID from the request body
        boolean mustBeHimself = mustBeHimself(request.getRequestURI()); // Flag for self check
        boolean isAdminCheckRequired = isAdminCheckRequired(request.getRequestURI()); // Flag for admin check

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwtToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwtToken);

        // Wrap the request to buffer the input stream
        HttpServletRequestWrapper requestWrapper = new BufferedRequestWrapper(request);

        // Parse the JSON request body if necessary
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(requestWrapper.getInputStream());

        // Check if the request body contains player ID
        if (jsonNode.has("playerId")) {
            playerIdFromBody = jsonNode.get("playerId").asText();
        }

        if (jsonNode.has("leagueId")) {
            leagueIdFromBody = jsonNode.get("leagueId").asText();
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            if (jwtService.isTokenValid(jwtToken, userDetails)) {
                // Extract player ID from the JWT token claims
                String playerIdFromToken = jwtService.extractClaim(jwtToken, claims -> claims.get("playerId", String.class));

                if (mustBeHimself) {
                    // Check for player ID in the request body
                    if (playerIdFromBody == null) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Player ID is required for this operation");
                        return;
                    }

                    // Check if the player ID from the body matches the player ID from the token
                    if (!playerIdFromBody.equals(playerIdFromToken)) {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access unauthorized");
                        return;
                    }
                }

                // If admin check is required, perform the check
                if (isAdminCheckRequired) {
                    // Check for leagueId
                    if (leagueIdFromBody == null) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "League ID is required for admin check");
                        return;
                    }

                    // Check if the player is an admin of the league
                    boolean isAdmin = playersService.isAdminOfLeague(playerIdFromToken, leagueIdFromBody);
                    if (!isAdmin) {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "User is not an admin of the league");
                        return;
                    }
                }

                // If checks pass, proceed with authentication
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        null
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(requestWrapper, response);
    }

    private boolean mustBeHimself(String requestURI) {
        return SELF_ROUTES.stream().anyMatch(pattern -> pattern.matcher(requestURI).matches());
    }

    private boolean isAdminCheckRequired(String requestURI) {
        return ADMIN_ROUTES.stream().anyMatch(pattern -> pattern.matcher(requestURI).matches());
    }

    private static class BufferedRequestWrapper extends HttpServletRequestWrapper {
        private byte[] requestBody;

        public BufferedRequestWrapper(HttpServletRequest request) throws IOException {
            super(request);
            requestBody = request.getInputStream().readAllBytes();
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(getInputStream()));
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            return new BufferedServletInputStream(requestBody);
        }
    }

    private static class BufferedServletInputStream extends ServletInputStream {
        private final byte[] buffer;
        private int index = 0;

        public BufferedServletInputStream(byte[] buffer) {
            this.buffer = buffer;
        }
        @Override
        public int read() throws IOException {
            if (index >= buffer.length) {
                return -1;
            }
            return buffer[index++];
        }
        @Override
        public boolean isFinished() {
            return index >= buffer.length;
        }
        @Override
        public boolean isReady() {
            return true;
        }
        @Override
        public void setReadListener(ReadListener readListener) {
        }
    }
}
