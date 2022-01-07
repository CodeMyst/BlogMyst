package rs.myst.backend.filters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import rs.myst.backend.constants.SecurityConstants;
import rs.myst.backend.model.User;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authManager;

    public AuthenticationFilter(AuthenticationManager authManager) {
        this.authManager = authManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            JsonNode json = new ObjectMapper().readTree(body);
            String username = json.get("username").asText();
            String password = json.get("password").asText();

            User user = new User();
            user.setUsername(username);
            user.setPasswordHash(password);

            return authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPasswordHash(), new ArrayList<>())
            );
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        Date exp = new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME);
        Key key = Keys.hmacShaKeyFor(SecurityConstants.KEY.getBytes());
        Claims claims = Jwts.claims().setSubject(((org.springframework.security.core.userdetails.User) authResult.getPrincipal()).getUsername());
        String token = Jwts.builder().setClaims(claims).signWith(key, SignatureAlgorithm.HS512).setExpiration(exp).compact();

        // spring and servlets are stupid
        // to set samesite you have to create some kind of response cookie,
        // and the response.setCookie asks for a servlet cookie and not this one
        // so gotta set the cookie through the header... kek

        ResponseCookie c = ResponseCookie.from("blogmyst", token)
                .httpOnly(true)
                .maxAge(Duration.ofMillis(SecurityConstants.EXPIRATION_TIME))
                .sameSite("lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, c.toString());
    }
}
