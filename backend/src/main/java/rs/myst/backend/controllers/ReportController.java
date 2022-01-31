package rs.myst.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rs.myst.backend.constants.AuthConstants;
import rs.myst.backend.model.Report;
import rs.myst.backend.model.ReportType;
import rs.myst.backend.model.User;
import rs.myst.backend.repositories.ReportRepository;
import rs.myst.backend.repositories.UserRepository;
import rs.myst.backend.services.UserDetailsImpl;

import java.sql.Timestamp;
import java.util.Date;

@RestController
@RequestMapping("/api/report")
public class ReportController {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    public ReportController(ReportRepository reportRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/{author}/{blogUrl}")
    @PreAuthorize(AuthConstants.USER_AUTH)
    public ResponseEntity<?> postReportBlog(@PathVariable String author, @PathVariable String blogUrl, @RequestBody String reason) {
        UserDetailsImpl currentUser = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(currentUser.getUsername()).orElseThrow();

        report(ReportType.BLOG, author + "/" + blogUrl, user, reason);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{author}/{blogUrl}/{postUrl}")
    @PreAuthorize(AuthConstants.USER_AUTH)
    public ResponseEntity<?> postReportPost(@PathVariable String author, @PathVariable String blogUrl, @PathVariable String postUrl, @RequestBody String reason) {
        UserDetailsImpl currentUser = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(currentUser.getUsername()).orElseThrow();

        report(ReportType.POST, author + "/" + blogUrl + "/" + postUrl, user, reason);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{commentId}")
    @PreAuthorize(AuthConstants.USER_AUTH)
    public ResponseEntity<?> postReportComment(@PathVariable Integer commentId, @RequestBody String reason) {
        UserDetailsImpl currentUser = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(currentUser.getUsername()).orElseThrow();

        report(ReportType.COMMENT, commentId.toString(), user, reason);

        return ResponseEntity.ok().build();
    }

    private void report(ReportType type, String id, User user, String reason) {
        Report report = new Report();
        report.setDate(new Timestamp(new Date().getTime()));
        report.setReason(reason);
        report.setType(type);
        report.setSubjectId(id);
        report.setUser(user);

        reportRepository.save(report);
    }
}
