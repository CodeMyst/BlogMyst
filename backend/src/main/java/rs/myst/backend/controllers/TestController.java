package rs.myst.backend.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.myst.backend.constants.AuthConstants;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @RequestMapping("/all")
    public String all() {
        return "Public content.";
    }

    @GetMapping("/user")
    @PreAuthorize(AuthConstants.USER_AUTH)
    public String user() {
        return "User content";
    }

    @GetMapping("/mod")
    @PreAuthorize(AuthConstants.MOD_AUTH)
    public String mod() {
        return "Mod content";
    }

    @GetMapping("/admin")
    @PreAuthorize(AuthConstants.ADMIN_AUTH)
    public String admin() {
        return "Admin content";
    }
}
