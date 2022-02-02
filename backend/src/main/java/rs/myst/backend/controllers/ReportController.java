package rs.myst.backend.controllers;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rs.myst.backend.constants.AuthConstants;
import rs.myst.backend.model.*;
import rs.myst.backend.repositories.CommentRepository;
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
    private final CommentRepository commentRepository;

    public ReportController(ReportRepository reportRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
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

        Comment comment = commentRepository.findById(commentId).orElseThrow();
        Post post = comment.getPost();
        Blog blog = post.getBlog();
        User author = blog.getAuthor();

        report(ReportType.COMMENT, author.getUsername() + "/" + blog.getUrl() + "/" + post.getUrl() + "#" + commentId, user, reason);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize(AuthConstants.MOD_AUTH)
    public ResponseEntity<?> getReports() {
        return ResponseEntity.ok(reportRepository.findAll(Sort.by(Sort.Direction.DESC, "date")));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(AuthConstants.MOD_AUTH)
    public ResponseEntity<?> deleteReport(@PathVariable Integer id) {
        reportRepository.deleteById(id);
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
