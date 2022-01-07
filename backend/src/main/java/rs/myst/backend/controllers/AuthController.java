package rs.myst.backend.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import rs.myst.backend.model.User;
import rs.myst.backend.model.UserRole;
import rs.myst.backend.payload.LoginRequest;
import rs.myst.backend.payload.MessageResponse;
import rs.myst.backend.payload.RegisterRequest;
import rs.myst.backend.payload.UserInfoResponse;
import rs.myst.backend.repositories.UserRepository;
import rs.myst.backend.services.UserDetailsImpl;
import rs.myst.backend.utils.JwtUtils;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookies = jwtUtils.generateJwtCookies(userDetails);

        String role = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst().orElseThrow();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookies.toString())
                .body(new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), role));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken."));
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));

        user.setRole(userRepository.count() == 0 ? UserRole.ADMIN : UserRole.USER);

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully."));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookies = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookies.toString())
                .body(new MessageResponse("You've been logged out."));
    }

    @GetMapping("/username")
    public ResponseEntity<Map<String, String>> getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof AnonymousAuthenticationToken) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();

        Map<String, String> res = new HashMap<>();
        res.put("username", userDetails.getUsername());

        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
