package rs.myst.backend.controllers;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import rs.myst.backend.model.User;
import rs.myst.backend.model.UserRole;
import rs.myst.backend.repositories.UserRepository;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepo, BCryptPasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public void register(@RequestBody RegisterInfo info) {
        User user = new User();
        user.setUsername(info.username);
        user.setPasswordHash(passwordEncoder.encode(info.password));
        user.setRole(UserRole.USER);
        userRepo.save(user);
    }

    private static class RegisterInfo {
        public String username;
        public String password;
    }
}
