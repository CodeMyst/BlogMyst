package rs.myst.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rs.myst.backend.model.User;
import rs.myst.backend.model.UserRole;
import rs.myst.backend.repositories.UserRepository;

import java.security.Principal;

@RestController
public class AuthController {
    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepo, BCryptPasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public void register(@RequestBody RegisterInfo info) {
        if (userRepo.existsByUsername(info.username)) {
            // already exists a user with the same username
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with the provided username already exists.");
        }

        User user = new User();
        user.setUsername(info.username);
        user.setPasswordHash(passwordEncoder.encode(info.password));
        user.setRole(UserRole.USER);
        userRepo.save(user);
    }

    @GetMapping("/username")
    public String getUsername(Principal principal) {
        return principal.getName();
    }

    private static class RegisterInfo {
        public String username;
        public String password;
    }
}
