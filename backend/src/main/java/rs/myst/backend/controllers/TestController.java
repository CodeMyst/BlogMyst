package rs.myst.backend.controllers;

import org.springframework.web.bind.annotation.*;
import rs.myst.backend.model.User;
import rs.myst.backend.repositories.UserRepository;

import java.util.List;

@RestController
public class TestController {
    private final UserRepository userRepo;

    public TestController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return (List<User>) userRepo.findAll();
    }

    @PostMapping("/users")
    public void addUser(@RequestBody User user) {
        userRepo.save(user);
    }
}
