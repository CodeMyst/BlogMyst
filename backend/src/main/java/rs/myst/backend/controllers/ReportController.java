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

        Report report = new Report();
        report.setDate(new Timestamp(new Date().getTime()));
        report.setReason(reason);
        report.setType(ReportType.BLOG);
        report.setSubjectId(author + "/" + blogUrl);
        report.setUser(user);

        reportRepository.save(report);

        return ResponseEntity.ok().build();
    }
}
