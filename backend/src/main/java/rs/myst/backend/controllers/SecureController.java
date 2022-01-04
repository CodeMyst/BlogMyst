package rs.myst.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO: Just for testing...

@RestController
@RequestMapping("/api/secure")
public class SecureController {
    @GetMapping
    public ResponseEntity getSecure() {
        return new ResponseEntity("woohoo you got a super secret secure endpoint!", HttpStatus.OK);
    }
}
