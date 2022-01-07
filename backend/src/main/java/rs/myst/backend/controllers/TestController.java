package rs.myst.backend.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @RequestMapping("/all")
    public String all() {
        return "Public content.";
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('MOD') or hasAuthority('ADMIN')")
    public String user() {
        return "User content";
    }

    @GetMapping("/mod")
    @PreAuthorize("hasAuthority('MOD')")
    public String mod() {
        return "Mod content";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String admin() {
        return "Admin content";
    }
}
